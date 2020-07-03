package com.gavincook.spfl.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeagueTableEntry implements Comparable<LeagueTableEntry> {
	
	private int position;
	private Long teamResourceId; 
	private String teamName;
	private String teamBadgeUrl;
	private int matchesPlayed;
	private int points; 
	private int goalsScored; 
	private int goalsConceded; 
	private int goalDifference; 
	private int wins; 
	private int draws; 
	private int losses; 
	private List<String> form = new ArrayList<>(4);
	
	public LeagueTableEntry(String teamName) {
		this.teamName = teamName;
	}
	
	@Override
	public int compareTo(LeagueTableEntry otherTableEntry) {
		
		int comparison = 0;
		
		if (this.getPoints() > otherTableEntry.getPoints()) {
			comparison = 1;
		} else if (this.getPoints() < otherTableEntry.getPoints()) {
			comparison = -1;
		} else {
			if (this.getGoalDifference() > otherTableEntry.getGoalDifference()) {
				comparison = 1;
			} else if (this.getGoalDifference() < otherTableEntry.getGoalDifference()) {
				comparison = -1;
			} else if (this.getGoalsScored() > otherTableEntry.getGoalsScored()) {
				comparison = 1;
			} else if (this.getGoalsScored() < otherTableEntry.getGoalsScored()) {
				comparison = -1;
			} else {
				if (this.getTeamName().compareTo(otherTableEntry.getTeamName()) > 0) {
					comparison = -1;
				} else {
					comparison = 1;
				}
			}
		}
		
		return comparison;
	}

}
