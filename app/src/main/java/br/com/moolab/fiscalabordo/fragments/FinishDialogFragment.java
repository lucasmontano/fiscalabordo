package br.com.moolab.fiscalabordo.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import br.com.moolab.fiscalabordo.utils.FontsUtils;
import br.com.moolab.fiscalabordo.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FinishDialogFragment extends DialogFragment {

    @InjectView(R.id.msg) TextView msg;
    @InjectView(R.id.title) TextView title;

    @OnClick(R.id.action_ok)
    public void cancel(View view) {
        FinishDialogFragment.this.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LinearLayout viewInflated = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_finish, null);

        // The Magic
        ButterKnife.inject(this, viewInflated);

        title.setTypeface(FontsUtils.getInstance().getRobotoBoldCondensed(getActivity().getAssets()));
        msg.setTypeface(FontsUtils.getInstance().getRobotoRegular(getActivity().getAssets()));

        Builder mBuilder = new Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat_Light));
        mBuilder.setView(viewInflated);

        return mBuilder.create();
    }
}