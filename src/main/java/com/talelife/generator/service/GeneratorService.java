package com.talelife.generator.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.talelife.generator.config.DbInfo;
import com.talelife.generator.dao.GeneratorDao;
import com.talelife.generator.utils.GenUtils;

@Service
public class GeneratorService {
	@Autowired
	private GeneratorDao sysGeneratorDao;

	public List<Map<String, Object>> queryList(Map<String, Object> map) {
		return sysGeneratorDao.queryList(map);
	}

	public int queryTotal(Map<String, Object> map) {
		return sysGeneratorDao.queryTotal(map);
	}

	public Map<String, String> queryTable(String tableName) {
		return sysGeneratorDao.queryTable(tableName);
	}

	public List<Map<String, Object>> queryColumns(String tableName) {
		return sysGeneratorDao.queryColumns(tableName);
	}

	public byte[] generatorCode(String[] tableNames, String subSysName, String basePackage,
			String moduleSimpleName,String port,String dbName,DbInfo dbInfo) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		List<File> templates = getTemplates();
		List<String> projectTemplates = getTemplates(templates,"project");
		List<String> tableTemplates = getTemplates(templates,"table");
		Set<String> zipTemplates = new HashSet<>();
		GenUtils.generate(zip, subSysName, basePackage, moduleSimpleName, port, dbName,projectTemplates,dbInfo,zipTemplates);
		
		for(String tableName : tableNames){
			Map<String, String> table = queryTable(tableName);
			List<Map<String, Object>> columns = queryColumns(tableName);
			GenUtils.generatorByTable(table, columns, zip, subSysName, basePackage, moduleSimpleName, tableTemplates,"web",dbInfo,zipTemplates);
		}
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}

	private List<File> getTemplates() {
		String templatePath = "";
		try {
			templatePath = ClassLoader.getSystemResource("template").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		//获取模板文件
		File templateFolder = new File(templatePath);
		List<File> templateFiles = com.talelife.generator.utils.StringUtils.getFiles(templateFolder);
		if(templateFiles.isEmpty())
			throw new RuntimeException("没有模板文件");
		return templateFiles;
	}
	
	private List<String> getTemplates(List<File> templateFiles,String type){
		List<String> templatesAbsolutePath = templateFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList());
		List<String> projects = new ArrayList<>();
		List<String> tables = new ArrayList<>();
		
		for (String absolutePath : templatesAbsolutePath) {
			String templatePath = absolutePath.substring(absolutePath.indexOf("template"));
			if(isTableTemplate(absolutePath)){
				tables.add(templatePath);
			}else{
				projects.add(templatePath);
			}
			
		}
			
		return "project".equals(type)?projects:tables;
	}

	private boolean isTableTemplate(String absolutePath) {
		return absolutePath.contains(".java") || absolutePath.contains("Mapper.xml");
	} 
}
