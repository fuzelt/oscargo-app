package com.oscargo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onResume() {
        System.out.println("MainActivity onResume");
        SharedPreferences sharedPreferences = getSharedPreferences("oscargo_shared_preferences", MODE_PRIVATE);
        boolean switch_location_checked = sharedPreferences.getBoolean("switch_location_checked", false);
        System.out.println("MainActivity onResume switch_location_checked " + switch_location_checked);

        String name = sharedPreferences.getString("name", null);
        System.out.println("MainActivity onResume name " + name);

        TextView switch_location_checked_text = findViewById(R.id.switch_location_checked_text);
        if(switch_location_checked){
            switch_location_checked_text.setText("Enviando localización");
        }else{
            switch_location_checked_text.setText("No se está enviando la localización");
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_config) {
            Intent intent = new Intent(this, ConfigActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}