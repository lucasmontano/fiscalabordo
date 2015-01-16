package br.com.moolab.fiscalabordo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity implements DialogConfirm.ConfirmCallback {

    public static final String DIALOG_CONFIRM = "DIALOG_CONFIRM";
    private static final String TAG_FINISH = "TAG_FINISH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ((FiscalAbordoApp) getApplication()).getTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Moolab")));
            } catch (android.content.ActivityNotFoundException anfe) {
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    @Override
    public void onConfirm(final String company, final String velocity, final Boolean belt, final Boolean broke, final Boolean bug, final Boolean stand, final boolean facebook) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PlaceholderFragment())
                .commit();

        DialogFinish dialogFinish = new DialogFinish();
        dialogFinish.show(getSupportFragmentManager(), TAG_FINISH);

        if (facebook) {
            ParseFacebookUtils.logIn(this, new LogInCallback() {
                @Override
                public void done(final ParseUser parseUser, com.parse.ParseException e) {
                    if (parseUser == null) {
                        saveAnonymous(company, velocity, belt, broke, bug, stand);
                    } else {
                        sendToParse(company, velocity, belt, broke, bug, stand, parseUser);

                        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                                new Request.GraphUserCallback() {

                                    @Override
                                    public void onCompleted(GraphUser user, Response response) {
                                        parseUser.put("name", user.getName());
                                        parseUser.put("link", user.getLink());
                                        parseUser.put("firstName", user.getFirstName());
                                        if (user.asMap() != null && user.asMap().get("email") != null) {
                                            parseUser.put("email", user.asMap().get("email"));
                                        }
                                        parseUser.saveEventually();
                                    }
                                });
                        Request.executeBatchAsync(request);
                    }
                }
            });
        } else {
            saveAnonymous(company, velocity, belt, broke, bug, stand);
        }
    }

    private void saveAnonymous(final String company, final String velocity, final Boolean belt, final Boolean broke, final Boolean bug, final Boolean stand) {

        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        if (ParseFacebookUtils.getSession() != null && ParseFacebookUtils.getSession().isOpened()) {
            ParseFacebookUtils.getSession().closeAndClearTokenInformation();
        }

        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                sendToParse(company, velocity, belt, broke, bug, stand, parseUser);
            }
        });
    }

    public void sendToParse(String company, String velocity, Boolean belt, Boolean broke, Boolean bug, Boolean stand, ParseUser user) {
        ParseObject registro = new ParseObject("Registro");
        registro.put("company", company);
        if (user != null) {
            registro.put("User", user);
        }
        registro.put("velocity", velocity);
        registro.put("broke", broke);
        registro.put("dirty", bug);
        registro.put("noBelt", belt);
        registro.put("crowded", stand);
        registro.saveEventually();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private TextView mVelocity;
        private FloatingActionButton mFab;
        private Switch bug;
        private Switch stand;
        private Switch belt;
        private Switch broke;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mVelocity = ((TextView) rootView.findViewById(R.id.header_title));
            mVelocity.setTypeface(Fonts.getInstance().getRobotoCondensed(getActivity().getAssets()));
            ((TextView) rootView.findViewById(R.id.header_subtitle)).setTypeface(Fonts.getInstance().getRobotoMedium(getActivity().getAssets()));

            mFab = (FloatingActionButton) rootView.findViewById(R.id.header_fab);
            mFab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    DialogConfirm confirm = new DialogConfirm();
                    Bundle args = new Bundle();
                    args.putString(DialogConfirm.ARG_VELOCITY, mVelocity.getText().toString());
                    args.putBoolean(DialogConfirm.ARG_BELT, belt.isChecked());
                    args.putBoolean(DialogConfirm.ARG_STAND, stand.isChecked());
                    args.putBoolean(DialogConfirm.ARG_BUG, bug.isChecked());
                    args.putBoolean(DialogConfirm.ARG_BROKE, broke.isChecked());
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
}
