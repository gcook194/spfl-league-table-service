package com.gavincook.spfl.model;

import java.util.List;

import lombok.Data;

@Data
public class FixtureListResponse {
	private Integer results; 
	private List<Fixture> fixtures;
}
