package ru.smartcity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPhelper extends AsyncTask<String, String, String> {

    private static final String TAG = HTTPhelper.class.getSimpleName();

    private String getResponse(String request) {
        String response = null;
        try {
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            InputStream is = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(is);
            return response;
        } catch (MalformedURLException e) {
            Log.e(TAG, "getJSON, MalformedURLException: " + e.getMessage());
            return "false\n";
        } catch (IOException e) {
            Log.e(TAG,"getJSON, IOException: " + e.getMessage());
            return "false\n";
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "convertStreamString, IOException: " + e.getMessage());
            return "false\n";
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "convertStreamString, IOException: " + e.getMessage());
                return "false\n";
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return getResponse(strings[0]);
    }
}
