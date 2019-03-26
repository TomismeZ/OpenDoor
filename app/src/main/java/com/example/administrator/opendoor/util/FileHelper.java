package com.example.administrator.opendoor.util;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;

public class FileHelper {
	public static void saveFile(Context context,String fileName,String info){
		FileOutputStream fout=null;
		BufferedOutputStream buffer=null;
		try {
			fout= context.openFileOutput(fileName, Context.MODE_APPEND);
			buffer=new BufferedOutputStream(fout);//为其实现缓冲功能
			byte[] bytes=info.getBytes();
			buffer.write(bytes);
			buffer.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				buffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public static String readFile(Context context,String fileName){
		String str="";
		BufferedInputStream buffered=null;
		FileInputStream in;
		try {
			in=context.openFileInput(fileName);
			buffered=new BufferedInputStream(in);
			byte[] bytes=new byte[buffered.available()];
			int len=0;
			while((len=buffered.read(bytes))!=-1){
				str=new String(bytes,0,len);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				buffered.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return str;
	}
	public static Boolean deleteFile(Context context,String fileName){
		context.deleteFile(fileName);
		return true;
	}
}
