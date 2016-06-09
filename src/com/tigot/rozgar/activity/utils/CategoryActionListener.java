package com.tigot.rozgar.activity.utils;

import android.support.v4.app.Fragment;
import android.view.View;

import com.tigot.rozgar.activity.category.CategoryFragment;
import com.tigot.rozgar.activity.category.TopCategoriesFragment;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.utils.bean.Holder;

public interface CategoryActionListener {

	void updateTitle(String title);
	void onNewsItemClicked(NewsItem newsItem, Integer showOld, Integer position);
	
	void onTopCategoryClicked(Integer categoryId);
	
	void registerCategoriesFragment(CategoryFragment categoryFragment);
	void registerTopCategoriesFragment(TopCategoriesFragment topCategoriesFragment, Holder<View> listViewParent);
	
	void onListScroll(Fragment fragment, Integer y);
	
}
