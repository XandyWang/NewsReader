package com.android.cc.news;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.android.cc.util.Util;
import com.app.cc.R;

public class NewsItemWebView extends Activity implements OnClickListener{
	
	private Context mContext = null;
	private Channel mChannel = Util.currentChannel;
	private NewsItem mItem = Util.currentItem;
	private TextView titles = null;
	
	
	private Button lastNew = null;
	private Button nextNew = null;
	private Button refreshNew = null;
	private Button shareNew = null;
	
	
	private ProgressWebView webview;

	private static String baidu = "http://gate.baidu.com/tc?from=opentc&src=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.newsitem_web_view);
		
		lastNew = (Button)findViewById(R.id.last_new);
		nextNew = (Button)findViewById(R.id.next_new);
		refreshNew = (Button)findViewById(R.id.refresh_new);
		shareNew = (Button)findViewById(R.id.share_new);
		lastNew.setOnClickListener(this);
		nextNew.setOnClickListener(this);
		refreshNew.setOnClickListener(this);
		shareNew.setOnClickListener(this);
		
		
		webview = (ProgressWebView) findViewById(R.id.web);
		webview.getSettings().setJavaScriptEnabled(true);
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        webview.loadUrl(baidu+mItem.link);
		
		super.onCreate(savedInstanceState);
		
	}
	
	/**
     * Creates a sharing {@link Intent}.
     *
     * @return The sharing intent.
     */
    private void createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mItem.title+" \n "+mItem.link);
        startActivity(Intent.createChooser(shareIntent, "分享" ));
    }
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.last_new:
			nextNew.setClickable(true);
			if(Util.currentItemID == 0){
				lastNew.setClickable(false);
				break;
			}
			Util.currentItemID--;
			mItem = mChannel.mNewsItems.get(Util.currentItemID);
			webview.loadUrl(baidu+mItem.link);
			break;
		case R.id.next_new:
			lastNew.setClickable(true);
			if(Util.currentItemID == mChannel.mNewsItems.size()-1 ){
				nextNew.setClickable(false);
				break;
			}
			Util.currentItemID++;
			mItem = mChannel.mNewsItems.get(Util.currentItemID);
			webview.loadUrl(baidu+mItem.link);
			break;
		case R.id.refresh_new:
			webview.loadUrl(baidu+mItem.link);
			break;
		case R.id.share_new:
			createShareIntent();
			break;
		}
		
	}





	//Web视图 
    public class HelloWebViewClient extends WebViewClient { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url); 
            return true; 
        } 
    } 
    
    private static final int ERROR = 10000;
    private static final int NOCONNECT = 10001;
    private static final int SHOW_DIALOG = 10002;
    private static final int DISMISS_DIALOG = 10003;
    
//    private void showCrouton(String croutonString, Style croutonStyle){
//    	Crouton.makeText(this, croutonString, croutonStyle).show();
//    }
    
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		switch(msg.what){
    		case ERROR:
//    			showCrouton("发现未知错误",Style.ALERT);
    			break;
    		case NOCONNECT:
//    			showCrouton("未连接网络",Style.ALERT);
    			break;
    		case SHOW_DIALOG:
//    			showCrouton("加载",Style.ALERT);
    			break;
    		case DISMISS_DIALOG:
    			break;
    		}
    		super.handleMessage(msg);
    	}
    };
    
    
    private boolean AnalysisHTML(){
    	String rssTag = "";
		String text = "";
		try {
			URL url = new URL("http://gate.baidu.com/tc?from=opentc&src="+mItem.link);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.connect();
			InputStream inputStream = urlConn.getInputStream();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inputStream, "utf-8");
			int event = parser.getEventType();
			while (XmlPullParser.END_DOCUMENT != event) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
					System.out.println("START_DOCUMENT---------------------------------");
					break;
				case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
					rssTag = parser.getName().toLowerCase();
					System.out.println("START_TAG--------------" + rssTag);
					if("title".equals(rssTag)){
						String temp = parser.nextText();
						int i = temp.indexOf('|');
						titles.setText(temp);
						System.out.println("Title------------------" + titles.getText().toString());
//						sc = true;
					}
					if("p".equals(rssTag)){
						text += parser.nextText()+"\n";
						System.out.println("p----------------------" + text);
					}
					if("img".equals(rssTag)){
//						text = parser.getAttributeValue(0);
//						System.out.println("img--------------------" + text);
					}
					break;
				case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
					rssTag = parser.getName().toLowerCase();
					System.out.println("END_TAG----------------" + rssTag);
					break;
				}
				event = parser.next();// 进入下一个元素并触发相应事件
			}// end while
//			newsContan.setText(text);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return true;
    }
    
    
}
