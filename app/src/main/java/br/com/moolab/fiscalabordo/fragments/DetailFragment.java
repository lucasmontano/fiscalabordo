package br.com.moolab.fiscalabordo.fragments;

import android.content.Context;
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

/**
 * Created by lucas on 15/01/15.
 */
public class DetailFragment extends Fragment {

    private static final String DIALOG_CONFIRM = "DIALOG_CONFIRM";

    private TextView mVelocity;
    private FloatingActionButton mFab;
    private Switch bug;
    private Switch stand;
    private Switch belt;
    private Switch broke;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mVelocity = ((TextView) rootView.findViewById(R.id.header_title));
        mVelocity.setTypeface(FontsUtils.getInstance().getRobotoCondensed(getActivity().getAssets()));
        ((TextView) rootView.findViewById(R.id.header_subtitle)).setTypeface(FontsUtils.getInstance().getRobotoMedium(getActivity().getAssets()));

        mFab = (FloatingActionButton) rootView.findViewById(R.id.header_fab);
        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConfirmDialogFragment confirm = new ConfirmDialogFragment();
                Bundle args = new Bundle();
                args.putString(ConfirmDialogFragment.ARG_VELOCITY, mVelocity.getText().toString());
                args.putBoolean(ConfirmDialogFragment.ARG_BELT, belt.isChecked());
                args.putBoolean(ConfirmDialogFragment.ARG_STAND, stand.isChecked());
                args.putBoolean(ConfirmDialogFragment.ARG_BUG, bug.isChecked());
                args.putBoolean(ConfirmDialogFragment.ARG_BROKE, broke.isChecked());
                confirm.setArguments(args);
                confirm.show(getActivity().getSupportFragmentManager(), DIALOG_CONFIRM);
            }
        });

        /**
         * Itens selecionados
         */
        belt = (Switch) rootView.findViewById(R.id.belt);
        bug = (Switch) rootView.findViewById(R.id.bug);
        stand = (Switch) rootView.findViewById(R.id.stand);
        broke = (Switch) rootView.findViewById(R.id.broke);

        ((View) belt.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                belt.setChecked( ! belt.isChecked());
            }
        });

        ((View) bug.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bug.setChecked( ! bug.isChecked());
            }
        });

        ((View) stand.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stand.setChecked( ! stand.isChecked());
            }
        });

        ((View) broke.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broke.setChecked( ! broke.isChecked());
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                location.getLatitude();
                mVelocity.setText(String.valueOf((int) (location.getSpeed() * 3.6)) + " Km/h");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { }

            public void onProviderEnabled(String provider) { }

            public void onProviderDisabled(String provider) { }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}