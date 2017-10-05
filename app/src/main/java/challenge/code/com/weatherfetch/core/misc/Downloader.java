package challenge.code.com.weatherfetch.core.misc;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import challenge.code.com.weatherfetch.core.weather.WCFailed;

/**
 * Created by Destiny Bonavita on 10/4/2017.
 */

public class Downloader extends AsyncTask<String, String, WCFailed> {
    public DownloaderCompletion handler;
    private String resp;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected WCFailed doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            /// Set that the client knows we're trying to read a GET response from the URL
            connection.setRequestMethod("GET");

            // Allow up to 10 seconds for the connection incase a slow connection
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);

            /// Connect to the URL
            connection.connect();

            /// Validate that we have a successful connection
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return WCFailed.Unknown;

            /// Read the response string
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.resp = ""; String line;

            while ((line = reader.readLine()) != null)
                resp += line + "\n";

            return null;
        }
        catch (MalformedURLException e) {
            return WCFailed.Unknown;
        }
        catch (FileNotFoundException e) {
            return WCFailed.InvalidZip;
        }
        catch (IOException e) {
            return WCFailed.NoConnection;
        }
    }

    @Override
    protected void onPostExecute(WCFailed s)
    {
        if (s == null)
            handler.Complete(resp);
        else
            handler.Failed(s);

        super.onPostExecute(s);
    }
}
