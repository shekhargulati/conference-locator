package com.conferencelocator.domain;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="conferences")
public class Conference {

	@Id
	private String id;
	
	
	private long tweetId;
	
	private String name;
	
	private String url;
	
	private String info;
	
	private Date cfpEndDate;
	
	private String tweetText;
	
	private String[] tags;
	
	private double[] location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Date getCfpEndDate() {
		return cfpEndDate;
	}

	public void setCfpEndDate(Date cfpEndDate) {
		this.cfpEndDate = cfpEndDate;
	}
	
	public void setTweetId(long tweetId) {
		this.tweetId = tweetId;
	}
	public long getTweetId() {
		return tweetId;
	}
	
	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}
	
	public String getTweetText() {
		return tweetText;
	}
	
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	public String[] getTags() {
		return tags;
	}
	
	public double[] getLocation() {
		return location;
	}
	
	public void setLocation(double[] location) {
		this.location = location;
	}
}
