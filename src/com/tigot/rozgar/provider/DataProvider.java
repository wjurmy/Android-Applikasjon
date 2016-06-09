package com.tigot.rozgar.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.tigot.rozgar.consts.Consts;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DataProvider extends ContentProvider {

    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME_CATEGORY = "CATEGORY";
	private static final String TABLE_NAME_NEWSITEM = "NEWSITEM";

	private static final String INDEX_NAME_CATEGORY_UNIQUE = "CATEGORY_UNIQUE";
	private static final String INDEX_NAME_NEWSITEM_UNIQUE = "NEWSITEM_UNIQUE";
 
    private static final UriMatcher sUriMatcher; 
    
    public static LinkedHashMap<String, String> sCategoryProjectionMap;  
    public static LinkedHashMap<String, String> sNewsitemProjectionMap;  
    
    private static final int QUERY_CATEGORY = 1;
    private static final int QUERY_NEWSITEM = 2;
    
    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	private Context localContext;
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION); 
        	Log.w("DatabaseHelper", "DatabaseHelper(context)");
        	localContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	Log.w("DatabaseHelper", "onCreate()");
            db.execSQL("CREATE TABLE " + TABLE_NAME_CATEGORY + " ("
                    + Consts.DB_COLUMN_ID + " INTEGER PRIMARY KEY," 
            		+ Consts.DB_COLUMN_CATEGORY_ID + " INTEGER not null,"
            		+ Consts.DB_COLUMN_CATEGORY_TITLE + " TEXT not null,"
            		+ Consts.DB_COLUMN_CATEGORY_IMAGE + " TEXT,"
            		+ Consts.DB_COLUMN_CATEGORY_LAST_DOWNLOADED + " INTEGER not null default 0"
                    + ");");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS  " + INDEX_NAME_CATEGORY_UNIQUE + " on "
                    + TABLE_NAME_CATEGORY + " (" + Consts.DB_COLUMN_CATEGORY_ID + ");");
            db.execSQL("CREATE TABLE " + TABLE_NAME_NEWSITEM + " ("
                    + Consts.DB_COLUMN_ID + " INTEGER PRIMARY KEY,"
            		+ Consts.DB_COLUMN_NEWSITEM_TITLE + " TEXT not null,"
            		+ Consts.DB_COLUMN_NEWSITEM_AUTHOR + " TEXT not null,"
                    + Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID + " INTEGER,"
            		+ Consts.DB_COLUMN_NEWSITEM_ARTICLE_IMAGE + " TEXT not null,"
            		+ Consts.DB_COLUMN_NEWSITEM_CREATED + " INTEGER not null," 
            		+ Consts.DB_COLUMN_NEWSITEM_ABSTRACT_TITLE + " TEXT not null," 
            		+ Consts.DB_COLUMN_NEWSITEM_CAPTION_IMAGE + " TEXT not null,"
            		+ Consts.DB_COLUMN_NEWSITEM_BODY + " TEXT not null,"
            		+ Consts.DB_COLUMN_NEWSITEM_ID_MD5 + " TEXT not null,"
            		+ " FOREIGN KEY(" + Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID + ") REFERENCES " + TABLE_NAME_CATEGORY + "(" + Consts.DB_COLUMN_ID + ") "
                    + ");");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS  " + INDEX_NAME_NEWSITEM_UNIQUE + " on "
                    + TABLE_NAME_NEWSITEM + " (" + Consts.DB_COLUMN_NEWSITEM_ID_MD5 + ");");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (1, 'اخبار');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (2, 'فرهنگی');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (3, 'شعر');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (4, 'سیاسی');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (5, 'اجتماعی');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (6, 'علمی');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (7, 'تاریخی');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (8, 'طنز');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (12, 'ورزش');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (13, 'داستان');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (14, 'آیامیدانید؟');");
            db.execSQL("INSERT INTO " + TABLE_NAME_CATEGORY + " ("
            		+ Consts.DB_COLUMN_CATEGORY_ID + ","
            		+ Consts.DB_COLUMN_CATEGORY_TITLE
                    + ") VALUES (15, 'هنرآشپزی');");
        }

        @Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	Log.w("DatabaseHelper", "onUpgrade()");
			Log.w("onUpgrade", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			if (oldVersion < 200) { 
				// TODO : add code here
			}
		}
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
    	Log.w("DataProvider", "onCreate()");
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    } 
    
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case QUERY_CATEGORY:
            count = db.delete(TABLE_NAME_CATEGORY, where, whereArgs);
            break;
        case QUERY_NEWSITEM:
            count = db.delete(TABLE_NAME_NEWSITEM, where, whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count; 
	}

    @Override
	public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case QUERY_CATEGORY:
            return Consts.CONTENT_TYPE_CATEGORY;
        case QUERY_NEWSITEM:
            return Consts.CONTENT_TYPE_NEWSITEM;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        } 
    }

    @Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db;
		long rowId;
        switch (sUriMatcher.match(uri)) {
        case QUERY_CATEGORY:
            db = mOpenHelper.getWritableDatabase();
            rowId = db.insert(TABLE_NAME_CATEGORY, null, initialValues);
            if (rowId > 0) {
                Uri noteUri = ContentUris.withAppendedId(Consts.CONTENT_CATEGORY_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            };
            break;
        case QUERY_NEWSITEM:
            db = mOpenHelper.getWritableDatabase();
            rowId = db.insert(TABLE_NAME_NEWSITEM, null, initialValues);
            if (rowId > 0) {
                Uri noteUri = ContentUris.withAppendedId(Consts.CONTENT_NEWSITEM_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            };
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        } 
        throw new SQLException("Failed to insert row into " + uri);
	}

    @Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
        case QUERY_CATEGORY:
            qb.setTables(TABLE_NAME_CATEGORY);
            qb.setProjectionMap(sCategoryProjectionMap);
        	break;
        case QUERY_NEWSITEM:
            qb.setTables(TABLE_NAME_NEWSITEM);
            qb.setProjectionMap(sNewsitemProjectionMap);
        	break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c; 
    }

    @Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
        switch (sUriMatcher.match(uri)) {
        case QUERY_CATEGORY:
        	count = db.update(TABLE_NAME_CATEGORY, values, selection, selectionArgs);
        	break;
        case QUERY_NEWSITEM:
        	count = db.update(TABLE_NAME_NEWSITEM, values, selection, selectionArgs);
        	break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Consts.AUTHORITY, TABLE_NAME_CATEGORY, QUERY_CATEGORY); 
        sUriMatcher.addURI(Consts.AUTHORITY, TABLE_NAME_NEWSITEM, QUERY_NEWSITEM); 

        sCategoryProjectionMap = new LinkedHashMap<String, String>();
        sCategoryProjectionMap.put(Consts.DB_COLUMN_ID, Consts.DB_COLUMN_ID); 
        sCategoryProjectionMap.put(Consts.DB_COLUMN_CATEGORY_ID, Consts.DB_COLUMN_CATEGORY_ID); 
        sCategoryProjectionMap.put(Consts.DB_COLUMN_CATEGORY_TITLE, Consts.DB_COLUMN_CATEGORY_TITLE); 
        sCategoryProjectionMap.put(Consts.DB_COLUMN_CATEGORY_IMAGE, Consts.DB_COLUMN_CATEGORY_IMAGE); 
        sCategoryProjectionMap.put(Consts.DB_COLUMN_CATEGORY_LAST_DOWNLOADED, Consts.DB_COLUMN_CATEGORY_LAST_DOWNLOADED); 
        sNewsitemProjectionMap = new LinkedHashMap<String, String>();
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_ID, Consts.DB_COLUMN_ID);
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_TITLE, Consts.DB_COLUMN_NEWSITEM_TITLE); 
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_AUTHOR, Consts.DB_COLUMN_NEWSITEM_AUTHOR); 
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID, Consts.DB_COLUMN_NEWSITEM_CATEGORY_ID);
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_ARTICLE_IMAGE, Consts.DB_COLUMN_NEWSITEM_ARTICLE_IMAGE); 
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_CREATED, Consts.DB_COLUMN_NEWSITEM_CREATED); 
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_ABSTRACT_TITLE, Consts.DB_COLUMN_NEWSITEM_ABSTRACT_TITLE); 
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_CAPTION_IMAGE, Consts.DB_COLUMN_NEWSITEM_CAPTION_IMAGE); 
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_BODY, Consts.DB_COLUMN_NEWSITEM_BODY); 
        sNewsitemProjectionMap.put(Consts.DB_COLUMN_NEWSITEM_ID_MD5, Consts.DB_COLUMN_NEWSITEM_ID_MD5); 
    }
    
}
