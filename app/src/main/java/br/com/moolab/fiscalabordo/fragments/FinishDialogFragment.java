package br.com.moolab.fiscalabordo.fragments;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.moolab.fiscalabordo.utils.FontsUtils;
import br.com.moolab.fiscalabordo.R;

public class FinishDialogFragment extends DialogFragment {

    private Builder mBuilder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LinearLayout viewInflated = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_finish, null);

        TextView title = (TextView) viewInflated.findViewById(R.id.title);
        title.setTypeface(FontsUtils.getInstance().getRobotoBoldCondensed(getActivity().getAssets()));

        TextView msg = (TextView) viewInflated.findViewById(R.id.msg);
        msg.setTypeface(FontsUtils.getInstance().getRobotoRegular(getActivity().getAssets()));

        ((Button) viewInflated.findViewById(R.id.action_ok)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FinishDialogFragment.this.dismiss();
            }
        });

        mBuilder = new Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat_Light));
        mBuilder.setView(viewInflated);

        return mBuilder.create();
    }
}