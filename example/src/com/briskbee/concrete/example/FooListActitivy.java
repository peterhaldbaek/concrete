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
package com.briskbee.concrete.example;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.briskbee.concrete.RecordAdapter;
import com.briskbee.concrete.Session;
import com.briskbee.concrete.example.context.ExampleContext;
import com.briskbee.concrete.example.db.DbHelper;
import com.briskbee.concrete.example.record.Foo;

/**
 * @author Peter Haldb�k, peter.haldbaek@gmail.com
 */
public class FooListActitivy extends ListActivity {
	public static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foolist);
        
        // Initialize session
        Session.setContext(new ExampleContext(this));
        
		Button button = (Button)findViewById(R.id.foolist_btn_add);
		OnClickListener buttonListener = new OnClickListener() {
			public void onClick(View v) {
				// Start foo activity
				Intent intent = new Intent(v.getContext(), FooActivity.class);
				startActivityForResult(intent, 0);
			}
		};
		button.setOnClickListener(buttonListener);
		
        populate();
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		populate();
	}

	private void populate() {
		Foo foo = new Foo(Session.getContext());
		List<Foo> list = foo.find();
		List<? extends Map<String, ?>> map = Foo.toMap(list);
		
		String[] from = new String[]{ "s", "i", "d" };
		int[] to = new int[]{ R.id.foolistitem_s, R.id.foolistitem_i, R.id.foolistitem_d };
		
		RecordAdapter adapter = new RecordAdapter(this, map, R.layout.foolistitem, from, to);
		setListAdapter(adapter);
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Start foo activity
		Intent intent = new Intent(v.getContext(), FooActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}
}