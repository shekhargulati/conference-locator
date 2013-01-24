package com.conferencelocator.scheduler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;

import com.conferencelocator.classifier.ConferenceClassifier;
import com.conferencelocator.domain.Conference;
import com.conferencelocator.utils.TwitterUtils;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLFetcher;

@Singleton
public class TweetsPoller {

	private static final String[] SEARCH_TERMS = { "cfp", "call for paper" };

	@Inject
	private MongoTemplate mongoTemplate;

	@Inject
	private Twitter twitter;

	@Inject
	private Logger logger;

	@Schedule(minute = "*/15", hour = "*", persistent = true)
	public void pollTweets() {
		logger.info("Job started at : " + new Date());
		for (String searchTerm : SEARCH_TERMS) {
			logger.info("Searching for Search Term : " + searchTerm);
			List<Status> tweets = search(searchTerm);
			for (Status tweet : tweets) {
				if (hasAtleastOneUrl(tweet) && !conferenceAlreadyExists(tweet)) {
					Conference conference = tweetToConference(tweet);
					if (conference != null) {
						mongoTemplate.insert(conference);
					}
				}
			}
		}
	}

	private Conference tweetToConference(Status tweet) {
		Conference conference = new Conference();
		conference.setTweetId(tweet.getId());
		String url = expandUrl(tweet.getURLEntities()[0]);
		logger.info("Expanded Url " + url);
		if (url == null) {
			logger.info("Not creating conference object because url is null for tweet "
					+ tweet.getText());
			return null;
		}
		try {
			TextDocument textDocument = new BoilerpipeSAXInput(HTMLFetcher
					.fetch(new URL(url)).toInputSource()).getTextDocument();
			String title = textDocument.getTitle();
			if (StringUtils.isBlank(title)
					|| StringUtils.equalsIgnoreCase("Redirect Notice", title)
					|| StringUtils
							.equalsIgnoreCase("Cannot find server", title)) {

				logger.info("Not creating conference because title is : "
						+ title);
				return null;
			}
			
			if(conferenceExistsWithSameUrlOrName(url,title)){
				logger.info("Not creating conference because a conference already exists with same name or url");
				return null;
			}
			
			ArticleExtractor.INSTANCE.process(textDocument);
			String text = textDocument.getContent();
			if (StringUtils.isBlank(text)) {
				logger.info("Boilerpipe didn't found any text for tweet : "+tweet.getText());
			}
			
			conference.setUrl(url);
			conference.setTweetText(tweet.getText());
			conference.setName(title);
			conference.setTags(classify(text));
			ISummariser summariser = new SimpleSummariser();
			conference.setInfo(summariser.summarise(text, 3));
			
			return conference;
		} catch (Exception e) {
			logger.severe("Exception encountered while reading data for tweet "
					+ e.getMessage());
			return null;
		}

	}

	private String[] classify(String text) throws Exception{
		List<String> tags = new ArrayList<String>();
		if(ConferenceClassifier.classify("java", text)){
			tags.add("java");
		}
		
		String nosqlTags = "nosql,mongodb,redis,neo4j,cassandra,hbase,riak,graphdb";
		if(ConferenceClassifier.classify(nosqlTags, text)){
			tags.addAll(Arrays.asList(nosqlTags.split(",")));
		}
		
		String bigdataTags = "bigdata, big data, hadoop,mahout,scalabe, scalability";
		if(ConferenceClassifier.classify(bigdataTags, text)){
			tags.addAll(Arrays.asList(bigdataTags.split(",")));
		}

		if(ConferenceClassifier.classify("ruby", text)){
			tags.add("ruby");
		}
		
		if(ConferenceClassifier.classify("python", text)){
			tags.add("python");
		}
		
		if(ConferenceClassifier.classify("scala", text)){
			tags.add("scala");
		}

		if(ConferenceClassifier.classify("php", text)){
			tags.add("php");
		}
		
		String mysqlTags = "mysql,rdbms";
		if(ConferenceClassifier.classify(mysqlTags, text)){
			tags.addAll(Arrays.asList(mysqlTags.split(",")));
		}
		
		String postgresTags = "postgres, postgresql,rdbms";
		if(ConferenceClassifier.classify(postgresTags, text)){
			tags.addAll(Arrays.asList(postgresTags.split(",")));
		}
		
		String mobileTags = "mobile,android";
		if(ConferenceClassifier.classify(mobileTags, text)){
			tags.addAll(Arrays.asList(mobileTags.split(",")));
		}
		
		String uiTags = "html5, css";
		if(ConferenceClassifier.classify(uiTags, text)){
			tags.addAll(Arrays.asList(uiTags.split(",")));
		}
		
		String javascriptTags = "javascript,node.js,angular.js,jquery,json,phantom.js";
		if(ConferenceClassifier.classify(javascriptTags, text)){
			tags.addAll(Arrays.asList(javascriptTags.split(",")));
		}
		
		String webTags = "web,web 2.0";
		if(ConferenceClassifier.classify(webTags, text)){
			tags.addAll(Arrays.asList(webTags.split(",")));
		}
		
		return tags.toArray(new String[0]);
	}
	private boolean conferenceExistsWithSameUrlOrName(String url, String name) {
		if(mongoTemplate.findOne(
				org.springframework.data.mongodb.core.query.Query
						.query(Criteria.where("url").is(url).orOperator(Criteria.where("name").is(name))),
				Conference.class) != null){
			return true;
		}
		
		return false;
	}

	private boolean conferenceAlreadyExists(Status status) {
		return mongoTemplate.findOne(
				org.springframework.data.mongodb.core.query.Query
						.query(Criteria.where("tweetId").is(status.getId())),
				Conference.class) != null;
	}

	private boolean hasAtleastOneUrl(Status status) {
		return !(status.getURLEntities() == null || status.getURLEntities().length == 0);
	}

	private List<Status> search(String searchTerm) {
		Query query = new Query();
		query.count(100);
		query.setQuery(searchTerm);
		QueryResult queryResult = null;
		try {
			queryResult = twitter.search(query);
		} catch (TwitterException twe) {
			return Collections.<Status> emptyList();
		}
		List<Status> tweets = queryResult.getTweets();
		return tweets;
	}

	private String expandUrl(URLEntity urlEntity) {
		String expandedUrl = urlEntity.getExpandedURL();
		String actualUrl = null;
		try {
			actualUrl = TwitterUtils.convertShortUrlToLongUrl(expandedUrl);
			return actualUrl;
		} catch (IOException e) {
			System.err.println(e);
			return null;
		}
	}
	

}
