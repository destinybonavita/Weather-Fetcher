package challenge.code.com.weatherfetch;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import challenge.code.com.weatherfetch.core.weather.DarkSky;
import challenge.code.com.weatherfetch.core.weather.WCFailed;
import challenge.code.com.weatherfetch.core.weather.WeatherCompletion;
import challenge.code.com.weatherfetch.core.weather.model.Report;

public class MainActivity extends AppCompatActivity {
    //region Properties

    /// Text
    private EditText zipInput;
    private TextView dailyLabel; private boolean dailyExpanded = false;

    /// Repo for getting all the weather data
    private DarkSky Weather;
    private Report WeatherReport;

    /// Images
    private ImageView plusOneH, plusTwoH, plusThreeH, plusFourH;

    /// Containers
    private ConstraintLayout hourlyView;
    private ListView dailyView;

    //endregion

    //region Overwritten Functions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// Set properties
        Weather = new DarkSky();

        /// Grab elements
        zipInput = (EditText) findViewById(R.id.zipInput);
        hourlyView = (ConstraintLayout)findViewById(R.id.hourlyView);
        dailyLabel = (TextView)findViewById(R.id.textView6);
        dailyView = (ListView)findViewById(R.id.dailyView);

        /// Grab image views
        plusOneH = (ImageView)findViewById(R.id.imageView);
        plusTwoH = (ImageView)findViewById(R.id.imageView2);
        plusThreeH = (ImageView)findViewById(R.id.imageView3);
        plusFourH = (ImageView)findViewById(R.id.imageView4);

        /// Create a basic listview adapter
        dailyView.setAdapter(new DailyAdapter(getBaseContext()));

        /// Show last weather data
        if (Weather.getLastZip(getBaseContext()).length() > 0) {
            zipInput.setText(Weather.getLastZip(getBaseContext()));
            RefreshWeather();
        }

        /// Schedule the weather data to refresh every 15 minutes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (zipInput.getText().length() > 0)
                    RefreshWeather();
            }
        }, TimeUnit.MINUTES.toMillis(15));

        /// Set listeners
        zipInput.setOnKeyListener(zipInputListener);
        dailyLabel.setOnClickListener(dailyViewTapped);
        hourlyView.setOnClickListener(hourlyViewTapped);
    }

    //endregion

    //region Functions

    private void RefreshWeather() {
        final String input = zipInput.getText().toString();

        /// If the user actually typed a 5 digit zip code, load the weather info
        if (input.length() == 5) {
            Weather.Request(input, new WeatherCompletion() {
                @Override
                public void Completed(Report report) {
                    /// Retain the last used zip code
                    Weather.setLastZip(getBaseContext(), input);

                    /// Enum through the hourly reports
                    try {
                        for (int i = 0; i < report.hourly.length(); i++) {
                            JSONObject rep = report.hourly.getJSONObject(i);
                            Drawable img = drawableByName(rep.getString("icon").replace("-", "_"));

                            switch (i) {
                                case 0:
                                    plusOneH.setImageDrawable(img);

                                    break;
                                case 1:
                                    plusTwoH.setImageDrawable(img);

                                    break;
                                case 2:
                                    plusThreeH.setImageDrawable(img);

                                    break;
                                case 3:
                                    plusFourH.setImageDrawable(img);

                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getBaseContext(), "Error displaying weather", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /// Set the adapter settings
                    DailyAdapter adapter = (DailyAdapter)dailyView.getAdapter();
                    adapter.isExpanded = dailyExpanded;
                    adapter.data = report.daily;

                    /// Reload the listview
                    adapter.notifyDataSetChanged();

                    /// Show the hourly view with the new data
                    hourlyView.setVisibility(View.VISIBLE);
                    dailyLabel.setVisibility(View.VISIBLE);
                    dailyView.setVisibility(View.VISIBLE);

                    /// Retain the current report
                    WeatherReport = report;

                    /// Inform the user the weather was loaded
                    Toast.makeText(getBaseContext(), "Loaded weather for " + input, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void Failed(WCFailed Reason) {
                    Toast.makeText(getBaseContext(), Reason == WCFailed.InvalidZip ? "The zip code you entered is invalid" : Reason == WCFailed.NoConnection ? "No internet connection." : "Failed to get weather report for " + input, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Toast.makeText(getBaseContext(), "The zip code you entered is invalid", Toast.LENGTH_SHORT).show();
    }

    private Drawable drawableByName(String name) {
        Resources resources = getBaseContext().getResources();
        int resourceId = resources.getIdentifier(name, "drawable", getBaseContext().getPackageName());

        return resources.getDrawable(resourceId);
    }
    //endregion

    //region Listeners

    private View.OnKeyListener zipInputListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            /// Enter / done key tapped
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                RefreshWeather();

                return true;
            }

            return false;
        }
    };

    private View.OnClickListener hourlyViewTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), hourly.class);
            intent.putExtra("json", WeatherReport.json);

            startActivity(intent);
        }
    };

    private View.OnClickListener dailyViewTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dailyExpanded = !dailyExpanded;
            dailyLabel.setText(dailyExpanded ? "Daily - Long" : "Daily - Short");

            DailyAdapter adapter = (DailyAdapter)dailyView.getAdapter();
            adapter.isExpanded = dailyExpanded;

            adapter.notifyDataSetChanged();
        }
    };

    //endregion
}
