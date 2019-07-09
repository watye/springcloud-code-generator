package com.talelife.generator.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StringUtils {
	private static final Pattern pattern = Pattern.compile("_[a-z]{1}");
	private static final Pattern dePattern = Pattern.compile("[a-z]{1}[A-Z]{1}");
	
	public static String enCamel(String name){
		if(org.springframework.util.StringUtils.isEmpty(name)){
			return name;
		}
		String result = name;
		Matcher m = pattern.matcher(name);
		while(m.find()){
			String f = m.group();
			result = result.replace(f, f.replace("_", "").toUpperCase());
		}
		return result;
	}
	
	public static String deCamel(String name){
		if(org.springframework.util.StringUtils.isEmpty(name)){
			return name;
		}
		String result = name;
		Matcher m = dePattern.matcher(name);
		while(m.find()){
			String f = m.group();
			String upperString = String.valueOf(f.charAt(1));
			
			String value = f.replace(upperString,"_"+upperString.toLowerCase());
			result = result.replace(f, value);
		}
		return result;
	}
	
	/**
	 * 获取目录下所有文件
	 * @param file
	 * @return
	 */
	public static List<File> getFiles(File file){
		List<File> files = new ArrayList<File>();
		
		if(!file.isDirectory()){
			files.add(file);
		}
		else{
			File[] list = file.listFiles();
			for(File file1 : list){
				files.addAll(getFiles(file1));
			}
		}
		return files;
	}
	
	/**
	 * 获取文件路径
	 * @param path
	 * @return
	 */
	public static String getFilePath(String path){
		int pos = path.lastIndexOf(File.separator);
		return path.substring(0,pos);
	}
}
