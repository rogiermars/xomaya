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
