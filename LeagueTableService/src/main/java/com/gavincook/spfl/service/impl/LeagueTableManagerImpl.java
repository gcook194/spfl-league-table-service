package com.gavincook.spfl.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
	public LeagueTable buildLeagueTable(List<Fixture> fixtures) {
		
		LeagueTable table = new LeagueTable();
		
		List<Team> teams = extractTeamNamesFromFixtures(fixtures);
		
		// loop round the teams and build a league table entry
		for (Team team : teams) {
			
			LeagueTableEntry entry = new LeagueTableEntry(team.getName());
			List<String> form = new ArrayList<>(4);
			
			for (Fixture fixture : fixtures) {
				
				// is our team the home team or away team?
				boolean homeTeam = fixture.getHomeTeamName().equals(team.getName());  
				boolean awayTeam = fixture.getAwayTeamName().equals(team.getName());
				
				if (homeTeam || awayTeam) {
					
					// set league table entry values
					entry.setGoalsScored(homeTeam ? entry.getGoalsScored() + fixture.getHomeGoals() : entry.getGoalsScored() + fixture.getAwayGoals());
					entry.setGoalsConceded(homeTeam ? entry.getGoalsConceded() + fixture.getAwayGoals() : entry.getGoalsConceded() + fixture.getHomeGoals());
					entry.setGoalDifference(entry.getGoalsScored() - entry.getGoalsConceded());
					entry.setMatchesPlayed(entry.getMatchesPlayed() +1);
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
			
			entry.setForm(form);
			table.addEntry(entry);
			Collections.sort(table.getEntries(), Collections.reverseOrder());
		}
		
		return table;
	}
	
	/**
	 * adds a win to the entry
	 * @param entry
	 */
	private void addWin(LeagueTableEntry entry) {
		entry.setWins(entry.getWins() + 1);
		entry.setPoints(entry.getPoints() + 3);
	}
	
	/**
	 * adds a draw to the entry
	 * @param entry
	 */
	public void addDraw(LeagueTableEntry entry) {
		entry.setDraws(entry.getDraws() + 1);
		entry.setPoints(entry.getPoints() +1);
	}
	
	/**
	 * adds a loss to the entry
	 * @param entry
	 */
	private void addLoss(LeagueTableEntry entry) {
		entry.setLosses(entry.getLosses() + 1);
	}
	
	/**
	 * loops round a list of fixtures and adds all team names
	 * @param fixtures
	 * @return
	 */
	private List<Team> extractTeamNamesFromFixtures(List<Fixture> fixtures) {
		
		List<Team> teams = new ArrayList<>();
		
		for (Fixture fixture : fixtures) {
			
			Optional<Team> homeTeam = teams.stream()
					.filter(t -> t.getName().equals(fixture.getHomeTeamName()))
					.findAny();
			
			if (!homeTeam.isPresent()) {
				Team teamToAdd = new Team();
				teamToAdd.setName(fixture.getHomeTeamName());
				teamToAdd.setResourceId(((Number)fixture.getHomeTeamResourceId()).longValue());
				teamToAdd.setLogo(fixture.getHomeTeamBadge());
				
				teams.add(teamToAdd);
			}
			
			Optional<Team> awayTeam = teams.stream()
					.filter(t -> t.getName().equals(fixture.getAwayTeamName()))
					.findAny();
			
			if (!awayTeam.isPresent()) {
				Team teamToAdd = new Team();
				teamToAdd.setName(fixture.getAwayTeamName());
				teamToAdd.setResourceId(((Number)fixture.getAwayTeamResourceId()).longValue());
				teamToAdd.setLogo(fixture.getAwayTeamBadge());
				
				teams.add(teamToAdd);
			}
			
		}
		
		return teams;
	}

}
