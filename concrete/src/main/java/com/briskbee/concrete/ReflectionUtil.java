/*
 *  Copyright (c) 2010 Peter Haldb�k, peter.haldbaek@gmail.com
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

import java.lang.reflect.Field;
import java.util.Date;

import android.database.Cursor;

/**
 * Utility class used for various reflection methods.
 * 
 * @author Peter Haldb�k, peter.haldbaek@gmail.com
 */
public class ReflectionUtil {
	
	/**
	 * Returns the value of the specified field and column based on the cursor.
	 * This is used when selecting data from the database.
	 * 
	 * @param field the field of the class
	 * @param cursor the database cursor holding the value
	 * @param column the column index of the table
	 * @return the value of the specified column
	 */
	public static Object getValue(Field field, Cursor cursor, int column) {
		// Determine type of key
		String type = field.getType().getName();
		if ("java.lang.String".equals(type)) {
			return cursor.getString(column);
		} else if ("long".equals(type)) {
			return cursor.getLong(column);
		} else if ("double".equals(type)) {
			return cursor.getDouble(column);
		} else if ("float".equals(type)) {
			return cursor.getFloat(column);
		} else if ("int".equals(type)) {
			return cursor.getInt(column);
		} else if ("short".equals(type)) {
			return cursor.getShort(column);
		} else if ("java.util.Date".equals(type)) {
			long l = cursor.getLong(column);
			return new Date(l);
		}
		
		return null;
	}
	
	/**
	 * Returns <code>true</code> if the specified field is a relation. This is determined
	 * by looking at the type of the field. If the type is an instance of the
	 * <code>Record</code> class it is considered to be a relation.
	 * 
	 * @param field the field to examine
	 * @return <code>true</code> if the field is a relation; <code>false</code> otherwise
	 */
	public static boolean isRelation(Object o, Field field) {
		if (o == null) {
			return Record.class.isAssignableFrom(field.getType());
		}
		return Record.class.isInstance(o);
	}
}
