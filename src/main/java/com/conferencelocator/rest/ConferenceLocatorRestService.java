package com.conferencelocator.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.conferencelocator.domain.Conference;
import com.conferencelocator.service.ConferenceLocatorService;

@Path("/conferences")
public class ConferenceLocatorRestService {

	@Inject
	private ConferenceLocatorService conferenceLocatorService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Conference> locate() {
		return conferenceLocatorService.find();

	}
}
