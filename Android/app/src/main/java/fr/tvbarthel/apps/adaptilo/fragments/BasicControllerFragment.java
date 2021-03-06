package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.UserEvent;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.models.io.ClosingCode;
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Basic controller composed of a four arrow pad on the left and two buttons
 * (A and B) on the right.
 * <p/>
 * As requested for a controller, a start and a select button are also displayed.
 */
public class BasicControllerFragment extends AdaptiloControllerFragment {

    /**
     * Log cat.
     */
    private static final String TAG = BasicControllerFragment.class.getName();

    /**
     * A {@link android.widget.TextView} used to show messages to the user.
     */
    private TextView mOnScreenMessage;

    /**
     * Current game name
     */
    private String mGameName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("fonts/Minecraftia.ttf");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //cancel all scheduled Croutons
        Crouton.cancelAllCroutons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_controller, container, false);

        mOnScreenMessage = (TextView) fragmentView.findViewById(R.id.basic_controller_on_screen_message);

        return fragmentView;
    }

    @Override
    protected int getSelectButtonId() {
        return R.id.basic_controller_btn_select;
    }

    @Override
    protected int getStartButtonId() {
        return R.id.basic_controller_btn_start;
    }

    @Override
    protected SparseArray<EventType> getControllerKeys() {
        SparseArray<EventType> keys = new SparseArray<EventType>();
        keys.put(R.id.basic_controller_btn_a, EventType.KEY_A);
        keys.put(R.id.basic_controller_btn_b, EventType.KEY_B);
        keys.put(R.id.basic_controller_btn_arrow_up, EventType.KEY_ARROW_UP);
        keys.put(R.id.basic_controller_btn_arrow_down, EventType.KEY_ARROW_DOWN);
        keys.put(R.id.basic_controller_btn_arrow_left, EventType.KEY_ARROW_LEFT);
        keys.put(R.id.basic_controller_btn_arrow_right, EventType.KEY_ARROW_RIGHT);
        return keys;
    }

    @Override
    protected AdaptiloSelectDialogFragment getSelectDialogFragment() {
        return new BasicControllerSelectDialogFragment();
    }

    @Override
    protected AdaptiloStartDialogFragment getStartDialogFragment() {
        return new BasicControllerStartDialogFragment();
    }

    @Override
    public void onGameStart(String gameName) {
        mGameName = gameName;
        //display game name
        showOnScreenMessage(mGameName);
    }

    @Override
    public void onMessageReceived(Message message) {
        switch (message.getType()) {
            case ON_ROLE_UNREGISTERED:

                //TODO, crouton only for test purpose
                String role = (String) message.getContent();
                Crouton.makeText(getActivity(), "Role : " + role + " leaves the room.", Style.ALERT).show();
                break;
            case ON_ROLE_REGISTERED:

                //TODO, crouton only for test purpose
                String joiningRole = (String) message.getContent();
                Crouton.makeText(getActivity(), "Role : " + joiningRole + " joins the room.", Style.CONFIRM).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onErrorReceived(Exception ex) {

    }

    @Override
    public void onConnectionClosed(int reason) {
        switch (reason) {
            case ClosingCode.REGISTRATION_REQUESTED_ROOM_IS_FULL:
                showOnScreenMessage(R.string.basic_controller_room_full);
                break;
            case ClosingCode.DISCONNECTION_DUE_TO_ROLE_REPLACEMENT:
                showOnScreenMessage(R.string.basic_controller_message_disconnected);
                break;
            case ClosingCode.REGISTRATION_REQUESTED_ROOM_UNKNOW:
                showOnScreenMessage(R.string.basic_controller_room_unknown);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onUserEventSend(UserEvent userEvent) {
        /**
         * Process a specific behavior for a given eventType and eventAction.
         *
         * Use {@link fr.tvbarthel.apps.adaptilo.models.UserEvent#getEventAction()}
         * with values {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
         *
         * As well as {@link fr.tvbarthel.apps.adaptilo.models.UserEvent#getEventType()}
         * with values {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
         *
         * For instance :
         *
         * {@code
         *  if (userEvent.getEventAction() == EventAction.ACTION_KEY_DOWN) {
         *      switch (userEvent.getEventType()) {
         *          case KEY_A:
         *              showOnScreenMessage("KEY_A pressed");
         *              break;
         *          case KEY_B:
         *              showOnScreenMessage("KEY_B pressed");
         *              break;
         *          default:
         *              showOnScreenMessage("key pressed");
         *              break;
         *      }
         *  }
         * }
         */
    }

    @Override
    public void onGameServerUnreachable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.basic_controller_network_unreachable_alert_title);
        builder.setMessage(R.string.basic_controller_network_unreachable_alert_content);
        builder.create().show();
    }

    @Override
    protected void onSelectDialogShown() {
        showOnScreenMessage(R.string.basic_controller_btn_select);
    }

    @Override
    protected void onStartDialogShown() {
        showOnScreenMessage(R.string.basic_controller_btn_start);
    }

    @Override
    public void onSelectDialogClosed(boolean optionSaved) {
        //display game name again once dialog is closed
        showOnScreenMessage(mGameName != null ? mGameName : "");
    }

    @Override
    public void onStartDialogClosed(int which) {
        switch (which) {
            case AdaptiloStartDialogFragment.BUTTON_DISCONNECT:
                showOnScreenMessage(R.string.basic_controller_message_disconnected);
                break;
            default:
                //display game name again once dialog is closed
                showOnScreenMessage(mGameName != null ? mGameName : "");
                break;
        }
    }

    @Override
    public void onScannerCanceled() {
        // hide the on screen message that should be R.string.basic_controller_message_loading
        hideOnScreenMessage();
    }

    @Override
    public void onScannerError(QrCodeException ex) {
        showOnScreenMessage(R.string.basic_controller_qrcode_scanner_error);
    }

    @Override
    public void onScannerSuccess(EngineConfig config) {
    }


    /**
     * Show an on screen message.
     *
     * @param resourceId the resource id of the string to be shown.
     */
    protected void showOnScreenMessage(int resourceId) {
        mOnScreenMessage.setVisibility(View.VISIBLE);
        mOnScreenMessage.setText(resourceId);
    }

    /**
     * Show an on screen message.
     *
     * @param message The string to be shown.
     */
    protected void showOnScreenMessage(String message) {
        mOnScreenMessage.setVisibility(View.VISIBLE);
        mOnScreenMessage.setText(message);
    }

    /**
     * Hide the on screen message.
     */
    protected void hideOnScreenMessage() {
        mOnScreenMessage.setVisibility(View.INVISIBLE);
    }
}
