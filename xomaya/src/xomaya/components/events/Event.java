/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components.events;

/**
 * This is a custom Xomaya event class.
 *
 * This documentation is part of the Xomaya Express software suite.
 * Please visit <A HREF="http://www.xomaya.com">http://www.xomaya.com</A> for more information
 * or to download our screen capture / screen recording software.
 */
public class Event {
    private EventType type = EventType.NOT_SPECIFIED;
    public Event(EventType et)
    {
        this.type = et;
    }

    /**
     * @return the type
     */
    public EventType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(EventType type) {
        this.type = type;
    }
}

