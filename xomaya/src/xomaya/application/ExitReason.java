/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.application;

/**
 * This type is designed to allow the application to track the various reasons
 * the application exits.
 * Tracking exit reasons helps in determining user usage patterns, so they can
 * be made more efficient.
 *
 * This documentation is part of the Xomaya Express  <A HREF="http://www.xomaya.com">screen capture software</A> suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.

 * @author Sean
 */
public enum ExitReason {
    DIALOG_CLOSE,
    APPLICATION_CLOSE,
    FATAL_CLOSE,
    COMPLETE,
    DO_EXIT,
    NO_WEBCAM,
    NO_LOGO,
    NO_DATASINK,
    NO_MEDIA_LOCATOR,
    NO_DEVICE
}
