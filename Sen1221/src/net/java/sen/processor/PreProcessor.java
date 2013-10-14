/*
 * PreProcessor.java - interface of preprocessor for tagger.
 * 
 * Copyright (C) 2004 Tsuyoshi Fukui Tsuyoshi Fukui <fukui556@oki.com>
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
 * along with Sen; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package net.java.sen.processor;

import java.util.Map;

public interface PreProcessor {
    public String process(String input, Map postProcessInfo);
}