package com.unisys;

import java.util.regex.Pattern;

public final class Constants {

	public static final String FILE_NAME = "FILE NAME";
	public static final String SOURCE_IP = "SOURCE IP";
	public static final String DESTINATION_IP = "DESTINATION IP";
	public static final String DATE = "DATE";
	public static final String TIME = "TIME";
	public static final String HTTP_REQUEST_TYPE = "HTTP REQUEST TYPE";
	public static final String URL = "URL";
	public static final String USER_ID = "USER ID";
	public static final String HTTP_STATUS_RESPONSE = "HTTP STATUS RESPONSE";
	public static final String TIME_IN_MILLISECONDS = "TIME IN MILLISECONDS";

	public static final String INPUT_FILE = "C:\\Users\\42967\\Downloads\\Access Logs";
	public static final String OUTPUT_FILE = "C:\\Users\\42967\\Desktop\\ACCESS_LOG_";

	public static final String AIRLINE = "airline=";
	
	public static final String UID_SEPERATOR = "&!@#%";	
	public static final String UID_SEPERATOR_EQUAL_TO = "=";	
	
	public static final String WINDOWS_SLASH = "\\";	
	
	public static final String UNIX_SLASH = "/";
	
	public static final String DB_SLASH = "/";
	
	public static final String EMPTY = "";
	
	public static final String CONFIG_FILE_PATH = "./config.properties";
	//public static final String CONFIG_FILE_PATH = "E:\\42967\\Unisys\\Tools\\LOCAL\\Access Log Parser\\accesslogparser\\config.properties";
	
	
	public static final String ROOT_DIRECTORY = "ROOT_DIRECTORY";
	public static final String REPORT_OUTPUT_DIRECTORY = "REPORT_OUTPUT_DIRECTORY";
	public static final String REPORT_FILE_NAME_PREFIX = "REPORT_FILE_NAME_PREFIX";
	public static final String REPORT_FILE_NAME_PREFIX_FOR_HOUR = "REPORT_FILE_NAME_PREFIX_FOR_HOUR";
	public static final String REPORT_FILE_NAME_PREFIX_FOR_MINUTES = "REPORT_FILE_NAME_PREFIX_FOR_MINUTES";
	public static final String REPORT_FILE_NAME_PREFIX_FOR_TIME = "REPORT_FILE_NAME_PREFIX_FOR_TIME";
	
	//pattern string
	public static final String IP_PATTERN_STRING = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\p{Space}(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
	public static final String DATE_PATTERN_STRING = "\\d{1,2}\\/\\p{Alpha}{1,3}\\/\\d{1,4}\\:\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}\\p{Blank}\\+\\d*";
	public static final String HTTP_REQUEST_PATTERN_STRING = "GET|POST|PUT|HEAD|DELETE|CONNECT|OPTIONS|TRACE|PATCH";
	public static final String URL_PATTERN_STRING_1 = "\\/lms\\-\\p{Alpha}{1}[\\/\\p{Graph}]+?\\p{Space}";
	public static final String URL_PATTERN_STRING_2 = "\\/LMSNG\\_\\p{Alpha}{1}[\\/\\p{Graph}]+?\\p{Space}";
	public static final String AL_PATTERN_STRING = "airline=\\p{Alpha}{1,2}";
	public static final String UID_PATTERN_STRING = "\\p{Space}\\p{Alnum}+&!@#%";
	public static final String UID_PATTERN_STRING_1 = "\\p{Alnum}+\\p{Space}\\d{3}\\p{Space}\\d+$";
	public static final String UID_PATTERN_STRING_2 = "userId=\\p{Alnum}+";
	public static final String HTTP_STATUS_RESPONSE_TIME_PATTERN_STRING = "\\d{3}\\p{Space}\\d+$";

	//patterns
	public static final Pattern IP_PATTERN = Pattern.compile(IP_PATTERN_STRING);
	public static final Pattern DATE_PATTERN = Pattern
			.compile(DATE_PATTERN_STRING);
	public static final Pattern HTTP_REQUEST_PATTERN = Pattern
			.compile(HTTP_REQUEST_PATTERN_STRING);
	public static final Pattern URL_PATTERN_1 = Pattern
			.compile(URL_PATTERN_STRING_1);
	public static final Pattern URL_PATTERN_2 = Pattern
			.compile(URL_PATTERN_STRING_2);
	public static final Pattern AL_TYPE_PATTERN = Pattern
			.compile(AL_PATTERN_STRING);
	public static final Pattern UID_PATTERN = Pattern
			.compile(UID_PATTERN_STRING);
	public static final Pattern UID_PATTERN_1 = Pattern
			.compile(UID_PATTERN_STRING_1);
	public static final Pattern UID_PATTERN_2 = Pattern
			.compile(UID_PATTERN_STRING_2);
	public static final Pattern HTTP_STATUS_RESPONSE_TIME_PATTERN = Pattern
			.compile(HTTP_STATUS_RESPONSE_TIME_PATTERN_STRING);
	
	public static final String JDBCSQL = /*"jdbc:sqlserver://"*/ "jdbc:oracle:thin";
	public static final String SEMICOLLON = ";";
	public static final String COLLON = ":";
	public static final String SPACE = " ";
	public static final String DB_HOST_PORT ="DB_HOST_PORT";	
	public static final String DB_URL = "DB_URL";
	public static final String DB_USER ="DB_USER";	
	public static final String DB_PWD ="DB_PWD";	
	public static final String DB_NAME ="DB_NAME";	
	public static final String USER ="user=";	
	public static final String PASSWORD ="password=";	
	public static final String DATABASE ="database=";
	public static final String DRIVER_CLASS = /*"com.microsoft.sqlserver.jdbc.SQLServerDriver"*/ "DRIVER_CLASS";
	public static final String FRED = "fred";	
	public static final String BETTY = "betty";	
	public static final String GETCONNECTIONFAILED ="getConnection() failed - ";	
	public static final String CLOSESQL ="closeSql() - ";	
	public static final String EXECUTEQUERYFAILED ="executeQuery() failed -";	
	public static final String DOUBLEHYPHEN ="--";	
	public static final String SQLEXCEPTION="SQL Exception";	
	public static final String UNBLETODISCONNECT="Unble to disconnect";	
	public static final String SQLEQUAL= "sql=(";	
	public static final String BRACKET= ")";	
	public static final String CREATEDSTATEMENTFAILED="createStatement() failed - ";	
	public static final String GETSQLCOLUMNFAILED="getSqlColumn() failed - ";
	public static final String COMMA = ", ";
	public static final String CLOSE_STATEMENT = ")";	
	public static final String QUOTE_COMMA = "', ";	
	public static final String QUOTE = "'";
	
	public static final String WRITE_TO_CSV = "WRITE_TO_CSV";
	public static final String WRITE_TO_DB = "WRITE_TO_DB";
	
	public static final String INSERT_LOG_QUERY = "insert into UTL_ACCESS_LOG(SOURCE_IP, DESTINATION_IP, LOG_DATE, HTTP_REQUEST_TYPE, URL, USER_ID, HTTP_STATUS_RESPONSE, TIME_IN_MILLISECONDS) values(";

	private Constants() {
	}

}
