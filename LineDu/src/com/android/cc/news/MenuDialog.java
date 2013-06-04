package com.android.cc.news;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cc.sort.Sort;
import com.android.cc.util.Util;
import com.app.cc.R;

public class MenuDialog extends Dialog {

	private Channel mChannel = null;
	private ChannelListView mChannelListView = null; // 频道下拉刷新列表

	private TextView ctitle = null;

	private Button disTake = null;
	private Button ediet = null;
	private Button delet = null;
	private Button add = null;
	private Button maneger = null;

	private Button editSort = null;
	private RadioGroup sortRadio = null;
	private AutoCompleteTextView editTitle = null;
	private AutoCompleteTextView editRss = null;
	private Button editsure = null;
	private Button editcancel = null;

	private Animation menuLeftEnter = null;
	private Animation menuRightEnter = null;

	private int selectId = 0;

	private ListView AllChannelList = null;
	private ScrollView editViewsEdit = null;
	private LinearLayout addExtra = null;

	private MenuDialog mDialog = null;

	private Context mContext = null;
	private Sort mSort = null;
	private RelativeLayout menuButtons = null;
	private LinearLayout editViews = null;

	private RadioGroup codeStype = null;
	private Button setCode = null;

	public MenuDialog(Context context) {
		super(context, R.style.ChannelMenu_Dialog);
		initDialog(context);
	}

	public void initDialog(Context context) {
		mContext = context;
		mDialog = this;
		setContentView(R.layout.channel_long_press_menu);
		
		mSort = Util.currentSort;
		
		menuButtons = (RelativeLayout) findViewById(R.id.button_views);
		editViews = (LinearLayout) findViewById(R.id.edit_views);

		ctitle = (TextView) findViewById(R.id.channel_menu_title);
		disTake = (Button) findViewById(R.id.channel_menu_take);
		ediet = (Button) findViewById(R.id.channel_menu_edite);
		delet = (Button) findViewById(R.id.channel_menu_delet);
		add = (Button) findViewById(R.id.channel_menu_add);
		maneger = (Button) findViewById(R.id.channel_menu_maneger);

		editSort = (Button) findViewById(R.id.edite_sort_button);
		editSort.setText(Util.currentSort.title);

		sortRadio = (RadioGroup) findViewById(R.id.edit_sort_radio_group);
		codeStype = (RadioGroup) findViewById(R.id.edit_code_radio_group);
		setCode = (Button) findViewById(R.id.edite_code_button);

		editRss = (AutoCompleteTextView) findViewById(R.id.edit_rss_text);
		editTitle = (AutoCompleteTextView) findViewById(R.id.edit_title_text);
		editsure = (Button) findViewById(R.id.menu_edit_sure);
		editcancel = (Button) findViewById(R.id.menu_edit_cancel);

		AllChannelList = (ListView) findViewById(R.id.all_channels_list);
		editViewsEdit = (ScrollView) findViewById(R.id.edit_views_edit);
		addExtra = (LinearLayout) findViewById(R.id.add_extra);

		ButtonClick click = new ButtonClick();

		disTake.setOnClickListener(click);
		ediet.setOnClickListener(click);
		delet.setOnClickListener(click);
		add.setOnClickListener(click);
		maneger.setOnClickListener(click);

		editSort.setOnClickListener(click);
		editsure.setOnClickListener(click);
		editcancel.setOnClickListener(click);
		setCode.setOnClickListener(click);
		
		menuLeftEnter = AnimationUtils.loadAnimation(mContext,
				R.anim.menu_left_enter);
		menuRightEnter = AnimationUtils.loadAnimation(mContext,
				R.anim.menu_right_enter);


		View.OnClickListener codeRadioClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				codeStype.setVisibility(View.GONE);
				setCode.setText(((RadioButton) v).getText());
				setCode.setVisibility(View.VISIBLE);
				setCode.startAnimation(menuLeftEnter);
			}
		};

		for (int i = codeStype.getChildCount() - 1; i >= 0; i--) {
			((RadioButton) codeStype.getChildAt(i))
					.setOnClickListener(codeRadioClick);
		}

		setCode.setText(((RadioButton) codeStype.getChildAt(0)).getText());

	}
	private boolean isInitEdit = false;
	private void initEdit(){
		if(isInitEdit) return ;
		isInitEdit = true;
		LinearLayout.LayoutParams mParams = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Resources resource = (Resources) getContext().getResources();
		ColorStateList csl = (ColorStateList) resource
				.getColorStateList(R.color.color_radiobutton);
		View.OnClickListener radioClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectId = (Integer) (v.getTag());
				sortRadio.setVisibility(View.GONE);
				// sortRadio.startAnimation(menuLeftEnter);
				Sort mSort = Sort.allSorts.get(selectId);
				mChannel.sid = mSort.sid;
				editSort.setText(mSort.title);
				editSort.setVisibility(View.VISIBLE);
				editSort.startAnimation(menuLeftEnter);
			}
		};

		int count = Sort.allSorts.size();
		for (int i = 0; i < count; i++) {
			addRadioButton(mParams, radioClick, csl,
					Sort.allSorts.get(i).title, i);
		}

		((RadioButton) sortRadio.getChildAt(Util.currentSort.sIndex))
				.setChecked(true);
	}
	

	private void addRadioButton(LayoutParams params,
			View.OnClickListener listener, ColorStateList csl, String text,
			int i) {
		RadioButton mRadioButton = new RadioButton(mContext);
		mRadioButton.setBackgroundResource(R.drawable.radio_button);
		mRadioButton.setButtonDrawable(R.drawable.radio_button);
		mRadioButton.setLayoutParams(params);
		mRadioButton.setTextColor(csl);
		mRadioButton.setOnClickListener(listener);
		mRadioButton.setText(text);
		mRadioButton.setTag(i);
		sortRadio.addView(mRadioButton);
	}

	public void setChannel(Channel c) {
		mChannel = c;
		ctitle.setText(mChannel.title);
	}

	public ChannelListView getChannelListView() {
		return mChannelListView;
	}

	public void setChannelListView(ChannelListView mChannelListView) {
		this.mChannelListView = mChannelListView;
	}
	
	class ACAdapter extends BaseAdapter{
		
		private List<Channel> allChannels = null;
		
		public ACAdapter(){
			allChannels = Channel.getAllChannel(mSort.sid);
		}
		
		public void update(Channel channel , int position){
			channel.isTake = (channel.isTake+1)%2;
			channel.updateChannelTake();
			allChannels.set(position, channel);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return allChannels.size();
		}

		@Override
		public Object getItem(int position) {
			return allChannels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CheckedTextView mTextView = null;
			if(null == convertView){
				convertView = LayoutInflater.from(mContext)
						.inflate(R.layout.all_channel_list_item, null); 
				mTextView = (CheckedTextView)convertView.findViewById(R.id.cktext);
				mTextView.setCheckMarkDrawable(R.drawable.btn_check_holo_light);
				convertView.setTag(mTextView);
			}
			else{
				mTextView = (CheckedTextView)convertView.getTag();
			}
			Channel temp = allChannels.get(position);
			mTextView.setText( temp.title );
//			mTextView.setChecked(false);
			
			if(temp.isTake == 1){
				AllChannelList.setItemChecked(position, true);
				mTextView.setChecked(true);
//				mTextView.toggle();
//				mTextView.isChecked();
				Log.d("isTake",temp.title+" ------true---"+mTextView.isChecked());
			}
			else{
				AllChannelList.setItemChecked(position, false);
				mTextView.setChecked(false);
				Log.d("isTake",temp.title+" ------false---"+mTextView.isChecked());
			}
			return convertView;
		}
		
	}
	

	private static final int isEdit = 10;
	private static final int isAdd = 11;
	private static final int isManeger = 12;

	private static int buttonStyle = 0;

	class ButtonClick implements android.view.View.OnClickListener {
		public void onClick(View v) {
			if (R.id.channel_menu_take == v.getId()) {
				Channel mChannel = Util.currentChannel;
				mChannel.isTake = (mChannel.isTake+1)%2;
				mChannel.updateChannelTake();
				mChannelListView.updateChannel();

			} else if (R.id.channel_menu_edite == v.getId()) {
				buttonStyle = isEdit;
				menuButtons.setVisibility(View.GONE);
				editViews.setVisibility(View.VISIBLE);
				editViewsEdit.setVisibility(View.VISIBLE);
				editSort.setVisibility(View.VISIBLE);
				sortRadio.setVisibility(View.GONE);
				initEdit();
				addExtra.setVisibility(View.GONE);
				editTitle.setText(mChannel.title);
				AllChannelList.setVisibility(View.GONE);
				editRss.setText(mChannel.rss);
				editViews.startAnimation(menuLeftEnter);
				return;

			} else if (R.id.channel_menu_delet == v.getId()) {
				Channel mChannel = Util.currentChannel;
				mChannel.deletChannel();
				mChannelListView.updateChannel();
			} else if (R.id.channel_menu_add == v.getId()) {
				buttonStyle = isAdd;
				menuButtons.setVisibility(View.GONE);
				editViews.setVisibility(View.VISIBLE);
				editViewsEdit.setVisibility(View.VISIBLE);
				editSort.setVisibility(View.VISIBLE);
				sortRadio.setVisibility(View.GONE);
				initEdit();
				addExtra.setVisibility(View.VISIBLE);
				setCode.setVisibility(View.VISIBLE);
				codeStype.setVisibility(View.GONE);
				AllChannelList.setVisibility(View.GONE);

				mChannel = new Channel(Util.currentSort.sid);
				editTitle.setText("");
				editTitle.setHint("在这里输入频道标题");
				editRss.setText("");
				editRss.setHint("在这里输入频道地址");
				ctitle.setText("新建频道");

				editViews.startAnimation(menuLeftEnter);
				return;

			} else if (R.id.channel_menu_maneger == v.getId()) {
				buttonStyle = isManeger;
				menuButtons.setVisibility(View.GONE);
				editViews.setVisibility(View.VISIBLE);
				editViewsEdit.setVisibility(View.GONE);
//				addExtra.setVisibility(View.GONE);
				AllChannelList.setVisibility(View.VISIBLE);
				AllChannelList.setItemsCanFocus(true);
				final ACAdapter mAdapter = new ACAdapter();
				AllChannelList.setAdapter(mAdapter);
				AllChannelList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						mAdapter.update((Channel)mAdapter.getItem(arg2), arg2);
					}
				});
				AllChannelList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				ViewGroup.LayoutParams params = AllChannelList.getLayoutParams();    
		        params.height = 200;    
		        AllChannelList.setLayoutParams(params);
				editViews.startAnimation(menuLeftEnter);
				ctitle.setText(mSort.title);
				return;

			} else if (R.id.edite_sort_button == v.getId()) {
				editSort.setVisibility(View.GONE);
				sortRadio.setVisibility(View.VISIBLE);
				sortRadio.startAnimation(menuRightEnter);
				return;

			} else if (R.id.edite_code_button == v.getId()) {

				editViews.setVisibility(View.VISIBLE);
				codeStype.setVisibility(View.VISIBLE);
				setCode.setVisibility(View.GONE);
				codeStype.startAnimation(menuRightEnter);
				return;

			} else if (R.id.menu_edit_sure == v.getId()) {
				String title,rss;
				switch (buttonStyle) {
				case isEdit:
					title = editTitle.getText().toString().trim();
					rss = editRss.getText().toString().trim();
					
					if(title.equals("")){
						Toast.makeText(mContext, "请输入标题", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					if(rss.equals("")){
						Toast.makeText(mContext, "请输入地址", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					mChannel.title = title;
					mChannel.rss = rss;
					mChannel.updateUserChannel();
					mChannelListView.updateChannel();
					break;
					
				case isAdd:
					
					title = editTitle.getText().toString().trim();
					rss = editRss.getText().toString().trim();
					
					if(title.equals("")){
						Toast.makeText(mContext, "请输入标题", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					if(rss.equals("")){
						Toast.makeText(mContext, "请输入地址", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					mChannel.title = title;
					mChannel.rss = rss;
					mChannel.isTake = 1;
					mChannel.code = setCode.getText().toString();
					
					mChannel.insertChannel();
					mChannelListView.updateChannel();
					break;
				case isManeger:
					mChannelListView.updateChannel();
					break;
				}
			} else if (R.id.menu_edit_cancel == v.getId()) {
				mChannel = Util.currentChannel;
				menuButtons.setVisibility(View.VISIBLE);
				menuButtons.startAnimation(menuRightEnter);
				editViews.setVisibility(View.GONE);
				ctitle.setText(mChannel.title);
				return;
			}
			mDialog.dismiss();
		}
	}
}
