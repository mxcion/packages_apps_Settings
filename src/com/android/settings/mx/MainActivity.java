package com.android.settings.mx;

import androidx.appcompat.app.AppCompatActivity;

import com.android.settings.R;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_style_preview);

        /*StatusbarBattery obj = new StatusbarBattery();

        final int[] status_bar_style = {obj.status_bar_style};*/

        TextView battery_level_val = (TextView)findViewById(R.id.battery_level_txt);
        ImageView battery_img = (ImageView)findViewById(R.id.battery_img);
        ImageView ic_battery_circle = (ImageView)findViewById(R.id.ic_battery_circle);

        Context context = this;

        //See for reference https://developer.android.com/training/monitoring-device-state/battery-monitoring
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        //Remove uneeded Decimal
        String numWihoutDecimal = String.valueOf(batteryPct).split("\\.")[0];

        battery_level_val.setText(numWihoutDecimal);

        /*if (status_bar_style[0] != 1){

            battery_img.setVisibility(View.GONE);
            ic_battery_circle.setVisibility(View.VISIBLE);


        }else{

            battery_img.setVisibility(View.VISIBLE);
            ic_battery_circle.setVisibility(View.GONE);

        }*/
    }
}