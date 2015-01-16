package br.com.moolab.fiscalabordo;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;

/**
 * Created by lucas on 14/01/15.
 */
public class FiscalABordoApp extends Application {

    public void onCreate() {
        Parse.initialize(this, "BH64VIC5VgSfnpAZ0k24q2XghUszuUjnleslRZag", "pTyDW3oMjr6FcPoZAEAH0rfxMpfD6luPNM2CSSwX");
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
