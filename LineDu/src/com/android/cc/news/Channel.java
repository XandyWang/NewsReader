/**
 * RSS��Ƶ���� ��װ��Ƶ��������
 */
package com.android.cc.news;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.android.cc.sort.Sort;
import com.android.cc.util.Util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Channel {
	public int cid = 0;
	public int sid = 0;
	public String title = ""; 				// Ƶ������
	public String rss = ""; 				// Ƶ��rss����
	public String link = ""; 				// Ƶ��html����
	public int isTake = 0; 				// �Ƿ���;
	public String code = ""; 				// Ƶ������
	public String desc = ""; 				// Ƶ������
	public String pubdate = ""; 			// RSS������ʱ��
	public String copyringht = ""; 			// ��Ȩ����
	public String img = ""; 				// ��Ƶ����ͼƬ
	public String file = ""; 				// ��Ƶ���Ļ����ļ�
	public static SQLiteDatabase db = null;
	private static String SLog = "Channel";
	
	/**
	 * ��ʼ������
	 */
	public Channel() {
	}
	
	/**
	 * ��ʼ������
	 */
	public Channel(int Sid) {
		this.sid = Sid;
	}

	/**
	 * ��ʼ������
	 * @param title Ƶ������
	 * @param link Ƶ������
	 * @param description Ƶ������
	 */
	public Channel(String title, String link, String description) {
		this.title = title;
		this.link = link;
		this.desc = description;
	}

//	"CREATE TABLE channel " 
//	"(cid int(11) NOT NULL,  "
//	"sid int(11) NOT NULL,  "
//	"title varchar(50) NOT NULL,  "
//	"rss varchar(200) NOT NULL, "
//	"link varchar(200) DEFAULT NULL, "
//	"istake int(1) NOT NULL, "
//	"code varchar(8) NOT NULL, "
//	"desc varchar(200) DEFAULT NULL, "
//	"pubdate varchar(20) DEFAULT NULL, "
//	"img varchar(200) DEFAULT NULL,"
//	"file varchar(200) DEFAULT NULL, " 
//	"PRIMARY KEY (cid) )";
	
	public static List<Channel> allChannels = null;
	/**
	 * ��ȡ���е�
	 * @param sid
	 */
	public static void getAllChannels(int sid) {
		if(null == allChannels){
			allChannels = new ArrayList<Channel>();
		}
		allChannels.clear();
		Log.d(SLog, "allChannels clear ");
		db = Util.openDatabase();
		String sql = "SELECT * FROM channel where channel.sid = ? order by channel.cid asc ";
		Cursor c = db.rawQuery(sql, new String[] { sid + "" });
		Channel temp;
		while (c.moveToNext()) {
			temp = new Channel();
			temp.cid = c.getInt(0);
			temp.sid = c.getInt(1);
			temp.title = c.getString(2);
			temp.rss = c.getString(3);
			temp.link = c.getString(4);
			temp.isTake = c.getInt(5);
			temp.code = c.getString(6);
			temp.desc = c.getString(7);
			temp.pubdate = c.getString(8);
			temp.img = c.getString(9);
			temp.file = c.getString(10);
			allChannels.add(temp);
		}
		db.close();
	}
	
	/**
	 * ��ȡ���е�
	 * @param sid
	 */
	public static List<Channel> getAllChannel(int sid) {
		List<Channel> allChannels = new ArrayList<Channel>();
		db = Util.openDatabase();
		String sql = "SELECT * FROM channel where channel.sid = ? order by channel.cid asc ";
		Cursor c = db.rawQuery(sql, new String[] { sid + "" });
		Channel temp;
		while (c.moveToNext()) {
			temp = new Channel();
			temp = new Channel();
			temp.cid = c.getInt(0);
			temp.sid = c.getInt(1);
			temp.title = c.getString(2);
			temp.rss = c.getString(3);
			temp.link = c.getString(4);
			temp.isTake = c.getInt(5);
			temp.code = c.getString(6);
			temp.desc = c.getString(7);
			temp.pubdate = c.getString(8);
			temp.img = c.getString(9);
			temp.file = c.getString(10);
			allChannels.add(temp);
		}
		db.close();
		return allChannels;
	}
	
//	public static List<Channel> takeChannel = null;
	/**
	 * ��ȡ����Ƶ��
	 * @param takeChannel 
	 * @param mSort
	 */
	public static void getTakeChannel(List<Channel> takeChannel,Sort mSort) {
//		if(null == takeChannel){
//			takeChannel = new ArrayList<Channel>();
//		}
		takeChannel.clear();
		Log.d(SLog, "allChannels " + mSort.title);
		db = Util.openDatabase();
		String sql = "SELECT * FROM channel where channel.sid = ? order by channel.cid asc ";
		Cursor c = db.rawQuery(sql, new String[] { mSort.sid + "" });
		Channel temp;
		while (c.moveToNext()) {
			temp = new Channel();
			temp.cid = c.getInt(0);
			temp.sid = c.getInt(1);
			temp.title = c.getString(2);
			temp.rss = c.getString(3);
			temp.link = c.getString(4);
			temp.isTake = c.getInt(5);
			temp.code = c.getString(6);
			temp.desc = c.getString(7);
			temp.pubdate = c.getString(8);
			temp.img = c.getString(9);
			temp.file = c.getString(10);
			if( 1 == temp.isTake){
				takeChannel.add(temp);
			}
		}
		db.close();
	}
	/**
	 * ��ȡĳһ�����µ����е�Ƶ��
	 * @param allChannels
	 * @param sid
	 */
	public static void getAllChannel(List<Channel> allChannels, int sid) {
		allChannels.clear();
		Log.d(SLog, "allChannels clear ");
		db = Util.openDatabase();
		String sql = "SELECT * FROM channel where channel.sid = ? order by channel.cid asc ";
		Cursor c = db.rawQuery(sql, new String[] { sid + "" });
		Channel temp;
		while (c.moveToNext()) {
			temp = new Channel();
			temp.cid = c.getInt(0);
			temp.sid = c.getInt(1);
			temp.title = c.getString(2);
			temp.rss = c.getString(3);
			temp.link = c.getString(4);
			temp.isTake = c.getInt(5);
			temp.code = c.getString(6);
			temp.desc = c.getString(7);
			temp.pubdate = c.getString(8);
			temp.img = c.getString(9);
			temp.file = c.getString(10);
			allChannels.add(temp);
		}
		db.close();
	}

	/**
	 * ����Ƿ��Ѿ�����
	 * @return 
	 */
	public boolean isDownLoad() {
		Log.d(SLog, "isDownLoad");
		File channel = new File(Util.nChannelPath, this.file);
		Log.d(SLog, "channel is " + channel.exists() );
		return channel.exists();
	}

	/**
	 * 
	 * @return ����Ƶ��
	 */
	public boolean downChannel2SD() {
		File parent = new File(Util.nChannelPath);
		if (!parent.exists()) {
			parent.mkdirs();
		}
		File file2SD = new File(parent, this.file);
		if (file2SD.exists()){
			file2SD.delete();
		}
		file2SD = new File(parent, this.file);
		try {
			URL url = new URL(this.rss);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();
				
				FileOutputStream fos = new FileOutputStream(file2SD);
				
				byte[] bt = new byte[1024];
				int read = 0;
				while ((read = is.read(bt)) != -1) {
					// os.write(buffer);�����������1024���ֽ�
					Log.d(SLog, read + "---------------------");
					fos.write(bt, 0, read);// ������������������
				}
				fos.flush();
				fos.close();
				is.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.d(SLog, "Error writing " + file2SD, e);
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(SLog, "Error writing " + file2SD, e);
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @return ��ȡ�Ѿ����ص�Ƶ��
	 */
	public boolean ReadChannel(){
		Log.d(SLog, "ReadChannel -- ");
		if (null == this.mNewsItems) {
			this.mNewsItems = new ArrayList<NewsItem>();
		}
		this.mNewsItems.clear();
		NewsItem itemTemp = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			InputStream mInputStream = new FileInputStream( new File(Util.nChannelPath,file) );
//			parser.setInput(new FileReader(new File(Util.nChannelPath,file)));
			parser.setInput(mInputStream, code);
			int event = parser.getEventType();
			String rssTag = null;
			String tagString = null;
			boolean inItem = false;
			while (XmlPullParser.END_DOCUMENT != event) {
				Log.d(SLog, "START-------------------------------------------- ");
				switch (event) {
				case XmlPullParser.START_DOCUMENT:// �жϵ�ǰ�¼��Ƿ����ĵ���ʼ�¼�
					Log.d(SLog, "START_DOCUMENT");
					break;
				case XmlPullParser.START_TAG:// �жϵ�ǰ�¼��Ƿ��Ǳ�ǩԪ�ؿ�ʼ�¼�
					rssTag = parser.getName().toLowerCase();
					Log.d(SLog, "START_TAG -- " + rssTag);
					if ("item".equals(rssTag)) {
						inItem = true;
						itemTemp = new NewsItem();
					}
					break;
				case XmlPullParser.TEXT:
					tagString = parser.getText();
					if (!inItem) {
						Log.d(SLog, "Channel TAG -- " + rssTag);
						if ("title".equals(rssTag)) {
							this.title = tagString;
						} else if ("image".equals(rssTag)) {
							this.img = tagString;
						} else if ("description".equals(rssTag)) {
							this.desc = tagString;
						} else if ("link".equals(rssTag)) {
							this.link = tagString;
						} else if ("pubdate".equals(rssTag)) {
							this.pubdate = tagString;
						}
					} else {
						Log.d(SLog, "Item TAG -- " + rssTag);
						if ("title".equals(rssTag)) {
							itemTemp.title = tagString;
						} else if ("link".equals(rssTag)) {
							itemTemp.link = tagString;
						} else if ("description".equals(rssTag)) {
							itemTemp.description = tagString;
						} else if ("category".equals(rssTag)) {
						} else if ("author".equals(rssTag)) {
							itemTemp.author = tagString;
						} else if ("pubdate".equals(rssTag)) {
							itemTemp.pubDate = tagString;
						}
					}
					Log.d(SLog, "TEXT -- " + tagString);
					break;
				case XmlPullParser.END_TAG:// �жϵ�ǰ�¼��Ƿ��Ǳ�ǩԪ�ؽ����¼�
					rssTag = parser.getName().toLowerCase();
					Log.d(SLog, "END_TAG -- " + rssTag);
					if ("channel".equals(rssTag)) {
						int i = updateChannel();
					} else if ("item".equals(rssTag)) {
						inItem = false;
						mNewsItems.add(itemTemp);
					}
					rssTag = "";
					break;
				}
				event = parser.next();// ������һ��Ԫ�ز�������Ӧ�¼�
			}// end while
			Log.d("debug", "mNewsItems.size() " + mNewsItems.size());
		} catch (XmlPullParserException e) {
			Log.d(SLog, "Error XmlPullParserException " , e);
			e.printStackTrace();
			return false;
		}// ������һ���¼�
		catch (IOException e) {
			Log.d(SLog, "Error IOException " , e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<NewsItem> mNewsItems = null;
	
//	"CREATE TABLE channel " 
//	"(cid int(11) NOT NULL,  "
//	"sid int(11) NOT NULL,  "
//	"title varchar(50) NOT NULL,  "
//	"rss varchar(200) NOT NULL, "
//	"link varchar(200) DEFAULT NULL, "
//	"istake int(1) NOT NULL, "
//	"code varchar(8) NOT NULL, "
//	"desc varchar(200) DEFAULT NULL, "
//	"pubdate varchar(20) DEFAULT NULL, "
//	"img varchar(200) DEFAULT NULL,"
//	"file varchar(200) DEFAULT NULL, " 
//	"PRIMARY KEY (cid) )";
	/**
	 * 
	 * @return �����û��޸ĵ���Ϣ sid title rss
	 */
	public int updateUserChannel() {
		Log.d(SLog, "updateUserChannel");
		db = Util.openDatabase();
		// ��������
		ContentValues cv = new ContentValues();
		cv.put("sid", this.sid);
		cv.put("title", this.title);
		cv.put("rss", this.rss);
//		cv.put("code", this.code);
//		cv.put("desc", this.desc);
		int n = db.update("channel", cv, "cid = '"+ cid +"'",null);
		db.close();
		return n;
	}

	/**
	 * �޸ı���
	 * @return
	 */
	public int updateChannelTitle() {
		db = Util.openDatabase();
		// ��������
		ContentValues cv = new ContentValues();
		cv.put("title", this.title);
		int n = db.update("channel", cv, "cid = '" + this.cid + "'",null);
		db.close();
		return n;
	}

	/**
	 * �޸Ķ���
	 * @return
	 */
	public int updateChannelTake() {
		db = Util.openDatabase();
		// ��������
		ContentValues cv = new ContentValues();
//		isTake = (isTake+1) %1 ;
		cv.put("istake", this.isTake);
		int n = db.update("channel", cv, "cid = ?", new String[] { this.cid
				+ "" });
		db.close();
		return n;
	}

	/**
	 * ����Ƶ��
	 * @return
	 */
	public int updateChannelSid() {
		db = Util.openDatabase();
		// ��������
		ContentValues cv = new ContentValues();
		cv.put("sid", this.sid);
		int n = db.update("channel", cv, "cid = ?", new String[] { this.cid
				+ "" });
		db.close();
		return n;
	}

	/**
	 * 
	 * @return
	 */
	public int updateChannel() {
		db = Util.openDatabase();
		// ��������
		ContentValues cv = new ContentValues();
//		cv.put("sid", this.sid);
		cv.put("link", this.link);
		cv.put("desc", this.desc);
		cv.put("pubdate", this.pubdate);
		cv.put("img", this.img);
		int n = db.update("channel", cv, "cid = ?", new String[] { this.cid
				+ "" });
		db.close();
		return n;
	}

	/**
	 * ɾ��Ƶ��
	 * @return
	 */
	public int deletChannel() {
		db = Util.openDatabase();
		// ɾ������
		int n = db.delete("channel", "cid = ? ", new String[] { this.cid + "" });
		db.close();
		return n;
	}

	/**
	 * ����Ƶ��
	 * @return
	 */
	public int insertChannel() {
		db = Util.openDatabase();
		Cursor c = db.rawQuery(
						"SELECT * FROM channel order by channel.cid desc limit 1",
						null);
		if (c.moveToFirst()) {
			this.cid = c.getInt(0) + 1;
		} else {
			this.cid = 10000;
		}
		String sql = "insert into channel values (?,?,?,?,?,?,?,?,?,?,?)";
		file = sid+""+cid+".xml";
		db.execSQL(sql, new Object[] { cid, sid, title, rss, link, isTake, code,desc,
				pubdate, img, file });
		db.close();
		return 0;
	}
}
