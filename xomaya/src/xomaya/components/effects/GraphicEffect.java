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
package xomaya.components.effects;

import javax.media.*;
import javax.media.format.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import xomaya.application.Globals;
import xomaya.logging.Log;

/**
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class GraphicEffect implements Effect {

    public volatile byte[] store = null;
    public volatile boolean screenCaptureEnabled = false;
    BufferedImage overlay = null;
    public static Format inputFormat;
    public static Format outputFormat;
    Format[] inputFormats;
    Format[] outputFormats;
    int flag = 0;
    SystemTimeBase systemTimeBase = new SystemTimeBase();

    static Log logger = new Log(GraphicEffect.class);

    public GraphicEffect() {

        inputFormats = new Format[]{
                    new RGBFormat(null,
                    Format.NOT_SPECIFIED,
                    Format.byteArray,
                    Format.NOT_SPECIFIED,
                    24,
                    3, 2, 1,
                    3, Format.NOT_SPECIFIED,
                    Format.TRUE,
                    Format.NOT_SPECIFIED)
                };

        outputFormats = new Format[]{
                    new RGBFormat(null,
                    Format.NOT_SPECIFIED,
                    Format.byteArray,
                    Format.NOT_SPECIFIED,
                    24,
                    3, 2, 1,
                    3, Format.NOT_SPECIFIED,
                    Format.TRUE,
                    Format.NOT_SPECIFIED)
                };
        try {
            overlay = ImageIO.read(new File("./media/footer-logo.gif"));
        } catch(Exception ex){
            logger.println("Could not locate xomaya logo");
            logger.println(ex);
            ex.printStackTrace();
            System.exit(1);
        }

    }

    // methods for interface Codec
    public Format[] getSupportedInputFormats() {
        return inputFormats;
    }

    public Format[] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return outputFormats;
        }

        if (matches(input, inputFormats) != null) {
            return new Format[]{outputFormats[0].intersects(input)};
        } else {
            return new Format[0];
        }
    }

    public Format setInputFormat(Format input) {
        inputFormat = input;
        return input;
    }

    public Format setOutputFormat(Format output) {
        logger.println("setOutputFormat");
        if (output == null || matches(output, outputFormats) == null) {
            return null;
        }
        RGBFormat incoming = (RGBFormat) output;

        Dimension size = incoming.getSize();
        int maxDataLength = incoming.getMaxDataLength();
        int lineStride = incoming.getLineStride();
        float frameRate = incoming.getFrameRate();
        int flipped = incoming.getFlipped();
        int endian = incoming.getEndian();

        if (size == null) {
            return null;
        }
        if (maxDataLength < size.width * size.height * 3) {
            maxDataLength = size.width * size.height * 3;
        }
        if (lineStride < size.width * 3) {
            lineStride = size.width * 3;
        }

        outputFormat = outputFormats[0].intersects(new RGBFormat(size,
                maxDataLength,
                null,
                frameRate,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                lineStride,
                Format.TRUE,
                Format.NOT_SPECIFIED));

        logger.println(outputFormat);

        return outputFormat;
    }

    public int process(Buffer inBuffer, Buffer outBuffer) {
        
        int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();
        validateByteArraySize(outBuffer, outputDataLength);

        outBuffer.setLength(outputDataLength);
        outBuffer.setFormat(outputFormat);
        outBuffer.setSequenceNumber(Globals.seqNo);
        
        byte[] inData = (byte[]) inBuffer.getData();
        byte[] outData = (byte[]) outBuffer.getData();

        if (flag == 0) {
            store = new byte[inData.length];
            logger.println("GraphicsEffect.process called");
            Globals.ttga = System.currentTimeMillis();
            flag++;
        }
        
        RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();
        int pxs = vfIn.getPixelStride();
        int ls = vfIn.getLineStride();
        if (vfIn.getFlipped() == Format.FALSE) {
            logger.println("Format is flipped!");
        }

        int iw = sizeIn.width;
        int ih = sizeIn.height;

        if (outData.length < iw * ih * 3) {
            logger.println("the buffer is not full");
            return BUFFER_PROCESSED_FAILED;
        }

        outBuffer.setFlags(Globals.bufferFlags);

        if (screenCaptureEnabled) {
            System.arraycopy(store, 0, outData, 0, store.length);
        } else {
            System.arraycopy(inData, 0, outData, 0, inData.length);
        }

        if( Globals.logo ){
            outData = plot( outData, 10, Globals.captureHeight - 10, inBuffer, overlay);
        }

        return BUFFER_PROCESSED_OK;

    }

    private byte[] plot(byte[] data, int x, int y, Buffer inBuffer, BufferedImage c) {
        if (c == null) {
            // can't render a null image.
            // throw new IllegalArgumentException
            return data;
        }
        // I am not sure if this is correct.
        int w = c.getWidth(); 
        int h = c.getHeight(); 
        // optomize
        // ???
        RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();
        int pxs = vfIn.getPixelStride();
        int ls = vfIn.getLineStride();

        int _w = sizeIn.width; /// 3;
        int _h = ((vfIn.getMaxDataLength()) / 3) / _w;

        int[] pixels = new int[w * h];
        c.getRGB(0, 0, w, h, pixels, 0, w);
        for (int _y = 0; _y < h; _y++) {
            for (int _x = 0; _x < w; _x++) {
                int pixel = pixels[(w * _y) + _x];
                //int alpha = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = (pixel) & 0xFF;
                int dest = 0;
                dest = data.length;
                dest -= (_w * _y * 3) - (_x * 3);
                if (dest < 0) {
                    continue;
                }
                if (dest + 3 > data.length) {
                    continue;
                }
                data[dest + 2] = (byte) ((r));
                data[dest + 1] = (byte) ((g));
                data[dest + 0] = (byte) ((b));
            }
        }

        return data;
    }

    private byte[] plot(byte[] data, int x, int y, Buffer inBuffer, Color c) {
        RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();
        int pxs = vfIn.getPixelStride();
        int ls = vfIn.getLineStride();

        int w = sizeIn.width; 
        int h = ((vfIn.getMaxDataLength()) / 3) / w;

        data = putPixel(data, x, y, w, h, c, pxs, ls);
        return data;
    }

    private byte[] putPixel(byte[] data, int x, int y, int w, int h, Color color, int pxs, int ls) {
        //Color white = Color.white;
        int r = color.getRed();
        int b = color.getBlue();
        int g = color.getGreen();
        Color c = new Color(r, g, b);

        data = putPixel(data, x, y, w, h, r, g, b, pxs, ls);
        return data;
    }

    private byte[] putPixel(byte[] data, int x, int y, int w, int h, int r, int g, int b, int pxs, int ls) {
        int dest = 0;
        dest = data.length;
        dest -= (w * y * 3) - (x * 3);
        if (dest < 0) {
            return data;
        }
        if (dest + 3 > data.length) {
            return data;
        }
        data[dest + 2] = (byte) ((r));
        data[dest + 1] = (byte) ((g));
        data[dest + 0] = (byte) ((b));
        return data;
    }

    // methods for interface PlugIn
    public String getName() {
        return "Graphic Effect";
    }

    public void open() {
    }

    public void close() {
    }

    public void reset() {
    }

    // methods for interface javax.media.Controls
    public Object getControl(String controlType) {
        return null;
    }

    public Object[] getControls() {
        return null;
    }

    // Utility methods.
    Format matches(Format in, Format outs[]) {
        for (int i = 0; i < outs.length; i++) {
            if (in.matches(outs[i])) {
                return outs[i];
            }
        }
        return null;
    }

    byte[] validateByteArraySize(Buffer buffer, int newSize) {
        Object objectArray = buffer.getData();
        byte[] typedArray;

        if (objectArray instanceof byte[]) {     // is correct type AND not null
            typedArray = (byte[]) objectArray;
            if (typedArray.length >= newSize) { // is sufficient capacity
                return typedArray;
            }
            byte[] tempArray = new byte[newSize];  // re-alloc array
            System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
            typedArray = tempArray;
        } else {
            typedArray = new byte[newSize];
        }
        buffer.setData(typedArray);
        return typedArray;
    }
}
