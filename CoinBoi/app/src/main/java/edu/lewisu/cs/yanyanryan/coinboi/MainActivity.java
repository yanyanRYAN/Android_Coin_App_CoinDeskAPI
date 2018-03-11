package edu.lewisu.cs.yanyanryan.coinboi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button currentBtn = findViewById(R.id.currentButton);
        Button historyBtn = findViewById(R.id.historyButton);
    }

    public void currentButtonClicked(View v){
        String url = "https://api.coindesk.com/v1/bpi/currentprice.json";
        Intent launchCurrentPrice = new Intent(this, WebList.class);
        launchCurrentPrice.putExtra("url", url);
        startActivity(launchCurrentPrice);
    }

    public void historyButtonClicked(View v){
        String url = "https://api.coindesk.com/v1/bpi/historical/close.json";
        Intent launchPriceHistory = new Intent(this, WebListHistory.class);
        launchPriceHistory.putExtra("url", url);
        startActivity(launchPriceHistory);
    }
}
