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

package xomaya.components.filters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * An MOV file filter
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class MOVFileFilter extends FileFilter
{

    public MOVFileFilter()
    {
        this("MOV files only", new String[] {
            ".mov"
        });
    }

    public MOVFileFilter(String description, String extension)
    {
        this(description, new String[] {
            extension
        });
    }

    public MOVFileFilter(String description, String extensions[])
    {
        if(description == null)
            this.description = extensions[0];
        else
            this.description = description;
        this.extensions = (String[])(String[])extensions.clone();
        toLower(this.extensions);
    }

    private void toLower(String array[])
    {
        int i = 0;
        for(int n = array.length; i < n; i++)
            array[i] = array[i].toLowerCase();

    }

    public String getDescription()
    {
        return description;
    }

    public boolean accept(File file)
    {
        if(file.isDirectory())
            return true;
        String path = file.getAbsolutePath().toLowerCase();
        int i = 0;
        for(int n = extensions.length; i < n; i++)
        {
            String extension = extensions[i];
            if(path.endsWith(extension) && path.charAt(path.length() - extension.length() - 1) == '.')
                return true;
        }

        return false;
    }

    String description;
    String extensions[];
}
