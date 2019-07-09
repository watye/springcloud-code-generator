package com.talelife.generator.config;

public final class DbInfo {

	private final String host;
	private final int port;
	private final String dbName;
	private final String userName;
	private final String password;
	
	public DbInfo(String host,int port,String userName,String password,String dbName) {
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getDbName() {
		return dbName;
	}
}
