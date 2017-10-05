package challenge.code.com.weatherfetch.core.zipcodes;

import challenge.code.com.weatherfetch.core.weather.WCFailed;

/**
 * Created by Destiny Bonavita on 10/4/2017.
 */

public interface ZipCompletion {
    public void Failed(WCFailed reason);
    public void Complete(Double lat, Double lon);
}
