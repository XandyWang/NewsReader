package com.android.cc.news;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cc.sort.Sort;
import com.android.cc.util.MyToast;
import com.android.cc.util.Util;
import com.android.cc.xlistview.XListView;
import com.android.cc.xlistview.XListView.IXListViewListener;
import com.app.cc.R;

public class ChannelListView implements OnItemClickListener,
		OnItemLongClickListener, IXListViewListener {
	
	private Context mContext = null;
	public View mChannelView = null; 				//频道界面的view

	private Sort mSort = null;						//该频道的分类
	private XListView mChannelListView = null;		//频道下拉刷新列表
//	private List<Channel> allChannels = null;		//所有频道数据源
	private List<Channel> takeChannels = null;		//订阅频道数据源
	private Dialog mDialog = null;					//弹出解析对话框
	
	private String SLog = "ChannelListView";
	
	public ChannelListView(Context context, Sort sort) {
		mContext = context;
		mSort = sort;
		if(null == takeChannels){
			takeChannels = new ArrayList<Channel>();
		}
		Channel.getTakeChannel(takeChannels, mSort);
		
		mChannelView = LayoutInflater.from(mContext).inflate(
				R.layout.channel_list_view, null);
		mChannelListView = (XListView) mChannelView
				.findViewById(R.id.Channel_ListView);
		mChannelListView.setUpdateTag(mSort.sid+"s");
		mChannelListView.setRefreshTime(getLastUpdataTime());
		mChannelListView.setAdapter(mAdapter);
		mChannelListView.setOnItemClickListener(this);
		mChannelListView.setOnItemLongClickListener(this);
		mChannelListView.setXListViewListener(this);
	}

	Intent itemlist = new Intent();
	private Channel sChannel = null;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(SLog, "select sort is " + mSort.title);
		sChannel = takeChannels.get(position - 1);
		if (null == mDialog) {
			mDialog = new Dialog(mContext, R.style.dialog);
			mDialog.setContentView(R.layout.progress_dialog);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!mDialog.isShowing()) {
					Log.d(SLog, "SHOW_PROGRESS_DIALOG");
					mHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
				}
				Util.checkNetwork(mContext);
				if (Util.isConnect) {
					if (sChannel.downChannel2SD()) {
						if (sChannel.ReadChannel()) {
							Util.currentChannel = sChannel;
							mHandler.sendEmptyMessage(SHOW_NEWITEMS);
						} else {
							mHandler.sendEmptyMessage(READCHANNELERROR);
						}
					} else {
						mHandler.sendEmptyMessage(DOWNLOADERROR);
					}
				} else {
					if (sChannel.isDownLoad()) {
						if (sChannel.ReadChannel()) {
							Util.currentChannel = sChannel;
							mHandler.sendEmptyMessage(SHOW_NEWITEMS);
						} else {
							mHandler.sendEmptyMessage(READCHANNELERROR);
						}
					}
					else {
						mHandler.sendEmptyMessage(NOCONNECT);
					}
				}
			}
		}).start();
	}

//	private MenuDialog mMenuDialog = null;

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		sChannel = takeChannels.get(position - 1);
		Util.currentChannel = sChannel;
		MenuDialog mMenuDialog;
//		if (null == mMenuDialog) {
			mMenuDialog = new MenuDialog(mContext);
			mMenuDialog.setChannelListView(this);
//		}
//		mMenuDialog.setChannel(takeChannels.get(position - 1));
		mMenuDialog.setChannel( sChannel );
		mMenuDialog.show();
		return true;
	}
	
	public void updateTake(){
		Sort mSort = Util.currentSort;
		
	}

	private static final int SHOW_PROGRESS_DIALOG = 10000;
	private static final int SHOW_NEWITEMS = 10001;
	private static final int ERROR = 10002;
	private static final int NOSDFILE = 10003;
	private static final int DOWNLOADERROR = 10004;
	private static final int READCHANNELERROR = 10005;
	private static final int NOCONNECT = 10006;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PROGRESS_DIALOG:
				mDialog.show();
				break;
			case SHOW_NEWITEMS:
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				itemlist.setClass(mContext, NewsItemListView.class);
				mContext.startActivity(itemlist);
				break;
			case NOSDFILE:
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				new MyToast(mContext, "未找到文件", Toast.LENGTH_SHORT);
				break;
			case DOWNLOADERROR:
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				new MyToast(mContext, "下载文件时出错", Toast.LENGTH_SHORT);
				break;
			case READCHANNELERROR:
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				new MyToast(mContext, "读取文件发生错误", Toast.LENGTH_SHORT);
				break;
			case NOCONNECT:
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				new MyToast(mContext, "未连接网络", Toast.LENGTH_SHORT);
				break;
			case ERROR:
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				new MyToast(mContext, "发现未知错误", Toast.LENGTH_SHORT);
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void setSort(Sort mSort) {
		this.mSort = mSort;
	}

	//外部调用  更新了分类
	public void updateChannel(Sort sort) {
		setSort(sort);
		Channel.getTakeChannel(takeChannels, mSort);
		mAdapter.notifyDataSetChanged();
	}

	//下拉刷新时  调用 不更新分类
	public void updateChannel() {
		Toast.makeText(mContext, "开始刷新"+mSort.title, Toast.LENGTH_SHORT).show();
		Channel.getTakeChannel(takeChannels, mSort);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
//				mChannelListView.setRefreshTime(getLastUpdataTime());
				
				updateChannel();
				mChannelListView.stopRefresh();
			}
		}, 500);
	}
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
	private static float secend = 1000f;
	private static float minute = 60 * 1000f;
	private static float hour = 60 * 60 * 1000f;
	private static float day = 24 * 60 * 60 * 1000f;
	private String getLastUpdataTime(){	
		SharedPreferences mPreferences = mContext.getSharedPreferences(Util.SP_APP, Context.MODE_PRIVATE);
		long lastTime = mPreferences.getLong(mSort.sid+"", 0);
		long curTime = System.currentTimeMillis();;
		String rString = "";
		if( 0l == lastTime ){
			Log.d(SLog, "lastTime " + lastTime );
			rString = formatter.format(lastTime);
		}
		else {
			Log.d(SLog, "System.currentTimeMillis() " + System.currentTimeMillis());
			long dTime = curTime-lastTime;
			if( dTime < secend ){
				rString = "刚刚更新";
			}
			else if(secend <= dTime && dTime < minute){
				rString = dTime/(secend) + "秒前更新";
			}
			else if(minute <= dTime && dTime < hour){
				rString = dTime/(minute)+"分钟前更新";
			}
			else if(hour <= dTime && dTime < day ){
				rString = dTime/(hour)+"小时前更新";
			}
			else {
				rString = "更新于" + formatter.format(lastTime);
			}
		}
		Log.d(SLog, "curTime " + curTime);
		storeLastTime(curTime);
		return rString;
	}
	
	private void storeLastTime(long time){
		SharedPreferences mPreferences = mContext.getSharedPreferences(Util.SP_APP, Context.MODE_PRIVATE);
		Editor editor = mPreferences.edit();
		editor.putLong( mSort.sid+"" , time );
		Log.d(SLog, "putLong " + time );
		editor.commit();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}

	private ChannelListAdapter mAdapter = new ChannelListAdapter();

	class ChannelListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return takeChannels.size();
		}

		@Override
		public Object getItem(int position) {
			return takeChannels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		// -------------------------------------------------------------------------------
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				mViewHandler = new ViewHandler();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.channel_list_item_view, null);
				mViewHandler.firstWord = (TextView) convertView
						.findViewById(R.id.first_channel_word);
				mViewHandler.cTitle = (TextView) convertView
						.findViewById(R.id.other_channel_word);
				mViewHandler.cDesc = (TextView) convertView
						.findViewById(R.id.channel_desc);
				mViewHandler.cPubDate = (TextView) convertView
						.findViewById(R.id.channel_pubdate);
				convertView.setTag(mViewHandler);
			} else {
				mViewHandler = (ViewHandler) convertView.getTag();
			}
			temp = takeChannels.get(position);
			mViewHandler.firstWord.setText(temp.title.substring(0, 1));
			mViewHandler.cTitle.setText(temp.title.substring(1));
			mViewHandler.cDesc.setText(temp.desc);
			mViewHandler.cPubDate.setText(temp.pubdate);
			return convertView;
		}

		private Channel temp;
		private ViewHandler mViewHandler = null;

		class ViewHandler {
			TextView firstWord = null;
			TextView cTitle = null;
			TextView cDesc = null;
			TextView cPubDate = null;
		}
	}
}