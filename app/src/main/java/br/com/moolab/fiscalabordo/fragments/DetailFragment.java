package br.com.moolab.fiscalabordo.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import br.com.moolab.fiscalabordo.R;
import br.com.moolab.fiscalabordo.utils.FontsUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lucas on 15/01/15.
 */
public class DetailFragment extends Fragment {

    private static final String DIALOG_CONFIRM = "DIALOG_CONFIRM";

    @InjectView(R.id.header_title) TextView mVelocity;
    @InjectView(R.id.header_subtitle) TextView mSubTitle;
    @InjectView(R.id.header_fab) FloatingActionButton mFab;

    @InjectView(R.id.bug) Switch bug;
    @InjectView(R.id.stand) Switch stand;
    @InjectView(R.id.belt) Switch belt;
    @InjectView(R.id.broke) Switch broke;
    @InjectView(R.id.fast) Switch fast;

    private Typeface robotoCondensed;
    private Typeface robotoMedium;

    public DetailFragment() {
    }

    @OnClick(R.id.fast_parent)
    public void fastSwitch(View view) {
        fast.setChecked( ! fast.isChecked());
    }

    @OnClick(R.id.bug_parent)
    public void bugSwitch(View view) {
        bug.setChecked( ! bug.isChecked());
    }

    @OnClick(R.id.belt_parent)
    public void beltSwitch(View view) {
        belt.setChecked( ! belt.isChecked());
    }

    @OnClick(R.id.stand_parent)
    public void standSwitch(View view) {
        stand.setChecked( ! stand.isChecked());
    }

    @OnClick(R.id.broke_parent)
    public void brokeSwitch(View view) {
        broke.setChecked( ! broke.isChecked());
    }

    @OnClick(R.id.header_fab)
    public void fabConfirm(View view) {
        ConfirmDialogFragment confirm = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString(ConfirmDialogFragment.ARG_VELOCITY, mVelocity.getText().toString());
        args.putBoolean(ConfirmDialogFragment.ARG_BELT, belt.isChecked());
        args.putBoolean(ConfirmDialogFragment.ARG_STAND, stand.isChecked());
        args.putBoolean(ConfirmDialogFragment.ARG_BUG, bug.isChecked());
        args.putBoolean(ConfirmDialogFragment.ARG_BROKE, broke.isChecked());
        args.putBoolean(ConfirmDialogFragment.ARG_FAST, fast.isChecked());
        confirm.setArguments(args);
        confirm.show(getActivity().getSupportFragmentManager(), DIALOG_CONFIRM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // The Magic
        ButterKnife.inject(this, rootView);

        robotoCondensed = FontsUtils.getInstance().getRobotoCondensed(getActivity().getAssets());
        robotoMedium = FontsUtils.getInstance().getRobotoMedium(getActivity().getAssets());

        mVelocity.setTypeface(robotoCondensed);
        mSubTitle.setTypeface(robotoMedium);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void setVelocity(Location location) {
        mVelocity.setText(String.valueOf((int) (location.getSpeed() * 3.6)) + " Km/h");
    }
}