package com.android.cc.sort;

import java.util.ArrayList;
import java.util.List;

import com.android.cc.util.Util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Sort {
	
	public static SQLiteDatabase db = null;
	public int sid = 10000;					//�����ID
	public String title = "";				//��������
//	public int ccount = 0;					//�����µ�Ƶ����
//	private List<NewsItem> mItems = null;	//�����µ�����
	public int sIndex = 0;
	
	public Sort(String stitle){
		title = stitle;
	}
	public Sort(){
		
	}
	
	public static List<Sort> allSorts = null;
	
	public static void getAllSort(){
		if(null == allSorts){
			allSorts = new ArrayList<Sort>();
		}
		allSorts.clear();
		int count = 0;
		db = Util.openDatabase();
		Cursor c = db.rawQuery("SELECT * FROM sort order by sort.sid asc ",null );
		Sort temp;
		while(c.moveToNext()){
			temp = new Sort();
			temp.sid = c.getInt(0);
			temp.title = c.getString(1);
			temp.sIndex = count++;
			allSorts.add(temp);
		}
		db.close();
	}
	
	public static List<Sort> getAllSortList(){
		List<Sort> allSorts = new ArrayList<Sort>();
		allSorts.clear();
		db = Util.openDatabase();
		Cursor c = db.rawQuery("SELECT * FROM sort order by sort.sid asc ",null );
		Sort temp;
		while(c.moveToNext()){
			temp = new Sort();
			temp.sid = c.getInt(0);
			temp.title = c.getString(1);
			allSorts.add(temp);
		}
		db.close();
		return allSorts;
	}
	
	public int insertSort(){
		db = Util.openDatabase();
		Cursor c = db.rawQuery("SELECT * FROM sort order by sort.sid desc limit 1",null );
		if(c.moveToFirst()){
			sid = c.getInt(0)+1;
		}
		else {
			sid = 10000;
		}
		String sql = "insert into sort values (?,?)";
		db.execSQL(sql,new Object[]{sid,title});
		db.close();
		return 0;
	}
	
	public int deletSort(){
		db = Util.openDatabase();
		//ɾ������  
        int n = db.delete("sort", "sid = ? ", new String[]{ sid+"" } );  
        db.close();
		return n; 
	}
	
	public int updateSort(){
		db = Util.openDatabase();
		//��������  
		ContentValues cv = new ContentValues();   
        cv.put("title", title); 
		int n = db.update("sort", cv, "sid = ?", new String[]{sid+""});  
		db.close();
		return n; 
	}
	
}
