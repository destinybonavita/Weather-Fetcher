package challenge.code.com.weatherfetch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import challenge.code.com.weatherfetch.core.weather.model.Report;

public class hourly extends AppCompatActivity {
    private Report weatherReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly);

        try {
            weatherReport = Report.reportFromJson(getIntent().getStringExtra("json"));

            HourlyAdapter adapter = new HourlyAdapter(getBaseContext());
            adapter.data = weatherReport.hourly;

            ListView listView = (ListView)findViewById(R.id.hourlyList);
            listView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            Toast.makeText(getBaseContext(), "Failed to show hourly weather", Toast.LENGTH_SHORT).show();
        }
    }
}
