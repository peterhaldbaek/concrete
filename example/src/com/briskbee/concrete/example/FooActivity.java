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
package com.briskbee.concrete.example;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.briskbee.concrete.ConcreteContext;
import com.briskbee.concrete.Session;
import com.briskbee.concrete.example.record.Foo;

/**
 * @author Peter Haldbæk, peter.haldbaek@gmail.com
 */
public class FooActivity extends Activity {
	private long id;
	
	private EditText mEditString;
	private EditText mEditInteger;
	private TextView mTextDate;
	
	private ConcreteContext context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.foo);
        
		id = getIntent().getLongExtra("id", -1);
		
        context = Session.getContext();
		mEditString = (EditText) findViewById(R.id.foo_string);
		mEditInteger = (EditText) findViewById(R.id.foo_integer);
		mTextDate = (TextView) findViewById(R.id.foo_date);
		
		Button btnAdd = (Button) findViewById(R.id.foo_btn_add);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = mEditString.getText().toString();
				String i = mEditInteger.getText().toString();
				
				// Store foo in database
				Foo foo = new Foo(context);
				foo.s = s;
				foo.i = (i == null ? 0 : Integer.parseInt(i));
				foo.d = new Date();
				foo.saveOrUpdate();
				
				// Return to previous activity
				setResult(RESULT_OK);
				finish();
			}
		});
		
		Button btnCancel = (Button) findViewById(R.id.foo_btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Go back
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		Button btnDelete = (Button) findViewById(R.id.foo_btn_delete);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Foo foo = new Foo(context);
				foo.deleteAll("_id = ?", String.valueOf(id));
				
				// Return to previous activity
				setResult(RESULT_OK);
				finish();
			}
		});
		
		if (!isNew()) {
			// Populate fields
			populate();
		}
	}
	
	private boolean isNew() {
		return id < 0;
	}
	
	private void populate() {
		// Load record
		Foo foo = new Foo(context);
		foo.load(id);
		
		mEditString.setText(foo.s);
		mEditInteger.setText(String.valueOf(foo.i));
		mTextDate.setText(FooListActitivy.DATEFORMAT.format(foo.d));
	}
}
