package challenge.code.com.weatherfetch.core.zipcodes;

import android.support.design.widget.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.HashMap;

import challenge.code.com.weatherfetch.core.misc.Downloader;
import challenge.code.com.weatherfetch.core.misc.DownloaderCompletion;
import challenge.code.com.weatherfetch.core.weather.WCFailed;

/**
 * Created by Destiny Bonavita on 10/4/2017.
 */

public class Zips {
    //region Static Properties
    // API Key to request coordinate data for zip codes
    public static final String API_KEY = "rvEku1jTKHeBpe34ectbInVVHYutW5qoB4J562ZQJdr9rudGHHSp5NlMLQfIVG6x";

    // API Format
    public static final String API = "https://www.zipcodeapi.com/rest/%s/info.json/%s/degrees";

    // Saved Zip Codes (to prevent having to request too many times)
    public static HashMap<String, Double[]> ZipCodes;
    //endregion

    public Zips() {
        if (ZipCodes == null)
            ZipCodes = new HashMap<String, Double[]>();
    }

    public void RequestZip(final ZipCompletion Completion, final String Zip)
    {
        /// Check to see if we have the zip codes cached
        if (ZipCodes.containsKey(Zip))
            Completion.Complete(ZipCodes.get(Zip)[0], ZipCodes.get(Zip)[1]);

        /// If not, download the zip information from ZipCodeApi
        else {
            Downloader downloader = new Downloader();

            downloader.handler = new DownloaderCompletion() {
                @Override
                public void Complete(String value) {
                    try {
                        JSONObject json = new JSONObject(value);

                        if (json.has("error_code") || !json.has("lat") || !json.has("lng"))
                            Completion.Failed(WCFailed.InvalidZip);
                        else {
                            // Cache the zipcode information to prevent having to run multiple requests
                            ZipCodes.put(Zip, new Double[] { json.getDouble("lat"), json.getDouble("lng") });

                            // Tell the completion handler we got it
                            Completion.Complete(json.getDouble("lat"), json.getDouble("lng"));
                        }
                    } catch (JSONException ex) {
                        Completion.Failed(WCFailed.Unknown);
                    }
                }

                @Override
                public void Failed(WCFailed reason) {
                    Completion.Failed(reason);
                }
            };

            downloader.execute(String.format(API, API_KEY, Zip.toString()));
        }
    }
}
