package com.kate.spider;

import java.io.BufferedReader;
import java.io.File;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class KingOfGlory {

	public static void main(String[] args) {
		String url = "http://pvp.qq.com/web201605/js/herolist.json";
		String KOG = "D:/Projects/Kate/Spider/KOG/";
		String imgUrlPrefix = "http://game.gtimg.cn/images/yxzj/img201606/skin/hero-info/";
		String imgUrlMidfix = "-bigskin-";
		String imgUrlSuffix = ".jpg";
		
		String heroInfo = getInfo(url);
		JSONArray heroInfoArray = JSON.parseArray(heroInfo);
		
		List<Map<String, Object>> heroList = getHeroList(heroInfoArray);
		for (Map<String, Object> hero : heroList) {
			String eName = hero.get("eName").toString();
			String cName = hero.get("cName").toString();
			String skinNum = hero.get("skinNum").toString();
			createDir(KOG + cName);
			for(int i = 1; i <= Integer.parseInt(skinNum); i++){
				String imgNameSuffix = eName + imgUrlMidfix + i + imgUrlSuffix;
				String imgName = KOG + cName + "/" + imgNameSuffix;
				String imgUrl = imgUrlPrefix + eName + "/" + imgNameSuffix;
				download(imgName, imgUrl);
			}
			System.out.println(cName + "的图片下载完成！");
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
	
	public static List<Map<String, Object>> getHeroList(JSONArray jsonArray) {
		List<Map<String, Object>> heroList = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < jsonArray.size(); i++) {
			Map<String, Object> heroInfo = new HashMap<String, Object>();
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			heroInfo.put("eName",jsonObject.get("ename"));
			heroInfo.put("cName", jsonObject.get("cname"));
			heroInfo.put("skinNum", jsonObject.get("skin_name").toString().split("\\|").length);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
