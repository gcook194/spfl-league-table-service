package com.gavincook.spfl.model;

import lombok.Data;

@Data
public class Fixture {
	
	private Long id; 
	private Long resourceId; 
	private Long leagueResourceId; 
	private String fixtureDate; 
	private String referee; 
	private String stadium;  
	private String roundStr; 
	private Integer homeGoals;
	private Integer awayGoals; 
	private Integer homeTeamResourceId;  
	private Integer awayTeamResourceId; 
	private String halfTimeScore;
	private String fullTimeScore; 
	private String extraTimeScore; 
	private String penaltiesScore;
	private String homeTeamName; 
	private String awayTeamName; 
	private String homeTeamBadge;
	private String awayTeamBadge;
	private String leagueName;
	private String leagueBadgeUrl;	
	private String status;
	
}
