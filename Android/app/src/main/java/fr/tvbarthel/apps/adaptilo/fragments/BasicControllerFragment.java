package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.UserEvent;
import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.models.io.Message;

public class BasicControllerFragment extends AdaptiloControllerFragment {

    /**
     * controller keys which can be pressed by the user
     */
    final int[] keys = {
            R.id.basic_controller_btn_a,
            R.id.basic_controller_btn_b,
            R.id.basic_controller_btn_arrow_up,
            R.id.basic_controller_btn_arrow_down,
            R.id.basic_controller_btn_arrow_left,
            R.id.basic_controller_btn_arrow_right};

    /**
     * A {@link android.widget.TextView} used to show messages to the user.
     */
    protected TextView mOnScreenMessage;

    protected Typeface mCustomTypeFace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_controller, container, false);

        mCustomTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Minecraftia.ttf");
        ((TextView) fragmentView.findViewById(R.id.basic_controller_game_name)).setTypeface(mCustomTypeFace);
        mOnScreenMessage = (TextView) fragmentView.findViewById(R.id.basic_controller_on_screen_message);
        mOnScreenMessage.setTypeface(mCustomTypeFace);

        initKeyButtons(fragmentView);
        initStartButton(fragmentView);
        initSelectButton(fragmentView);

        return fragmentView;
    }

    @Override
    public void onDetach() {
        mAdaptiloEngine.stop();
        super.onDetach();
    }

    @Override
    public void onMessage(Message messageToHandle) {
        // TODO handle the message
        MessageType messageType = messageToHandle.getType();
        if (MessageType.ENGINE_READY.equals(messageType)) {
            handleEngineReadyMessage(messageToHandle);
        }
    }

    @Override
    public AlertDialog onStartDialogNeeded() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.create();
    }

    @Override
    protected void onScannerCanceled() {
        // hide the on screen message that should be R.string.basic_controller_message_loading
        hideOnScreenMessage();
    }

    @Override
    protected void onScannerError(QrCodeException ex) {
        super.onScannerError(ex);
        showOnScreenMessage(R.string.basic_controller_qrcode_scanner_error);
    }

    @Override
    protected void onScannerSuccess(EngineConfig config) {
        super.onScannerSuccess(config);
        mAdaptiloEngine.start();
    }

    private void handleEngineReadyMessage(Message message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showOnScreenMessage(R.string.basic_controller_message_playing);
            }
        });
    }

    /**
     * start QrCode scanner to load a game config.
     */
    public void startQrCodeScanner() {
        showOnScreenMessage(R.string.basic_controller_message_loading);
        start();
    }

    /**
     * Init the start button.
     *
     * @param fragmentView the {@link android.view.View} for the fragment's UI.
     */
    protected void initStartButton(View fragmentView) {
        Button startButton = (Button) fragmentView.findViewById(R.id.basic_controller_btn_start);
        startButton.setTypeface(mCustomTypeFace);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQrCodeScanner();
            }
        });
    }

    /**
     * Init the select button.
     *
     * @param fragmentView the {@link android.view.View} for the fragment's UI.
     */
    protected void initSelectButton(View fragmentView) {
        Button selectButton = (Button) fragmentView.findViewById(R.id.basic_controller_btn_select);
        selectButton.setTypeface(mCustomTypeFace);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BasicControllerOptionsFragment options = new BasicControllerOptionsFragment(new BasicControllerOptionsFragment.Callbacks() {
                    @Override
                    public void onDialogHidden() {
                        mAdaptiloEngine.resume();
                    }
                });
                mAdaptiloEngine.pause();
                (options).show(getFragmentManager(), "dialog_options");
            }
        });
    }

    /**
     * Init the key buttons.
     *
     * @param fragmentView the {@link android.view.View} for the fragment's UI.
     */
    protected void initKeyButtons(View fragmentView) {
        final View.OnTouchListener keyListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final UserEvent userEvent = extractUserEvent(v, event);
                if (userEvent != null) {
                    mAdaptiloEngine.sendUserInput(new Message(MessageType.USER_INPUT, userEvent));
                }
                return false;
            }
        };

        for (int buttonId : keys) {
            final Button button = (Button) fragmentView.findViewById(buttonId);
            button.setOnTouchListener(keyListener);
            button.setTypeface(mCustomTypeFace);
        }
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
     * Hide the on screen message.
     */
    protected void hideOnScreenMessage() {
        mOnScreenMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * Extract a {@link fr.tvbarthel.apps.adaptilo.models.UserEvent}
     *
     * @param view        the {@link android.view.View} the touch event has been dispatched to.
     * @param motionEvent The MotionEvent object containing full information about the event.
     * @return
     */
    protected UserEvent extractUserEvent(View view, MotionEvent motionEvent) {
        final EventAction eventAction = extractEventAction(motionEvent);
        if (eventAction == null) return null;
        final EventType eventType = extractEventType(view);
        if (eventType == null) return null;
        return new UserEvent(eventType, eventAction);
    }

    /**
     * Extract the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction} from a {@link android.view.MotionEvent}.
     *
     * @param motionEvent the {@link android.view.MotionEvent} from which the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction} will be extracted.
     * @return the extracted {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
     */
    protected EventAction extractEventAction(MotionEvent motionEvent) {
        final int motionAction = motionEvent.getActionMasked();
        EventAction eventAction = null;
        if (motionAction == MotionEvent.ACTION_DOWN) {
            eventAction = EventAction.ACTION_KEY_DOWN;
        } else if (motionAction == MotionEvent.ACTION_UP) {
            eventAction = EventAction.ACTION_KEY_UP;
        }
        return eventAction;
    }

    /**
     * Extract the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType} associated with a {@link android.view.View}.
     *
     * @param view the {@link android.view.View} the event has been dispatched to.
     * @return the extracted {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
     */
    protected EventType extractEventType(View view) {
        final int viewId = view.getId();
        EventType eventType;
        switch (viewId) {
            case R.id.basic_controller_btn_a:
                eventType = EventType.KEY_A;
                break;
            case R.id.basic_controller_btn_b:
                eventType = EventType.KEY_B;
                break;
            case R.id.basic_controller_btn_arrow_left:
                eventType = EventType.KEY_ARROW_LEFT;
                break;
            case R.id.basic_controller_btn_arrow_up:
                eventType = EventType.KEY_ARROW_UP;
                break;
            case R.id.basic_controller_btn_arrow_right:
                eventType = EventType.KEY_ARROW_RIGHT;
                break;
            case R.id.basic_controller_btn_arrow_down:
                eventType = EventType.KEY_ARROW_DOWN;
                break;
            default:
                eventType = null;
        }
        return eventType;
    }

}
