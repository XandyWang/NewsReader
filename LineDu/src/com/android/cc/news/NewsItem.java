/**
 * 该类为频道下的子项，封装了子项的一些内容
 * @author wangxiaoyang
 */

package com.android.cc.news;

public class NewsItem {
	public String title = "";			//条目的标题
	public String link = "";			//条目的连接
	public String description = "";		//条目的描述
	public String pubDate = "";			//发布的时间
	public String author = "";			//作者
	
	public NewsItem(){
		
	}
	
	public NewsItem(String title,String link,String description){
		this.title = title;
		this.link = link;
		this.description = description;
	}
	
}
