package edu.lewisu.cs.yanyanryan.coinboi;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class WebListHistory extends ListActivity {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_list_history);

        Intent getter = getIntent();
        url = getter.getStringExtra("url");
        Log.d("onCreate Url", url);

        DownloadCoin getHistory = new DownloadCoin();
        getHistory.execute(url);
    }

    private class DownloadCoin extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>{

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            dialog = ProgressDialog.show(WebListHistory.this, "", "Downloading Data...", true, false);
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {

            //import calendar
            Calendar now = Calendar.getInstance();
            int today = now.get(Calendar.DATE);
            int month = now.get(Calendar.MONTH);
            int year = now.get(Calendar.YEAR);

            Log.d("Date", "" + today);
            Log.d("Month", "" + month);
            Log.d("Year", "" + year);

            StringBuffer jsonData = new StringBuffer();

            HashMap<String, String> yesterdayPrice;
            ArrayList<HashMap<String,String>> results = new ArrayList<>();

            try{
                //get url
                URL url = new URL(strings[0]);
                Log.d("DownloadCoin URL",  url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                //read
                String line = bufferedReader.readLine();
                while(line != null){
                    jsonData.append(line);
                    line = bufferedReader.readLine();

                }
                Log.d("Results mah boi:", jsonData.toString());
                //Since we have the url and json read in, lets make it a json object

                JSONObject jsonObject = new JSONObject(jsonData.toString());

                JSONObject bitcoinClosingPriceObject = jsonObject.getJSONObject("bpi");
                String bitcoinClosingPrice = bitcoinClosingPriceObject.toString();
                Log.d("Bitcoin Closing Price", " " + bitcoinClosingPrice);

                yesterdayPrice = new HashMap<>();
                Iterator<String> keys = bitcoinClosingPriceObject.keys();

                while(keys.hasNext()){
                    String key = keys.next();

                    String value = bitcoinClosingPriceObject.getString(key);
                    Log.d("Keys:", key);
                    Log.d("Value:", value);
                    yesterdayPrice.put("time", key);
                    yesterdayPrice.put("price", value);
                }
                results.add(yesterdayPrice);




                Arrays.toString(results.toArray());
                Log.d("Array after loop", Arrays.toString(results.toArray()));
                return results;


            } catch (Exception e){
                Log.d("Error", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
           // super.onPostExecute(hashMaps);
            if(dialog.isShowing()){
                dialog.dismiss();
            }

            Log.d("Array post execute", Arrays.toString(hashMaps.toArray()));
            ListAdapter adapter = new SimpleAdapter(WebListHistory.this, hashMaps, R.layout.row, new String[]{"time","price"}, new int[]{R.id.time_text, R.id.price_text});

            WebListHistory.this.setListAdapter(adapter);
        }
    }
}
