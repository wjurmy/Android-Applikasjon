package com.tigot.rozgar.activity.category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.content.CursorLoader;
//import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.tigot.rozgar.R;
import com.tigot.rozgar.activity.utils.CategoryActionListener;
import com.tigot.rozgar.activity.utils.CategoryActivityListener;
import com.tigot.rozgar.app.RozgarApplication;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.Category;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.provider.DataProvider;
import com.tigot.rozgar.provider.DataProviderFacade;
import com.tigot.rozgar.service.DownloadCategoryService;
import com.tigot.rozgar.task.TaskManager;
import com.tigot.rozgar.task.impl.DownloadImageTask;
import com.tigot.rozgar.utils.FileCacheUtils;
import com.tigot.rozgar.utils.ImageUtils;
import com.tigot.rozgar.utils.PrimitiveUtils;
import com.tigot.rozgar.utils.bean.Holder;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopCategoriesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private Handler handler;

	private LayoutInflater inflater;

	private CursorAdapter adapter;
	private Cursor newsitemCursor;

	private CategoryActionListener actionListener;
	
	private BroadcastReceiver onReloadReceiver;
	
	private Boolean isListViewTouched = false;
	private Holder<View> listViewParent = new Holder<View>(null);
	private LinkedHashMap<Integer, Integer> position2ListItemHeight = new LinkedHashMap<Integer, Integer>(); 

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static TopCategoriesFragment newInstance(int sectionNumber, Integer categoryId) {
		Log.w("TopCategoriesFragment", "***newInstance()");
		TopCategoriesFragment fragment = new TopCategoriesFragment();
		return fragment;
	}

	public TopCategoriesFragment() {
		Log.w("TopCategoriesFragment", "CategoryFragmentinit()");
		handler = new Handler();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.w("TopCategoriesFragment", "onAttach() ");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.w("TopCategoriesFragment", "onCreate() [savedInstanceState:" + savedInstanceState + "]");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.w("TopCategoriesFragment", "onActivityCreated() [savedInstanceState:" + savedInstanceState + "]");
		super.onActivityCreated(savedInstanceState);
		// Create a new Adapter and bind it to the List View
		adapter = new ResourceCursorAdapter(getActivity(), R.layout.item_topcategory, null) {
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				Log.d("TopCategoriesFragment", "bindView(), [cursor.count:" + cursor.getCount() + "]");

				final Category category = new Category(cursor.getInt(Consts.COLUMN_INDEX_ID), cursor.getInt(Consts.COLUMN_INDEX_CATEGORY_ID),
						cursor.getString(Consts.COLUMN_INDEX_CATEGORY_TITLE), cursor.getString(Consts.COLUMN_INDEX_CATEGORY_IMAGE), 
						new Date(cursor.getLong(Consts.COLUMN_INDEX_CATEGORY_LAST_DOWNLOADED)));

				LinearLayout categoryCont = (LinearLayout) view.findViewById(R.id.categoryCont);
				TextView categoryTitle = (TextView) view.findViewById(R.id.categoryTitle);
				LinearLayout topCategoryCont = (LinearLayout) view.findViewById(R.id.topCategoryCont);
				

//				String aUrlPath = "http://rozgar.eu/subdomain/dari/files/Category_img/news_icon_273341448.png";
				categoryTitle.setText(category.getTitle());
				topCategoryCont.removeAllViews(); 
				List<NewsItem> newsitems = DataProviderFacade.getCategoryItemsLimit(getActivity(), category.getCategoryId(), 3);
				if (newsitems.size() > 0) {
					final NewsItem newsitem = newsitems.get(0);
					View newsitemView = inflater.inflate(R.layout.item_newsitem_tiny, null);
					TextView title = (TextView) newsitemView.findViewById(R.id.title);
					ImageView articleImage = (ImageView) newsitemView.findViewById(R.id.articleImage);
					title.setText(newsitem.getTitle());  
					String aUrlPath = Consts.URL_NEWSITEM_IMAGE_PREFIX + newsitem.getArticleImage();
					if (newsitem.getArticleImage() != null && FileCacheUtils.isExists(aUrlPath)) {
						try { 
							Holder<Bitmap> bitmap = new Holder<Bitmap>(FileCacheUtils.get(aUrlPath));
							if (bitmap.getValue() != null) {
								ImageUtils.fixImageViewLayout(null, articleImage, bitmap.getValue());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					topCategoryCont.addView(newsitemView);
					newsitemView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							((CategoryActivityListener) getActivity()).getActionListener().onNewsItemClicked(newsitem, 10, 0);
						}
					});
				}
				categoryCont.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						((CategoryActivityListener) getActivity()).getActionListener().onTopCategoryClicked(category.getCategoryId());
					}
				});
				
				final Integer position = cursor.getPosition();
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
		
		actionListener = ((CategoryActivityListener) getActivity()).getActionListener();
		actionListener.registerTopCategoriesFragment(this, listViewParent);

		getListView().setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.w("TopCategoriesFragment", "getListView().onScrollStateChanged() [scrollState:" + scrollState + "]");
				isListViewTouched = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Integer prevHeight = 0;
				for (Integer position : position2ListItemHeight.keySet()) {
					if (position < firstVisibleItem) {
						prevHeight += position2ListItemHeight.get(position);
					}
				}
				Log.w("TopCategoriesFragment", "getListView().onScroll() [firstVisibleItem:" + firstVisibleItem + "], [prevHeight:" + prevHeight + "], [totalHeight:" + (prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop())) + "]");
				Log.w("TopCategoriesFragment", "getListView().onScroll() [actionListener:" + actionListener + "] [isListViewTouched:" + isListViewTouched + "]");
				if (actionListener != null && isListViewTouched) {
					actionListener.onListScroll(TopCategoriesFragment.this, prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop()));
				} 
			}
		});

		Log.w("TopCategoriesFragment", "onActivityCreated() EXIT");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.w("TopCategoriesFragment", "onCreateView() [savedInstanceState:" + savedInstanceState + "]");
		this.inflater = inflater;
		View rootView = inflater.inflate(R.layout.fragment_top_categories, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.w("TopCategoriesFragment", "onViewCreated() [savedInstanceState:" + savedInstanceState + "]");
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
		}

		Bundle data = new Bundle(); 
		getLoaderManager().initLoader(android.R.id.list, data, this);
		
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.w("TopCategoriesFragment", "onViewStateRestored() [savedInstanceState:" + savedInstanceState + "]");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.w("TopCategoriesFragment", "onStart()");
		super.onStart();

		Log.w("TopCategoriesFragment", "onStart() [getListView().getChildAt(0):" + getListView().getChildAt(0) + "]");

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Consts.RECEIVER_ACTION_RELOAD_TOP_UI);
		onReloadReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.w("TopCategoriesFragment", "onReceive() RECEIVER_ACTION_RELOAD_TOP_UI");
				try {
					Bundle data = new Bundle();
					getLoaderManager().restartLoader(android.R.id.list, data, TopCategoriesFragment.this);
				} catch (Exception ex) { 
					ex.printStackTrace(); 
				} 
			}
		};
		getActivity().registerReceiver(onReloadReceiver, intentFilter);

		final List<Category> categories = DataProviderFacade.getCategories(getActivity());
		Intent intent = new Intent(getActivity(), DownloadCategoryService.class);
		intent.putExtra(Consts.INTENT_DOWNLOAD_CALLBACK_ACTION, Consts.RECEIVER_ACTION_RELOAD_TOP_UI);
		intent.putExtra(Consts.INTENT_DOWNLOAD_BEFORE, new Date().getTime() + 24*60*60*1000);
		getActivity().startService(intent);  
		
	}

	@Override
	public void onResume() { 
		Log.w("TopCategoriesFragment", "onResume() ");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.w("TopCategoriesFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.w("TopCategoriesFragment", "onStop");
		super.onStop();

//		refreshTopCategoriesTimer.cancel();
		getActivity().unregisterReceiver(onReloadReceiver);
	}

	@Override 
	public void onSaveInstanceState(Bundle outState) {
		Log.w("TopCategoriesFragment", "onSaveInstanceState() ");
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onDestroyView() {
		Log.w("TopCategoriesFragment", "onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.w("TopCategoriesFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.w("TopCategoriesFragment", "onDetach()");
		super.onDetach();
	}

	// ******************* LoaderCallback ********************

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		Log.w("TopCategoriesFragment", "LoaderManager.LoaderCallbacks.onCreateLoader(),");

		String selection; 
		String[] selectionArgs;
		CursorLoader loader = new CursorLoader(getActivity(), Consts.CONTENT_CATEGORY_URI, DataProvider.sCategoryProjectionMap.keySet().toArray(
				new String[DataProvider.sCategoryProjectionMap.size()]), null, null, Consts.DB_COLUMN_CATEGORY_ID + " ASC"); 
		//Consts.DB_COLUMN_CATEGORY_ID + " ASC"

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.w("TopCategoriesFragment", "LoaderManager.LoaderCallbacks.onLoadFinished(), [count:" + cursor.getCount() + "]");
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.w("TopCategoriesFragment", "LoaderManager.LoaderCallbacks.onLoaderReset()");
		adapter.swapCursor(null);
	}

	// *************** interface *******************

	public void scrollListViewTogether(Integer y) {
		if (!isListViewTouched) {
			Log.w("TopCategoriesFragment", "scrollListViewTogether()");
//			getListView().scrollTo(0, y);
//			getListView().smoothScrollBy(y - getListView().gets, 1);
			Integer prevHeight = 0;
			for (Integer position : position2ListItemHeight.keySet()) {
				if (position < getListView().getFirstVisiblePosition()) {
					prevHeight += position2ListItemHeight.get(position);
				} 
			}
			Integer totalHeight = prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop());
			Log.w("TopCategoriesFragment", "scrollListViewTogether [firstVisibleItem:" + getListView().getFirstVisiblePosition() + "], [prevHeight:" + prevHeight + "], [totalHeight:" + totalHeight + "], [y:" + y + "]");
//			Log.w("TopCategoriesFragment", "getListView().onScroll() [actionListener:" + actionListener + "] [view.getY():" + view.getY() + "] [view.getScrollY:" + view.getScrollY() + "]");
//			if (actionListener != null) {
//				actionListener.onListScroll(CategoryFragment.this, prevHeight - (getListView().getChildAt(0) == null ? 0 : getListView().getChildAt(0).getTop()));
//			}
			
			getListView().smoothScrollBy(y - totalHeight, 1);
		}
	}
	
	// **************************** show **************************
	
}
