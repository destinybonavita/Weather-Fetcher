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

import java.util.concurrent.TimeUnit;

/**
 * Created by Destiny Bonavita on 10/5/2017.
 */

public class HourlyAdapter extends BaseAdapter {
    //region Properties
    /// Data to display
    public JSONArray data = null;

    /// Other shared properties
    private Context context;
    private LayoutInflater inflater;
    //endregion

    public HourlyAdapter(Context context) {
        this.context = context;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : 24;
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

            view = inflater.inflate(R.layout.hourly_listview_item, parent, false);

            ImageView statusImgView = (ImageView)view.findViewById(R.id.statusImage);
            TextView temp = (TextView)view.findViewById(R.id.tempLbl);
            TextView hourLbl = (TextView)view.findViewById(R.id.hourLbl);
            TextView windSpeed = (TextView)view.findViewById(R.id.windSpeedLbl);
            TextView windGust = (TextView)view.findViewById(R.id.windGustLbl);

            statusImgView.setImageDrawable(img);

            hourLbl.setText("+" + (position + 1));
            temp.setText(dat.getString("temperature"));

            windSpeed.setText(dat.getString("windSpeed") + " / " + dat.getString("windBearing"));
            windGust.setText(dat.getString("windGust"));
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
