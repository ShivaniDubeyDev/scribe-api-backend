package com.shivani.scribeapi.config;

public final class AppConstants {

	private AppConstants() {
		throw new UnsupportedOperationException("This is a static constant utility class and cannot be instantiated.");
	}

	public static final String PAGE_NUMBER = "0";
	public static final String PAGE_SIZE = "10";
	public static final String SORT_BY = "postId"; // Default for posts
	public static final String SORT_USERS_BY = "id"; // Default primary key field for User Entity
	public static final String SORT_CATEGORIES_BY = "catId"; // Default primary key field for Category Entity
	public static final String SORT_DIR = "asc";
	public static final Integer NORMAL_USER = 502;
	public static final Integer ADMIN_USER = 501;
}
