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
package com.briskbee.concrete.example.context;

import android.content.Context;

import com.briskbee.concrete.ConcreteContext;
import com.briskbee.concrete.db.ConcreteDbHelper;
import com.briskbee.concrete.example.db.DbHelper;
import com.briskbee.concrete.log.AndroidLog;
import com.briskbee.concrete.log.Log;

/**
 * @author Peter Haldbæk, peter.haldbaek@gmail.com
 */
public class ExampleContext implements ConcreteContext {
	private Context context;
	private ConcreteDbHelper dbHelper;
	private Log log;
	
	public ExampleContext(Context context) {
		this.context = context;
	}
	
	@Override
	public ConcreteDbHelper getDbHelper() {
		if (dbHelper == null)
			dbHelper = new DbHelper(context);
		
		return dbHelper;
	}

	@Override
	public Log getLog() {
		if (log == null)
			log = new AndroidLog();
		
		return log;
	}
}
