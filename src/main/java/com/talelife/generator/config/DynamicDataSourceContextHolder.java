package com.talelife.generator.config;

public class DynamicDataSourceContextHolder {
	private static String ikey;
    /**
     * To switch DataSource
     *
     * @param key the key
     */
    public static void setDataSourceKey(String key) {
    	ikey = key;
    }

    /**
     * Get current DataSource
     *
     * @return data source key
     */
    public static String getDataSourceKey() {
        return ikey;
    }
}
