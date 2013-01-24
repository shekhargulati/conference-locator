package com.conferencelocator.config;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.Mongo;

public class ApplicationConfig {

	private static final String OAuthConsumerKey = "D5QsM4lVmWaY71FAje5yA";
	private static final String OAuthConsumerSecret = "XcumieVWKz466he7Fi9BgeowyQKC4yTxEj9IXRFFg";
	private static final String OAuthAccessToken = "66993334-1Q3bdTQuhygGHNeNUgdmtJEjP5grTAsrIemZ8ku7T";
	private static final String OAuthAccessTokenSecret = "XTNQdVaPfdFcQq6E30WHX6E69j5Ke3gVeU3va7brOYc";

//	@Produces
//	public MongoTemplate mongoTemplate() throws Exception {
//		Mongo mongo = new Mongo("localhost", 27017);
//		String databaseName = "conferencelocator";
//		MongoTemplate mongoTemplate = new MongoTemplate(mongo, databaseName);
//		return mongoTemplate;
//	}
	
	@Produces
	public MongoTemplate mongoTemplate() throws Exception {
		String host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
		int port = Integer.valueOf(System.getenv("OPENSHIFT_MONGODB_DB_PORT"));
		
		Mongo mongo = new Mongo(host,port);
		String databaseName = System.getenv("OPENSHIFT_APP_NAME");
		String username = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
		String password = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
		MongoTemplate mongoTemplate = new MongoTemplate(mongo, databaseName, new UserCredentials(username, password));
		return mongoTemplate;
	}
	
	@Produces
	public Twitter twitter(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(OAuthConsumerKey);
		cb.setOAuthConsumerSecret(OAuthConsumerSecret);
		cb.setOAuthAccessToken(OAuthAccessToken);
		cb.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		
		TwitterFactory twitterFactory = new TwitterFactory(cb.build());
		return twitterFactory.getInstance();
	}
	
	@Produces
    public Logger logger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
	
}
