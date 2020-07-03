package com.gavincook.spfl.model;

import java.util.List;

import lombok.Data;

@Data
public class LeagueTableResponse {
	private Integer results; 
	private List<LeagueTable> leagueTables; 
}
