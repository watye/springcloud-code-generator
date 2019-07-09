/**
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.talelife.generator.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import com.talelife.generator.config.DbInfo;
import com.talelife.generator.entity.ColumnEntity;
import com.talelife.generator.entity.TableEntity;

public class GenUtils {
	
	private static Map<String, Object> createContextDetail(
			String subSysName, String basePackage, String moduleSimpleName,
			Configuration config, boolean hasBigDecimal, TableEntity tableEntity, String mainPath) {
		//封装模板数据
		Map<String, Object> map = new HashMap<>();
		map.put("tableName", tableEntity.getTableName());
		map.put("comments", tableEntity.getComments());
		map.put("pk", tableEntity.getPk());
		map.put("className", tableEntity.getClassName());
		map.put("classname", tableEntity.getClassname());
		map.put("pathName", tableEntity.getClassname().toLowerCase());
		map.put("columns", tableEntity.getColumns());
		map.put("hasBigDecimal", hasBigDecimal);
		map.put("mainPath", mainPath);
		map.put("package", basePackage);
		map.put("packageFolder", basePackage.replace(".", File.separator));
		map.put("moduleName", WordUtils.capitalize(moduleSimpleName));
		map.put("moduleSimple", moduleSimpleName.toLowerCase());
		map.put("systemName", subSysName);
		map.put("author", config.getString("author"));
		map.put("email", config.getString("email"));
		map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		return map;
	}

	private static TableEntity createTableEntity(Map<String, String> table, List<Map<String, Object>> columns,
			Configuration config) {
		//表信息
		TableEntity tableEntity = new TableEntity();
		tableEntity.setTableName(table.get("tableName"));
		tableEntity.setComments(table.get("tableComment"));
		//表名转换成Java类名
		String className = tableToJava(tableEntity.getTableName(), config.getString("tablePrefix"));
		tableEntity.setClassName(className);
		tableEntity.setClassname(StringUtils.uncapitalize(className));
		
		//列信息
		List<ColumnEntity> columsList = new ArrayList<>();
		for(Map<String, Object> column : columns){
			ColumnEntity columnEntity = new ColumnEntity();
			columnEntity.setColumnName(column.get("columnName").toString());
			columnEntity.setDataType(column.get("dataType").toString());
			columnEntity.setComments(column.get("columnComment").toString());
			columnEntity.setExtra(column.get("extra").toString());
			columnEntity.setNullable(column.get("nullable").toString());
			if(null != column.get("characterMaximumLength")){
				columnEntity.setCharacterMaximumLength(Integer.valueOf(column.get("characterMaximumLength").toString()));
			}
			
			//列名转换成Java属性名
			String attrName = columnToJava(columnEntity.getColumnName());
			columnEntity.setAttrName(attrName);
			columnEntity.setAttrname(StringUtils.uncapitalize(attrName));
			
			//列的数据类型，转换成Java类型
			String attrType = config.getString(columnEntity.getDataType(), "unknowType");
			columnEntity.setAttrType(attrType);
			//是否主键
			if("PRI".equalsIgnoreCase(column.get("columnKey").toString()) && tableEntity.getPk() == null){
				tableEntity.setPk(columnEntity);
			}
			
			columsList.add(columnEntity);
		}
		tableEntity.setColumns(columsList);
		
		//没主键，则第一个字段为主键
		if(tableEntity.getPk() == null){
			tableEntity.setPk(tableEntity.getColumns().get(0));
		}
		return tableEntity;
	}

	private static void putEntry(ZipOutputStream zip,String tpll, String file, VelocityContext context, StringWriter sw)
			throws IOException {
		Template tpl;
		tpl = Velocity.getTemplate(tpll, "UTF-8");
		tpl.merge(context, sw);
		zip.putNextEntry(new ZipEntry(file));
		IOUtils.write(sw.toString(), zip, "UTF-8");
		IOUtils.closeQuietly(sw);
		zip.closeEntry();
	}
	
	
	/**
	 * 列名转换成Java属性名
	 */
	public static String columnToJava(String columnName) {
		if(!columnName.contains("_")){
			String firstLetter = columnName.substring(0,1);
			String remain = columnName.substring(1);
			return firstLetter.toUpperCase()+remain;
		}
		return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
	}
	/**
	 * 表名转换成Java类名
	 */
	public static String tableToJava(String tableName, String tablePrefix) {
		if(StringUtils.isNotBlank(tablePrefix)){
			tableName = tableName.replace(tablePrefix, "");
			return WordUtils.capitalizeFully(tableName, new char[]{'_'}).replace("_", "");
		}
		return null;
	}
	
	/**
	 * 获取配置信息
	 */
	public static Configuration getConfig(){
		try {
			return new PropertiesConfiguration("generator.properties");
		} catch (ConfigurationException e) {
			throw new RRException("获取配置文件失败，", e);
		}
	}

	public static String getPackagePath(String basePackage) {
		return basePackage.replace(".", "/");
	}
	
	public static Map<String, Object> createContext(String subSysName, String basePackage, String moduleSimpleName,
			String port, String dbName, Configuration config, boolean hasBigDecimal, String mainPath, DbInfo dbInfo) {
		//封装模板数据
		Map<String, Object> map = new HashMap<>();
		map.put("hasBigDecimal", hasBigDecimal);
		map.put("mainPath", mainPath);
		map.put("package", basePackage);
		map.put("moduleSimpleName", moduleSimpleName);
		map.put("port", port);
		map.put("dbName", dbName);
		map.put("moduleSimple", moduleSimpleName.toLowerCase());
		map.put("systemName", subSysName);
		map.put("moduleName", config.getString("moduleName"));
		map.put("author", config.getString("author"));
		map.put("email", config.getString("email"));
		map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		map.put("dbInfo", dbInfo);
		return map;
	}
	
	public static void generate(
			ZipOutputStream zip, String subSysName, String basePackage,
			String moduleSimpleName,String port,String dbName,List<String> projectTemplates, DbInfo dbInfo, Set<String> zipTemplates) {
				
		//配置信息
		Configuration config = getConfig();
		boolean hasBigDecimal = false;
		//设置velocity资源加载器
		Properties prop = new Properties();  
		prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");  
		Velocity.init(prop);
		
		String mainPath = config.getString("mainPath");
		
		Map<String, Object> map = createContext(
				subSysName, basePackage, moduleSimpleName,
				port, dbName, config,hasBigDecimal, mainPath,dbInfo);
		VelocityContext context = new VelocityContext(map);
		try {
			StringWriter sw = null;
			for(String templateFile : projectTemplates){
				String outputFileName = getOutputFileName(context, templateFile);
				if(zipTemplates.contains(outputFileName)){
					continue;
				}
				sw = new StringWriter();
				putEntry(zip,templateFile, outputFileName, context, sw);
				zipTemplates.add(outputFileName);
			}
		} catch (IOException e1) {
			throw new RRException("渲染模板失败，", e1);
		}
	}
	/**
	 * 获取输出文件名称
	 * @param context
	 * @param velocityEngine
	 * @param tempFileName
	 * @return
	 */
	private static String getOutputFileName(VelocityContext context,String tempFileName) {
		VelocityEngine ve = new VelocityEngine(); 
		Properties properties = new Properties(); 
		ve.init(properties); //初始化 
		Writer fileNameWriter = new StringWriter();
		String outputFileName = tempFileName.replace(File.separator,"@");
		ve.evaluate(context, fileNameWriter, "", outputFileName);
		outputFileName = fileNameWriter.toString().replace("@", File.separator);
		outputFileName = outputFileName.substring(outputFileName.indexOf(File.separator)+1);
		if(outputFileName.endsWith(".vm")){
			outputFileName = outputFileName.replace(".vm", "");
		}
		return outputFileName;
	}
	/**
	 * 生成代码
	 * @param basePackage 
	 * @param basePackage2 
	 * @param subSysName 
	 * @param moduleSimpleName 
	 * @param templates 
	 * @param mode api h5 web dao 
	 * @param dbInfo 
	 * @param zipTemplates 
	 */
	public static void generatorByTable(
			Map<String, String> table,
			List<Map<String, Object>> columns,
			ZipOutputStream zip, String subSysName,
			String basePackage, String moduleSimpleName, List<String> templates,
			String mode, DbInfo dbInfo,Set<String> zipTemplates){
		
		//配置信息
		Configuration config = getConfig();
		boolean hasBigDecimal = false;
		TableEntity tableEntity = createTableEntity(table, columns, config);
		
		for(Map<String, Object> column : columns){
			//列的数据类型，转换成Java类型
			String attrType = config.getString(column.get("dataType").toString(), "unknowType");
			if (!hasBigDecimal && attrType.equals("BigDecimal" )) {
				hasBigDecimal = true;
			}
		}
		
		//设置velocity资源加载器
		Properties prop = new Properties();  
		prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");  
		Velocity.init(prop);

		String mainPath = config.getString("mainPath");
		
		Map<String, Object> map = createContextDetail(subSysName, basePackage, moduleSimpleName,
				config, hasBigDecimal,tableEntity, mainPath);
        VelocityContext context = new VelocityContext(map);
        //获取模板列表
		for(String template : templates){
			String outputFileName = getOutputFileName(context, template);
			if(zipTemplates.contains(outputFileName)){
				continue;
			}
			//渲染模板
			StringWriter sw = new StringWriter();
			Template tpl = Velocity.getTemplate(template, "UTF-8");
			tpl.merge(context, sw);
			try {
				zip.putNextEntry(new ZipEntry(outputFileName));
				IOUtils.write(sw.toString(), zip, "UTF-8");
				IOUtils.closeQuietly(sw);
				zip.closeEntry();
				zipTemplates.add(outputFileName);
			} catch (IOException e) {
				throw new RRException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
			}
		}
	}
}
