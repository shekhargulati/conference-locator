package com.conferencelocator.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import twitter4j.HashtagEntity;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

/**
 * A utility class for twitter related stuff
 * 
 * @author shekhar
 * 
 */
public abstract class TwitterUtils {

    private static final String[] SHORT_URL_DOMAINS = { "bit.ly", "t.co", "awe.sm", "su.pr", "ow.ly", "tr.im", "wp.me", "red.ht", "tinyurl.com",
            "is.gd", "goo.gl", "lnkd.in", "www.google.com", "feedproxy.google.com", "feeds.feedburner.com", "www.linkedin.com", "networkedblogs.com" };

    private static final int MAX_LOOPS = 5;

    /**
     * Checks if a tweet is a retweet
     * 
     * @param tweet
     * 
     * @return true if retweet else false
     */
    public static boolean isRetweet(String tweet) {
        if (StringUtils.startsWith(tweet, "RT @")) {
            return true;
        }
        return false;
    }

    /**
     * Convert HashTagEntities to String tags
     * 
     * @param hashtagEntities
     * @return List of string hash tags
     * 
     */
    public static List<String> hashTags(HashtagEntity[] hashtagEntities) {
        List<String> tags = new ArrayList<String>();
        if (ArrayUtils.isEmpty(hashtagEntities)) {
            return tags;
        }
        for (HashtagEntity hashtagEntity : hashtagEntities) {
            tags.add(StringUtils.lowerCase(hashtagEntity.getText()));
        }
        return tags;
    }

    public static String replaceAllUrlsInATweetWithAnchorTags(String text, URLEntity[] urlEntities) {
        for (URLEntity urlEntity : urlEntities) {
            String displayURL = urlEntity.getURL().toString();
            text = text.replace(displayURL, UrlUtils.convertUrlToAnchorTagWithTargetBlank(displayURL, displayURL));
        }
        return text;
    }

    public static String convertShortUrlToLongUrl(String url) throws IOException {
        return convertShortUrlToLongUrl(new URL(url));
    }

    public static String convertShortUrlToLongUrl(URL url) throws IOException {
        for (int i = 0; i < MAX_LOOPS; i++) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            String expandedUrl = urlConnection.getHeaderField("location");
            if (expandedUrl == null) {
                return url.toString();
            }
            String anotherExpandedUrl = convertShortUrlToLongUrl(expandedUrl);
            if(StringUtils.equalsIgnoreCase(expandedUrl, anotherExpandedUrl)){
                return expandedUrl;
            }
            url = new URL(anotherExpandedUrl);
        }
        return url.toString();
    }

    public static boolean isShortUrl(String expandedUrl) {
        URL url = null;
        try {
            url = new URL(expandedUrl);
        } catch (MalformedURLException e) {
            return false;
        }
        String host = url.getHost();
        for (String domain : SHORT_URL_DOMAINS) {
            if (StringUtils.equalsIgnoreCase(host, domain)) {
                return true;
            }
        }
        return false;
    }

    public static Twitter getNewTwitterInstance() {
        ConfigurationBuilder builder = new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey("D5QsM4lVmWaY71FAje5yA")
                .setOAuthConsumerSecret("XcumieVWKz466he7Fi9BgeowyQKC4yTxEj9IXRFFg")
                .setOAuthAccessToken("66993334-1Q3bdTQuhygGHNeNUgdmtJEjP5grTAsrIemZ8ku7T")
                .setOAuthAccessTokenSecret("XTNQdVaPfdFcQq6E30WHX6E69j5Ke3gVeU3va7brOYc");

        TwitterFactory twitterFactory = new TwitterFactory(builder.build());
        Twitter twitter = twitterFactory.getInstance();
        return twitter;
    }

}
