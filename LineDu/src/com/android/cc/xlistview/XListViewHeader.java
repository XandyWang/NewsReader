package com.android.cc.xlistview;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.cc.R;


public class XListViewHeader extends LinearLayout {
	
	private Context mContext = null;
	public static String spTag = "XListViewHeader";

	private LinearLayout mContainer = null;
	private ImageView mArrowImageView = null;
	private ProgressBar mProgressBar = null;
	private TextView mHintTextView = null;
	private TextView mLastUpdate = null;

	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;

	private final int ROTATE_ANIM_DURATION = 180;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		// 初始情况，设置下拉刷新view高度为0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistviewheader, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);
		
		mArrowImageView = (ImageView)findViewById(R.id.header_arrow);
		mHintTextView = (TextView)findViewById(R.id.header_text_tip);
		mLastUpdate = (TextView)findViewById(R.id.header_last_update_time);
		//刷新下拉时间
		mLastUpdate.setText(getLastUpdataTime());
		mProgressBar = (ProgressBar)findViewById(R.id.header_progressBar);
		
		mRotateUpAnim = new RotateAnimation(0.0f, 180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
		
	}
	
	public String lastTimeTag = "";
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
	private static long secend = 1000;
	private static long minute = 60 * 1000;
	private static long hour = 60 * 60 * 1000;
	private static long day = 24 * 60 * 60 * 1000;
	private String getLastUpdataTime(){	
		SharedPreferences mPreferences = mContext.getSharedPreferences(spTag, Context.MODE_PRIVATE);
		long lastTime = mPreferences.getLong(lastTimeTag, 0);
		long curTime = System.currentTimeMillis();
		String rString = "";
		if( 0l == lastTime ){
			Log.d(spTag, "lastTime " + lastTime );
			storeLastTime();
			rString = formatter.format(lastTime);
		}
		else {
			Log.d(spTag, "System.currentTimeMillis() " + System.currentTimeMillis());
			long dTime = curTime-lastTime;
			if( dTime < secend ){
				rString = "刚刚更新";
			}
			else if(secend <= dTime && dTime < minute){
				rString = (int)(dTime/(secend)) + "秒前更新";
			}
			else if(minute <= dTime && dTime < hour){
				rString = (int)(dTime/(minute))+"分钟前更新";
			}
			else if(hour <= dTime && dTime < day ){
				rString = (int)(dTime/(hour))+"小时前更新";
			}
			else {
				rString = "更新于" + formatter.format(lastTime);
			}
		}
		Log.d(spTag, "curTime " + curTime);
//		storeLastTime(curTime);
		return rString;
	}
	
	public void storeLastTime(){
		long time = System.currentTimeMillis();
		SharedPreferences mPreferences = mContext.getSharedPreferences(spTag, Context.MODE_PRIVATE);
		Editor editor = mPreferences.edit();
		editor.putLong( lastTimeTag , time );
		Log.d(spTag, "putLong " + time );
		editor.commit();
	}
	
	public void setRefreshTime(String time){
		mLastUpdate.setText(time);
	}
	
	public void setState(int state) {
		if (state == mState) return ;
		
		if (state == STATE_REFRESHING) {				// 显示进度
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {										// 显示箭头图片
			
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}
		
		switch(state){
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				//刷新下拉时间
				mLastUpdate.setText(getLastUpdataTime());
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText(R.string.xlistview_header_hint_normal); //下拉刷新
			
			break;
		case STATE_READY:
			if(STATE_NORMAL == mState){
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(R.string.xlistview_header_hint_ready);//松开刷新数据
			}
			
//			if (mState != STATE_READY) {
//				mArrowImageView.clearAnimation();
//				mArrowImageView.startAnimation(mRotateUpAnim);
//				mHintTextView.setText(R.string.xlistview_header_hint_ready);//松开刷新数据
//			}
			
			break;
		case STATE_REFRESHING:
			mHintTextView.setText(R.string.xlistview_header_hint_loading);
			break;
			default:
		}
		
		mState = state;
	}
	
	public void setVisiableHeight(int height) {
		if (height < 0) height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}
}
