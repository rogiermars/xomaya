/**
 * Copyright (c) 2010
 *
 * Jason Foss, Sean Beecroft, senatorfoss at ou dot edu, http://www.xomaya.com
 *
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
package xomaya.components.datasource;


import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import xomaya.application.Globals;
import xomaya.logging.Log;

/**
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class JavaSoundStream implements PushBufferStream, Runnable {


    /* JMF STUFF */
    protected ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
    protected int maxDataLength;
    protected boolean started;
    protected Thread thread;
    protected BufferTransferHandler transferHandler;
    protected Control[] controls = new Control[0];
    protected AudioFormat audio_out_format;
    protected javax.sound.sampled.AudioFormat audio_in_format;
    static Log logger = new Log(JavaSoundStream.class);

    /***************************************************************************
     * PushBufferStream
     ***************************************************************************/
    private int seqNo = 0;
    int flag = 0;

    /* JavaSound Stuff */
    TargetDataLine tdl = null;

    public JavaSoundStream() {

        /* Start the thread */
        thread = new Thread(this);

        /* JavaSound Stuff */
        //javax.sound.sampled.AudioFormat formats[] = JavaSoundFormats.getFormatList(); /* Write your own code to get an array of acceptable formats */

        javax.sound.sampled.AudioFormat formats[] = new javax.sound.sampled.AudioFormat[1];
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;
        formats[0] = new javax.sound.sampled.AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);


        for (javax.sound.sampled.AudioFormat format : formats) {
            DataLine.Info dlinfo = new DataLine.Info(TargetDataLine.class,
                    format);
            if (AudioSystem.isLineSupported(dlinfo)) {
                try {
                    audio_in_format = format;
                    tdl = (TargetDataLine) AudioSystem.getLine(dlinfo);
                    break;
                } catch (LineUnavailableException e) {
                    // No need to do anything
                }
            }
        }

        /* If we don't have a targetdataoutput object, give up */
        if (tdl == null) {
            logger.println("No TargetDataLines found!");
            return;
        }

        /* JMF Stuff */

        logger.println(audio_in_format);

        audio_out_format = new AudioFormat(AudioFormat.LINEAR,
                (double) audio_in_format.getSampleRate(),
                audio_in_format.getSampleSizeInBits(),
                audio_in_format.getChannels(),
                (audio_in_format.isBigEndian()) ? AudioFormat.BIG_ENDIAN : AudioFormat.LITTLE_ENDIAN,
                (audio_in_format.getEncoding() == javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED) ? AudioFormat.SIGNED : AudioFormat.UNSIGNED);

    }

    /***************************************************************************
     * SourceStream
     ***************************************************************************/
    public ContentDescriptor getContentDescriptor() {
        return cd;
    }

    public long getContentLength() {
        return LENGTH_UNKNOWN;
    }

    public boolean endOfStream() {
        return false;
    }

    public Format getFormat() {
        return audio_out_format;
    }

    public void read(Buffer buffer) throws IOException {

        if (flag == 0) {
            logger.println("JavaSoundStream.read called");
            //logger.println(System.currentTimeMillis() - Globals.ttga);
            Globals.ts = System.currentTimeMillis();
            flag++;
        }
        synchronized (this) {
            Object outdata = buffer.getData();
            outdata = new byte[tdl.available()];
            buffer.setData(outdata);
            int length = tdl.read((byte[]) outdata, 0, ((byte[]) outdata).length);
            buffer.setFlags(Globals.bufferFlags);
            buffer.setTimeStamp(Globals.time.getNanoseconds());
            buffer.setFormat(audio_out_format);
            buffer.setSequenceNumber(Globals.seqNo++);
            buffer.setLength(length);

        }
    }

    public void setTransferHandler(BufferTransferHandler transferHandler) {
        synchronized (this) {
            this.transferHandler = transferHandler;
            notifyAll();
        }
    }
    boolean b = false;

    void start(boolean started) {
        synchronized (this) {
            this.started = started;
            if (started && !thread.isAlive()) {
                thread = new Thread(this);
                thread.start();
            }
            /* Open up the TargetDataLine */
            if (tdl != null) {
                try {

                    tdl.open();
                    tdl.start();
                    //System.out.println("Audio Started!");

                } catch (LineUnavailableException e) {
                    this.started = false;
                }
            }
            notifyAll();
        }
    }

    /***************************************************************************
     * Runnable
     ***************************************************************************/
    public void run() {
        while (started) {
            synchronized (this) {
                while (transferHandler == null && started) {
                    try {
                        wait(100);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
            //Thread.yield();
            if (started && transferHandler != null) {
                transferHandler.transferData(this);
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException ise) {
                    ise.printStackTrace();
                }
            }
        }
    }

    // Controls
    public Object[] getControls() {
        return controls;
    }

    public Object getControl(String controlType) {
        try {
            Class cls = Class.forName(controlType);
            Object cs[] = getControls();
            for (int i = 0; i < cs.length; i++) {
                if (cls.isInstance(cs[i])) {
                    return cs[i];
                }
            }
            return null;

        } catch (Exception e) {   // no such controlType or such control
            return null;
        }
    }
}
