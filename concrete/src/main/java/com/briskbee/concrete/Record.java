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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * <p>
 * The Record class represents a record in the database. It contains methods
 * for basic CRUD operations as well as methods for querying the database
 * for records. Use this class by creating a subclass with public variables:
 * </p>
 * 
 * <pre>
 * public class Foo extends Record<Foo> {
 *   public int i;
 *   public String s;
 *   public Date d;
 *   
 *   public Foo(ConcreteContext context) {
 *     super(context);
 *   }
 * }
 * </pre>
 * 
 * <p>
 * This represents a record in a table called <code>foo</code>. The table
 * contains the elements i of type <code>integer</code>, s of type
 * <code>text</code> and d of type <code>integer</code> (dates are modelled
 * as integers).
 * </p>
 * 
 * @author Peter Haldbæk, peter.haldbaek@gmail.com
 */
public abstract class Record<T extends Record<?>> {
	private String DELIMITER = "_";
	
	public long _id = -1;
	
	private ConcreteContext context;
	
	private String TABLE;
	private String fieldName; // The name of the field this class is in the declaring class
	
	private Map<String, Field> fields;
	private Map<String, Record<T>> relations; // <Field name, Record>
	
	public Record(ConcreteContext context) {
		this.context = context;
	}
	
	/**
	 * Loads the record with the specified id.
	 * @param id id of the record to load
	 */
	public void load(long id) {
		this._id = id;
		
		SQLiteDatabase db = context.getDbHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			// Create sql
			String[] selectionArgs = new String[]{ String.valueOf(_id) };
			String sql = createSelectSql(selectionArgs);
			
			// Execute query
			cursor = db.rawQuery(sql, selectionArgs);
			
			// Extract data
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String columnName = cursor.getColumnName(i);
					setValue(this, columnName, cursor, i);
				}
				
				cursor.moveToNext();
			}
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		} finally {
			if (cursor != null)
				cursor.close();
			if (db != null && db.isOpen())
				db.close();
		}
	}
	
	/* package */ String createSelectSql(String[] selectionArgs) {
		StringBuffer sql = new StringBuffer("SELECT ");
		
		// Add fields
		getSelectList(sql, "", true);
		
		sql.append(" FROM ");
		sql.append(getTable());
		for (Record<T> relation : getRelations().values())
			sql.append(", " + relation.getTable());
		if ((selectionArgs != null && selectionArgs.length > 0) || !getRelations().isEmpty()) {
			sql.append(" WHERE ");
			// inner join
			// TODO Add support for more than one level of relations
			for (Record<T> relation : getRelations().values()) {
				sql.append(getTable());
				sql.append(".");
				sql.append(relation.getTable());
				sql.append("_id=");
				sql.append(relation.getTable());
				sql.append("._id AND ");
			}
			sql.append(getTable());
			sql.append("._id = ?");
		}
		
		return sql.toString();
	}
	
	private void setValue(Record<T> record, String columnName, Cursor cursor, int column) {
		Field field = record.getFields().get(columnName);
		if (field != null) {
			// Simple field
			Object value = ReflectionUtil.getValue(field, cursor, column);
			setValue(record, field, value);
		} else {
			String f = columnName.substring(0, columnName.indexOf(DELIMITER));
			Record<T> relation = record.getRelations().get(f);
			if (relation != null) {
				// Relation
				columnName = columnName.substring(columnName.indexOf(DELIMITER)+1);
				setValue(relation, columnName, cursor, column);
			}
		}
	}
	
	private String getSelectList(StringBuffer selectList, String prefix, boolean first) {
		// Simple fields first
		for (Field field : getFields().values()) {
			appendField(selectList, getTable(), prefix, field, first);
			first = false;
		}
		
		// Then relations recursively
		for (String fieldName : getRelations().keySet()) {
			Record<T> relation = getRelations().get(fieldName);
			relation.getSelectList(selectList, isEmpty(prefix) ? relation.fieldName : prefix + DELIMITER + relation.fieldName, first);
		}
		
		return selectList.toString();
	}
	
	private void setValue(Object receiver, Field field, Object value) {
		try {
			field.set(receiver, value);
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		}
	}
	
	private void appendField(StringBuffer buffer, String table, String prefix, Field field, boolean first) {
		if (!first)
			buffer.append(", ");
		buffer.append(table);
		buffer.append(".");
		buffer.append(field.getName());
		buffer.append(" AS ");
		if (!isEmpty(prefix)) {
			buffer.append(prefix);
			buffer.append(DELIMITER);
		}
		buffer.append(field.getName());
	}
	
	/**
	 * Saves the record in the database.
	 */
	public void save() {
		SQLiteDatabase db = context.getDbHelper().getWritableDatabase();
		try {
			ContentValues values = getContentValues();
			_id = db.insert(getTable(), null, values);
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}
	
	/**
	 * Updates the record in the database.
	 */
	public void update() {
		SQLiteDatabase db = context.getDbHelper().getWritableDatabase();
		try {
			ContentValues values = getContentValues();
			String whereClause = BaseColumns._ID + " = ?";
			String[] whereArgs = new String[]{ String.valueOf(_id) };
			db.update(getTable(), values, whereClause, whereArgs);
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}
	
	/**
	 * Either saves or updates the record in the database. If the
	 * record has it id field set, the record is updated; otherwise
	 * a new record is created in the database.
	 */
	public void saveOrUpdate() {
		if (_id > -1)
			update();
		else
			save();
	}
	
	/**
	 * Deletes the record in the database (without cascade).
	 */
	public void delete() {
		SQLiteDatabase db = context.getDbHelper().getWritableDatabase();
		try {
			String[] whereArgs = new String[]{ String.valueOf(_id) };
			db.delete(getTable(), BaseColumns._ID + " = ?", whereArgs);
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}
	
	/**
	 * Returns the first record found by the specified query.
	 * 
	 * @param selection the selection query
	 * @param selectionArgs the arguments for the selection query
	 * @param orderBy the order of the records
	 * @return the first record found by the specified query
	 */
	public T first(String selection, String[] selectionArgs, String orderBy) {
		return find(selection, selectionArgs, null, null, orderBy, "1").get(0);
	}
	
	/**
	 * Returns all records for the class.
	 * 
	 * @return all records for the class
	 */
	public List<T> find() {
		return find(null, null, null, null, null, null);
	}
	
	/**
	 * Returns the records matching the specified search criteria.
	 * 
	 * @param selection the selection query
	 * @param selectionArgs the arguments for the selection query
	 * @param groupBy the group by clause
	 * @param having the having clause
	 * @param orderBy the order of the records
	 * @param limit the limit clause
	 * @return the records matching the specified search criteria
	 */
	public List<T> find(String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		SQLiteDatabase db = context.getDbHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			List<T> result = new ArrayList<T>();
			
			// Create sql
			String sql = createSelectSql(selectionArgs);
			
			// Execute query
			cursor = db.rawQuery(sql, selectionArgs);
			
			// Extract data
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Constructor<? extends Record> constructor = getClass().getConstructor(new Class[]{ ConcreteContext.class });
				Record<T> o = (Record<T>)constructor.newInstance(context);
				
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String columnName = cursor.getColumnName(i);
					setValue(o, columnName, cursor, i);
				}
				
				result.add((T)o);
				cursor.moveToNext();
			}
			
			return result;
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		} finally {
			if (cursor != null)
				cursor.close();
			if (db != null && db.isOpen())
				db.close();
		}
	}
	
	/**
	 * Converts a list of records to a map which can be used by the <code>RecordAdapter</code>
	 * when populating activities (views).
	 * 
	 * @param records the list of records
	 * @return a map compatible with the <code>RecordAdapter</code>
	 * @see RecordAdapter
	 */
	public static List<? extends Map<String, ?>> toMap(List<? extends Record<?>> records) {
		if (records == null)
			return null;
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Record<?> record : records) {
			// Create new item
			Map<String, Object> item = new TreeMap<String, Object>();
			list.add(item);
			
			// Loop through all fields
			for (Field field : record.getClass().getFields()) {
				try {
					item.put(field.getName(), field.get(record));
				} catch (Exception e) {
					Session.getContext().getLog().e(Record.class.getSimpleName(), e.getMessage(), e);
					throw new ConcreteException(e);
				}
			}
			
			// Loop through all methods
			for (Method method : record.getClass().getDeclaredMethods()) {
				try {
					if (method.getParameterTypes().length == 0 && method.getName().startsWith("get")) {
						item.put(method.getName().substring(3).toLowerCase(), method.invoke(record).toString());
					}
				} catch (Exception e) {
					Session.getContext().getLog().e(Record.class.getSimpleName(), e.getMessage(), e);
					throw new ConcreteException(e);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Deletes all records of the class which fulfills the specified condition.
	 * 
	 * @param condition the condition
	 * @param arguments the arguments used in the condition
	 */
	public void deleteAll(String condition, String... arguments) {
		SQLiteDatabase db = null;
		try {
			db = context.getDbHelper().getWritableDatabase();
			db.delete(getTable(), condition, arguments);
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		} finally {
			if (db != null && db.isOpen())
				db.close();
		}
	}
	
	/**
	 * Returns the name of the underlying database table used by this class.
	 * 
	 * @return the name of the underlying database table
	 */
	protected String getTable() {
		if (TABLE == null)
			TABLE = getClass().getSimpleName();
		
		return TABLE;
	}
	
	private ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		for (Field field : getFields().values()) {
			try {
				String name = field.getName();
				if (!BaseColumns._ID.equals(name)) {
					String type = field.getType().getName();
					if ("java.lang.String".equals(type)) {
						values.put(name, (String)field.get(this));
					} else if ("long".equals(type)) {
						values.put(name, (Long)field.get(this));
					} else if ("double".equals(type)) {
						values.put(name, (Double)field.get(this));
					} else if ("float".equals(type)) {
						values.put(name, (Float)field.get(this));
					} else if ("int".equals(type)) {
						values.put(name, (Integer)field.get(this));
					} else if ("short".equals(type)) {
						values.put(name, (Short)field.get(this));
					} else if ("java.util.Date".equals(type)) {
						Date d = (Date)field.get(this);
						values.put(name, d.getTime());
					}
				}
			} catch (Exception e) {
				context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
				throw new ConcreteException(e);
			}
		}
		
		return values;
	}
	
	private Map<String, Field> getFields() {
		if (fields == null) {
			initializeFields();
		}
		
		return fields;
	}
	
	private Map<String, Record<T>> getRelations() {
		if (relations == null) {
			initializeFields();
		}
		
		return relations;
	}
	
	@SuppressWarnings("unchecked")
	private void initializeFields() {
		fields = new TreeMap<String, Field>();
		relations = new TreeMap<String, Record<T>>();
		try {
			for (Field f : getClass().getFields()) {
				Object o = f.get(this);
				if (ReflectionUtil.isRelation(o, f)) {
					((Record<T>)o).fieldName = f.getName();
					relations.put(((Record<T>)o).fieldName, (Record<T>)o);
				}
				else
					fields.put(f.getName(), f);
			}
		} catch (Exception e) {
			context.getLog().e(this.getClass().getSimpleName(), e.getMessage(), e);
			throw new ConcreteException(e);
		}
	}
	
	private boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
}
