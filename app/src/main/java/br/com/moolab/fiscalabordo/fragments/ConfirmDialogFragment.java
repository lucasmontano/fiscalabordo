package br.com.moolab.fiscalabordo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import br.com.moolab.fiscalabordo.utils.FontsUtils;
import br.com.moolab.fiscalabordo.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ConfirmDialogFragment extends DialogFragment {

    public static final String ARG_VELOCITY = "ARG_VELOCITY";
    public static final String ARG_BELT = "ARG_BELT";
    public static final String ARG_BUG = "ARG_BUG";
    public static final String ARG_STAND = "ARG_STAND";
    public static final String ARG_BROKE = "ARG_BROKE";
    public static final String ARG_FAST = "ARG_FAST";

    private ConfirmCallback mCallback;

    @InjectView(R.id.company) EditText company;
    @InjectView(R.id.velocity) TextView velocity;
    @InjectView(R.id.public_data) TextView publicData;
    @InjectView(R.id.facebook) Switch facebookLogin;

    @InjectView(R.id.is_belt) ImageView isBelt;
    @InjectView(R.id.is_stand) ImageView isStand;
    @InjectView(R.id.is_bug) ImageView isBug;
    @InjectView(R.id.is_broke) ImageView isBroke;
    @InjectView(R.id.is_fast) ImageView isFast;

    private Typeface robotoRegular;
    private Typeface robotoBoldCondensed;

    public interface ConfirmCallback {
        public void onConfirm(String company, String velocity, Boolean fast, Boolean belt, Boolean broke, Boolean bug, Boolean stand, boolean user);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ConfirmCallback) activity;
        robotoRegular = FontsUtils.getInstance().getRobotoRegular(activity.getAssets());
        robotoBoldCondensed = FontsUtils.getInstance().getRobotoBoldCondensed(activity.getAssets());
    }

    @OnClick(R.id.action_ok)
    public void ok(View view) {
        mCallback.onConfirm(
                company.getText().toString(),
                velocity.getText().toString(),
                getArguments().getBoolean(ARG_FAST),
                getArguments().getBoolean(ARG_BELT),
                getArguments().getBoolean(ARG_BROKE),
                getArguments().getBoolean(ARG_BUG),
                getArguments().getBoolean(ARG_STAND),
                facebookLogin.isChecked()
        );
        ConfirmDialogFragment.this.dismiss();
    }

    @OnClick(R.id.action_cancel)
    public void cancel(View view) {
        ConfirmDialogFragment.this.dismiss();
    }

    @OnClick(R.id.facebook_row)
    public void checkLogin(View view) {
        facebookLogin.setChecked(!facebookLogin.isChecked());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LinearLayout viewInflated = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_confirm, null);

        Bundle arguments = getArguments();

        // The Magic
        ButterKnife.inject(this, viewInflated);

        // Bus Velocity
        velocity.setText(arguments.getString(ARG_VELOCITY));

        // Options Selected
        isBelt.setVisibility(arguments.getBoolean(ARG_BELT) ? View.VISIBLE : View.GONE);
        isStand.setVisibility(arguments.getBoolean(ARG_STAND) ? View.VISIBLE : View.GONE);
        isBug.setVisibility(arguments.getBoolean(ARG_BUG) ? View.VISIBLE : View.GONE);
        isBroke.setVisibility(arguments.getBoolean(ARG_BROKE) ? View.VISIBLE : View.GONE);
        isFast.setVisibility(arguments.getBoolean(ARG_FAST) ? View.VISIBLE : View.GONE);

        // Fonts
        velocity.setTypeface(robotoBoldCondensed);
        publicData.setTypeface(robotoRegular);
        company.setTypeface(robotoRegular);

        Builder mBuilder = new Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat_Light));
        mBuilder.setView(viewInflated);

        return mBuilder.create();
    }
}