package com.jankovicd.flightadvisor.auth.enums;

public enum RoleEnum {

	ADMIN("ADMIN"), REGULAR("REGULAR");

	private String name;

	private RoleEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
