package com.talelife.generator.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.talelife.generator.config.DataSourceBuilder;
import com.talelife.generator.config.DbInfo;
import com.talelife.generator.config.DynamicDataSourceContextHolder;
import com.talelife.generator.config.DynamicRoutingDataSource;
import com.talelife.generator.service.GeneratorService;
import com.talelife.generator.utils.GenUtils;
import com.talelife.generator.utils.PageUtils;
import com.talelife.generator.utils.Query;
import com.talelife.generator.utils.R;

@RestController
@RequestMapping("/sys/generator")
public class GeneratorController {
	@Autowired
	private GeneratorService sysGeneratorService;
	@Autowired
	private DynamicRoutingDataSource dynamicRoutingDataSource;
	private Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params,HttpServletRequest request){
		String dbName = params.get("dbName").toString();
		String dbHost = params.get("dbHost").toString();
		String dbPort = params.get("dbPort").toString();
		String dbUserName = params.get("dbUserName").toString();
		String dbPassword = params.get("dbPassword").toString();
		if(StringUtils.isEmpty(dbHost)) {
			PageUtils pageUtil = new PageUtils(new ArrayList<>(), 0, 10, 1);
			return R.ok().put("page", pageUtil);
		}
		DbInfo info = new DbInfo(dbHost, Integer.valueOf(dbPort), dbUserName, dbPassword,dbName);
		String key = info.getHost()+"_"+info.getPort()+"_"+info.getDbName();
		DynamicDataSourceContextHolder.setDataSourceKey(key);
		if(!targetDataSources.containsKey(key)) {
			targetDataSources.put(key, DataSourceBuilder.build(info));
			dynamicRoutingDataSource.setTargetDataSources(targetDataSources );
		}
		//查询列表数据
		Query query = new Query(params);
		List<Map<String, Object>> list = sysGeneratorService.queryList(query);
		int total = sysGeneratorService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 生成代码
	 */
	@RequestMapping("/code")
	public void code(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String[] tableNames = new String[]{};
		String subSysName = request.getParameter("subSysName");
		String basePackage = request.getParameter("basePackage");
		String simpleName = request.getParameter("simpleName");
		String port = request.getParameter("port");
		String dbName = request.getParameter("dbName");
		String dbHost = request.getParameter("dbHost");
		String dbPort = request.getParameter("dbPort");
		String dbUserName = request.getParameter("dbUserName");
		String dbPassword = request.getParameter("dbPassword");
		DbInfo info = new DbInfo(dbHost, Integer.valueOf(dbPort), dbUserName, dbPassword,dbName);
		String key = info.getHost()+"_"+info.getPort()+"_"+info.getDbName();
		DynamicDataSourceContextHolder.setDataSourceKey(key);
		if(!targetDataSources.containsKey(key)) {
			targetDataSources.put(key, DataSourceBuilder.build(info));
			dynamicRoutingDataSource.setTargetDataSources(targetDataSources );
		}
		String tables = request.getParameter("tables");
		tableNames = JSON.parseArray(tables).toArray(tableNames);
		
		byte[] data = null;
		try {
			data = sysGeneratorService.generatorCode(tableNames,subSysName,basePackage,simpleName,port,dbName,info);
			
			response.reset();  
			response.setHeader("Content-Disposition", "attachment; filename=\""+subSysName+".zip\"");  
			response.addHeader("Content-Length", "" + data.length);  
			response.setContentType("application/octet-stream; charset=UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
  
        IOUtils.write(data, response.getOutputStream());  
	}
}
