package com.tigot.rozgar.activity.newsitem;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;

import com.tigot.rozgar.R;
import com.tigot.rozgar.app.RozgarApplication;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.utils.DisplayUtils;
import com.tigot.rozgar.utils.FileCacheUtils;
import com.tigot.rozgar.utils.ImageUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
 
public class NewsItemFragment extends Fragment {

//	private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
	
	private static final String ARG_SECTION_NUMBER = "SECTION_NUMBER";
	private static final String ARG_NEWSITEM = "NEWSITEM";

	private LinearLayout itemCont;
	private ImageView itemArticleImage;
	private TextView itemTitle;
	private TextView itemAuthor;
	private TextView itemCreated;
	private TextView itemAbstractTitle;
	private TextView itemBody;
	
	private NewsItem newsitem;
	
	public static NewsItemFragment newInstance(int sectionNumber, NewsItem newsitem) {
		NewsItemFragment fragment = new NewsItemFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putParcelable(ARG_NEWSITEM, newsitem);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		Log.w("NewsItemFragment", "onAttach()");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {             
		Log.w("NewsItemFragment", "onCreate() [savedInstanceState:" + savedInstanceState + "]");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.w("NewsItemFragment", "onActivityCreated() [savedInstanceState:" + savedInstanceState + "]");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.w("NewsItemFragment", "onCreateView() [savedInstanceState:" + savedInstanceState + "]");
//		return super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_news_item, container, false);
		
		itemCont = (LinearLayout) view.findViewById(R.id.itemCont);
		itemArticleImage = (ImageView) view.findViewById(R.id.itemArticleImage);
		itemTitle = (TextView) view.findViewById(R.id.itemTitle);
		itemAuthor = (TextView) view.findViewById(R.id.itemAuthor); 
		itemCreated = (TextView) view.findViewById(R.id.itemCreated);
		itemAbstractTitle = (TextView) view.findViewById(R.id.itemAbstractTitle);
		itemBody = (TextView) view.findViewById(R.id.itemBody);  
		 
		// TypeFace Title
		Typeface titleFontType = Typeface.createFromAsset(getActivity().getAssets(), "fonts/W_amir.TTF");
		itemTitle.setTypeface(titleFontType);

		// TypeFace Title
		Typeface abstractFontType = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AdobeArabic-Bold.otf");
		itemAbstractTitle.setTypeface(abstractFontType);

		// TypeFace body
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AdobeArabic-Regular.otf");
		itemAuthor.setTypeface(font);  
		itemBody.setTypeface(font);  
		
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.w("NewsItemFragment", "onViewCreated() [savedInstanceState:" + savedInstanceState + "]");
		super.onViewCreated(view, savedInstanceState);
		
		newsitem = getArguments().getParcelable(ARG_NEWSITEM);
		if (savedInstanceState != null) {
			newsitem = savedInstanceState.getParcelable(Consts.UI_SESSION_CATEGORY_NEWS_ITEM);
		}
		
		ViewGroup.LayoutParams lp = itemCont.getLayoutParams();
		lp.width = (int) (DisplayUtils.isLandscape() ? DisplayUtils.getDisplayWidth()*0.75 : ViewGroup.LayoutParams.FILL_PARENT);
		itemCont.setLayoutParams(lp); 
		
		try { 
			Bitmap bm = FileCacheUtils.get(Consts.URL_NEWSITEM_IMAGE_PREFIX + newsitem.getArticleImage());
			Integer width = bm.getWidth();
			Integer height = bm.getHeight();
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, width, height, false);
			
			itemArticleImage.setImageDrawable(new BitmapDrawable(scaledBitmap));
			if (scaledBitmap.getWidth() > scaledBitmap.getHeight()) {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
 		itemTitle.setText(newsitem.getTitle());     
		itemAuthor.setText(newsitem.getAuthor());
		itemCreated.setText(Consts.DATE_FORMAT.format(newsitem.getCreated()));
		itemAbstractTitle.setText(newsitem.getAbstractTitle());
		String body = newsitem.getBody();
		itemBody.setText(Html.fromHtml(body));
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.w("NewsItemFragment", "onViewStateRestored() [savedInstanceState:" + savedInstanceState + "]");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.w("NewsItemFragment", "onStart()");
		super.onStart();

	}

	@Override
	public void onResume() {
		Log.w("NewsItemFragment", "onResume()");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.w("NewsItemFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.w("NewsItemFragment", "onStop");
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.w("NewsItemFragment", "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		
		outState.putParcelable(Consts.UI_SESSION_CATEGORY_NEWS_ITEM, newsitem);
	}

	@Override
	public void onDestroyView() {
		Log.w("NewsItemFragment", "onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.w("NewsItemFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.w("NewsItemFragment", "onDetach()");
		super.onDetach();
	}

	// ************************ interface ********************
	
	public void handleFeedItem(NewsItem newsItem) {
		this.newsitem = newsItem;
	}
	
	// ******************** show *****************************
	
}
