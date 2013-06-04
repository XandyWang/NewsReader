package com.android.cc.news;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cc.util.MyToast;
import com.android.cc.util.Util;
import com.android.cc.xlistview.XListView;
import com.android.cc.xlistview.XListView.IXListViewListener;
import com.app.cc.R;

public class NewsItemListView extends Activity implements OnItemClickListener,IXListViewListener {
	
	private Context mContext;
	
	private XListView mNewsItemListView;						//下拉刷新列表
	private Channel channel = Util.currentChannel;				//当前频道
	private TextView cTitle = null;								//频道标题
	private List<NewsItem> mNewsItems = channel.mNewsItems;		//频道里的item
	
	private String SLog = "NewsItemListView";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_item_view);
		mContext = this;
		mNewsItemListView = (XListView)findViewById(R.id.NewsItem_ListView);
		mAdapter = new NewsListViewAdapter();
		mNewsItemListView.setUpdateTag(channel.cid+"c");
		mNewsItemListView.setAdapter(mAdapter);
		mNewsItemListView.setOnItemClickListener(this);
		mNewsItemListView.setXListViewListener(this);
		cTitle = (TextView)findViewById(R.id.news_channel_title);
		cTitle.setText(channel.title);
	}
	
	private static final int ERROR = 10001;
	private static final int READCHANNELERROR = 10002;
	private static final int DOWNLOADERROR = 10003;
	private static final int STOP_REFRESH = 10005;
	private static final int NOSDFILE = 10006;
	private static final int NOCONNECT = 10007;
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case STOP_REFRESH:
					mAdapter.notifyDataSetChanged();
					mNewsItemListView.stopRefresh();
					break;
				case NOSDFILE:
					new MyToast(mContext, "未找到文件", Toast.LENGTH_SHORT);
					break;
				case DOWNLOADERROR:
					new MyToast(mContext, "下载文件时出错", Toast.LENGTH_SHORT);
					break;
				case READCHANNELERROR:
					new MyToast(mContext, "读取文件发生错误", Toast.LENGTH_SHORT);
					break;
				case NOCONNECT:
					new MyToast(mContext, "未连接网络", Toast.LENGTH_SHORT);
					break;
				case ERROR:
					new MyToast(mContext, "发现未知错误", Toast.LENGTH_SHORT);
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Util.currentItem = mNewsItems.get(position-1);
		Util.currentItemID = position-1;
		Util.checkNetwork(mContext);
		if (Util.isConnect) {
			goToItem();
		}
		else{
			new MyToast(mContext, "未连接网络", Toast.LENGTH_SHORT);
		}
	}
	
	private void goToItem(){
		startActivity(new Intent(mContext, NewsItemWebView.class));
	}
	
	@Override
	public void onRefresh() {
		Log.d(SLog, "onRefresh----------------------" );
		new Thread(new Runnable() {
			@Override
			public void run() {
				Util.checkNetwork(mContext);
				if (Util.isConnect) {
					if (channel.downChannel2SD()) {
						if (channel.ReadChannel()) {
							//更新拉下列表
							mHandler.sendEmptyMessage(STOP_REFRESH);
						} else {
							mHandler.sendEmptyMessage(READCHANNELERROR);
						}
					} else {
						mHandler.sendEmptyMessage(DOWNLOADERROR);
					}
				} else {
					if (channel.isDownLoad()) {
						if (channel.ReadChannel()) {
							//更新拉下列表
							mHandler.sendEmptyMessage(STOP_REFRESH);
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
	
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}
	
	
	private NewsListViewAdapter mAdapter ;
	class NewsListViewAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mNewsItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mNewsItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(null == convertView){
				convertView = LayoutInflater.from(mContext)
						.inflate(R.layout.newsitem_list_item_view, null);
				vHandler = new ViewHandler();
				vHandler.title = (TextView)convertView.findViewById(R.id.item_title);
//				vHandler.desc = (TextView)convertView.findViewById(R.id.item_desc);
				vHandler.pubData = (TextView)convertView.findViewById(R.id.item_pubtime);
				convertView.setTag(vHandler);
			}
			else{
				vHandler = (ViewHandler)convertView.getTag();
			}
			temp = mNewsItems.get(position);
			vHandler.setView(temp);
//			vHandler.title.setText(Html.fromHtml(temp.title));
//			if(temp.description.equals("") ||null == temp.description ){
//				vHandler.desc.setText(temp.description);
//			}
//			else{
//				vHandler.desc.setVisibility(View.GONE);
//			}
//			vHandler.pubData.setText(temp.pubDate);
			return convertView;
		}
		
		NewsItem temp = null;
		ViewHandler vHandler = null;
		
		class ViewHandler {
			TextView title = null;
//			TextView desc = null;
			TextView pubData = null;
			
			private void setView(NewsItem  mNewsItem){
				title.setText(mNewsItem.title);
				pubData.setText(mNewsItem.pubDate);
			}
		}
	}
}
