package com.android.cc.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cc.R;

public class MyToast {
	private TextView text = null;
	private Toast mToast = null;
	public MyToast(Context context,String string,int dtime){
		LayoutInflater inflater = LayoutInflater.from(context);
	    View view = inflater.inflate(R.layout.my_toast, null);
	    text = (TextView)view.findViewById(R.id.toast_text);
	    text.setText(string);
	    mToast = new Toast(context);
	    mToast.setView(view);
	    mToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, 80);
	    mToast.setDuration(dtime);
	    mToast.show();
	}
}
