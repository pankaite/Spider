package com.kate.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JZPAJ {

	public static void main(String[] args) {
		String url = "https://comp-sync.webapp.163.com/g78_hero/free_convey";
		String JZPAJ = "D:/Projects/Kate/Spider/JZPAJ/";
		String vUrl = "https://moba.v.netease.com/ssl/idle/";
		String vUrlSuffix = ".mp4";
		
		String heroInfo = getInfo(url);
		JSONObject heroInfoJson = JSON.parseObject(heroInfo);
		
		List<Map<String, Object>> heroList = getHeroList(heroInfoJson);
		for (Map<String, Object> hero : heroList) {
			String heroId = hero.get("heroId").toString();
			String heroName = hero.get("heroName").toString();
			createDir(JZPAJ + heroName);
			download(JZPAJ + heroName + "/" + heroName + heroId + vUrlSuffix, vUrl + heroId + vUrlSuffix);
			System.out.println(heroName + "的视频下载完成！");
		}		
	}
	
	public static String getInfo(String url) {
		String result = "";
		BufferedReader br = null;
		try {
			URL realUrl = new URL(url);
			URLConnection connection = realUrl.openConnection();
			connection.connect();
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}

		} catch (Exception e) {
			System.out.println("发送Get请求出现异常！ " + e);
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static List<Map<String, Object>> getHeroList(JSONObject jsonObject) {
		List<Map<String, Object>> heroList = new ArrayList<Map<String,Object>>();
		JSONObject heros = JSON.parseObject(jsonObject.get("data").toString());
		for (String heroName : heros.keySet()) {
			JSONObject hero = JSON.parseObject(heros.get(heroName).toString());
			Map<String, Object> heroInfo = new HashMap<String, Object>();
			heroInfo.put("heroId", hero.get("式神ID"));
			heroInfo.put("heroName", heroName);
			heroList.add(heroInfo);
		}
		return heroList;
	}
	
	public static void createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + " 失败，目标目录已经存在");
		}
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + " 成功！");
		} else {
			System.out.println("创建目录" + destDirName + " 失败！");
		}
	}

	public static void download(String imgName, String url) {
		try {
			URL realUrl = new URL(url);
			InputStream is = realUrl.openStream();
			FileOutputStream fos = new FileOutputStream(new File(imgName));
			byte[] buf = new byte[1024];
			int length = 0;
			while ((length = is.read(buf, 0, buf.length)) != -1) {
				fos.write(buf, 0, length);
			}
			is.close();
			fos.close();
		} catch(FileNotFoundException e){
			System.out.println(url + "的视频资源不存在！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
