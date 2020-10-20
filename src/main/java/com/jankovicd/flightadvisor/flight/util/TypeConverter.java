package com.jankovicd.flightadvisor.flight.util;

import org.springframework.stereotype.Component;

@Component
public class TypeConverter {

	public Integer convertToInteger(String value) {
		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	public Long convertToLong(String value) {
		try {
			return Long.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	public Float convertToFloat(String value) {
		try {
			return Float.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	public Double convertToDouble(String value) {
		try {
			return Double.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	public String convertToString(String value) {
		if (value == null || value.equals("\\N")) {
			return null;
		}
		if (value.startsWith("\"")) {
			return value.substring(1, value.length() - 1);
		}

		return value;
	}

}
