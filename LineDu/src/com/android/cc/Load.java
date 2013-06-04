package com.android.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.cc.util.MyToast;
import com.android.cc.util.Util;
import com.app.cc.R;

public class Load extends Activity {
	private Context mContext = null;
	
	private String SLog = "Load";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_load);
		
		mContext = this;
		
		Log.d(SLog, "onCreate");
		
		ImageView logo = (ImageView)findViewById(R.id.covers);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.load_anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				Log.d(SLog, "onAnimationStart");
				new Thread(new Runnable() {
					@Override
					public void run() {
						Util.checkNetwork(mContext);
						Log.d(SLog, "new Thread new Runnable checkNetwork ");
					}
				}).start();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Log.d(SLog, "onAnimationEnd");
				//¼ì²âÄÚ´æ¿¨
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					new MyToast(mContext, getString(R.string.load_nosdcard), Toast.LENGTH_LONG);
					finish();
				}
				if(!Util.importDB(mContext) ){
					new MyToast(mContext, getString(R.string.load_database_error), Toast.LENGTH_LONG);
					finish();
				}
				if(!Util.isConnect){
					new MyToast(mContext, getString(R.string.load_noconnect), Toast.LENGTH_LONG);
				}
				Intent mMainView = new Intent(mContext, MainView.class);
				startActivity(mMainView);
				overridePendingTransition(R.anim.left_enter, R.anim.left_exit);
				finish();
			}
		});
		logo.setAnimation(anim );
		Log.d(SLog, "logo.setAnimation");
	}
}
