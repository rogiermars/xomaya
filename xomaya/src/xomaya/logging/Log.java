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
package xomaya.logging;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import xomaya.application.Globals;
import xomaya.application.Registry;

/**
 * This is the Log class. Application logging is done through this
 * class.
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Log
{

    public Log(Class clazz)
    {
        writer = null;
        this.clazz = null;
        system = true;
        console = true;
        file = true;
        try
        {
            Calendar c = Calendar.getInstance();
            //FIX ME.
            String fileName = "." + File.separator + "logs" + File.separator + "xomaya-" + c.get(5) + "-" + c.get(2) + "-" + c.get(1) + ".log";
            File f = new File(fileName);
            FileOutputStream fos = new FileOutputStream(f, true);
            writer = new PrintWriter(fos);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
            ex.printStackTrace();
        }
        this.clazz = clazz;
    }

    public void println(Exception ex)
    {
        println(ex.getMessage());
    }

    public void println(Error er)
    {
        println(er.getMessage());
    }

    public void println(Object o)
    {
        println(o.toString());
    }

    public void print(String o)
    {
        try
        {
            if(system){
                System.out.print(o);
            }
            if(this.console)
            {
                Console c = (Console)Registry.get("Console");
                if( c != null ){
                    c.print(o);
                }
            }
            if(file)
            {
                writer.print(o);
                writer.flush();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void println(String str)
    {
        try
        {
            long l = System.currentTimeMillis();
            Date d = new Date(l);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
            String sd = df.format(d);
            String line = sd + ":" + clazz.getName() + ":" + str;
            if(system){
                System.out.println(line);
            }
            if(this.console)
            {
                Console c = (Console)Registry.get("Console");
                if( c != null ){
                    c.println(line);
                }
            }
            if(file)
            {
                writer.println(line);
                writer.flush();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    PrintWriter writer;
    Class clazz;
    boolean system;
    boolean console;
    boolean file;
}
