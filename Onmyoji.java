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

public class Onmyoji {

	public static void main(String[] args) {
		String url = "https://g37simulator.webapp.163.com/get_heroid_list?rarity=0&page=1&per_page=200";
		String Onmyoji = "D:/Projects/Kate/Spider/Onmyoji/";
		String yys = "https://yys.res.netease.com/pc/zt/20161108171335/data/shishen_";
		String beforeAwake = "big_beforeAwake/";
		String afterAwake = "big_afterAwake/";
		String skin = "skin/";
		String imgUrlSuffix = ".png";
		
		String heroInfo = getInfo(url);
		JSONObject heroInfoJson = JSON.parseObject(heroInfo);
		
		List<Map<String, Object>> heroList = getHeroList(heroInfoJson);
		for (Map<String, Object> hero : heroList) {
			String heroId = hero.get("heroId").toString();
			String heroName = hero.get("heroName").toString();
			createDir(Onmyoji + heroName);
			download(Onmyoji + heroName + "/" + heroId + "_beforeAwake" + imgUrlSuffix, yys + beforeAwake + heroId + imgUrlSuffix);
			download(Onmyoji + heroName + "/" + heroId + "_afterAwake" + imgUrlSuffix, yys + afterAwake + heroId + imgUrlSuffix);
			download(Onmyoji + heroName + "/" + heroId + "_skin" + imgUrlSuffix, yys + skin + heroId + "-1" + imgUrlSuffix);
			System.out.println(heroName + "的图片下载完成！");
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
		for (String heroId : heros.keySet()) {
			JSONObject hero = JSON.parseObject(heros.get(heroId).toString());
			Map<String, Object> heroInfo = new HashMap<String, Object>();
			heroInfo.put("heroId", heroId);
			heroInfo.put("heroName", hero.get("name"));
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
			System.out.println(url + "的图片资源不存在！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
