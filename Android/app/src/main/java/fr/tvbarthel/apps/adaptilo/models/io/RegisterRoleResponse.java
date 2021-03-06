package fr.tvbarthel.apps.adaptilo.models.io;

import com.google.gson.annotations.SerializedName;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;

/**
 * Encapsulate data for role registration response.
 * <p/>
 * Data send back to the role when registration succeeded.
 */
public class RegisterRoleResponse {

    /**
     * Generated external id used to identify the role.
     */
    @SerializedName(MessageDeserializerHelper.NODE_EXTERNAL_ID)
    private String mExternalId;

    /**
     * Room id in which the role is registered.
     * <p/>
     * A generated room id is send back for new registration.
     * <p/>
     * The room id used in registration request is send back for joining room registration.
     */
    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROOM)
    private String mRoomId;

    /**
     * Build a registre response send back when registration succeeded
     *
     * @param extId  external id gave to the role instance
     * @param roomId room id in which the given role instance is registered.
     */
    public RegisterRoleResponse(String extId, String roomId) {
        mExternalId = extId;
        mRoomId = roomId;
    }

    /**
     * Retrieve external id associated to the device during registration process.
     *
     * @return external id. Should be store has it must be added to the server requests.
     */
    public String getExternalId() {
        return mExternalId;
    }

    /**
     * Retrieve room id in which the player has been registered.
     *
     * @return room id.
     */
    public String getRoomId() {
        return mRoomId;
    }

}
