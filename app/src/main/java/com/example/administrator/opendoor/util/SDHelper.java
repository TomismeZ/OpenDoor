package com.example.administrator.opendoor.util;

import java.io.BufferedOutputStream;

import java.io.File;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SDHelper {
	private static File path;

	public static void saveFile(Context context, String fileName, String info) {
		BufferedOutputStream buffer = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {// 判断sd卡状态
			// Environment.getExternalStorageDirectory();//得到SD卡根目录
			path = context.getExternalFilesDir(null);// 默认类型，私有的

			if (path != null) {
				try {
					FileOutputStream out = new FileOutputStream(path + "/"
							+ fileName,true);//ture表示可追加，默认为false，私有
					buffer = new BufferedOutputStream(out);// 为其实现缓冲功能
					byte[] bytes = info.getBytes();
					buffer.write(bytes);
					buffer.flush();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						buffer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public static String readFile(Context context, String fileName) {

		String str = "";
		BufferedInputStream buffered = null;
		FileInputStream in;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {// 判断sd卡状态
			// Environment.getExternalStorageDirectory();//得到SD卡根目录
//			path = context.getExternalFilesDir(null);// 默认类型，私有的
//			path=new File(Environment.getExternalStorageDirectory()+"/com.myncic.ican/files");
//			Toast.makeText(context, "文件的路径: "+path, Toast.LENGTH_LONG).show();

			path=new File(Environment.getExternalStorageDirectory().getPath()+"/Myncic/.ican/files");

			Log.d("OpenDoorActivity","文件存放的位置："+path);

			File file = new File(path + "/" + fileName);
			if (file == null || !file.exists() || file.isDirectory()) {
//				Toast.makeText(context, "文件不存在，读取失败", Toast.LENGTH_LONG).show();
				Log.d("OpenDoorActivity","文件不存在");
			} else {
				try {
					in = new FileInputStream(path + "/" + fileName);
					buffered = new BufferedInputStream(in);
					byte[] bytes = new byte[buffered.available()];
					int len = 0;
					while ((len = buffered.read(bytes)) != -1) {
						str = new String(bytes, 0, len);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						buffered.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else{
			Toast.makeText(context, "您的SD卡不存在！", Toast.LENGTH_LONG).show();
		}
		return str;
	}

	public static Boolean deleteFile(Context context, String fileName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {// 判断sd卡状态
			// Environment.getExternalStorageDirectory();//得到SD卡根目录
//			path = context.getExternalFilesDir(null);// 默认类型，私有的

			path=new File(Environment.getExternalStorageDirectory().getPath()+"/Myncic/.ican/files");

			File file = new File(path + "/" + fileName);
			if (file == null || !file.exists() || file.isDirectory()) {
//				Toast.makeText(context, "文件不存在，删除失败", Toast.LENGTH_LONG).show();
				Log.d("OpenDoorActivity","文件不存在");
				return false;
			}
			file.delete();

		}
		return true;
	}

	public static Boolean isExistFile(Context context, String fileName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {// 判断sd卡状态
			// Environment.getExternalStorageDirectory();//得到SD卡根目录
//			path = context.getExternalFilesDir(null);// 默认类型，私有的
			path=new File(Environment.getExternalStorageDirectory().getPath()+"/Myncic/.ican/files");

			File file = new File(path + "/" + fileName);
			if (file == null || !file.exists() || file.isDirectory()) {
//				Toast.makeText(context, "文件不存在", Toast.LENGTH_LONG).show();

				Log.d("OpenDoorActivity","文件不存在");
				return false;
			}
//			file.delete();

		}
		return true;
	}
}
