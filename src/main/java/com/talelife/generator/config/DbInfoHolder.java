package com.talelife.generator.config;
//package com.syswinsoft.roc.infrastructure.generator.config;
//
//public class DbInfoHolder {
//	private static ThreadLocal<DbInfo> dbInfo = new ThreadLocal<DbInfo>();
//	public static DbInfo defaultDbInfo = new DbInfo("172.31.118.22",3306,"roc-cloud-platform","root","admin@dev");
//	
//	static {
//		if(null == dbInfo.get()) {
//			setDbInfo(defaultDbInfo);
//		}
//	}
//	
//	public static String getHost() {
//		return dbInfo.get().getHost();
//	}
//
//	public static int getPort() {
//		return dbInfo.get().getPort();
//	}
//
//	public static String getUserName() {
//		return dbInfo.get().getUserName();
//	}
//
//	public static String getPassword() {
//		return dbInfo.get().getPassword();
//	}
//	
//	public static String getDbName() {
//		return dbInfo.get().getDbName();
//	}
//	
//	public static void setDbInfo(DbInfo info) {
//		dbInfo.set(info);
//	}
//}
