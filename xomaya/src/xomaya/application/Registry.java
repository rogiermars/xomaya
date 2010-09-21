/**
 * Copyright (c) 2010 Sean Beecroft, http://xomaya.com/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package xomaya.application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import xomaya.logging.Log;

/**
 *
 * This documentation is part of the Xomaya Express  <A HREF="http://www.xomaya.com">screen capture software</A> suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Registry {
    static Map ht = new HashMap();
    public static void register(String key, Object value)
    {
        ht.put(key,value);
    }

    public static Object get(String key)
    {
        return ht.get(key);
    }

    public static void printAll()
    {
        Set set = ht.keySet();
        Iterator i = set.iterator();
        while(i.hasNext()){
            String k = (String)i.next();
            Object o = ht.get(k);
            logger.println(k + "->" + o);
        }
    }
    static Log logger = new Log(Registry.class);
}
