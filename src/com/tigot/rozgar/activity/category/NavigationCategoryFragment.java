package com.tigot.rozgar.activity.category;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.tigot.rozgar.R;
import com.tigot.rozgar.R.drawable;
import com.tigot.rozgar.R.layout;
import com.tigot.rozgar.R.menu;
import com.tigot.rozgar.R.string;
import com.tigot.rozgar.activity.utils.CategoryActivityListener;
import com.tigot.rozgar.app.RozgarApplication;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.Category;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.provider.DataProvider;
import com.tigot.rozgar.provider.DataProviderFacade;
import com.tigot.rozgar.service.DownloadCategoryImagesService;
import com.tigot.rozgar.service.DownloadCategoryService;
import com.tigot.rozgar.task.TaskManager;
import com.tigot.rozgar.task.impl.DownloadImageTask;
import com.tigot.rozgar.utils.FileCacheUtils;
import com.tigot.rozgar.utils.ImageUtils;
import com.tigot.rozgar.utils.PrimitiveUtils;
import com.tigot.rozgar.utils.bean.AdjustLayoutRunnable;
import com.tigot.rozgar.utils.bean.Holder;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavigationCategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
	private static final String STATE_SELECTED_CATEGORY_ID = "selected_navigation_drawer_category_id";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks; 

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private Handler handler;

	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private int mCurrentSelectedCategoryId = Consts.CATEGORY_DEFAULT_CATEGORY_ID;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	private CursorAdapter adapter;
	
	private BroadcastReceiver onReloadReceiver;
	
	private Map<Integer, Integer> categoryIdToPosition = new HashMap<Integer, Integer>();
	private Map<Integer, ImageView> categoryId2ImageViews = new HashMap<Integer, ImageView>();

	public NavigationCategoryFragment() {
		handler = new Handler();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mCurrentSelectedCategoryId = savedInstanceState.getInt(STATE_SELECTED_CATEGORY_ID);
			mFromSavedInstanceState = true;
		}
		Log.w("NavigationCategoryFragment", "onCreate(), [mCurrentSelectedPosition:" + mCurrentSelectedPosition + "], [mCurrentSelectedCategoryId:" + mCurrentSelectedCategoryId + "]");

		if (savedInstanceState == null) {
			// Select either the default item (0) or the last selected item.
			selectItem(mCurrentSelectedPosition, mCurrentSelectedCategoryId);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("NavigationCategoryFragment", "onCreateView()");
		mDrawerListView = (ListView) inflater.inflate(R.layout.fragment_category_list, container, false);
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.w("NavigationCategoryFragment", "onItemClick(), [object:" + adapter.getItem(position).getClass().getCanonicalName() + "], [tag:" + view.getTag() + "]");
				adapter.getItem(position); 
				selectItem((Integer) view.getTag());
			}
		});
		adapter = new ResourceCursorAdapter(getActivity(), R.layout.item_slidingmenu_category, null) {
			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				Log.d("NavigationCategoryFragment", "bindView(), [cursor.count:" + cursor.getCount() + "]");

				final Category category = new Category(cursor.getInt(Consts.COLUMN_INDEX_ID), cursor.getInt(Consts.COLUMN_INDEX_CATEGORY_ID),
						cursor.getString(Consts.COLUMN_INDEX_CATEGORY_TITLE), cursor.getString(Consts.COLUMN_INDEX_CATEGORY_IMAGE), 
						new Date(cursor.getLong(Consts.COLUMN_INDEX_CATEGORY_LAST_DOWNLOADED)));

				final ImageView categoryImage = (ImageView) view.findViewById(R.id.categoryImage);
				TextView categoryTitle = (TextView) view.findViewById(R.id.categoryTitle);
				
				categoryIdToPosition.put(category.getCategoryId(), cursor.getPosition());
				categoryId2ImageViews.put(category.getCategoryId(), categoryImage);

				categoryTitle.setText(category.getTitle());
				String aUrlPath = Consts.URL_CATEGORY_IMAGE_PREFIX + category.getImage();
				if (category.getImage() != null && FileCacheUtils.isExists(aUrlPath)) {
					try {
						Holder<Bitmap> bitmap = new Holder<Bitmap>(FileCacheUtils.get(aUrlPath));
						if (bitmap.getValue() != null) {
							categoryImage.setImageBitmap(bitmap.getValue());
							categoryImage.setTag(Boolean.TRUE); 
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (category.getImage() != null) {
					// Request downloading category image (if it's not cached yet)
					AdjustLayoutRunnable adjustLayoutRunnable = new AdjustLayoutRunnable() {  
						@Override
						public void run() { 
							final Integer length = ImageUtils.convertDip2Pixel(RozgarApplication.getAppContext(), Consts.ARTICLE_WIDTH_DP);
							Integer width = getBm().getWidth() * length / Math.max(getBm().getHeight(), getBm().getWidth());
							Integer height = getBm().getHeight() * length / Math.max(getBm().getHeight(), getBm().getWidth());
							Bitmap scaledBitmap = Bitmap.createScaledBitmap(getBm(), width, height, false);
							if (scaledBitmap.getWidth() > scaledBitmap.getHeight()) {
								RelativeLayout.LayoutParams lp = (LayoutParams) categoryImage.getLayoutParams();
								Integer viewHeight = length;
								viewHeight = viewHeight * scaledBitmap.getHeight() / scaledBitmap.getWidth();
								lp.height = viewHeight; 
								categoryImage.setLayoutParams(lp);  
							}
							categoryImage.setVisibility(View.GONE);      
			 				categoryImage.setImageBitmap(scaledBitmap);
							categoryImage.setTag(Boolean.TRUE);
							categoryImage.setScaleType(ScaleType.CENTER_INSIDE);
							handler.postDelayed(new Runnable() {  
								@Override
								public void run() {
									categoryImage.setVisibility(View.VISIBLE);
								}
							}, 500);
						}
					};
					TaskManager.getInst().execute( 
							new DownloadImageTask(getActivity(), handler, categoryImage
									, Consts.URL_CATEGORY_IMAGE_PREFIX + category.getImage(), 40, 1, adjustLayoutRunnable));
				}
				
				view.setTag(category.getCategoryId());
				categoryImage.setTag(category.getCategoryId());
				categoryTitle.setTag(category.getCategoryId());
			}
		};
		mDrawerListView.setAdapter(adapter);
		
		// initialize cursor for this list fragment adapter 
		Bundle data = new Bundle(); 
		getLoaderManager().initLoader(0, data, this);

		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

		// Request to download category images if they aren't cached yet 
		Intent intent = new Intent(getActivity(), DownloadCategoryImagesService.class);
		getActivity().startService(intent);    
		
		return mDrawerListView;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 *
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onStart() {
		Log.w("NavigationCategoryFragment", "onStart()");
		super.onStart();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Consts.RECEIVER_ACTION_RELOAD_CATEGORY_IMAGES);
		onReloadReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.w("NavigationCategoryFragment", "onReceive() RECEIVER_ACTION_RELOAD_CATEGORY_IMAGES");
				try {
					List<Category> categories = DataProviderFacade.getCategories(getActivity());
					for (Category category : categories) {
						ImageView imageView = categoryId2ImageViews.get(category.getCategoryId());
						String aUrlPath = Consts.URL_CATEGORY_IMAGE_PREFIX + category.getImage();
						if (category.getImage() != null && FileCacheUtils.isExists(aUrlPath)) {
							try {
								Holder<Bitmap> bitmap = new Holder<Bitmap>(FileCacheUtils.get(aUrlPath));
								if (bitmap.getValue() != null) {
									imageView.setImageBitmap(bitmap.getValue());
									imageView.setTag(Boolean.TRUE);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					Log.e("NavigationCategoryFragment", "onReceive() RECEIVER_ACTION_RELOAD_CATEGORY_IMAGES");
				} catch (Exception ex) {  
					ex.printStackTrace(); 
				} 
			} 
		};
		getActivity().registerReceiver(onReloadReceiver, intentFilter);

	}

	@Override
	public void onResume() {
		Log.w("NavigationCategoryFragment", "onResume() ");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.w("NavigationCategoryFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.w("NavigationCategoryFragment", "onStop");
		super.onStop();

		getActivity().unregisterReceiver(onReloadReceiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
		outState.putInt(STATE_SELECTED_CATEGORY_ID, mCurrentSelectedCategoryId);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

//		if (item.getItemId() == R.id.action_example) {
//			Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
//			return true;
//		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position, Integer categoryId);
	}
	
	// ******************* LoaderCallback ********************
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		Log.w("NavigationCategoryFragment", "LoaderManager.LoaderCallbacks.onCreateLoader(),");

		String selection; 
		String[] selectionArgs;
		CursorLoader loader = new CursorLoader(getActivity(), Consts.CONTENT_CATEGORY_URI
				, DataProvider.sCategoryProjectionMap.keySet().toArray(new String[DataProvider.sCategoryProjectionMap.size()])
				, null, null, Consts.DB_COLUMN_CATEGORY_ID + " ASC");

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.w("NavigationCategoryFragment", "LoaderManager.LoaderCallbacks.onLoadFinished(), [count:" + cursor.getCount() + "]");
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.w("NavigationCategoryFragment", "LoaderManager.LoaderCallbacks.onLoaderReset()");
		adapter.swapCursor(null);
	}

	// ********************* interface ********************
	
	public void selectItem(Integer categoryId) {
		int position = getPositionByCategoryId(categoryId);
		selectItem(position, categoryId);
	}
	
	public void selectItem(int position, Integer categoryId) {
		Log.w("NavigationCategoryFragment", "selectItem() [position:" + position + "] [categoryId:" + categoryId + "]");
		mCurrentSelectedPosition = position;
		mCurrentSelectedCategoryId = categoryId;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position, categoryId);
		}
	}

	// ********************** utils ***********************
	
	private Integer getPositionByCategoryId(Integer categoryId) {
		return categoryIdToPosition.get(categoryId);
	}

}
