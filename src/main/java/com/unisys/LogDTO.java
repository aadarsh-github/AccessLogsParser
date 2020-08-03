package com.unisys;

public class LogDTO {

	private String fileName;
	private String sourceIPAddress;
	private String destinationIPAddress;
	private String date;
	private String time;
	private String httpRequestType;
	private String userID;
	private String url;
	private String airlineType;
	private String httpStatusResponse;
	private String timeTaken;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSourceIPAddress() {
		return sourceIPAddress;
	}

	public void setSourceIPAddress(String sourceIPAddress) {
		this.sourceIPAddress = sourceIPAddress;
	}

	public String getDestinationIPAddress() {
		return destinationIPAddress;
	}

	public void setDestinationIPAddress(String destinationIPAddress) {
		this.destinationIPAddress = destinationIPAddress;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHttpRequestType() {
		return httpRequestType;
	}

	public void setHttpRequestType(String httpRequestType) {
		this.httpRequestType = httpRequestType;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAirlineType() {
		return airlineType;
	}

	public void setAirlineType(String airlineType) {
		this.airlineType = airlineType;
	}

	public String getHttpStatusResponse() {
		return httpStatusResponse;
	}

	public void setHttpStatusResponse(String httpStatusResponse) {
		this.httpStatusResponse = httpStatusResponse;
	}

	public String getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(String timeTaken) {
		this.timeTaken = timeTaken;
	}

}
