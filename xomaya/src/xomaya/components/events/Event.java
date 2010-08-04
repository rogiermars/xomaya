/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xomaya.components.events;

/**
 *
 * @author beecrofs
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

