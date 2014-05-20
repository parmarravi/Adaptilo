package fr.tvbarthel.apps.adaptilo.models;

import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;

/**
 * Encapsulate Event data
 */
public class Event {

    /**
     * type of the event, KEY_A for instance.
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
     */
    protected EventType mEventType;

    /**
     * action performed for the given type, ACTION_KEY_DOWN for instance.
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
     */
    protected EventAction mEventAction;

    public Event() {

    }

    public Event(EventType type, EventAction action) {
        mEventType = type;
        mEventAction = action;
    }

    public EventType getEventType() {
        return mEventType;
    }

    public void setEventType(EventType mEventType) {
        this.mEventType = mEventType;
    }

    public EventAction getEventAction() {
        return mEventAction;
    }

    public void setEventAction(EventAction mEventAction) {
        this.mEventAction = mEventAction;
    }
}
