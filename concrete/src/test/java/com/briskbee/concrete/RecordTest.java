package com.briskbee.concrete;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import android.database.Cursor;


public class RecordTest extends BaseTestCase {
	
	@Test
	public void testLoad() {
		Cursor cursor = new MyCursor();
		when(database.rawQuery(anyString(), any(String[].class))).thenReturn(cursor);
		
		Session.setContext(context);
		SimpleRecord record = new SimpleRecord(Session.getContext());
		record.load(1);
		
		String sql = "SELECT SimpleRecord._id AS _id, SimpleRecord.d AS d, SimpleRecord.i AS i, SimpleRecord.s AS s FROM SimpleRecord WHERE SimpleRecord._id = ?";
		String[] selectionArgs = new String[]{ "1" };
		verify(database).rawQuery(sql, selectionArgs);
	}
	
	@Test
	public void testFind() {
		Cursor cursor = new MyCursor();
		when(database.rawQuery(anyString(), any(String[].class))).thenReturn(cursor);
		
		Session.setContext(context);
		SimpleRecord record = new SimpleRecord(Session.getContext());
		record.find();
		
		String sql = "SELECT SimpleRecord._id AS _id, SimpleRecord.d AS d, SimpleRecord.i AS i, SimpleRecord.s AS s FROM SimpleRecord";
		verify(database).rawQuery(sql, null);
	}
}
