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

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;

/**
 * Adapter used for populating the view with a list of records. The
 * adapter uses the primary key (or id of the record) instead of
 * the position in the list which is handy when OnClick events are
 * handled in the list. 
 * 
 * @author Peter Haldbæk, peter.haldbaek@gmail.com
 */
public class RecordAdapter extends SimpleAdapter {
	private List<? extends Map<String, ?>> data;

	public RecordAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see android.widget.SimpleAdapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		if (data == null)
			return -1;
		
		Map<String, ?> map = data.get(position);
		if (map == null)
			return -1;
		
		return (Long) map.get("_id");
	}
}
