/*
 * FileAccessorFactory - factory to obtain suitable FileAccessor.
 * 
 * Copyright (C) 2002 Takashi Okamoto
 * Takashi Okamoto <tora@debian.org>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,   
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Sen; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package net.java.sen.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory to obtain following FileAccessor.
 * 
 * <pre>
 * 
 *     MappedBufferedReader: if you use j2sdk1.4, this class is used.
 *     FullBufferedReader  : if you use other than j2sdk1.4, this class
 *                           is used.
 *     RandomAccessReader  : this class is optional. If you don't have
 *  j2sdk1.4 and consulme
 *  
 *  
 * </pre>
 * 
 * If you want to specify the class, please set canonical class name at
 * net.java.sen.io.FileAccessor propertiy by JVM's -D option.
 */

public class FileAccessorFactory {

  public static FileAccessor getInstance(String name) throws IOException {
    return getInstance(new File(name));
  }

  public static FileAccessor getInstance(File file) throws IOException {
    String prop = System.getProperty("net.java.sen.io.FileAccessor");
    if (prop != null) {
      try {
        Class c = Class.forName(prop);
        Constructor con = c.getConstructor(new Class[]{File.class});
        Object i = con.newInstance(new Object[]{file});
        return (FileAccessor) i;
      } catch (ClassNotFoundException ce) {
        System.err.println("warn: " + prop + " isn't found. use default class");
      } catch (ClassCastException cce) {
      } catch (InstantiationException ie) {
      } catch (IllegalAccessException ia) {
      } catch (NoSuchMethodException me) {
      } catch (InvocationTargetException ite) {
      }
      System.err.println("warn: " + prop
          + " is invalid type. use default class");
    }

    try {
      Class.forName("java.nio.MappedByteBuffer");
      return new MappedBufferedReader(file);
    } catch (ClassNotFoundException ce) {
      return new FullBufferedReader(file);
    }
  }
}