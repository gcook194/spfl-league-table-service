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
	
	/**
	 * sorts a league table by goals scored descending.
	 * @return
	 */
	LeagueTable buildTopScoringTeamsTable(LeagueTable leagueTable);

	/**
	 * sorts a league table by goals conceded ascending.
	 * @return
	 */
	LeagueTable buildTopDefensiveTeamsTable(LeagueTable leagueTable);

	/**
	 * sorts a league table by goals scored divided by fixtures played
	 * @param leagueTable
	 * @return
	 */
	LeagueTable buildTopScorersPerGameTable(LeagueTable leagueTable);

	/**
	 * sorts a league table by goals conceded divided by fixtures played
	 * @param leagueTable
	 * @return
	 */
	LeagueTable buildTopDefencePerGameTable(LeagueTable leagueTable);

	/**
	 * sorts a league table by goal difference
	 * @param leagueTable
	 * @return
	 */
	LeagueTable buildTopGaolDifferenceTable(LeagueTable leagueTable);

	/**
	 * sorts a league table by goal difference divided by fixtures played descending
	 * @param leagueTable
	 * @return
	 */
	LeagueTable buildTopGaolDifferencePerGameTable(LeagueTable leagueTable);

}
