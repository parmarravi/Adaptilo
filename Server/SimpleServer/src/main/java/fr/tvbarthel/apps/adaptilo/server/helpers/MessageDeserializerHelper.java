package fr.tvbarthel.apps.adaptilo.server.helpers;

import com.google.gson.*;
import fr.tvbarthel.apps.adaptilo.server.models.Event;
import fr.tvbarthel.apps.adaptilo.server.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.server.models.io.Message;
import fr.tvbarthel.apps.adaptilo.server.models.io.RegisterRoleRequest;

import java.lang.reflect.Type;


/**
 * Adapter for deserialization of {@link Message}
 */
public class MessageDeserializerHelper implements JsonDeserializer<Message> {

    /**
     * json node key for external id
     */
    public static final String NODE_EXTERNAL_ID = "externalId";

    /**
     * json node key for message
     */
    public static final String NODE_MESSAGE = "message";

    /**
     * json node key for message type
     */
    public static final String NODE_TYPE = "type";

    /**
     * json node key for message content
     */
    public static final String NODE_CONTENT = "content";

    /**
     * json node key for game name
     */
    public static final String NODE_GAME_NAME = "gameName";

    /**
     * json node key for game room
     */
    public static final String NODE_GAME_ROOM = "gameRoom";

    /**
     * json node key for game role
     */
    public static final String NODE_GAME_ROLE = "gameRole";

    /**
     * json node key for replacement policy
     */
    public static final String NODE_SHOULD_REPLACE = "replace";

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        MessageType messageType = context.deserialize(jsonObject.get(NODE_TYPE), MessageType.class);

        Message message = new Message();
        message.setType(messageType);

        JsonElement content = jsonObject.get(NODE_CONTENT);

        switch (messageType) {
            case CONNECTION_COMPLETED:
                String connectionId = context.deserialize(content, String.class);
                message.setContent(connectionId);
                break;

            case VIBRATOR:
                Long duration = context.deserialize(content, Long.class);
                message.setContent(duration);
                break;

            case VIBRATOR_PATTERN:
                long[] pattern = context.deserialize(content, long[].class);
                message.setContent(pattern);
                break;

            case REGISTER_ROLE_REQUEST:
                RegisterRoleRequest registrationRequest = context.deserialize(content, RegisterRoleRequest.class);
                message.setContent(registrationRequest);
                break;

            case SENSOR:
            case USER_INPUT:
                Event event = context.deserialize(content, Event.class);
                message.setContent(event);
                break;

        }
        return message;
    }
}
