/*
 * CSVData.java
 * 
 * Copyright (C) 2004 Takashi Okamoto
 * Tsuyoshi Fukui <fukui556@oki.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package net.java.sen.util;

import java.util.*;
import java.io.*;

/**
 * create CSV string. 
 */
public class CSVData {
	protected LinkedList elements;
	
	/**
	 * Constructor for CSVData
     */
    public CSVData() {
    	elements = new LinkedList();
    }
    
    /**
     * Append the specified element to the buffer.
     * @param element element to be appended.
     */
    public void append(String element) {
    	elements.add(element);
    }

    /**
     * Insert the specified element to the buffer.
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     */
    public void insert(int index, String element) {
    	elements.add(index, element);
    }
    
    /**
     * Removes the element at the specified position in the buffer.
     * @param index index at which the specified element is to be removed.
     */
    public void remove(int index) {
    	elements.remove(index);
    }

    /**
     * Replaces the element at the specified position in the buffer with the specified element.
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(int index, String element) {
    	elements.set(index, element);
    }

    /**
     * remove all element.
     */
    public void clear() {
    	elements.clear();
    }

    /**
     * Return a string representation.
     * <p>
     * @return string representation of this CSVData.
     */
    public String toString() {
    	StringBuffer buf = new StringBuffer();
    	Iterator itr = elements.iterator();
    	boolean isFirst = true;
    	while (itr.hasNext()) {
    		String element = enquote((String)itr.next());
    		if (isFirst) {
    			isFirst = false;
    		} else {
    			buf.append(',');
    		} 
    		buf.append(element);
    	}
    	return new String(buf);
    }

    protected String enquote(String element) {
    	if (element.length() == 0) {
    		return element;
    	}
    	if (element.indexOf('"') == -1 && element.indexOf(',') == -1) {
    		return element;
    	}
    	
    	int size = element.length();
    	StringBuffer buf = new StringBuffer(size*2);
    	buf.append('"');
    	for (int i = 0; i < size; i++) {
    		char ch = element.charAt(i);
    		if (ch == '"') {
    			buf.append('"');
    		}
    		buf.append(ch);
    	}
    	buf.append('"');
    	return new String(buf);
    }
    
    public static void main(String[] args) throws Exception {
    	BufferedReader in
		= new BufferedReader(new InputStreamReader(System.in));
    	String line = null;
    	CSVData csvData = new CSVData();
    	while ((line = in.readLine()) != null) {
    		if (line.equals("END")) {
    			System.out.println(csvData);
    			csvData.clear();
    			continue;
    		}
    		csvData.append(line);
    	}
    }
}
