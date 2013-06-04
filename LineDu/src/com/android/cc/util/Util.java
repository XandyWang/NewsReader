package com.android.cc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.widget.Toast;

import com.android.cc.news.Channel;
import com.android.cc.news.NewsItem;
import com.android.cc.sort.Sort;
import com.app.cc.R;

public class Util {
	
	private static String SLog = "Util";
//	public static int currentSIndex = 0;
	public static Sort currentSort = null;
	public static Channel currentChannel = null;
	public static NewsItem currentItem = null;
	public static int currentItemID = 0;


	// Environment.getExternalStorageDirectory().getAbsolutePath()��ȡsdcard·��
	private static String sdcard = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	// ��������ݿ��ļ���
	public static final String DB_NAME = "mynews.db";

	// ���ݿ�İ�װ·��
	public static final String DB_PATH = sdcard + "/mynews/database";

	// ���ݿ����ֻ��������ݿ��λ��
	public static final String DB = DB_PATH + "/" + DB_NAME;

//	public static final String channelPath = sdcard + "/loveyes/channel";
	// Ƶ��ͼƬ
	public static final String cIconPath = sdcard + "/mynews/cIcon";
	public static final String nIconPath = sdcard + "/mynews/nIcon";

	public static final String nChannelPath = sdcard + "/mynews/nChannel";

	public static SQLiteDatabase db = null;
	
	
	public static final String SP_APP = "loveeyes";

	// �������ݿ�
	/**
	 * �����Ѵ��ڵ����ݿ⣬�������ɹ�����ô����true�����򷵻�false
	 * 
	 * @param context
	 * @return
	 */
	public static boolean importDB(Context context) {
		Log.d(SLog,"importDB");
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}
		File mFile = new File(DB);
		// �ж����ݿ��ļ��Ƿ���ڣ�����������ִ�е��룬����ֱ�Ӵ����ݿ�
		if (!mFile.exists()) {
			File mParent = new File(DB_PATH);
			InputStream mInputStream = context.getResources().openRawResource(
					R.raw.loveyes);
			createFile(mInputStream, mParent, mFile);
		}
		return mFile.exists();
	}
	
	public static void showToast(Context mContext ,String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * �򿪵��뵽SDCARD�ϵ����ݿ�,ʧ�ܷ���null,���򷵻ش򿪵����ݿ�
	 * 
	 * @return SQLiteDatabase
	 */
	public static SQLiteDatabase openDatabase() {
		Log.d(SLog,"openDatabase");
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return null;
		}
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DB, null,
				SQLiteDatabase.OPEN_READWRITE);
		return db;
	}

	public Images getImage(String Imgpath) {
		Log.d(SLog,"getImage");
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return null;
		}
		Images mImages = new Images();
		return mImages;
	}

	public static void createFile(InputStream mInputStream, File parent,
			File file) {
		Log.d(SLog,"createFile");
		try {
			// Make sure the Parent directory exists.
			if (!parent.exists()) {
				parent.mkdirs();
			}
			// Very simple code to copy a picture from the application's
			// resource into the external file. Note that this code does
			// no error checking, and assumes the picture is small (does not
			// try to copy it in chunks). Note that if external storage is
			// not currently mounted this will silently fail.
			InputStream is = mInputStream;
			OutputStream os = new FileOutputStream(file);
			byte[] data = new byte[is.available()];
			is.read(data);
			os.write(data);
			is.close();
			os.close();
			// Log.d(SLog, "file = " + file.getPath());
			// Tell the media scanner about the new file so that it is
			// immediately available to the user.
			// MediaScannerConnection.scanFile(context,
			// new String[] { file.toString() }, null,
			// new MediaScannerConnection.OnScanCompletedListener() {
			// @Override
			// public void onScanCompleted(String path, Uri uri) {
			// Log.d(SLog, "Scanned " + path + ":");
			// Log.d(SLog, "-> uri=" + uri);
			// }
			// });
		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.d(SLog, "Error writing " + file, e);
		}
	}

	/**
	 * @Title: detect
	 * @Description: TODO �ж��û��Ƿ����� ����������
	 * @param act
	 * @return
	 */
	public static boolean isConnect = false; // �Ƿ���������
	public static boolean isWifi = false; // �Ƿ�ʹ��Wifi

	public static boolean checkNetwork(Context context) {
		isConnect = true;
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getApplicationContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);
			if (manager == null) {
				isConnect = false;
				return false;
			}
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			if (networkinfo == null || !networkinfo.isAvailable()) {
				isConnect = false;
				return false;
			}
			String typeName = networkinfo.getTypeName().toLowerCase(); // WIFI/MOBILE
			if (typeName.equals("wifi")) {
				isWifi = true;
				isConnect = true;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
