package com.android.cc.sort;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.cc.MainView;
import com.android.cc.util.Util;
import com.app.cc.R;
import com.slidingmenu.lib.SlidingMenu;

public class SortView implements OnItemClickListener, OnItemLongClickListener, OnDismissListener {

	private Context mContext = null;
	private SlidingMenu sMenu = null;
	public View mSortView = null;
	private ListView SortList = null;
	private SortAdapter mSortAdapter = null;

	private MainView mainView = null;

	public SortView(MainView view, SlidingMenu menu) {
		mainView = view;
		mContext = mainView;
		sMenu = menu;
		mSortView = LayoutInflater.from(mContext).inflate(R.layout.left_menu,
				null);
		SortList = (ListView) mSortView.findViewById(R.id.sort_list_view);
		mSortAdapter = new SortAdapter();
		TextView footView = new TextView(mContext);
		footView.setText("+");
		footView.setTextSize((float) 20.0);
		footView.setHeight(50);
		footView.setGravity(Gravity.CENTER);
		SortList.addFooterView(footView);

		SortList.setAdapter(mSortAdapter);
		SortList.setOnItemClickListener(this);
		SortList.setOnItemLongClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (Sort.allSorts.size() != position) {
			mSortAdapter.notifyChoose(position);
			sMenu.toggle();
			mainView.setPages(position);
		} else {
			SortMenuDialog mDialog = new SortMenuDialog(new Sort());
			mDialog.setOnDismissListener(this);
			mDialog.show();
		}
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		mSortAdapter.updata();
		mainView.updatePages(isEdit);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (Sort.allSorts.size() != position) {
			SortMenuDialog mDialog = new SortMenuDialog(Sort.allSorts.get(position));
			mDialog.setOnDismissListener(this);
			mDialog.show();
		}
		return true;
	}

	public void setSortItemId(int position) {
		mSortAdapter.notifyChoose(position);
	}

	class SortAdapter extends BaseAdapter {

		public int choose = 0;
		private List<Sort> allSorts = null;

		public SortAdapter() {
			Sort.getAllSort();
			allSorts = Sort.allSorts;
		}

		public void notifyChoose(int mChoose) {
			Sort.getAllSort();
			allSorts = Sort.allSorts;
			this.choose = mChoose;
			this.notifyDataSetChanged();
		}
		
		public void updata(){
			Sort.getAllSort();
			allSorts = Sort.allSorts;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.allSorts.size();
		}

		@Override
		public Object getItem(int position) {
			return this.allSorts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.left_menu_item, null);
				mHandle = new ViewHandle();
				mHandle.tip = (TextView) convertView
						.findViewById(R.id.select_tip);
				mHandle.icon = (ImageView) convertView
						.findViewById(R.id.channel_icon);
				mHandle.title = (TextView) convertView
						.findViewById(R.id.channel_title);
				convertView.setTag(mHandle);
			} else {
				mHandle = (ViewHandle) convertView.getTag();
			}
			mHandle.setUnChooseView();
			// convertView.setBackgroundColor(0xFFD3D3D3);
			if (this.choose == position) {
				mHandle.setChooseView();
				// convertView.setBackgroundColor(0xFFFFF8DC);
			}
			mHandle.title.setText(allSorts.get(position).title);
			return convertView;
		}

		private ViewHandle mHandle = null;

		class ViewHandle {
			TextView tip = null;
			ImageView icon = null;
			TextView title = null;

			public void setChooseView() {
				this.tip.setVisibility(View.VISIBLE);
				// this.title.setTextColor(0xffFF8C00);
			}

			public void setUnChooseView() {
				this.tip.setVisibility(View.INVISIBLE);
				// this.title.setTextColor(0xff000000);
			}
		}
	}
	
	public boolean isEdit = false;
	class SortMenuDialog extends Dialog {
		
		private Sort msort = null;
		
		private AutoCompleteTextView sortTitle = null;
		private Button sortSure = null;
		private Button sortCancel = null;
		private TextView sTitle = null;
		
		public SortMenuDialog(Sort sort) {
			super(mainView, R.style.ChannelMenu_Dialog);
			msort = sort;
			initDialog();
		}
		
		private void initDialog(){
			setContentView(R.layout.sort_menu_dialog);
			
			sortTitle = (AutoCompleteTextView)findViewById(R.id.sort_menu_title_edit);
			sTitle = (TextView) findViewById(R.id.sort_title);
			ButtonClick click = new ButtonClick();
			sortSure = (Button) findViewById(R.id.sort_menu_sure);
			sortSure.setOnClickListener(click);
			sortCancel = (Button)findViewById(R.id.sort_menu_cancel);
			sortCancel.setOnClickListener(click);
			
			if(msort.title.equals("")){
				isEdit = false;
				sTitle.setText("新建分类");
				sortTitle.setHint("输入新的分类");
				sortCancel.setText("返回");
			}
			else {
				isEdit = true;
				sTitle.setText("修改分类");
				sortTitle.setText(msort.title);
				sortCancel.setText("删除");
			}
			
		}
		
		class ButtonClick implements View.OnClickListener{

			@Override
			public void onClick(View v) {
				if( R.id.sort_menu_sure == v.getId() ){
					String temp = sortTitle.getText().toString();
					if( temp.equals("") ){
						Util.showToast(mContext, "分类名称不能为空");
						return ;
					}
					else{
						msort.title = temp;
						if(isEdit){
							msort.updateSort();
						}
						else {
							msort.insertSort();
						}
					}
				}
				else if( R.id.sort_menu_cancel == v.getId() ){
					if(isEdit){
						msort.deletSort();
						mSortAdapter.choose = 0;
					}
				}
//				mSortAdapter.updata();
				SortMenuDialog.this.dismiss();
			}
		}
	}
	
}
