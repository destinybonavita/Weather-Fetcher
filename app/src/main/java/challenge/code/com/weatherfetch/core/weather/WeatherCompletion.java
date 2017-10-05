package challenge.code.com.weatherfetch.core.weather;

import challenge.code.com.weatherfetch.core.weather.model.Report;

public interface WeatherCompletion {
    public void Completed(Report report);
    public void Failed(WCFailed Reason);
}
