package com.hhkj.gas.www.common;
import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipException;
import org.json.JSONException;
import org.json.JSONObject;

public class FileUtils {

	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}
	/**
	 * 检查数据库是否存在
	 * @return
	 */
	public static boolean db_exits(){
		File file = new File(Common.DB_DIR+Common.DB_NAME);
		if(file.exists()){
			return true;
		}
		return false;
	}


	/**
	 *
	 * @param zipFile
	 *            源文件
	 * @param folderPath
	 *            目标目录
	 * @return
	 * @throws ZipException
	 * @throws IOException
	 */

	// 读取指定路径文本文件
	public static synchronized String read(String filePath) {
		StringBuilder str = new StringBuilder();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filePath));
			String s;
			try {
				while ((s = in.readLine()) != null)
					str.append(s + '\n');
			} finally {
				in.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str.toString();
	}
	private static   boolean deleteDir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	/**
	 * 新名字和旧名字一样就生成，不一样就重新生成
	 * @param file
	 * @param fileNs
	 * @param childFileName
	 * @return
	 */
	public static String name(File file,String fileNs[],String childFileName){
		for(int i=0;i<fileNs.length;i++){
			if(fileNs[i].substring(0,fileNs[i].lastIndexOf(".")).equals(childFileName)){
				return childFileName;
			}
		}
		if(fileNs.length!=0){
			deleteDir(file);
		}
		//
		return childFileName;
	}
	public static void writeLog(String text,String tag) {
		String childFileName = TimeUtil.getTimeLog(System.currentTimeMillis());
		String logPath = Common.APK_LOG;
		File file = new File(Common.APK_LOG);
		if(!file.exists()){
			file.mkdirs();
			//不存在就创建目录
		}
		String fileNs[] = file.list();
		String realName = name(file, fileNs, childFileName)+".txt";
		if (text == null)
			return;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(logPath+realName,
					true));
			try {
				out.write(tag+"-----"+TimeUtil.getTime(System.currentTimeMillis())+"<br>");
				out.write(text);
				out.write("<br>");
			} finally {
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	// 写入指定的文本文件，append为true表示追加，false表示重头开始写，
	// text是要写入的文本字符串，text为null时直接返回
	public static void write(String filePath, boolean append, String text) {
		if (text == null)
			return;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filePath,
					append));
			try {
				out.write(text);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获得指定文件的byte数组
	 */
	private byte[] getBytes(String filePath){
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static void getFile(byte[] bfile, String filePath,String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath+"\\"+fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}


	/**
	 * 433
	 *
	 * @param temp
	 * @return
	 */
	public static String getReals(String temp) {
		return temp.replaceAll("^(0+)", "");
	}

	/**
	 * 格式化json
	 * @return
	 * @throws JSONException
	 */
	public static String formatJson(String json) throws JSONException{
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getString("d");
	}
}
