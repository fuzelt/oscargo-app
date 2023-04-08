package com.oscargo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class ConfigActivity extends AppCompatActivity {

    TextInputLayout nameEditText;
    TextInputLayout phoneEditText;
    TextInputLayout patentEditText;
    TextInputLayout capacityEditText;
    TextInputLayout destinationEditText;
    TextInputLayout returnEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().setTitle("Configuración");


        SharedPreferences sharedPreferences = getSharedPreferences("oscargo_shared_preferences", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", null);
        nameEditText = findViewById(R.id.nameEditText);
        nameEditText.getEditText().setText(name);

        String phone = sharedPreferences.getString("phone", null);
        phoneEditText = findViewById(R.id.phoneEditText);
        phoneEditText.getEditText().setText(phone);

        String patent = sharedPreferences.getString("patent", null);
        patentEditText = findViewById(R.id.patentEditText);
        patentEditText.getEditText().setText(patent);

        if(sharedPreferences.getInt("capacity", 0)>0) {
            int capacity = sharedPreferences.getInt("capacity", 0);
            capacityEditText = findViewById(R.id.capacityEditText);
            capacityEditText.getEditText().setText(String.valueOf(capacity));
        }

        String destination = sharedPreferences.getString("destination", null);
        destinationEditText = findViewById(R.id.destinationEditText);
        destinationEditText.getEditText().setText(destination);

        String returnValue = sharedPreferences.getString("return", null);
        returnEditText = findViewById(R.id.returnEditText);
        returnEditText.getEditText().setText(returnValue);

        Button clickButton = (Button) findViewById(R.id.save);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SharedPreferences sharedPreferences = getSharedPreferences("oscargo_shared_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String keyString = "keyString";
                String valueString = "valueString";
                int valueInteger = 0;

                patentEditText = findViewById(R.id.patentEditText);
                keyString = "patent";
                valueString = String.valueOf(patentEditText.getEditText().getText());
                editor.putString(keyString, valueString);
                editor.commit();

                phoneEditText = findViewById(R.id.phoneEditText);
                keyString = "phone";
                valueString = String.valueOf(phoneEditText.getEditText().getText());
                editor.putString(keyString, valueString);
                editor.commit();

                nameEditText = findViewById(R.id.nameEditText);
                keyString = "name";
                valueString = String.valueOf(nameEditText.getEditText().getText());
                editor.putString(keyString, valueString);
                editor.commit();

                capacityEditText = findViewById(R.id.capacityEditText);
                keyString = "capacity";
                valueInteger = Integer.parseInt(String.valueOf(capacityEditText.getEditText().getText()));
                editor.putInt(keyString, valueInteger);
                editor.commit();

                destinationEditText = findViewById(R.id.destinationEditText);
                keyString = "destination";
                valueString = String.valueOf(destinationEditText.getEditText().getText());
                editor.putString(keyString, valueString);
                editor.commit();

                returnEditText = findViewById(R.id.returnEditText);
                keyString = "return";
                valueString = String.valueOf(returnEditText.getEditText().getText());
                editor.putString(keyString, valueString);
                editor.commit();

                Toast.makeText(getApplicationContext(),"Datos guardados correctamente",Toast.LENGTH_SHORT).show();

                String patent = sharedPreferences.getString("patent", null);
                System.out.println("patent: " + patent);


            }
        });

    }
}