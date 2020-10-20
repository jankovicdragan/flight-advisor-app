package com.jankovicd.flightadvisor.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class Bundle {
	public static String getBundleMessage(String bundleKey) {
		Locale locale = new Locale("en", "En");
		ResourceBundle exampleBundle = ResourceBundle.getBundle("exception.message", locale);
		return exampleBundle.getString(bundleKey);
	}

}
