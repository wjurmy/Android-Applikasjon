package com.tigot.rozgar.activity.category;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tigot.rozgar.R;
import com.tigot.rozgar.activity.NewsItemActivity;
import com.tigot.rozgar.activity.utils.CategoryActionListener;
import com.tigot.rozgar.activity.utils.CategoryActivityListener;
import com.tigot.rozgar.app.RozgarApplication;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.provider.DataProvider;
import com.tigot.rozgar.provider.DataProviderFacade;
import com.tigot.rozgar.service.DownloadCategoryService;
import com.tigot.rozgar.task.TaskManager;
import com.tigot.rozgar.task.impl.DownloadImageTask;
import com.tigot.rozgar.utils.DisplayUtils;
import com.tigot.rozgar.utils.ImageUtils;
import com.tigot.rozgar.utils.ReflectionUtils;
import com.tigot.rozgar.utils.bean.AdjustLayoutRunnable;
import com.tigot.rozgar.utils.bean.Holder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.content.CursorLoader;
//import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;

/**
 * A placeholder fragment containing a simple view.
 */
public class CategoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private Handler handler;

	private LayoutInflater inflater;
	private ImageView progressImageRecent;
	private ImageView progressImageOld;

	private CursorAdapter adapter;
	private Cursor newsitemCursor;
	private BroadcastReceiver onReloadReceiver;

	private CategoryActionListener actionListener;

	private Integer categoryId;
	// mostOldNews determines a date for SQL query WHERE clause and for downloading articles 
	// with Consts.URL_CATEGORY. 
	private Date mostOldNews;
	// request 10 articles first
	private Integer showOld = 10;
	// toShowOld is used to specify that user requests older articles. Article list should be
	// appended with new articles and list should be scrolled a little to show next article
	private Boolean toShowOld = false;
	private List<ImageView> articleImages = new LinkedList<ImageView>();
	
	private Boolean isListViewTouched = false;
	private LinkedHashMap<Integer, Integer> position2ListItemHeight = new LinkedHashMap<Integer, Integer>();
	
	private Integer lastFirstVisibleItem;
	private Integer lastVisibleItemCount;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static CategoryFragment newInstance(int sectionNumber, Integer categoryId) {
		Log.w("CategoryFragment", "***newInstance()");
		CategoryFragment fragment = new CategoryFragment();
		fragment.setCategoryId(categoryId);
		return fragment;
	}

	public CategoryFragment() {
		Log.w("CategoryFragment", "CategoryFragmentinit()");
		handler = new Handler();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.w("CategoryFragment", "onAttach() [categoryId:" + categoryId + "], [obj:" + DataProviderFacade.getCategoryById(getActivity(), categoryId) + "]");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.w("CategoryFragment", "onCreate() [savedInstanceState:" + savedInstanceState + "]");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.w("CategoryFragment", "onActivityCreated() [savedInstanceState:" + savedInstanceState + "]");
		super.onActivityCreated(savedInstanceState);
		// Create a new Adapter and bind it to the List View
		adapter = new ResourceCursorAdapter(getActivity(), R.layout.item_newsitem, null) {
			@Override
			public void bindView(View view, Context context, final Cursor cursor) {
				Log.d("CategoryFragment", "bindView(), [cursor.count:" + cursor.getCount() + "], [categoryId:" + categoryId + "]");

				final NewsItem newsitem = new NewsItem(cursor.getInt(Consts.COLUMN_INDEX_ID), cursor.getString(Consts.COLUMN_INDEX_NEWSITEM_TITLE),
						cursor.getString(Consts.COLUMN_INDEX_NEWSITEM_AUTHOR), cursor.getInt(Consts.COLUMN_INDEX_NEWSITEM_CATEGORY_ID),
						cursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ARTICLE_IMAGE), new Date(cursor.getLong(Consts.COLUMN_INDEX_NEWSITEM_CREATED)),
						cursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ABSTRACT_TITLE), cursor.getString(Consts.COLUMN_INDEX_NEWSITEM_CAPTION_IMAGE),
						cursor.getString(Consts.COLUMN_INDEX_NEWSITEM_BODY)    
						, cursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ID_MD5)); 

				ScrollView scrollView1 = (ScrollView) view.findViewById(R.id.scrollView1);
				RelativeLayout relativeLayout1 = (RelativeLayout) view.findViewById(R.id.relativeLayout1);
				RelativeLayout relativeLayout2 = (RelativeLayout) view.findViewById(R.id.relativeLayout2);
				TextView article_TitleText = (TextView) view.findViewById(R.id.title);
				TextView authorText = (TextView) view.findViewById(R.id.author);  
				TextView createdText = (TextView) view.findViewById(R.id.created);  
				TextView abstraactText = (TextView) view.findViewById(R.id.abstraact);
				TextView image_captionText = (TextView) view.findViewById(R.id.image_caption);
				final ImageView imageViewView = (ImageView) view.findViewById(R.id.articleImage);

				Log.w("CategoryFragment", "bindView(), [title:" + newsitem.getTitle() + "], [categoryId:" + newsitem.getCategoryId() + "]");
				// TypeFace Title
				Typeface titleFontType = Typeface.createFromAsset(getActivity().getAssets(), "fonts/W_amir.TTF");
				article_TitleText.setTypeface(titleFontType);

				// TypeFace Title
				Typeface abstractFontType = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AdobeArabic-Bold.otf");
				abstraactText.setTypeface(abstractFontType);

				// Locate the ImageView in singleitemview.xml
				imageViewView.setImageDrawable(null);
				articleImages.add(imageViewView);

				// Set results to the TextViews
				article_TitleText.setText("" + newsitem.getTitle());
				authorText.setText("" + newsitem.getAuthor());
				createdText.setText(Consts.DATE_FORMAT.format(newsitem.getCreated()));
				abstraactText.setText("" + newsitem.getAbstractTitle());
				image_captionText.setText("null".equals(newsitem.getImageCaption()) ? "" : newsitem.getImageCaption());
				
				final Integer articleWidthPx = Math.min(ImageUtils.convertDip2Pixel(RozgarApplication.getAppContext(), Consts.ARTICLE_WIDTH_DP), DisplayUtils.getDisplayWidth()/3);
				RelativeLayout.LayoutParams lp = (LayoutParams) image_captionText.getLayoutParams();
				lp.width = articleWidthPx;
				image_captionText.setLayoutParams(lp);
				
				final Integer position = cursor.getPosition();
				OnClickListener onClickListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.w("CategoryFragment", "onCreate() [categoryId:" + categoryId + "] [newsitem.getCategoryId():" + newsitem.getCategoryId() + "] [position:" + position + "]");
						((CategoryActivityListener) getActivity()).getActionListener().onNewsItemClicked(newsitem, showOld, position);
					} 
				}; 
				relativeLayout1.setOnClickListener(onClickListener);
				relativeLayout2.setOnClickListener(onClickListener);

				// Request downloading article image (if it's not cached yet)
				AdjustLayoutRunnable adjustLayoutRunnable = new AdjustLayoutRunnable() {  
					@Override 
					public void run() {  
						Integer width = getBm().getWidth() * articleWidthPx / Math.max(getBm().getHeight(), getBm().getWidth());
						Integer height = getBm().getHeight() * articleWidthPx / Math.max(getBm().getHeight(), getBm().getWidth());
						Bitmap scaledBitmap = Bitmap.createScaledBitmap(getBm(), width, height, false);
						RelativeLayout.LayoutParams lp = (LayoutParams) imageViewView.getLayoutParams();
						lp.width = width;
						lp.height = height;
						imageViewView.setLayoutParams(lp); 
						
						imageViewView.setVisibility(View.GONE);      
		 				imageViewView.setImageBitmap(scaledBitmap);
						imageViewView.setTag(Boolean.TRUE);
						imageViewView.setScaleType(ScaleType.CENTER_INSIDE);
						handler.postDelayed(new Runnable() {  
							@Override
							public void run() {
								imageViewView.setVisibility(View.VISIBLE);
							}
						}, 500);
					}
				};
				TaskManager.getInst().execute(
						new DownloadImageTask(getActivity(), handler, imageViewView
								, Consts.URL_NEWSITEM_IMAGE_PREFIX + newsitem.getArticleImage(), 75, 1
								, adjustLayoutRunnable));
				
				view.addOnLayoutChangeListener(new OnLayoutChangeListener() {
					@Override
					public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
						if (position2ListItemHeight.get(position) < view.getHeight()) {
							position2ListItemHeight.put(position, view.getHeight());
						}
					} 
				});
				position2ListItemHeight.put(cursor.getPosition(), view.getHeight());
			}
		};
		setListAdapter(adapter);

//		getListView().setOnScrollListener(new OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				Log.w("CategoryFragment", "getListView().onScrollStateChanged() [view.getY():" + view.getY() + "] [view.ScaleY:" + view.getScaleY() + "]");
//			}
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//			}
//		});

		Log.w("CategoryFragment", "onActivityCreated() EXIT");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.w("CategoryFragment", "onCreateView() [savedInstanceState:" + savedInstanceState + "]");
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_category, container, false);
		progressImageRecent = (ImageView) rootView.findViewById(R.id.progressImageRecent);
		progressImageOld = (ImageView) rootView.findViewById(R.id.progressImageOld);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.w("CategoryFragment", "onViewCreated() [savedInstanceState:" + savedInstanceState + "]");
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
			categoryId = savedInstanceState.getInt(Consts.UI_SESSION_CATEGORY_CATEGORY_ID);
			if (savedInstanceState.containsKey(Consts.UI_SESSION_CATEGORY_MOST_OLD_NEWS)) {
				mostOldNews = new Date(savedInstanceState.getLong(Consts.UI_SESSION_CATEGORY_MOST_OLD_NEWS));
			}
			showOld = savedInstanceState.getInt(Consts.UI_SESSION_CATEGORY_SHOW_OLD);
		}
		Log.w("CategoryFragment", "onViewCreated() [categoryId:" + categoryId + "] [mostOldNews:" + mostOldNews + "] [showOld:" + showOld + "]");

		Bundle data = new Bundle();
		data.putInt("showOld", showOld);
		getLoaderManager().initLoader(android.R.id.list, data, this);
		Log.w("CategoryFragment", "onViewCreated() [getLoaderManager():" + getLoaderManager().getClass().getCanonicalName() + "]");
		
		actionListener = ((CategoryActivityListener) getActivity()).getActionListener();
		actionListener.registerCategoriesFragment(this);
		actionListener.updateTitle(DataProviderFacade.getCategoryById(getActivity(), categoryId).getTitle());
		
		final Holder<Float> initYWithoutScroll = new Holder<Float>(null);
		// cancel loading recent/old articles gesture if onScroll(...) happens 
		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.w("CategoryFragment", "getListView().onScrollStateChanged() [scrollState:" + scrollState + "]");
				isListViewTouched = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.w("CategoryFragment", "onScroll() [firstVisibleItem:" + firstVisibleItem + "] [y:" + view.getScrollY() + "], [top:"
						+ (view.getChildAt(0) == null ? "ISNULL" : view.getChildAt(0).getTop()) + "]");
				lastFirstVisibleItem = firstVisibleItem;
				lastVisibleItemCount = visibleItemCount;
				
				Integer prevHeight = 0;
				for (Integer position : position2ListItemHeight.keySet()) {
					if (position < firstVisibleItem) {
						prevHeight += position2ListItemHeight.get(position);
					}
				}
				Log.w("CategoryFragment", "getListView().onScroll() [firstVisibleItem:" + firstVisibleItem + "], [prevHeight:" + prevHeight + "], [totalHeight:" + (prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop())) + "]");
				Log.w("CategoryFragment", "getListView().onScroll() [actionListener:" + actionListener + "] [isListViewTouched:" + isListViewTouched + "]");
				if (actionListener != null && isListViewTouched) {
					actionListener.onListScroll(CategoryFragment.this, prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop()));
				}
				
				initYWithoutScroll.setValue(null);
			}
		});
		// check if user performed gestures to load the most recent or old articles
		getListView().setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (initYWithoutScroll.getValue() == null) {
					initYWithoutScroll.setValue(event.getY());
				}
				Log.w("CategoryFragment", "onTouch() [event.y:" + event.getY() + "] [reload:" + (event.getY() - initYWithoutScroll.getValue()) + "]");
				if (lastFirstVisibleItem == 0 && event.getY() - initYWithoutScroll.getValue() > 150) {
					initYWithoutScroll.setValue(event.getY());
					loadRecent();
				}
				if (lastFirstVisibleItem + lastVisibleItemCount == getListView().getCount() && event.getY() - initYWithoutScroll.getValue() < -150) {
					initYWithoutScroll.setValue(event.getY());
					loadOld();
				}
				return false;
			}
		});
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.w("CategoryFragment", "onViewStateRestored() [savedInstanceState:" + savedInstanceState + "]");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.w("CategoryFragment", "onStart()");
		super.onStart();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Consts.RECEIVER_ACTION_RELOAD_CATEGORY_UI);
		onReloadReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.w("CategoryFragment", "onReceive() RECEIVER_ACTION_RELOAD_UI [showOld:" + showOld + "]");
				try {
					// if RECEIVER_ACTION_RELOAD_UI was initially caused by loadOld() or loadRecent() 
					// then hide loading images 
					stopLoadingRecent();
					stopLoadingOld();
					
					Bundle data = new Bundle();
					data.putInt("showOld", showOld);
					// if RECEIVER_ACTION_RELOAD_UI was initially caused by loadOld() then scroll appended 
					// list a little
					if (toShowOld) {
						toShowOld = false; 
						getLoaderManager().restartLoader(android.R.id.list, data, CategoryFragment.this);
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									getListView().smoothScrollBy(150, 500);
//									getListView().scrollTo(0, 500);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}, 500);
					} else {
						getLoaderManager().restartLoader(android.R.id.list, data, CategoryFragment.this);
					}
				} catch (Exception ex) { 
					ex.printStackTrace();
				}
			}
		};
		getActivity().registerReceiver(onReloadReceiver, intentFilter);

		Intent intent = new Intent(getActivity(), DownloadCategoryService.class);
		intent.putExtra(Consts.INTENT_DOWNLOAD_CATEGORY_ID, categoryId);
		intent.putExtra(Consts.INTENT_DOWNLOAD_BEFORE, new Date().getTime() + 24*60*60*1000);
		getActivity().startService(intent); 

		Log.w("CategoryFragment", "onStart() [getListView().getChildAt(0):" + getListView().getChildAt(0) + "]");

	}

	@Override
	public void onResume() {
		Log.w("CategoryFragment", "onResume() [channelId:" + categoryId + "]");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.w("CategoryFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.w("CategoryFragment", "onStop");
		super.onStop();

		getActivity().unregisterReceiver(onReloadReceiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.w("CategoryFragment", "onSaveInstanceState() [channelId:" + categoryId + "]");
		super.onSaveInstanceState(outState);

		if (categoryId != null) {
			outState.putInt(Consts.UI_SESSION_CATEGORY_CATEGORY_ID, categoryId);
		}
		if (mostOldNews != null) {
			outState.putLong(Consts.UI_SESSION_CATEGORY_MOST_OLD_NEWS, mostOldNews.getTime());
		}
		outState.putInt(Consts.UI_SESSION_CATEGORY_SHOW_OLD, showOld);
	}

	@Override
	public void onDestroyView() {
		Log.w("CategoryFragment", "onDestroyView()");
		super.onDestroyView();
		
		// manually recycle bitmaps
		for (ImageView imageView : articleImages) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
			Log.w("CategoryFragment", "onDestroyView(), [bitmapDrawable:" + bitmapDrawable + "]");
			imageView.setImageDrawable(null);
			if (bitmapDrawable != null) {
				bitmapDrawable.getBitmap().recycle();
			}
		}
	}

	@Override
	public void onDestroy() {
		Log.w("CategoryFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDetach() { 
		Log.w("CategoryFragment", "onDetach()");
		super.onDetach();
	}

	// ******************* LoaderCallback ********************

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		Log.w("CategoryFragment", "LoaderManager.LoaderCallbacks.onCreateLoader(), [channelId:" + categoryId + "]");

		String selection;
		String[] selectionArgs;
		selection = Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID + "=?"; 
		selectionArgs = new String[] { "" + categoryId };
		CursorLoader loader = new CursorLoader(getActivity(), Consts.CONTENT_NEWSITEM_URI, DataProvider.sNewsitemProjectionMap.keySet().toArray(
				new String[DataProvider.sNewsitemProjectionMap.size()]), selection, selectionArgs, Consts.DB_COLUMN_NEWSITEM_CREATED + " DESC" 
		+ " LIMIT " + data.getInt("showOld"));

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.w("CategoryFragment", "LoaderManager.LoaderCallbacks.onLoadFinished(), [count:" + cursor.getCount() + "]");
		if (cursor.getCount() > 0) { 
			while (cursor.moveToNext()) {
				Date created = new Date(cursor.getLong(Consts.COLUMN_INDEX_NEWSITEM_CREATED));
				if (mostOldNews == null || mostOldNews.after(created)) {
					mostOldNews = created;
				}
			}
			cursor.moveToFirst();
		}
		adapter.swapCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.w("CategoryFragment", "LoaderManager.LoaderCallbacks.onLoaderReset()");
		adapter.swapCursor(null);
		stopLoadingRecent();
		stopLoadingOld();
	}

	// *************** interface *******************

	public void scrollListViewTogether(Integer y) {
		if (!isListViewTouched) {
			Log.w("CategoryFragment", "scrollListViewTogether()");
//			getListView().scrollTo(0, y);
//			getListView().smoothScrollBy(y - getListView().gets, 1);
			Integer prevHeight = 0;
			for (Integer position : position2ListItemHeight.keySet()) {
				if (position < getListView().getFirstVisiblePosition()) {
					prevHeight += position2ListItemHeight.get(position);
				} 
			}
			Integer totalHeight = prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop());
			Log.w("CategoryFragment", "scrollListViewTogether [firstVisibleItem:" + getListView().getFirstVisiblePosition() + "], [prevHeight:" + prevHeight + "], [totalHeight:" + totalHeight + "], [y:" + y + "]");
//			Log.w("CategoryFragment", "getListView().onScroll() [actionListener:" + actionListener + "] [view.getY():" + view.getY() + "] [view.getScrollY:" + view.getScrollY() + "]");
//			if (actionListener != null) {
//				actionListener.onListScroll(CategoryFragment.this, prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop()));
//			}
			
			getListView().smoothScrollBy(y - totalHeight, 1);
		}
	}
	
	/**
	 * request the most recent articles from DownloadCategoryService
	 */
	public void loadRecent() {
		Log.w("CategoryFragment", "loadRecent()");
		Intent intent = new Intent(getActivity(), DownloadCategoryService.class);
		intent.putExtra(Consts.INTENT_DOWNLOAD_CATEGORY_ID, categoryId);
		intent.putExtra(Consts.INTENT_DOWNLOAD_BEFORE, new Date().getTime());
		getActivity().startService(intent);
		handler.post(new Runnable() {
			public void run() {
				try {
					startLoadingRecent();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	/**
	 * user requests older articles. set showOld to showOld + 10, set market toShowOld to TRUE
	 * and start DownloadCategoryService service
	 */
	public void loadOld() {
		Log.w("CategoryFragment", "loadOld(),");
		Intent intent = new Intent(getActivity(), DownloadCategoryService.class);
		intent.putExtra(Consts.INTENT_DOWNLOAD_CATEGORY_ID, categoryId);
		intent.putExtra(Consts.INTENT_DOWNLOAD_BEFORE, mostOldNews != null ? mostOldNews.getTime() : new Date().getTime());
		getActivity().startService(intent);

		showOld += 10;
		toShowOld = true;
		handler.post(new Runnable() {
			public void run() {
				try {
					startLoadingOld();  
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	// **************************** show **************************
	
	/**
	 * show loading image when user requests the most recent articles
	 */
	private void startLoadingRecent() { 
		Log.w("CategoryFragment", "startLoadingRecent()");
		Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation);
		rotation.setRepeatCount(Animation.INFINITE);
		progressImageRecent.setVisibility(View.VISIBLE);
		progressImageRecent.startAnimation(rotation);      
	}

	/**
	 * stop showing loading image
	 */
	private void stopLoadingRecent() {
		Log.w("CategoryFragment", "stopLoadingRecent()");
		progressImageRecent.setVisibility(View.GONE);
		progressImageRecent.clearAnimation();
	}
 	
	/**
	 * show loading image when user requests older articles
	 */
	private void startLoadingOld() { 
		Log.w("CategoryFragment", "startLoadingOld()");
		Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation);
		rotation.setRepeatCount(Animation.INFINITE);
		progressImageOld.setVisibility(View.VISIBLE);
		progressImageOld.startAnimation(rotation);      
	}

	/**
	 * stop showing loading image
	 */
	private void stopLoadingOld() {
		Log.w("CategoryFragment", "stopLoadingOld()");
		progressImageOld.setVisibility(View.GONE);
		progressImageOld.clearAnimation();
	}
 	
	
}
