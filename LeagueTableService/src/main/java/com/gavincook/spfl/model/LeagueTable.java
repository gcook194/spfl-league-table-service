package com.gavincook.spfl.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LeagueTable {
	
	private List<LeagueTableEntry> entries = new ArrayList<>();
	
	public void addEntry(LeagueTableEntry entry) {
		this.entries.add(entry);
	}

}
