
package com.android.cc;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cc.news.ChannelListView;
import com.android.cc.sort.Sort;
import com.android.cc.sort.SortView;
import com.android.cc.util.MyToast;
import com.android.cc.util.Util;
import com.aphidmobile.flip.FlipViewController;
import com.aphidmobile.flip.FlipViewController.ViewFlipListener;
import com.app.cc.R;
import com.slidingmenu.lib.SlidingMenu;

public class MainView extends Activity {

	private Context mContext = null;

	private String SLog = "MainView";

	private TextView mSortTitle = null;
	private Sort mSort = null;
	private SlidingMenu mSlidMenu;
	private SortView mSortView = null;
	
	private Button setButton = null;

	private List<Sort> allSorts = null;

	private FlipViewController flipView;
	private FlipViewAdapter mFlipViewAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_view);

		Log.d(SLog, "onCreate start");

		fTime = System.currentTimeMillis();
		mContext = this;
		
		setButton = (Button)findViewById(R.id.set_channel);
		setButton.getScrollY();
		
		Sort.getAllSort();
		allSorts = Sort.allSorts;

		mSortTitle = (TextView) findViewById(R.id.sort_title);

		mSlidMenu = new SlidingMenu(mContext);
		mSlidMenu.setMode(SlidingMenu.LEFT);
		mSlidMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidMenu.setShadowDrawable(R.drawable.shadow);
		mSlidMenu.setBehindOffsetRes(R.dimen.behindoffset);
		mSlidMenu.setFadeDegree(0.35f);

		mSortView = new SortView(this, mSlidMenu);

		mSlidMenu.setMenu(mSortView.mSortView);

		mSlidMenu.attachToActivity(MainView.this, SlidingMenu.SLIDING_WINDOW);

		flipView = (FlipViewController) findViewById(R.id.flipView);
		
		flipView.setOnViewFlipListener(new ViewFlipListener() {
			@Override
			public void onViewFlipped(View view, int position) {
				Log.d(SLog, "onViewFlipped --------- " + position);
				setHeadTip(position);
				storePage(position);
				
			}
		});
		
		mFlipViewAdapter = new FlipViewAdapter();
		flipView.setAdapter( mFlipViewAdapter );
		
		setLastPages();
//		flipView.getSelectedView();
		
	}

	public void updatePages(boolean pageId){
		Sort.getAllSort();
		allSorts = Sort.allSorts;
		mFlipViewAdapter.notifyDataSetChanged();
		if(pageId){
			setPages(0);
		}
	}


	class FlipViewAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return allSorts.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(SLog, "flipView setAdapter getView " + position);
			View mView = convertView; 
			ChannelListView mChannelListView;
			if (convertView == null) {
				Log.d(SLog, "convertView ------- " + position);
				mChannelListView = new ChannelListView(mContext, allSorts.get(position));
				mView = mChannelListView.mChannelView;
				mView.setTag(mChannelListView);
			} else {
				mView = convertView;
				mChannelListView = (ChannelListView) mView.getTag();
			}
			mChannelListView.updateChannel(allSorts.get(position));
			return mView;
		}
	}
	
	public void setPages(int id){
		
		flipView.setSelection(id);
//		updateChannel();
		setHeadTip(id);
		storePage(id);
	}
	
	private void setLastPages(){
		SharedPreferences mPreferences = getSharedPreferences(Util.SP_APP, Context.MODE_PRIVATE);
		int selectPageID = mPreferences.getInt("selectpageid", 0);
		flipView.setSelection(selectPageID);
		setHeadTip(selectPageID);
	}
	
	private void storePage(int pageId){
		SharedPreferences mPreferences = getSharedPreferences(Util.SP_APP, Context.MODE_PRIVATE);
		Editor editor = mPreferences.edit();
		editor.putInt( "selectpageid", pageId );
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		flipView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		flipView.onPause();
	}

	private void setHeadTip(int id) {
		Log.d(SLog, "setHeadTip  " + id);
		mFlipViewAdapter.notifyDataSetChanged();
		mSort = allSorts.get(id);
		mSortTitle.setText(mSort.title);
		mSortView.setSortItemId(id);
		Util.currentSort = Sort.allSorts.get(id);
	}
	
	
	@SuppressWarnings("unused")
	private void updata(){
		allSorts = Sort.allSorts;
		mFlipViewAdapter.notifyDataSetChanged();
		setLastPages();
	}

	private long fTime = 0L;
	private long sTime = 0L;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			sTime = System.currentTimeMillis();
			if ((sTime - fTime) > 2000) {
				new MyToast(mContext, getString(R.string.mainview_exit), Toast.LENGTH_SHORT);
				fTime = sTime;
				return false;
			} else {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
