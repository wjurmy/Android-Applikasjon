package com.tigot.rozgar.activity;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tigot.rozgar.R;
import com.tigot.rozgar.activity.newsitem.NewsItemFragment;
import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.Category;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.provider.DataProviderFacade;

public class NewsItemActivity extends ActionBarActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private NewsPagerAdapter mNewsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	private Integer newsitemId;
	private Integer categoryId;
	private Integer showOld;
	private Integer position;

	private Category category;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.w("NewsItemActivity", "onCreate() [savedInstanceState:" + savedInstanceState + "]");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_newsitem);

		if (getIntent() != null) {
			newsitemId = getIntent().getIntExtra(Consts.INTENT_NEWSITEM_NEWSITEM_ID, -1);
			categoryId = getIntent().getIntExtra(Consts.INTENT_NEWSITEM_CATEGORY_ID, -1);
			showOld = getIntent().getIntExtra(Consts.INTENT_NEWSITEM_CATEGORY_SHOW_OLD, -1);
			position = getIntent().getIntExtra(Consts.INTENT_NEWSITEM_POSITION, -1);
		}
		Log.w("NewsItemActivity", "onCreate() [categoryId:" + categoryId + "] [newsitemId:" + newsitemId + "] [showOld:" + showOld + "] [position:" + position + "]");
		category = DataProviderFacade.getCategoryById(this, categoryId);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mNewsPagerAdapter = new NewsPagerAdapter(getSupportFragmentManager(), this, categoryId, showOld);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mNewsPagerAdapter); 
		mViewPager.setCurrentItem(position);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setIcon(R.drawable.icon_back);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(category.getTitle());
		actionBar.setHomeButtonEnabled(true);
		
	}

	@Override
	public void onAttachFragment(Fragment fragment) { 
		Log.w("NewsItemActivity", "onAttachFragment() [fragment:" + fragment + "]");
		super.onAttachFragment(fragment);
	}

	@Override
	protected void onStart() {
		Log.w("NewsItemActivity", "onStart()");
		super.onStart(); 

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.w("NewsItemActivity", "onRestoreInstanceState() [savedInstanceState:" + savedInstanceState + "]");
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState != null) {
		} 
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.w("NewsItemActivity", "onNewIntent() [intent:" + intent + "]");
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		Log.w("NewsItemActivity", "onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.w("NewsItemActivity", "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.w("NewsItemActivity", "onStop()");
		super.onStop();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.w("NewsItemActivity", "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		
	}

	@Override
	protected void onDestroy() {
		Log.w("NewsItemActivity", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.w("NewsItemActivity", "onCreateOptionsMenu()");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.w("NewsItemActivity", "onOptionsItemSelected()");
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case R.id.action_settings:
//			NavUtils.navigateUpFromSameTask(this);
			return true;
		case android.R.id.home:
//			NavUtils.navigateUpFromSameTask(this);
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class NewsPagerAdapter extends FragmentPagerAdapter {

		private List<NewsItem> newsitems;

		public NewsPagerAdapter(FragmentManager fm, Context context, Integer categoryId, Integer showOld) {
			super(fm);
			
			newsitems = DataProviderFacade.getCategoryItemsLimit(context, categoryId, showOld);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return NewsItemFragment.newInstance(position + 1, newsitems.get(position));
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return newsitems.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "title" + position;
		}
	}

}
