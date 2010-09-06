/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import xomaya.logging.Log;

/**
 *
 * @author beecrofs
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
