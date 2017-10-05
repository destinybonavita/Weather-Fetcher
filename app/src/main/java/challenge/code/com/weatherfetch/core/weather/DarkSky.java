package challenge.code.com.weatherfetch.core.weather;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import challenge.code.com.weatherfetch.core.misc.Downloader;
import challenge.code.com.weatherfetch.core.misc.DownloaderCompletion;
import challenge.code.com.weatherfetch.core.weather.model.Report;
import challenge.code.com.weatherfetch.core.zipcodes.ZipCompletion;
import challenge.code.com.weatherfetch.core.zipcodes.Zips;

/**
 * Created by Destiny Bonavita on 10/4/2017.
 */

public class DarkSky {
    //region Static
    public static final String API_KEY = "88b31e10ec14cb4e1ef1f54c1e015cb6";

    public static final String API = "https://api.darksky.net/forecast/%s/%s,%s?exclude=currently,minutely,alerts";
    //endregion

    //region Properties
    // Cached reports
    public HashMap<String, Report> Reports;

    // Zip Codes
    public Zips ZipCodes;
    //endregion

    public DarkSky() {
        Reports = new HashMap<String, Report>();
        ZipCodes = new Zips();
    }

    public static String getLastZip(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);

        return prefs.getString("Zip", "");
    }

    public static void setLastZip(Context context, String zip) {
        SharedPreferences prefs = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("Zip", zip);

        editor.commit();
    }

    public void Request(final String ZipCode, final WeatherCompletion Completion) {
        ZipCodes.RequestZip(new ZipCompletion() {
            @Override
            public void Failed(WCFailed reason) {
                Completion.Failed(reason);
            }

            @Override
            public void Complete(Double lat, Double lon) {
                /// Check to see if we have a weather report stored
                boolean shouldRequest = !Reports.containsKey(ZipCode);

                /// If we have a report stored, check to see if it's greater than 15 minutes old
                if (!shouldRequest) {
                    Date now = new Date();
                    Date timestamp = Reports.get(ZipCode).timestamp;

                    /// Calculate the duration difference in milliseconds
                    long duration = now.getTime() - timestamp.getTime();

                    /// Convert the milliseconds to minutes and verify it's less than 15
                    shouldRequest = TimeUnit.MILLISECONDS.toMinutes(duration) < 15;
                }

                /// If we should still request, download the latest information
                if (shouldRequest) {
                    Downloader download = new Downloader();

                    download.handler = new DownloaderCompletion() {
                        @Override
                        public void Complete(String value) {
                            try {
                                Reports.put(ZipCode, Report.reportFromJson(value));

                                Completion.Completed(Reports.get(ZipCode));
                            }
                            catch (JSONException e) {
                                Completion.Failed(WCFailed.Unknown);
                            }
                        }

                        @Override
                        public void Failed(WCFailed reason) {
                            Completion.Failed(reason);
                        }
                    };

                    download.execute(String.format(API, API_KEY, lat.toString(), lon.toString()));
                }
            }
        }, ZipCode);
    }
}
