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
package com.briskbee.concrete;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import android.database.Cursor;


/**
 * @author Peter Haldbæk, peter.haldbaek@gmail.com
 */
public class RelationRecordTest extends BaseTestCase {
	
	@Test
	public void testLoad() {
		Cursor cursor = new MyCursor();
		when(database.rawQuery(anyString(), any(String[].class))).thenReturn(cursor);
		
		Session.setContext(context);
		RelationRecord record = new RelationRecord(Session.getContext());
		record.load(1);
		
		String sql = "SELECT RelationRecord._id AS _id, RelationRecord.s AS s, SimpleRecord._id AS sr__id, SimpleRecord.d AS sr_d, SimpleRecord.i AS sr_i, SimpleRecord.s AS sr_s FROM RelationRecord, SimpleRecord WHERE RelationRecord.SimpleRecord_id=SimpleRecord._id AND RelationRecord._id = ?";
		String[] selectionArgs = new String[]{ "1" };
		verify(database).rawQuery(sql, selectionArgs);
	}
}
