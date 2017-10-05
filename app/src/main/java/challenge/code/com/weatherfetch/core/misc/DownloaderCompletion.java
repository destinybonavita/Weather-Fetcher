package challenge.code.com.weatherfetch.core.misc;

import org.json.JSONException;

import challenge.code.com.weatherfetch.core.weather.WCFailed;

/**
 * Created by Destiny Bonavita on 10/4/2017.
 */

public interface DownloaderCompletion {
    public void Complete(String value);
    public void Failed(WCFailed reason);
}
