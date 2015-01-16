package br.com.moolab.fiscalabordo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import br.com.moolab.fiscalabordo.fragments.ConfirmDialogFragment;
import br.com.moolab.fiscalabordo.fragments.DetailFragment;
import br.com.moolab.fiscalabordo.fragments.FinishDialogFragment;


public class MainActivity extends ActionBarActivity implements ConfirmDialogFragment.ConfirmCallback {

    private static final String TAG_FINISH = "TAG_FINISH";

    private Tracker tracker;

    private Location lastLocation;
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            initDetail();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        // Parse Analytics, tracking automatic
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        // GA tracking screen
        tracker = ((FiscalABordoApp) getApplication()).getTracker();
        tracker.setScreenName("Main");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (detailFragment == null || location == null) {
                    return;
                }
                lastLocation = location;
                detailFragment.setVelocity(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { }

            public void onProviderEnabled(String provider) { }

            public void onProviderDisabled(String provider) { }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_more_apps) {

            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_apps_link))));
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
    public void onConfirm(final String company, final String velocity, final Boolean fast, final Boolean belt, final Boolean broke, final Boolean bug, final Boolean stand, final boolean facebook) {

        tracker.send(
            new HitBuilders.EventBuilder()
                    .setCategory("registrando")
                    .setAction("velocidade")
                    .setLabel(company)
                    .setValue(Long.valueOf(velocity.replaceAll(" Km/h", "")))
            .build()
        );

        if (belt) {
            tracker.send(
                new HitBuilders.EventBuilder()
                    .setCategory("registrando")
                    .setAction("sinto_de_seguranca")
                    .setLabel(company)
                    .build()
            );
        }

        if (fast) {
            tracker.send(
                    new HitBuilders.EventBuilder()
                            .setCategory("registrando")
                            .setAction("velocidade")
                            .setLabel(company)
                            .build()
            );
        }

        if (stand) {
            tracker.send(
                    new HitBuilders.EventBuilder()
                            .setCategory("registrando")
                            .setAction("superlotado")
                            .setLabel(company)
                            .build()
            );
        }

        if (bug) {
            tracker.send(
                    new HitBuilders.EventBuilder()
                            .setCategory("registrando")
                            .setAction("falta_higienizacao")
                            .setLabel(company)
                            .build()
            );
        }

        if (broke) {
            tracker.send(
                    new HitBuilders.EventBuilder()
                            .setCategory("registrando")
                            .setAction("estragado")
                            .setLabel(company)
                            .build()
            );
        }

        // Refresh Details
        initDetail();

        // Feedback Dialog, confirming registration
        FinishDialogFragment dialogFinish = new FinishDialogFragment();
        dialogFinish.show(getSupportFragmentManager(), TAG_FINISH);

        // If is facebook login auth
        if (facebook) {

            // Make login and get user info, otherwise save anonymous
            ParseFacebookUtils.logIn(this, new LogInCallback() {
                @Override
                public void done(final ParseUser parseUser, com.parse.ParseException e) {
                    if (parseUser == null) {
                        saveAnonymous(company, velocity, fast, belt, broke, bug, stand);
                    } else {
                        sendToParse(company, velocity, fast, belt, broke, bug, stand, parseUser);

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
            saveAnonymous(company, velocity, fast, belt, broke, bug, stand);
        }
    }

    private void initDetail() {
        detailFragment = new DetailFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, detailFragment)
                .commit();
    }

    private void saveAnonymous(final String company, final String velocity, final Boolean fast, final Boolean belt, final Boolean broke, final Boolean bug, final Boolean stand) {

        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
        if (ParseFacebookUtils.getSession() != null && ParseFacebookUtils.getSession().isOpened()) {
            ParseFacebookUtils.getSession().closeAndClearTokenInformation();
        }

        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                sendToParse(company, velocity, fast, belt, broke, bug, stand, parseUser);
            }
        });
    }

    public void sendToParse(String company, String velocity, Boolean fast, Boolean belt, Boolean broke, Boolean bug, Boolean stand, ParseUser user) {

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        ParseObject registro = new ParseObject("Registro");
        registro.put("company", company);
        if (user != null) {
            registro.put("User", user);
        }
        if (lastLocation != null) {
            ParseGeoPoint geo = new ParseGeoPoint();
            geo.setLatitude(lastLocation.getLatitude());
            geo.setLongitude(lastLocation.getLongitude());
            registro.put("location", geo);
        }
        registro.put("fast", fast);
        registro.put("velocity", velocity);
        registro.put("broke", broke);
        registro.put("dirty", bug);
        registro.put("noBelt", belt);
        registro.put("crowded", stand);
        registro.saveEventually();
    }
}
