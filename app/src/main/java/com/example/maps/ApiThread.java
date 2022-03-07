package com.example.maps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiThread extends AsyncTask<Void, Void, String> {
    public JSONObject jObject;
    public double latitud;
    public double longitud;
    public ApiThread(double lat,double lng){
        this.latitud=lat;
        this.longitud=lng;
    }
    @Override
    protected String doInBackground(Void... voids) {
        //crear objecte url
        URL url = null;
        try {
            url = new URL("https://api.sunrise-sunset.org/json?lat=36.7201600&lng=-4.4203400");
            //Realitzarem la connexió mitjançant la classe HttpURLConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // Read API results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String data = bufferedReader.readLine();
            return data;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        try {
            jObject = new JSONObject(data);
            //Ara hem d’agafar les dades del “fill” results
            jObject = jObject.getJSONObject("results");

            //Finalment agafem el “fill” sunrise de results
            String sunrise = jObject.getString("sunrise");
            Log.i("logtest", "------>" + sunrise);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
