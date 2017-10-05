package challenge.code.com.weatherfetch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

/**
 * Created by Destiny Bonavita on 10/5/2017.
 */

public class DailyAdapter extends BaseAdapter {
    //region Properties
    /// Data to display
    public JSONArray data = null;

    /// Which view we should be using and how much data to display
    public boolean isExpanded = false;

    /// Other shared properties
    private Context context;
    private LayoutInflater inflater;
    //endregion

    public DailyAdapter(Context context) {
        this.context = context;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : !isExpanded ? 3 : 7;
    }

    @Override
    public Object getItem(int position) {
        try {
            return data == null ? null : data.get(position);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        try {
            JSONObject dat = data.getJSONObject(position);
            Drawable img = drawableByName(dat.getString("icon").replace("-", "_"));

            if (isExpanded) {
                view = inflater.inflate(R.layout.daily_exp_listview_item, parent, false);

                ImageView statusImgView = (ImageView)view.findViewById(R.id.statusImage);
                TextView dayLbl = (TextView)view.findViewById(R.id.dayLbl);
                TextView sunrise = (TextView)view.findViewById(R.id.sunriseLbl);
                TextView sunset = (TextView)view.findViewById(R.id.sunsetlbl);
                TextView tempHigh = (TextView)view.findViewById(R.id.tempHighLbl);
                TextView tempLow = (TextView)view.findViewById(R.id.tempLowLbl);
                TextView tempMax = (TextView)view.findViewById(R.id.tempMaxLbl);
                TextView tempMin = (TextView)view.findViewById(R.id.tempMinLbl);
                TextView windSpeed = (TextView)view.findViewById(R.id.windSpeedLbl);
                TextView windGust = (TextView)view.findViewById(R.id.windGustLbl);

                statusImgView.setImageDrawable(img);

                dayLbl.setText("+" + (position + 1));
                sunrise.setText(TimeUnit.MILLISECONDS.toHours(dat.getInt("sunriseTime")) + "");
                sunset.setText(TimeUnit.MILLISECONDS.toHours(dat.getInt("sunsetTime")) + "");

                tempHigh.setText(dat.getString("temperatureHigh"));
                tempLow.setText(dat.getString("temperatureLow"));

                tempMax.setText(dat.getString("temperatureMax"));
                tempMin.setText(dat.getString("temperatureMin"));

                windSpeed.setText(dat.getString("windSpeed") + " / " + dat.getString("windBearing"));
                windGust.setText(dat.getString("windGust"));
            }
            else
            {
                view = inflater.inflate(R.layout.daily_listview_item, parent, false);

                ImageView imageView = (ImageView)view.findViewById(R.id.imageView5);
                TextView textView = (TextView)view.findViewById(R.id.textView7);

                textView.setText("Day +" + (position + 1));
                imageView.setImageDrawable(img);
            }
        }
        catch (Exception ex) {

        }

        return view;
    }

    private Drawable drawableByName(String name) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(name, "drawable", context.getPackageName());

        return resources.getDrawable(resourceId);
    }
}
