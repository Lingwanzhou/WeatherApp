package com.example.zhouli.weatherapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {
    private String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=Ottawa&mode=xml&appid=306b57fc8679397bbbe482cf9a465d5d";

    private TextView minTemp;
    private TextView maxTemp;
    private TextView windtex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        minTemp = findViewById(R.id.min_temp_output);
        maxTemp = findViewById(R.id.max_temp_output);
        windtex = findViewById(R.id.wind_output);
        new weatherForcast().execute(null, null, null);

    }

    private class weatherForcast extends AsyncTask<String, Integer, String> {
        InputStream inputStream;

        String wind;
        String min;
        String max;



        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(weatherUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(10000); //in milliseconds
                urlConnection.setConnectTimeout(15000); //in millisenconds
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inputStream, null);
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType != XmlPullParser.START_TAG) {
                       eventType= parser.next();
                        continue;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("temperature")) {
                            min = parser.getAttributeValue(null, "min");
                            max = parser.getAttributeValue(null, "max");
                        } else if (parser.getName().equals("speed")) {
                            wind = parser.getAttributeValue(null, "value");

                        }

                    }
                    eventType=parser.next();
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            minTemp.setText("Min: " + min + "C");
            maxTemp.setText("Max: " + max + "C");
            windtex.setText("Wind: " + wind);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
