package challenge.code.com.weatherfetch.core.weather.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Destiny Bonavita on 10/5/2017.
 */

public class Report {

    public String json;
    public Date timestamp;

    public JSONArray hourly;
    public JSONArray daily;

    public static Report reportFromJson(String Value) throws JSONException {
        Report report = new Report();

        /// Retain the json string & timestamp, used later
        report.json = Value;
        report.timestamp = new Date();

        /// Parse the json object
        JSONObject json = new JSONObject(Value);

        /// Get the hours & days
        report.hourly = json.getJSONObject("hourly").getJSONArray("data");
        report.daily = json.getJSONObject("daily").getJSONArray("data");

        return report;
    }
}
