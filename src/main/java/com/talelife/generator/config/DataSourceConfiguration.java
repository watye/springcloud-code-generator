package com.talelife.generator.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataSourceConfiguration {
	public static DbInfo defaultDbInfo = new DbInfo("172.31.118.22",3306,"root","admin@dev","estate-cloud-platform");
	@Bean
	public DynamicRoutingDataSource dynamicRoutingDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
//        DataSource defaultDataSource = null;
        DataSource ds = DataSourceBuilder.build(defaultDbInfo);
//        defaultDataSource = ds;
//        
//        if(null == defaultDataSource) {
//        	throw new RuntimeException("未配置默认数据源！");
//        }

        // Set master datasource as default
//        dynamicRoutingDataSource.setDefaultTargetDataSource(defaultDataSource);
        Map<Object, Object> targetDataSources = new HashMap<>();
        String key = defaultDbInfo.getHost()+"_"+defaultDbInfo.getPort()+"_"+defaultDbInfo.getDbName();
        targetDataSources.put(key, ds);
		dynamicRoutingDataSource.setTargetDataSources(targetDataSources);

        return dynamicRoutingDataSource;
	}

	/**
	 * 初始化动态数据源
	 * @param config
	 * @return
	 */
	/**
	 * 初始化动态数据源
	 * @param config
	 * @return
	 */
    @Bean("dataSource")
    public DataSource dataSource(@Autowired DynamicRoutingDataSource dynamicRoutingDataSource) {
        return dynamicRoutingDataSource;
    }

    /**
     * Sql session factory bean.
     * Here to config datasource for SqlSessionFactory
     * <p>
     * You need to add @{@code @ConfigurationProperties(prefix = "mybatis")}, if you are using *.xml file,
     * the {@code 'mybatis.type-aliases-package'} and {@code 'mybatis.mapper-locations'} should be set in
     * {@code 'application.properties'} file, or there will appear invalid bond statement exception
     *
     * @return the sql session factory bean
     */
    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Autowired DynamicRoutingDataSource dynamicRoutingDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource(dynamicRoutingDataSource));
        return sqlSessionFactoryBean;
    }

    /**
     * Transaction manager platform transaction manager.
     *
     * @return the platform transaction manager
     */
    @Bean
    public PlatformTransactionManager transactionManager(@Autowired DynamicRoutingDataSource dynamicRoutingDataSource) {
        return new DataSourceTransactionManager(dataSource(dynamicRoutingDataSource));
    }
}
