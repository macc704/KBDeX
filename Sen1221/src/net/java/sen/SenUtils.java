/*
 * SenUtils.java - utilities for Sen.
 * 
 * Copyright (C) 2001, 2002 Takashi Okamoto <tora@debian.org>
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

package net.java.sen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class SenUtils {
    private static char separator = System.getProperty("file.separator")
            .charAt(0);

    /**
     * Get real path. Real path means the path which is added to sen's home
     * directory expressed by sen.home property. If sen.home property is null,
     * it's same as original path.
     * 
     * @param name
     * @return path
     */
    public static String getRealPath(String name) {
        String senHome = System.getProperty("sen.home");
        if (senHome == null) {
            return name;
        } else {
            return senHome + File.separatorChar + name;
        }
    }

    /**
     * load file into char arrray
     * 
     * @param str
     *            file name
     * @return char array which is representation of file.
     */
    public static char[] loadFile(String str) {
        char c[] = null;
        BufferedReader reader = null;
        try {

            File f = new File(str);
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f)));
            int len = (int) f.length();
            c = new char[len];
            for (int i = 0; i < len; i++) {
                int tmp = reader.read();
                c[i] = (char) tmp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader!=null)
                    reader.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return c;
    }

    final public static int readBSInt(InputStream is) throws IOException {
        int i1 = is.read();
        int i2 = is.read();
        int i3 = is.read();
        int i4 = is.read();

        return i1 + (i2 << 8) + (i3 << 16) + (i4 << 24);
    };

    final public static int readBSInt(RandomAccessFile is) throws IOException {
        int i1 = is.read();
        int i2 = is.read();
        int i3 = is.read();
        int i4 = is.read();
        i1 = ((((i1) >= 0) ? (i1) : (256 + i1)));
        i2 = ((((i2) >= 0) ? (i2) : (256 + i2)));
        i3 = ((((i3) >= 0) ? (i3) : (256 + i3)));
        i4 = ((((i4) >= 0) ? (i4) : (256 + i4)));

        return i1 + (i2 << 8) + (i3 << 16) + (i4 << 24);
    };

    final public static short readBSShort(RandomAccessFile is)
            throws IOException {
        int i1 = is.read();
        int i2 = is.read();

        i1 = ((((i1) >= 0) ? (i1) : (256 + i1)));
        i2 = ((((i2) >= 0) ? (i2) : (256 + i2)));

        return (short) (i1 + (i2 << 8));
    };

    final public static short readShort(RandomAccessFile is) throws IOException {
        int i1 = is.read();
        int i2 = is.read();

        return (short) ((i1 << 8) + i2);

    };

    public static String getPath(String path, String parent) {
        if (path.charAt(0) != separator
                || (path.length() > 1 && path.charAt(1) == ':' && separator == '\\')) {
            return parent + separator + path;
        } else {
            return path;
        }

    }
    /*
     * public static short lengthInDATCode(char[] c, int pos, int length){ byte
     * b[] = new byte[length]; if(pos+length >= c.length){ length = c.length-pos; }
     * for(int i=0;i <length;i++){ b[i] = (c[i+pos]
     * <128)?(byte)c[i+pos]:(byte)(c[i+pos]-256); } try{ return (short)new
     * String(b,Configurator.getConf().getCharset()).length(); } catch (Exception
     * e){ throw new RuntimeException(e.toString()); } }
     * 
     * public static char[] unicodeToDATCode(char[] c){ char n[] = null;
     * 
     * try{ byte b[] = new
     * String(c).getBytes(Configurator.getConf().getCharset()); n = new
     * char[b.length]; for(int i=0;i <b.length;i++){ n[i] =
     * ((char)(((b[i])>=0)?(b[i]):(256+(char)b[i]))); } } catch (Exception e){
     * throw new RuntimeException(e.toString()); } return n; }
     * 
     * public static int mulibyteLength(char[] c,int pos){ char n[] = new
     * char[pos]; for(int i=0;i <n.length;i++)n[i] = c[i]; try{ byte b[] = new
     * String(n).getBytes(Configurator.getConf().getCharset()); return b.length; }
     * catch (Exception e){ throw new RuntimeException(e.toString()); } }
     * 
     * public static void main(String args[]){ char c[] =
     * unicodeToDATCode("ƒeƒXƒg".toCharArray());
     * System.out.println(lengthInDATCode(c, 2, 4));
     *  }
     */
}