/**
 * ����ΪƵ���µ������װ�������һЩ����
 * @author wangxiaoyang
 */

package com.android.cc.news;

public class NewsItem {
	public String title = "";			//��Ŀ�ı���
	public String link = "";			//��Ŀ������
	public String description = "";		//��Ŀ������
	public String pubDate = "";			//������ʱ��
	public String author = "";			//����
	
	public NewsItem(){
		
	}
	
	public NewsItem(String title,String link,String description){
		this.title = title;
		this.link = link;
		this.description = description;
	}
	
}
