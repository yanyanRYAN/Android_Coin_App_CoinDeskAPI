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
import java.util.HashMap;

public class WebList extends ListActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_list);

        Intent getter = getIntent();
        url = getter.getStringExtra("url");
        Log.d("Url", url);

        DownloadCoin getCurrent = new DownloadCoin();
        getCurrent.execute(url);

    }

    private class DownloadCoin extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>{

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            dialog = ProgressDialog.show(WebList.this, "", "Downloading Data...", true, false);
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... strings) {


            StringBuffer jsonData = new StringBuffer();

            //might not be needed
            HashMap<String, String> currentPrice;
            ArrayList<HashMap<String,String>> results = new ArrayList<>();

            try{
                //get url
                URL url = new URL(strings[0]);
                Log.d("URL",  url.toString());
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

                //I am dumb I was trying to convert json object into array when there was no array in the json -_- it fixed now
                JSONObject jsonObject = new JSONObject(jsonData.toString());
                //JSONObject bitcoinPriceIndex = jsonObject.getJSONObject("bpi").getJSONObject("USD").getJSONObject("rate");  //bruh, I can chain methods with json objects...
                JSONObject bitcoinPriceIndex = jsonObject.getJSONObject("bpi").getJSONObject("USD");
                //float bitcoinPriceFloat = jsonObject.getJSONObject("bpi").getJSONObject("USD").getJSONObject("rate");
                //JSONObject bitcoinPriceUSD = jsonObject.getJSONObject("bpi").getJSONObject("USD").getJSONObject("rate_float");
                JSONObject timeObject = jsonObject.getJSONObject("time");
                //JSONObject disclaimerPrompt = jsonObject.getJSONObject("disclaimer");
                String disclaimerPrompt = jsonObject.getString("disclaimer");
                Log.d("Text", disclaimerPrompt.toString());

                String timeUpdated = timeObject.getString("updated");
                Log.d("Time", timeUpdated.toString());

                String priceUSD = bitcoinPriceIndex.getString("rate");
                Log.d("Results", "$" + priceUSD.toString()); //it works!


                for(int i = 0; i < bitcoinPriceIndex.length(); i++){
                    Log.d("Iterations", "Object iteration " + i); //iterates 5 times, there is 5 objects in the USD
                }
                //put into hashmap
                currentPrice = new HashMap<>();
                currentPrice.put("time", timeUpdated);
                currentPrice.put("price", priceUSD);
                results.add(currentPrice);

                Arrays.toString(results.toArray());
                Log.d("Array", Arrays.toString(results.toArray()));
                return results;

            } catch (Exception e){
                Log.e("Error", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            //super.onPostExecute(hashMaps);
            if(dialog.isShowing()){
                dialog.dismiss();
            }

            Log.d("Array post execute", Arrays.toString(hashMaps.toArray()));
            ListAdapter adapter = new SimpleAdapter(WebList.this, hashMaps, R.layout.row, new String[]{"time", "price"}, new int[]{R.id.time_text, R.id.price_text});

            WebList.this.setListAdapter(adapter);


        }
    }

}
