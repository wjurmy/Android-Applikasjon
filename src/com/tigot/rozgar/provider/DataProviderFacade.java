package com.tigot.rozgar.provider;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.tigot.rozgar.consts.Consts;
import com.tigot.rozgar.data.bean.Category;
import com.tigot.rozgar.data.bean.NewsItem;
import com.tigot.rozgar.utils.PrimitiveUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;

public class DataProviderFacade {

	public static Cursor getCategoriesCursor(Context context) {
		return context.getContentResolver().query(Consts.CONTENT_CATEGORY_URI, Consts.PROJECTION_CATEGORIES, null, null, null);
	}

	public static List<Category> getCategories(Context context) {
		List<Category> channels = new LinkedList<Category>();
		Cursor categoryCursor = getCategoriesCursor(context);
		if (categoryCursor.getCount() > 0) {
			while (categoryCursor.moveToNext()) {
				Integer id = categoryCursor.getInt(Consts.COLUMN_INDEX_ID);
				Integer categoryId = categoryCursor.getInt(Consts.COLUMN_INDEX_CATEGORY_ID);
				String title = categoryCursor.getString(Consts.COLUMN_INDEX_CATEGORY_TITLE);
				String image = categoryCursor.getString(Consts.COLUMN_INDEX_CATEGORY_IMAGE);
				Date lastDownloaded = new Date(categoryCursor.getLong(Consts.COLUMN_INDEX_CATEGORY_LAST_DOWNLOADED));
				channels.add(new Category(id, categoryId, title, image, lastDownloaded));
			}
			categoryCursor.close();
		} else {
			categoryCursor.close();
		}
		return channels;
	}

	public static Category getCategoryById(Context context, Integer categoryId) {
		Cursor categoryCursor = context.getContentResolver().query(Consts.CONTENT_CATEGORY_URI, Consts.PROJECTION_CATEGORIES, Consts.DB_COLUMN_CATEGORY_ID + "=?", new String[] {"" + categoryId}, null);;
		if (categoryCursor.getCount() > 0) {
			categoryCursor.moveToNext();
			Integer id = categoryCursor.getInt(Consts.COLUMN_INDEX_ID);
			String title = categoryCursor.getString(Consts.COLUMN_INDEX_CATEGORY_TITLE);
			String image = categoryCursor.getString(Consts.COLUMN_INDEX_CATEGORY_IMAGE);
			Date lastDownloaded = new Date(categoryCursor.getLong(Consts.COLUMN_INDEX_CATEGORY_LAST_DOWNLOADED));
			Category category = new Category(id, categoryId, title, image, lastDownloaded);
			categoryCursor.close();
			return category;
		} else {
			categoryCursor.close();
		}
		return null;
	}

	public static Cursor getNewsitemsCursor(Context context) {
		return context.getContentResolver().query(Consts.CONTENT_NEWSITEM_URI, Consts.PROJECTION_NEWSITEMS, null, null, Consts.DB_COLUMN_NEWSITEM_CREATED + " DESC");
	}

	public static List<NewsItem> getFeeditems(Context context) {
		List<NewsItem> newsitems = new LinkedList<NewsItem>();
		Cursor newsitemsCursor = getNewsitemsCursor(context);
		if (newsitemsCursor.getCount() > 0) {
			while (newsitemsCursor.moveToNext()) {
				Integer id = newsitemsCursor.getInt(Consts.COLUMN_INDEX_ID);
				String title = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_TITLE);
				String author = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_AUTHOR);
				Integer categoryId = newsitemsCursor.getInt(Consts.COLUMN_INDEX_NEWSITEM_CATEGORY_ID);
				String articleImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ARTICLE_IMAGE);
				Date created = new Date(newsitemsCursor.getLong(Consts.COLUMN_INDEX_NEWSITEM_CREATED));
				String abstractTitle = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ABSTRACT_TITLE);
				String captionImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_CAPTION_IMAGE);
				String body = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_BODY);
				String idMD5 = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ID_MD5);
				newsitems.add(new NewsItem(id, title, author, categoryId, articleImage, created, abstractTitle
						, captionImage, body, idMD5));
			}
			newsitemsCursor.close();
		} else {
			newsitemsCursor.close();
		}
		return newsitems;
	}

	public static Cursor getCategoryItemsCursor(Context context, Integer categoryId) {
		return context.getContentResolver().query(Consts.CONTENT_NEWSITEM_URI, Consts.PROJECTION_NEWSITEMS, Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID + "=?", new String[] { "" + categoryId }, Consts.DB_COLUMN_NEWSITEM_CREATED + " DESC");
	}

	public static List<NewsItem> getCategoryItems(Context context, Integer categoryId) {
		List<NewsItem> newsitems = new LinkedList<NewsItem>();
		Cursor newsitemsCursor = getCategoryItemsCursor(context, categoryId);
		if (newsitemsCursor.getCount() > 0) {
			while (newsitemsCursor.moveToNext()) {
				Integer id = newsitemsCursor.getInt(Consts.COLUMN_INDEX_ID);
				String title = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_TITLE);
				String author = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_AUTHOR);
				String articleImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ARTICLE_IMAGE);
				Date created = new Date(newsitemsCursor.getLong(Consts.COLUMN_INDEX_NEWSITEM_CREATED));
				String abstractTitle = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ABSTRACT_TITLE);
				String captionImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_CAPTION_IMAGE);
				String body = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_BODY);
				String idMD5 = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ID_MD5);
				newsitems.add(new NewsItem(id, title, author, categoryId, articleImage, created, abstractTitle
						, captionImage, body, idMD5));
			}
			newsitemsCursor.close();
		} else {
			newsitemsCursor.close();
		}
		return newsitems;
	}

	public static Cursor getCategoryItemsLimitCursor(Context context, Integer categoryId, Integer limit) {
		return context.getContentResolver().query(Consts.CONTENT_NEWSITEM_URI, Consts.PROJECTION_NEWSITEMS, Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID + "=?", new String[] { "" + categoryId }, Consts.DB_COLUMN_NEWSITEM_CREATED + " DESC LIMIT " + limit);
	}

	public static List<NewsItem> getCategoryItemsLimit(Context context, Integer categoryId, Integer limit) {
		List<NewsItem> newsitems = new LinkedList<NewsItem>();
		Cursor newsitemsCursor = getCategoryItemsLimitCursor(context, categoryId, limit);
		if (newsitemsCursor.getCount() > 0) {
			while (newsitemsCursor.moveToNext()) {
				Integer id = newsitemsCursor.getInt(Consts.COLUMN_INDEX_ID);
				String title = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_TITLE);
				String author = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_AUTHOR);
				String articleImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ARTICLE_IMAGE);
				Date created = new Date(newsitemsCursor.getLong(Consts.COLUMN_INDEX_NEWSITEM_CREATED));
				String abstractTitle = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ABSTRACT_TITLE);
				String captionImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_CAPTION_IMAGE);
				String body = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_BODY);
				String idMD5 = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ID_MD5);
				newsitems.add(new NewsItem(id, title, author, categoryId, articleImage, created, abstractTitle
						, captionImage, body, idMD5));
			}
			newsitemsCursor.close();
		} else {
			newsitemsCursor.close();
		}
		return newsitems;
	}

	public static Cursor getAllCategoryItemsCursor(Context context) {
		return context.getContentResolver().query(Consts.CONTENT_NEWSITEM_URI, Consts.PROJECTION_NEWSITEMS, null, null, Consts.DB_COLUMN_NEWSITEM_CREATED + " DESC");
	}

	public static List<NewsItem> getAllCategoryItems(Context context) {
		List<NewsItem> newsitems = new LinkedList<NewsItem>();
		Cursor newsitemsCursor = getAllCategoryItemsCursor(context);
		if (newsitemsCursor.getCount() > 0) {
			while (newsitemsCursor.moveToNext()) {
				Integer id = newsitemsCursor.getInt(Consts.COLUMN_INDEX_ID);
				String title = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_TITLE);
				String author = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_AUTHOR);
				Integer categoryId = newsitemsCursor.getInt(Consts.COLUMN_INDEX_NEWSITEM_CATEGORY_ID);
				String articleImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ARTICLE_IMAGE);
				Date created = new Date(newsitemsCursor.getLong(Consts.COLUMN_INDEX_NEWSITEM_CREATED));
				String abstractTitle = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ABSTRACT_TITLE);
				String captionImage = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_CAPTION_IMAGE);
				String body = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_BODY);
				String idMD5 = newsitemsCursor.getString(Consts.COLUMN_INDEX_NEWSITEM_ID_MD5);
				newsitems.add(new NewsItem(id, title, author, categoryId, articleImage, created, abstractTitle
						, captionImage, body, idMD5));
			}
			newsitemsCursor.close();
		} else {
			newsitemsCursor.close();
		}
		return newsitems;
	}

	// ********************* channel ********************

	public static void updateCategoryTimestamp(Context context, Integer id, Date lastDownloaded) {
		ContentValues values = new ContentValues();
		values.put(Consts.DB_COLUMN_CATEGORY_LAST_DOWNLOADED, lastDownloaded.getTime());
		context.getContentResolver().update(Consts.CONTENT_CATEGORY_URI, values, Consts.DB_COLUMN_ID + "=?", new String[] { "" + id });
	}

	public static void updateCategoryImage(Context context, Integer categoryId, String image) {
		ContentValues values = new ContentValues();
		values.put(Consts.DB_COLUMN_CATEGORY_IMAGE, image);
		context.getContentResolver().update(Consts.CONTENT_CATEGORY_URI, values, Consts.DB_COLUMN_CATEGORY_ID + "=?", new String[] { "" + categoryId });
	}

	// ********************* feeditem ********************

	public static void addNewsitems(Context context, List<NewsItem> newsitems) {
		ContentValues[] listValues = new ContentValues[newsitems.size()];
		for (int i = 0; i < newsitems.size(); i++) {
			NewsItem newsitem = newsitems.get(i); 
			ContentValues values = new ContentValues();
			values.put(Consts.DB_COLUMN_NEWSITEM_TITLE, newsitem.getTitle());
			values.put(Consts.DB_COLUMN_NEWSITEM_AUTHOR, newsitem.getAuthor());
			values.put(Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID, newsitem.getCategoryId());
			values.put(Consts.DB_COLUMN_NEWSITEM_ARTICLE_IMAGE, newsitem.getArticleImage());
			values.put(Consts.DB_COLUMN_NEWSITEM_CREATED, newsitem.getCreated().getTime());
			values.put(Consts.DB_COLUMN_NEWSITEM_ABSTRACT_TITLE, newsitem.getAbstractTitle());
			values.put(Consts.DB_COLUMN_NEWSITEM_CAPTION_IMAGE, newsitem.getImageCaption());
			values.put(Consts.DB_COLUMN_NEWSITEM_BODY, newsitem.getBody());
			values.put(Consts.DB_COLUMN_NEWSITEM_ID_MD5, PrimitiveUtils.md5(newsitem.getTitle() + newsitem.getCreated()));
			listValues[i] = values;
		}
		try {
			context.getContentResolver().bulkInsert(Consts.CONTENT_NEWSITEM_URI, listValues);
		} catch (SQLException ce) {
			for (NewsItem newsitem : newsitems) {
				try {
					addNewsitem(context, newsitem.getTitle(), newsitem.getAuthor(), newsitem.getCategoryId(), newsitem.getArticleImage()
							, newsitem.getCreated(), newsitem.getAbstractTitle(), newsitem.getImageCaption(), newsitem.getBody());
				} catch (Exception ex) {
				}
			}
		}
	}

	public static void addNewsitem(Context context, String title, String author, Integer categoryId, String articleImage
			, Date created, String abstractTitle, String captionImage, String body) {
		ContentValues values = new ContentValues();
		values.put(Consts.DB_COLUMN_NEWSITEM_TITLE, title);
		values.put(Consts.DB_COLUMN_NEWSITEM_AUTHOR, author);
		values.put(Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID, categoryId);
		values.put(Consts.DB_COLUMN_NEWSITEM_ARTICLE_IMAGE, articleImage);
		values.put(Consts.DB_COLUMN_NEWSITEM_CREATED, created.getTime());
		values.put(Consts.DB_COLUMN_NEWSITEM_ABSTRACT_TITLE, abstractTitle);
		values.put(Consts.DB_COLUMN_NEWSITEM_CAPTION_IMAGE, captionImage);
		values.put(Consts.DB_COLUMN_NEWSITEM_BODY, body);
		values.put(Consts.DB_COLUMN_NEWSITEM_ID_MD5, PrimitiveUtils.md5(title + created));
		context.getContentResolver().insert(Consts.CONTENT_NEWSITEM_URI, values);
	}

	public static void deleteFeeditem(Context context, NewsItem newsitem) {
		context.getContentResolver().delete(Consts.CONTENT_NEWSITEM_URI, Consts.DB_COLUMN_ID + "=?", new String[] { "" + newsitem.getId() });
	}

	public static void deleteOldFeeditems(Context context, Long timestamp) {
		context.getContentResolver().delete(Consts.CONTENT_NEWSITEM_URI, Consts.DB_COLUMN_NEWSITEM_CREATED + "<?", new String[] { "" + timestamp });
	}

}
