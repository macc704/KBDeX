/*
 * PairObject.java - PairObject
 * 
 * Copyright (C) 2001, 2002 Taku Kudoh, Takashi Okamoto Takashi Okamoto
 * <tora@debian.org>
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

package net.java.sen.tools;

public class PairObject implements Comparable {
  public Object key = null;
  public Object value = null;
  public PairObject(Object key, Object value) {
    this.key = key;
    this.value = value;
  }
  public int compareTo(Object o) {
    return ((Comparable) key).compareTo((Comparable) (((PairObject) o).key));
  }
}

