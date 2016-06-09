package com.tigot.rozgar.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tigot.rozgar.R;
import com.tigot.rozgar.activity.category.CategoryFragment;
import com.tigot.rozgar.activity.category.NavigationCategoryFragment;
import com.tigot.rozgar.activity.category.TopCategoriesFragment;
import com.tigot.rozgar.activity.utils.CategoryActionListener;
import com.tigot.rozgar.activity.utils.CategoryActivityListener;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.Category;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.provider.DataProviderFacade;
import com.tigot.rozgar.service.DownloadCategoryService;
import com.tigot.rozgar.utils.DisplayUtils;
import com.tigot.rozgar.utils.bean.Holder;

public class CategoriesActivity extends ActionBarActivity implements NavigationCategoryFragment.NavigationDrawerCallbacks, CategoryActivityListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationCategoryFragment mNavigationCategoryFragment;

	private FrameLayout topCategoriesCont;
	
	private CharSequence mTitle;

	private CategoryActionListener categoryActionListener;
	
	private CategoryFragment categoryFragment;
	private TopCategoriesFragment topCategoriesFragment;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.act_category); 

		mNavigationCategoryFragment = (NavigationCategoryFragment) getSupportFragmentManager().findFragmentById(
				R.id.navigation_drawer);
		mTitle = getTitle();   
		topCategoriesCont = (FrameLayout) findViewById(R.id.topCategoriesCont);

		// Set up the drawer. 
		mNavigationCategoryFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		
		categoryActionListener = new CategoryActionListener() {
			@Override 
			public void onNewsItemClicked(NewsItem newsitem, Integer showOld, Integer position) {
				Intent intent = new Intent(CategoriesActivity.this, NewsItemActivity.class);
				intent.putExtra(Consts.INTENT_NEWSITEM_NEWSITEM_ID, newsitem.getId());
				intent.putExtra(Consts.INTENT_NEWSITEM_CATEGORY_ID, newsitem.getCategoryId());
				intent.putExtra(Consts.INTENT_NEWSITEM_CATEGORY_SHOW_OLD, showOld);
				intent.putExtra(Consts.INTENT_NEWSITEM_POSITION, position);
				CategoriesActivity.this.startActivity(intent);
			}
			@Override
			public void updateTitle(String title) {
				mTitle = title;
			}
			@Override
			public void onTopCategoryClicked(Integer categoryId) {
				mNavigationCategoryFragment.selectItem(categoryId);
				
				Category category = DataProviderFacade.getCategoryById(CategoriesActivity.this, categoryId);
				mTitle = category.getTitle();
				restoreActionBar();    
			}
			@Override  
			public void onListScroll(Fragment fragment, Integer y) {   
				Log.w("CategoriesActivity", "onListScroll() 1 [y:" + y + "]");
				if (fragment != categoryFragment && categoryFragment != null) {
					categoryFragment.scrollListViewTogether(y);
				} else if (fragment != topCategoriesFragment && topCategoriesFragment != null) {
					topCategoriesFragment.scrollListViewTogether(y);
				}
			}
			@Override
			public void registerCategoriesFragment(CategoryFragment categoryFragment) {
				Log.w("CategoriesActivity", "registerCategoriesFragment() [categoryFragment:" + categoryFragment + "]");
				CategoriesActivity.this.categoryFragment = categoryFragment;
			}
			@Override
			public void registerTopCategoriesFragment(TopCategoriesFragment topCategoriesFragment, Holder<View> listViewParent) {
				Log.w("CategoriesActivity", "registerTopCategoriesFragment() [topCategoriesFragment:" + topCategoriesFragment + "]");
				CategoriesActivity.this.topCategoriesFragment = topCategoriesFragment;
				listViewParent.setValue(topCategoriesCont);
			}
		};
	}

	@Override
	public void onNavigationDrawerItemSelected(int position, Integer categoryId) {
		Log.w("CategoriesActivity", "onNavigationDrawerItemSelected() [categoryId:" + categoryId + "]");
		// update the main content by replacing fragments 
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.categoryCont, CategoryFragment.newInstance(position + 1, categoryId))
				.commit();
	} 

	@Override
	public void onAttachFragment(Fragment fragment) { 
		Log.w("CategoriesActivity", "onAttachFragment() [fragment:" + fragment + "]");
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onStart() {
		Log.w("CategoriesActivity", "onStart()");
		super.onStart();  
		
		if (!DisplayUtils.isTablet() || !DisplayUtils.isLandscape()) {
			topCategoriesCont.setVisibility(View.GONE); 
		}
	} 

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.w("CategoriesActivity", "onRestoreInstanceState() [savedInstanceState:" + savedInstanceState + "]");
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState != null) {
		} 
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.w("CategoriesActivity", "onNewIntent() [intent:" + intent + "]");
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		Log.w("CategoriesActivity", "onResume()");
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Log.w("CategoriesActivity", "KEYCODE_BACK()");
//			if (itemFragmentCont.getVisibility() == View.VISIBLE) {
//				itemFragmentCont.setVisibility(View.GONE);
//				return true;
//			}
//		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		Log.w("CategoriesActivity", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.w("CategoriesActivity", "onStop()");
		super.onStop();

//		unregisterReceiver(onReloadReceiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.w("CategoriesActivity", "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		
	}

	@Override
	protected void onDestroy() {
		Log.w("CategoriesActivity", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationCategoryFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.act_main, menu);
			restoreActionBar();    
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// ********************** show ************************
	
	/**
	 * There are 2 modes: the first category is shown in different way (middle alignment of title)
	 */
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (Consts.CATEGORY_DEFAULT.equals(mTitle)) {
			LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View customView = inflator.inflate(R.layout.abc_action_bar_custom, null);
			TextView title = (TextView) customView.findViewById(R.id.actionbarInitLabel);
			title.setText(Consts.CATEGORY_DEFAULT);

			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setIcon(R.drawable.pixel);
			actionBar.setDisplayShowTitleEnabled(false );
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setCustomView(customView);
		} else { 
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setIcon(R.drawable.pixel);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayShowCustomEnabled(false);
			actionBar.setTitle(mTitle);
		}
		actionBar.setDisplayShowHomeEnabled(true); 
		actionBar.setHomeButtonEnabled(true);
	}


	@Override
	public CategoryActionListener getActionListener() {
		return categoryActionListener;
	}

}
