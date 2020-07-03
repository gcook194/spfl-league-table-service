package com.gavincook.spfl.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gavincook.spfl.model.Fixture;
import com.gavincook.spfl.model.FixtureListResponse;
import com.gavincook.spfl.model.LeagueTable;

@Service
public interface LeagueTableManager {
	
	/**
	 * gets a list of fixtures with a given status
	 * @param status
	 * @return
	 */
	FixtureListResponse getFixturesByStatusAndLeagueResourceId(String status, Long leagueResourceId);
	
	/**
	 * builds the league table for a given league
	 * @param fixtures
	 * @return
	 */
	LeagueTable buildLeagueTable(List<Fixture> fixtures);

}
