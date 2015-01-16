package br.com.moolab.fiscalabordo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class DialogConfirm extends DialogFragment {

    public static final String ARG_VELOCITY = "ARG_VELOCITY";
    public static final String ARG_BELT = "ARG_BELT";
    public static final String ARG_BUG = "ARG_BUG";
    public static final String ARG_STAND = "ARG_STAND";
    public static final String ARG_BROKE = "ARG_BROKE";

    private Builder mBuilder;
    private ConfirmCallback mCallback;
    private EditText company;
    private TextView velocity;
    private Switch facebookLogin;

    public interface ConfirmCallback {
        public void onConfirm(String company, String velocity, Boolean belt, Boolean broke, Boolean bug, Boolean stand, boolean user);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ConfirmCallback) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LinearLayout viewInflated = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm, null);

        company = ((EditText) viewInflated.findViewById(R.id.company));

        velocity = ((TextView) viewInflated.findViewById(R.id.velocity));
        velocity.setTypeface(Fonts.getInstance().getRobotoBoldCondensed(getActivity().getAssets()));
        velocity.setText(getArguments().getString(ARG_VELOCITY));

        ((ImageView) viewInflated.findViewById(R.id.is_belt)).setVisibility(getArguments().getBoolean(ARG_BELT) ? View.VISIBLE : View.GONE);
        ((ImageView) viewInflated.findViewById(R.id.is_stand)).setVisibility(getArguments().getBoolean(ARG_STAND) ? View.VISIBLE : View.GONE);
        ((ImageView) viewInflated.findViewById(R.id.is_bug)).setVisibility(getArguments().getBoolean(ARG_BUG) ? View.VISIBLE : View.GONE);
        ((ImageView) viewInflated.findViewById(R.id.is_broke)).setVisibility(getArguments().getBoolean(ARG_BROKE) ? View.VISIBLE : View.GONE);

        TextView publicData = (TextView) viewInflated.findViewById(R.id.public_data);
        publicData.setTypeface(Fonts.getInstance().getRobotoRegular(getActivity().getAssets()));

        company.setTypeface(Fonts.getInstance().getRobotoRegular(getActivity().getAssets()));


        facebookLogin = ((Switch) viewInflated.findViewById(R.id.facebook));
        ((View) facebookLogin.getParent()).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin.setChecked( ! facebookLogin.isChecked());
            }
        });

        ((Button) viewInflated.findViewById(R.id.action_ok)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onConfirm(
                        company.getText().toString(),
                        velocity.getText().toString(),
                        getArguments().getBoolean(ARG_BELT),
                        getArguments().getBoolean(ARG_BROKE),
                        getArguments().getBoolean(ARG_BUG),
                        getArguments().getBoolean(ARG_STAND),
                        facebookLogin.isChecked()
                );
                DialogConfirm.this.dismiss();
            }
        });


        ((Button) viewInflated.findViewById(R.id.action_cancel)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogConfirm.this.dismiss();
            }
        });

        mBuilder = new Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat_Light));
        mBuilder.setView(viewInflated);

        return mBuilder.create();
    }
}