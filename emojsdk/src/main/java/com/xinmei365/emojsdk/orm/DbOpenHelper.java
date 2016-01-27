/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xinmei365.emojsdk.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 5;
	private static final String DTATBASE_NAME = "emoj_sdk.db";
	//private static DbOpenHelper instance;

	private static final String CREATE_EMOJID_PROPERTY = "CREATE TABLE "
			+ EmojIDPropertyDao.TABLE_NAME + " ("
			+ EmojIDPropertyDao.COLUMN_NAME_EMOJTID + " TEXT PRIMARY KEY, "
			+ EmojIDPropertyDao.COLUMN_NAME_EMOJ_PROPERTY + " TEXT);";


	private static final String CREATE_CANDIATE_EMOJ_TABLE = "CREATE TABLE "
			+ CandiateEmojDao.TABLE_NAME + " ("
			+ CandiateEmojDao.COLUMN_NAME_EMOJTAG + " TEXT PRIMARY KEY, "
			+ CandiateEmojDao.COLUMN_NAME_EMOJ_CONTENT + " TEXT);";

	private static final String CREATE_RECENT_EMOJ_TABLE = "CREATE TABLE "
			+ RecentEmojDao.TABLE_NAME + " ("
			+ RecentEmojDao.COLUMN_NAME_TIMESTAMP + " TEXT PRIMARY KEY, "
			+ RecentEmojDao.COLUMN_NAME_ID + " TEXT, "
			+ RecentEmojDao.COLUMN_NAME_unicode + " TEXT, "
			+ RecentEmojDao.COLUMN_NAME_CategoryId + " TEXT, "
			+ RecentEmojDao.COLUMN_NAME_EMOJ_TYPE + " TEXT, "
			+ RecentEmojDao.COLUMN_NAME_CategoryName + " TEXT);";

	public DbOpenHelper(Context context) {
		super(context, DTATBASE_NAME, null, DATABASE_VERSION);
	}
//	public static void init(Context context){
//		if (instance == null) {
//			instance = new DbOpenHelper(context.getApplicationContext());
//		}
//	}
//	public static DbOpenHelper getInstance() {
//		return instance;
//	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CANDIATE_EMOJ_TABLE); //search emoji content by tag
		db.execSQL(CREATE_EMOJID_PROPERTY);
		db.execSQL(CREATE_RECENT_EMOJ_TABLE); //recent emoji table

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
//	public void closeDB() {
//	    if (instance != null) {
//	        try {
//	            SQLiteDatabase db = instance.getWritableDatabase();
//	            db.close();
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	        instance = null;
//	    }
//	}
	
}
