package com.talelife.generator.config;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceBuilder {
	public static DruidDataSource build(DbInfo info) {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername(info.getUserName());
        ds.setPassword(info.getPassword());
        ds.setUrl(String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8",
        		info.getHost(),info.getPort(),info.getDbName()));
        //配置初始化大小、最小、最大
        //连接泄漏监测
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(30);
        //设置数据源参数，防止输入错误的数据库连接导致无限重连
        ds.setNotFullTimeoutRetryCount(2);
        ds.setMaxWait(5000);
        ds.setConnectionErrorRetryAttempts(2);
        ds.setBreakAfterAcquireFailure(true);
        ds.setFailFast(true);
        return ds;
    }
}
