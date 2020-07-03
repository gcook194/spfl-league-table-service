package com.gavincook.spfl.model;

import lombok.Data;

@Data
public class Team {
	private Long id; 
	private Long resourceId; 
	private String name;
	private String logo;
	private String founded;
	private String stadium;
	private Integer stadiumCapacity; 
}
