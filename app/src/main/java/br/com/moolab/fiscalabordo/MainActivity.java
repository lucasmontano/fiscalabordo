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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import br.com.moolab.fiscalabordo.fragments.ConfirmDialogFragment;
import br.com.moolab.fiscalabordo.fragments.DetailFragment;
import br.com.moolab.fiscalabordo.fragments.FinishDialogFragment;
import br.com.moolab.fiscalabordo.utils.FontsUtils;


public class MainActivity extends ActionBarActivity implements ConfirmDialogFragment.ConfirmCallback {

    public static final String DIALOG_CONFIRM = "DIALOG_CONFIRM";
    private static final String TAG_FINISH = "TAG_FINISH";
    private Tracker tracker;

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
        tracker = ((FiscalAbordoApp) getApplication()).getTracker();
        tracker.setScreenName("Main");
        tracker.send(new HitBuilders.AppViewBuilder().build());
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

    private void initDetail() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DetailFragment())
                .commit();
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
}
