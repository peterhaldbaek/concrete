/*
 *  Copyright (c) 2010 Peter Haldbæk, peter.haldbaek@gmail.com
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.briskbee.concrete.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.briskbee.concrete.db.ConcreteDbHelper;

/**
 * @author Peter Haldbæk, peter.haldbaek@gmail.com
 */
public class DbHelper extends SQLiteOpenHelper implements ConcreteDbHelper {
	private static final String DATABASE_NAME = "concrete-example.db";
	private static final int DATABASE_VERSION = 1;
	private static String sql =
		"CREATE TABLE foo (" +
		BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"s TEXT NOT NULL, " +
		"i INTEGER NOT NULL, " +
		"d INTEGER NOT NULL);";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
	
	private void createTables(SQLiteDatabase db) {
		Log.d(this.getClass().getSimpleName(), "onCreate: " + sql);
		db.execSQL(sql);
	}
}
