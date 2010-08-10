/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components.datasource;

/**
 * This interface is used to allow components to transmit Transfer events.
 * The ImageDataSource / ImageDataStream currently use this interface.
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public interface TransferListener {
    public void transferCompleted();
    public void transferStarted();
}
