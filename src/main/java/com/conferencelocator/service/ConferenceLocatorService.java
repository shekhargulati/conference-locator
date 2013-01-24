package com.conferencelocator.service;

import java.util.List;

import javax.ejb.Stateless;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.conferencelocator.domain.Conference;

import javax.inject.Inject;

@Stateless
public class ConferenceLocatorService {

	@Inject
	private MongoTemplate mongoTemplate;
	
	public void create(Conference conference){
		mongoTemplate.insert(conference);
	}

	public List<Conference> find() {
		Query query = new Query();
		query.limit(10);
		return mongoTemplate.find(query,Conference.class);
	}
}
