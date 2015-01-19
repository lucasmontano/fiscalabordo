package br.com.moolab.fiscalabordo;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;

/**
 * Created by lucas on 14/01/15.
 */
public class FABordoApp extends Application {

    public void onCreate() {
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
    }

    private Tracker mTracker;


    public synchronized Tracker getTracker() {
        if (mTracker == null) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.analytics);

        }
        return mTracker;
    }
}
