package com.dare;

import java.text.SimpleDateFormat;

final public class Constants {

	public static final String CHALLENGE_PROVIDER_AUTHORITY = "com.dare.content.challenges";
	public static final String SUBMISSION_PROVIDER_AUTHORITY = "com.dare.content.submissions";
	
	public static final String DARE_SERVICE_URL = "http://quiet-mist-1776.herokuapp.com";
	public static final SimpleDateFormat DARE_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static final int LOADER_CHALLENGES = 0;
	public static final int LOADER_SUBMISSIONS = 1;
	
	// prevent instantiation
	private Constants() {}
}
