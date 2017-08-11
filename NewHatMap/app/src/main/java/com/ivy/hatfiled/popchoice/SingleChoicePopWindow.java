package com.ivy.hatfiled.popchoice;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.ivy.hatfiled.appui.R;
import java.util.List;

public class SingleChoicePopWindow extends AbstractChoicePopWindow {

	private SingleChoicAdapter<String> mSingleChoicAdapter;

	public SingleChoicePopWindow(Context context, View parentView, List<String> list) {
		super(context, parentView, list);
		initData();
	}

	protected void initData() {
		mSingleChoicAdapter = new SingleChoicAdapter<String>(mContext, mList, R.drawable.singlechoice_checkbox);
		mListView.setAdapter(mSingleChoicAdapter);
		mListView.setOnItemClickListener(mSingleChoicAdapter);
		Utils.setListViewHeightBasedOnChildren(mListView);
	}

	public int getSelectItem() {
		return mSingleChoicAdapter.getSelectItem();
	}
}
