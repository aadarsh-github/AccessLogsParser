package com.unisys;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.csvreader.CsvWriter;

public class AccessLogParser {

	private static CsvWriter csvWriter;
	private static DateFormat inputFileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat outputFileDateFormat = new SimpleDateFormat("yyyy.MM.dd_HHmm");
	private static DateFormat logFileDateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
	private static DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private static Boolean hasTimeDuration = false;
	private static Boolean hasStartEndTimeDuration = false;
	private static Calendar calendar = Calendar.getInstance();

	public static String starEndDateTime = "";

	public static String startDateTime = "";
	public static String endDateTime = "";

	public static String startDate = "";
	public static String startTime = "";
	public static String endDate = "";
	public static String endTime = "";

	private static List<LogDTO> logs = new ArrayList<LogDTO>();

	public static PropertyFileReader prop = new PropertyFileReader();

	public static String dbFlag = null;

	public static void main(String[] args) throws IOException, ParseException {
		System.out.println("AccessLogsProcessor.....");

		inputFileDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		outputFileDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		logFileDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		inputDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));

		File file = null;

		String outputFileProperty = null;

		if (args.length > 0) {
			String rootDirectoryForCurrentDay = prop.getProperty(Constants.ROOT_DIRECTORY);
			if (StringUtils.isNotBlank(args[0]) && args[0].contains("HOURS")) {
				hasTimeDuration = true;
				endDateTime = logFileDateFormat.format(calendar.getTime());
				calendar.add(Calendar.HOUR_OF_DAY, -Integer.valueOf(StringUtils.substringBefore(args[0], "HOURS")));
				startDateTime = logFileDateFormat.format(calendar.getTime());
				// String logDateFromInput = inputFileDateFormat.format(calendar.getTime());
				// rootDirectoryForCurrentDay = rootDirectoryForCurrentDay + "access_log." +
				// logDateFromInput;
				rootDirectoryForCurrentDay = rootDirectoryForCurrentDay + "access_log.log";
				file = new File(rootDirectoryForCurrentDay);
				outputFileProperty = Constants.REPORT_FILE_NAME_PREFIX_FOR_HOUR;
			} else if (StringUtils.isNotBlank(args[0]) && args[0].contains("MINUTES")) {
				hasTimeDuration = true;
				endDateTime = logFileDateFormat.format(calendar.getTime());
				calendar.add(Calendar.MINUTE, -Integer.valueOf(StringUtils.substringBefore(args[0], "MINUTES")));
				startDateTime = logFileDateFormat.format(calendar.getTime());
				// String logDateFromInput = inputFileDateFormat.format(calendar.getTime());
				rootDirectoryForCurrentDay = rootDirectoryForCurrentDay + "access_log.log";
				file = new File(rootDirectoryForCurrentDay);
				outputFileProperty = Constants.REPORT_FILE_NAME_PREFIX_FOR_MINUTES;
			} else if (StringUtils.isNotBlank(args[0]) && args[0].contains("TO")) {
				hasStartEndTimeDuration = true;
				// 18/12/2018:11:08:00 TO 18-12-2018:23:08:00
				starEndDateTime = args[0];

				startDate = StringUtils.substringBefore(starEndDateTime, "TO");
				endDate = StringUtils.substringAfter(starEndDateTime, "TO");

				Date date = new Date();
				String currentDate = logFileDateFormat.format(date);
				startDateTime = logFileDateFormat.format(logFileDateFormat.parse(startDate));
				endDateTime = logFileDateFormat.format(logFileDateFormat.parse(endDate));

				Long diffInMillies = Math.abs(
						logFileDateFormat.parse(currentDate).getTime() - logFileDateFormat.parse(startDate).getTime());
				Long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
				calendar.add(Calendar.DATE, -diff.intValue());

				// String logDateFromInput = inputFileDateFormat.format(calendar.getTime());
				rootDirectoryForCurrentDay = rootDirectoryForCurrentDay + "access_log.log";
				file = new File(rootDirectoryForCurrentDay);

				outputFileProperty = Constants.REPORT_FILE_NAME_PREFIX_FOR_TIME;
			}
		} else {
			outputFileProperty = Constants.REPORT_FILE_NAME_PREFIX;
			String rootDirectoryForPreviousDay = prop.getProperty(Constants.ROOT_DIRECTORY);
			calendar.add(Calendar.DATE, -1);
			String date = inputFileDateFormat.format(calendar.getTime());
			rootDirectoryForPreviousDay = rootDirectoryForPreviousDay + "access_log." + date + ".log";
			file = new File(rootDirectoryForPreviousDay);
		}
		if ("Y".equals(prop.getProperty(Constants.WRITE_TO_CSV))) {
			csvWriter = new CsvWriter(new FileWriter(prop.getProperty(Constants.REPORT_OUTPUT_DIRECTORY)
					+ prop.getProperty(outputFileProperty) + outputFileDateFormat.format(calendar.getTime()) + ".csv",
					true), ',');
			writeToCSV(Constants.SOURCE_IP, Constants.DESTINATION_IP, Constants.DATE, Constants.HTTP_REQUEST_TYPE,
					Constants.URL, Constants.USER_ID, Constants.HTTP_STATUS_RESPONSE, Constants.TIME_IN_MILLISECONDS);
		}

		dbFlag = prop.getProperty(Constants.WRITE_TO_DB);
		dbora db1 = null;

		if (dbFlag != null && "Y".equals(dbFlag)) {
			db1 = new dbora();
		}

		showFiles(file, csvWriter, db1);
		// writeToDB();
	}

	private static void showFiles(File file, CsvWriter csvWriter, dbora db1) throws IOException, ParseException {
		String fileExtn = null;
		Date parsedLoggerTime = null;
		boolean isWritten = false;
		try {
			String slash = "";
			if (getOSName().contains("Windows")) {
				slash = Constants.WINDOWS_SLASH;
			} else {
				slash = Constants.UNIX_SLASH;
			}

			fileExtn = StringUtils.substringAfterLast(file.toString(), slash);
			if (fileExtn.startsWith("access_log")) {
				readLogFile(file, logs);
				isWritten = true;
			}
		} catch (IOException e) {
			System.out.println("File : " + fileExtn + " does not found in the system.");
			e.printStackTrace();
		}

		if (csvWriter != null && isWritten) {
			System.out.println("CSV Size: " + logs.size());
			for (LogDTO logDTO : logs) {
				if (hasTimeDuration) {
					String dateTime = logDTO.getDate().trim() + " " + logDTO.getTime().trim();
					parsedLoggerTime = inputDateFormat.parse(dateTime);
					if (parsedLoggerTime.after(logFileDateFormat.parse(startDateTime))) {
						if (StringUtils.isNotBlank(logDTO.getUrl()) && logDTO.getUrl().contains("'")) {
							logDTO.setUrl(StringUtils.substringBefore(logDTO.getUrl(), "'"));
						} else if (StringUtils.isNotBlank(logDTO.getUrl()) && logDTO.getUrl().contains(",")) {
							logDTO.setUrl(StringUtils.substringBefore(logDTO.getUrl(), ","));
						}
						writeToCSV(logDTO.getSourceIPAddress(), logDTO.getDestinationIPAddress(), logDTO.getDate(),
								logDTO.getHttpRequestType(), logDTO.getUrl(), logDTO.getUserID(),
								logDTO.getHttpStatusResponse(), logDTO.getTimeTaken());
						writeToDB(logDTO, db1);
					}
				} else if (hasStartEndTimeDuration) {
					String dateTime = logDTO.getDate().trim() + " " + logDTO.getTime().trim();
					parsedLoggerTime = inputDateFormat.parse(dateTime);
					// dd/MMM/yyyy:HH:mm:ss z
					if (parsedLoggerTime.after(logFileDateFormat.parse(startDateTime))
							&& parsedLoggerTime.before(logFileDateFormat.parse(endDateTime))) {
						if (StringUtils.isNotBlank(logDTO.getUrl()) && logDTO.getUrl().contains("'")) {
							logDTO.setUrl(StringUtils.substringBefore(logDTO.getUrl(), "'"));
						} else if (StringUtils.isNotBlank(logDTO.getUrl()) && logDTO.getUrl().contains(",")) {
							logDTO.setUrl(StringUtils.substringBefore(logDTO.getUrl(), ","));
						}
						writeToCSV(logDTO.getSourceIPAddress(), logDTO.getDestinationIPAddress(), logDTO.getDate(),
								logDTO.getHttpRequestType(), logDTO.getUrl(), logDTO.getUserID(),
								logDTO.getHttpStatusResponse(), logDTO.getTimeTaken());
						writeToDB(logDTO, db1);
					}

				} else {
					if (StringUtils.isNotBlank(logDTO.getUrl()) && logDTO.getUrl().contains("'")) {
						logDTO.setUrl(StringUtils.substringBefore(logDTO.getUrl(), "'"));
					} else if (StringUtils.isNotBlank(logDTO.getUrl()) && logDTO.getUrl().contains(",")) {
						logDTO.setUrl(StringUtils.substringBefore(logDTO.getUrl(), ","));
					}
					writeToCSV(logDTO.getSourceIPAddress(), logDTO.getDestinationIPAddress(), logDTO.getDate(),
							logDTO.getHttpRequestType(), logDTO.getUrl(), logDTO.getUserID(),
							logDTO.getHttpStatusResponse(), logDTO.getTimeTaken());
					writeToDB(logDTO, db1);
				}
			}
			csvWriter.close();
		}

		if (dbFlag != null && "Y".equals(dbFlag)) {
			if (db1 != null) {
				db1.closeSql();
			}
		}
	}

	private static void writeToDB(LogDTO logDTO, dbora db1) throws FileNotFoundException {
		if (dbFlag != null && "Y".equals(dbFlag)) {
			if (db1 != null) {
				String insertQuery = Constants.INSERT_LOG_QUERY;
				StringBuffer insertQueryStr = new StringBuffer();
				insertQueryStr.append(insertQuery);
				insertQueryStr.append(Constants.QUOTE + logDTO.getSourceIPAddress() + Constants.QUOTE_COMMA);
				insertQueryStr.append(Constants.QUOTE + logDTO.getDestinationIPAddress() + Constants.QUOTE_COMMA);
				// insertQueryStr.append(Constants.QUOTE + logDTO.getDate() +
				// Constants.QUOTE_COMMA);
				insertQueryStr.append("TO_TIMESTAMP(" + Constants.QUOTE + logDTO.getDate()
						+ "','yyyy-MM-dd HH24:mi:ss')" + Constants.COMMA);
				insertQueryStr.append(Constants.QUOTE + logDTO.getHttpRequestType() + Constants.QUOTE_COMMA);
				insertQueryStr.append(Constants.QUOTE + logDTO.getUrl() + Constants.QUOTE_COMMA);
				insertQueryStr.append(Constants.QUOTE + logDTO.getUserID() + Constants.QUOTE_COMMA);
				insertQueryStr.append(Constants.QUOTE + logDTO.getHttpStatusResponse() + Constants.QUOTE_COMMA);
				insertQueryStr.append(Constants.QUOTE + logDTO.getTimeTaken() + Constants.QUOTE);
				insertQueryStr.append(Constants.CLOSE_STATEMENT);

				db1.execute(insertQueryStr.toString());
			}
		}
	}

	private static void readLogFile(File name, List<LogDTO> logs) throws IOException, ParseException {
		LogDTO logDTO = null;
		FileInputStream fstream = new FileInputStream(name);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String inputLineLine = null;
		while ((inputLineLine = br.readLine()) != null) {
			logDTO = new LogDTO();
			extractLogs(name, logDTO, inputLineLine);
			logs.add(logDTO);
		}
		br.close();
	}

	private static void extractLogs(File name, LogDTO logDTO, String inputLine) throws IOException, ParseException {
		Matcher ipPatternMatcher = Constants.IP_PATTERN.matcher(inputLine);
		Matcher datePatternMatcher = Constants.DATE_PATTERN.matcher(inputLine);
		Matcher httpRequestPatternMatcher = Constants.HTTP_REQUEST_PATTERN.matcher(inputLine);
		Matcher airlineTypePatternMatcher = Constants.AL_TYPE_PATTERN.matcher(inputLine);
		Matcher urlPatternMatcher1 = Constants.URL_PATTERN_1.matcher(inputLine);
		Matcher urlPatternMatcher2 = Constants.URL_PATTERN_2.matcher(inputLine);
		Matcher userIDPatternMatcher = Constants.UID_PATTERN.matcher(inputLine);
		Matcher userIDPatternMatcher1 = Constants.UID_PATTERN_1.matcher(inputLine);
		Matcher userIDPatternMatcher2 = Constants.UID_PATTERN_2.matcher(inputLine);
		Matcher httpStatusResponseTimePatternMatcher = Constants.HTTP_STATUS_RESPONSE_TIME_PATTERN.matcher(inputLine);

		if (ipPatternMatcher.find()) {
			logDTO.setSourceIPAddress(StringUtils.substringBefore(ipPatternMatcher.group(), " "));
			logDTO.setDestinationIPAddress(StringUtils.substringAfter(ipPatternMatcher.group(), " "));
		}
		if (datePatternMatcher.find()) {
			logDTO.setDate(dateTimeFormat.format(logFileDateFormat.parse(datePatternMatcher.group())));
			logDTO.setTime(timeFormat.format(logFileDateFormat.parse(datePatternMatcher.group())));
		}
		if (httpRequestPatternMatcher.find()) {
			logDTO.setHttpRequestType(httpRequestPatternMatcher.group());
		}
		if (airlineTypePatternMatcher.find()) {
			logDTO.setAirlineType(airlineTypePatternMatcher.group());
		}
		if (urlPatternMatcher1.find()) {
			logDTO.setUrl(urlPatternMatcher1.group());
		} else if (urlPatternMatcher2.find()) {
			logDTO.setUrl(urlPatternMatcher2.group());
		}
		if (userIDPatternMatcher.find()) {
			logDTO.setUserID(StringUtils.substringBefore(userIDPatternMatcher.group(), Constants.UID_SEPERATOR));
		}

		if (StringUtils.isEmpty(logDTO.getUserID())) {
			if (userIDPatternMatcher1.find()) {
				logDTO.setUserID(StringUtils.substringBefore(userIDPatternMatcher1.group(), StringUtils.SPACE));
			}
		}

		if (StringUtils.isEmpty(logDTO.getUserID())) {
			if (userIDPatternMatcher2.find()) {
				logDTO.setUserID(
						StringUtils.substringAfter(userIDPatternMatcher2.group(), Constants.UID_SEPERATOR_EQUAL_TO));
			}
		}

		String userID = null;
		try {
			if (logDTO.getUserID() == null && inputLine.contains(".faces") && inputLine.contains("-")) {
				int indexOfFaces = inputLine.indexOf(".faces");
				String inputAfterFaces = inputLine.substring(indexOfFaces + 6, inputLine.length());
				if ((inputAfterFaces.indexOf("-") + 1) < inputAfterFaces.lastIndexOf("-")) {
					userID = inputAfterFaces.substring(inputAfterFaces.indexOf("-") + 1,
							inputAfterFaces.lastIndexOf("-"));
					if (userID.contains("200")) {
						userID = userID.replace("200", "").trim();
					} else if (userID.contains("302")) {
						userID = userID.replace("302", "").trim();
					}
				}
			}
			if (userID == null && logDTO.getUserID() == null && inputLine.contains("::")) {
				int indexOfDoubleColon = inputLine.indexOf("::");
				int indexOfNumber = inputLine.indexOf("200 ");
				if (indexOfNumber > (indexOfDoubleColon + 2)) {
					userID = inputLine.substring(indexOfDoubleColon + 2, indexOfNumber).trim();
					if (userID.contains("200")) {
						userID = userID.replace("200", "").trim();
					} else if (userID.contains("302")) {
						userID = userID.replace("302", "").trim();
					}
				}
			} else if (inputLine.contains("SuggestiveSecurityServlet")) {
				String[] inputs = inputLine.split(" ");
				for (int i = inputs.length - 1; i > 0; i--) {
					if ("200".equals(inputs[i])) {
						userID = inputs[i - 1];
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("UserID not found :: " + inputLine);
		}

		if (userID != null && userID.length() < 30 && userID.length() > 0) {
			logDTO.setUserID(userID);
		}

		if (httpStatusResponseTimePatternMatcher.find()) {
			logDTO.setHttpStatusResponse(httpStatusResponseTimePatternMatcher.group().substring(0, 3));
			logDTO.setTimeTaken(httpStatusResponseTimePatternMatcher.group().substring(3,
					httpStatusResponseTimePatternMatcher.group().length()));
		}
	}

	private static void writeToCSV(String sourceIP, String destinationIP, String date, String httpRequestType,
			String url, String userID, String httpStatusResponse, String timeInMilliSeconds) throws IOException {
		csvWriter.write(sourceIP);
		csvWriter.write(destinationIP);
		csvWriter.write(date);
		csvWriter.write(httpRequestType);
		csvWriter.write(url);
		csvWriter.write(userID);
		csvWriter.write(httpStatusResponse);
		csvWriter.write(timeInMilliSeconds);
		csvWriter.endRecord();
	}

	private static String getOSName() {
		return System.getProperty("os.name");
	}
}