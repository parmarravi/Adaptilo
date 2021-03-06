package fr.tvbarthel.apps.adaptilo.models.enums;

/**
 * Used to identify a controller when
 * {@link fr.tvbarthel.apps.adaptilo.models.enums.MessageType#REPLACE_CONTROLLER} happened.
 */
public enum ControllerType {

    /**
     * Default controller  with a cross pad and 2 buttons.
     */
    BASIC_CONTROLLER,

    /**
     * Drums controller with two drums and clap listener.
     */
    DRUMS_CONTROLLER
}
