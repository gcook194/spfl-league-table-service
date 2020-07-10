package com.gavincook.spfl.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gavincook.spfl.Constants;
import com.gavincook.spfl.model.Fixture;
import com.gavincook.spfl.model.LeagueTable;
import com.gavincook.spfl.model.LeagueTableResponse;
import com.gavincook.spfl.service.LeagueTableManager;

@RestController
@RequestMapping("/league-tables")
public class LeagueTableController {
	
	private LeagueTableManager leagueTableMgr;
	
	@Autowired
	public LeagueTableController(LeagueTableManager leagueTableMgr) {
		this.leagueTableMgr = leagueTableMgr;
	}

	@GetMapping("/{leagueId}")
	public ResponseEntity<LeagueTableResponse> getCurrentLeagueTable(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		LeagueTable table = leagueTableMgr.buildLeagueTable(fixtures, Constants.FIXTURE_WEEK_ALL);
		
		response.setLeagueTables(Arrays.asList(table));
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/{leagueId}/top-scoring-teams")
	public ResponseEntity<LeagueTableResponse> getTopScoringTeamsTable(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		LeagueTable LeagueTable = leagueTableMgr.buildLeagueTable(fixtures, Constants.FIXTURE_WEEK_ALL);
		LeagueTable topScoringTeamsTable = leagueTableMgr.buildTopScoringTeamsTable(LeagueTable);
		
		response.setLeagueTables(Arrays.asList(topScoringTeamsTable));
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/{leagueId}/top-defensive-teams")
	public ResponseEntity<LeagueTableResponse> getTopDefensiveTeamsTable(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		LeagueTable LeagueTable = leagueTableMgr.buildLeagueTable(fixtures, Constants.FIXTURE_WEEK_ALL);
		LeagueTable topScoringTeamsTable = leagueTableMgr.buildTopDefensiveTeamsTable(LeagueTable);
		
		response.setLeagueTables(Arrays.asList(topScoringTeamsTable));
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/{leagueId}/goals-scored-per-game")
	public ResponseEntity<LeagueTableResponse> getTopScorersPerGameTable(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		LeagueTable LeagueTable = leagueTableMgr.buildLeagueTable(fixtures, Constants.FIXTURE_WEEK_ALL);
		LeagueTable topScoringTeamsTable = leagueTableMgr.buildTopScorersPerGameTable(LeagueTable);
		
		response.setLeagueTables(Arrays.asList(topScoringTeamsTable));
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/{leagueId}/goals-conceded-per-game")
	public ResponseEntity<LeagueTableResponse> getTopDefendersPerGameTable(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		LeagueTable LeagueTable = leagueTableMgr.buildLeagueTable(fixtures, Constants.FIXTURE_WEEK_ALL);
		LeagueTable topScoringTeamsTable = leagueTableMgr.buildTopDefencePerGameTable(LeagueTable);
		
		response.setLeagueTables(Arrays.asList(topScoringTeamsTable));
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/{leagueId}/goal-difference")
	public ResponseEntity<LeagueTableResponse> getTopGoalDifferenceTable(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		LeagueTable LeagueTable = leagueTableMgr.buildLeagueTable(fixtures, Constants.FIXTURE_WEEK_ALL);
		LeagueTable topScoringTeamsTable = leagueTableMgr.buildTopGaolDifferenceTable(LeagueTable);
		
		response.setLeagueTables(Arrays.asList(topScoringTeamsTable));
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/{leagueId}/goal-difference-per-game")
	public ResponseEntity<LeagueTableResponse> getTopGoalDifferencePerGameTable(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		LeagueTable LeagueTable = leagueTableMgr.buildLeagueTable(fixtures, Constants.FIXTURE_WEEK_ALL);
		LeagueTable topScoringTeamsTable = leagueTableMgr.buildTopGaolDifferencePerGameTable(LeagueTable);
		
		response.setLeagueTables(Arrays.asList(topScoringTeamsTable));
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/{leagueId}/league-table-by-matchday")
	public ResponseEntity<LeagueTableResponse> getMatchweekLeagueTableByLeagueAndTeam(@PathVariable Long leagueId) {
		
		LeagueTableResponse response = new LeagueTableResponse();
		
		// build the table
		List<Fixture> fixtures = leagueTableMgr.getFixturesByStatusAndLeagueResourceId("FT", leagueId).getFixtures();
		List<LeagueTable> LeagueTables = leagueTableMgr.buildMatchweekLeagueTable(fixtures);
		
		response.setLeagueTables(LeagueTables);
		response.setResults(response.getLeagueTables().size());
		
		return ResponseEntity.ok().body(response);
	}
}
