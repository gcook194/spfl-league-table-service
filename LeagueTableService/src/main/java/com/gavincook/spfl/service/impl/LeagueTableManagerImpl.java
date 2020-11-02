package com.gavincook.spfl.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gavincook.spfl.Constants;
import com.gavincook.spfl.model.Fixture;
import com.gavincook.spfl.model.FixtureListResponse;
import com.gavincook.spfl.model.LeagueTable;
import com.gavincook.spfl.model.LeagueTableEntry;
import com.gavincook.spfl.model.Team;
import com.gavincook.spfl.service.LeagueTableManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LeagueTableManagerImpl implements LeagueTableManager {

	@Value("${zuul.fixture-service-url}")
	private String fixtureServiceUrl;

	@Value("${zuul.team-service-url}")
	private String teamServiceUrl;
	
	private WebClient webClient;
	
	public LeagueTableManagerImpl() {
		this.webClient = WebClient.create();
	}

	@Override
	public FixtureListResponse getFixturesByStatusAndLeagueResourceId(String status, Long leagueResourceId) {
		log.info("fetching fixtures for league " + leagueResourceId);
		return webClient
				.get()
				.uri(fixtureServiceUrl + "/fixtures/status/" + status + "/league/" + leagueResourceId)
				.retrieve()
				.toEntity(FixtureListResponse.class)
				.block()
				.getBody();
	}

	@Override
	public LeagueTable buildLeagueTable(List<Fixture> fixtures, int fixtureWeek) {

		LeagueTable table = new LeagueTable();

		List<Team> teams = extractTeamNamesFromFixtures(fixtures);

		// loop round the teams and build a league table entry
		for (Team team : teams) {

			LeagueTableEntry entry = new LeagueTableEntry(team.getName());
			List<String> form = new ArrayList<>(4);

			for (Fixture fixture : fixtures) {
				
				int fixtureWeekVar = extractRoundAsIntegerFromFixture(fixture);
				
				if (fixtureWeek == Constants.FIXTURE_WEEK_ALL || fixtureWeekVar <= fixtureWeek) {
					
					// is our team the home team or away team?
					boolean homeTeam = fixture.getHomeTeamName().equals(team.getName());
					boolean awayTeam = fixture.getAwayTeamName().equals(team.getName());

					if (homeTeam || awayTeam) {

						// set league table entry values
						entry.setGoalsScored(homeTeam ? entry.getGoalsScored() + fixture.getHomeGoals()
								: entry.getGoalsScored() + fixture.getAwayGoals());
						entry.setGoalsConceded(homeTeam ? entry.getGoalsConceded() + fixture.getAwayGoals()
								: entry.getGoalsConceded() + fixture.getHomeGoals());
						entry.setGoalDifference(entry.getGoalsScored() - entry.getGoalsConceded());
						entry.setMatchesPlayed(entry.getMatchesPlayed() + 1);
						entry.setTeamResourceId(team.getResourceId());
						entry.setTeamBadgeUrl(team.getLogo());

						// determine if home team won, lost, or drew
						if (fixture.getHomeGoals() > fixture.getAwayGoals()) {
							if (homeTeam) {
								addWin(entry);
								form.add("W");
							} else {
								addLoss(entry);
								form.add("L");
							}
						} else if (fixture.getHomeGoals() < fixture.getAwayGoals()) {
							if (homeTeam) {
								addLoss(entry);
								form.add("L");
							} else {
								addWin(entry);
								form.add("W");
							}
						} else {
							addDraw(entry);
							form.add("D");
						}
					}					
				}
			}

			entry.setForm(form);

			/*
			 * Determine aggregated data for each entry: - points per game - goals scored
			 * per game - goals conceded per game - goal difference per game
			 */
			this.calculatePointsPerGame(entry);
			this.calculateGoalsScoredPerGame(entry);
			this.calculateGoalsConcededPerGame(entry);
			this.calculateGoalDifferencePerGame(entry);

			table.addEntry(entry);
		}

		// default sort can be overriden through other methods
		Collections.sort(table.getEntries(), Collections.reverseOrder());
		
		// set the position for each team in the table 
		for (int i = 0; i < table.getEntries().size(); i++) {
			table.getEntries().get(i).setPosition(i+1);
		}

		return table;
	}

	/**
	 * calculates goal difference per game for a League Table Entry
	 * 
	 * @param entry
	 */
	private void calculateGoalDifferencePerGame(LeagueTableEntry entry) {
		float goalDifferencePerGame = (float) entry.getGoalDifference() / entry.getMatchesPlayed();
		entry.setGoalDifferencePerGame(goalDifferencePerGame);
	}

	/**
	 * calculates goals conceded per game for a League Table Entry
	 * 
	 * @param entry
	 */
	private void calculateGoalsConcededPerGame(LeagueTableEntry entry) {
		float goalsConcededPerGame = (float) entry.getGoalsConceded() / entry.getMatchesPlayed();
		entry.setGoalsConcededPerGame(goalsConcededPerGame);
	}

	/**
	 * calculates goals scored per game for a League Table Entry
	 * 
	 * @param entry
	 */
	private void calculateGoalsScoredPerGame(LeagueTableEntry entry) {
		float goalsScoredPerGame = (float) entry.getGoalsScored() / entry.getMatchesPlayed();
		entry.setGoalsScoredPerGame(goalsScoredPerGame);
	}

	/**
	 * calculates points per game for a League Table Entry
	 * 
	 * @param entry
	 */
	private void calculatePointsPerGame(LeagueTableEntry entry) {
		float pointsPerGame = (float) entry.getPoints() / entry.getMatchesPlayed();
		entry.setPointsPerGame(pointsPerGame);
	}

	/**
	 * adds a win to the entry
	 * 
	 * @param entry
	 */
	private void addWin(LeagueTableEntry entry) {
		entry.setWins(entry.getWins() + 1);
		entry.setPoints(entry.getPoints() + 3);
	}

	/**
	 * adds a draw to the entry
	 * 
	 * @param entry
	 */
	private void addDraw(LeagueTableEntry entry) {
		entry.setDraws(entry.getDraws() + 1);
		entry.setPoints(entry.getPoints() + 1);
	}

	/**
	 * adds a loss to the entry
	 * 
	 * @param entry
	 */
	private void addLoss(LeagueTableEntry entry) {
		entry.setLosses(entry.getLosses() + 1);
	}

	/**
	 * loops round a list of fixtures and adds all team names
	 * 
	 * @param fixtures
	 * @return
	 */
	private List<Team> extractTeamNamesFromFixtures(List<Fixture> fixtures) {

		List<Team> teams = new ArrayList<>();

		for (Fixture fixture : fixtures) {

			Optional<Team> homeTeam = teams.stream().filter(t -> t.getName().equals(fixture.getHomeTeamName()))
					.findAny();

			if (!homeTeam.isPresent()) {
				Team teamToAdd = new Team();
				teamToAdd.setName(fixture.getHomeTeamName());
				teamToAdd.setResourceId(((Number) fixture.getHomeTeamResourceId()).longValue());
				teamToAdd.setLogo(fixture.getHomeTeamBadge());

				teams.add(teamToAdd);
			}

			Optional<Team> awayTeam = teams.stream().filter(t -> t.getName().equals(fixture.getAwayTeamName()))
					.findAny();

			if (!awayTeam.isPresent()) {
				Team teamToAdd = new Team();
				teamToAdd.setName(fixture.getAwayTeamName());
				teamToAdd.setResourceId(((Number) fixture.getAwayTeamResourceId()).longValue());
				teamToAdd.setLogo(fixture.getAwayTeamBadge());

				teams.add(teamToAdd);
			}

		}

		return teams;
	}

	@Override
	public LeagueTable buildTopScoringTeamsTable(LeagueTable leagueTable) {

		Collections.sort(leagueTable.getEntries(), (LeagueTableEntry entry1,
				LeagueTableEntry entry2) -> entry2.getGoalsScored() - entry1.getGoalsScored());

		return leagueTable;
	}

	@Override
	public LeagueTable buildTopDefensiveTeamsTable(LeagueTable leagueTable) {

		Collections.sort(leagueTable.getEntries(), (LeagueTableEntry entry1,
				LeagueTableEntry entry2) -> entry1.getGoalsConceded() - entry2.getGoalsConceded());

		return leagueTable;
	}

	@Override
	public LeagueTable buildTopScorersPerGameTable(LeagueTable leagueTable) {

		Collections.sort(leagueTable.getEntries(), (LeagueTableEntry entry1, LeagueTableEntry entry2) -> Float
				.compare(entry2.getGoalsScoredPerGame(), entry1.getGoalsScoredPerGame()));

		return leagueTable;
	}

	@Override
	public LeagueTable buildTopDefencePerGameTable(LeagueTable leagueTable) {

		Collections.sort(leagueTable.getEntries(), (LeagueTableEntry entry1, LeagueTableEntry entry2) -> Float
				.compare(entry1.getGoalsConcededPerGame(), entry2.getGoalsConcededPerGame()));

		return leagueTable;
	}

	@Override
	public LeagueTable buildTopGaolDifferenceTable(LeagueTable leagueTable) {

		Collections.sort(leagueTable.getEntries(), (LeagueTableEntry entry1, LeagueTableEntry entry2) -> Float
				.compare(entry2.getGoalDifference(), entry1.getGoalDifference()));

		return leagueTable;
	}

	@Override
	public LeagueTable buildTopGaolDifferencePerGameTable(LeagueTable leagueTable) {

		Collections.sort(leagueTable.getEntries(), (LeagueTableEntry entry1, LeagueTableEntry entry2) -> Float
				.compare(entry2.getGoalDifferencePerGame(), entry1.getGoalDifferencePerGame()));

		return leagueTable;
	}

	@Override
	public List<LeagueTable> buildMatchweekLeagueTable(List<Fixture> fixtures) {

		List<LeagueTable> tables = new ArrayList<>();

		// get the number of match weeks from the list of fixtures 
		int matchweekCount = 0;

		for (int i = 0; i < fixtures.size(); i++) {
			int roundNum = extractRoundAsIntegerFromFixture(fixtures.get(i));
			matchweekCount = matchweekCount < roundNum? roundNum : matchweekCount;
		}
		
		// need to loop round each match week to create one table per match week
		for (int i = 0; i < matchweekCount; i++) {
			tables.add(buildLeagueTable(fixtures, i+1));
		}

		return tables;
	}
	
	private int extractRoundAsIntegerFromFixture(Fixture fixture) {
		
		int roundNum = 0; 
		
		String roundStr = fixture.getRoundStr();

		try {
			roundNum = Integer.parseInt(roundStr.substring(roundStr.length()-2));	
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return roundNum;
	}

}
