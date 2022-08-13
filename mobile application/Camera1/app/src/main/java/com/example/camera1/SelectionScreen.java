package com.example.camera1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SelectionScreen extends AppCompatActivity {


    CardView banana;
    CardView cabbage;
    CardView coffee;
    CardView corn;
    CardView cotton;
    CardView rice;
    CardView sugarcane;
    CardView tomato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_screen);


        banana = findViewById(R.id.banana);
        cabbage = findViewById(R.id.cabbage);
        coffee = findViewById(R.id.coffee);
        corn = findViewById(R.id.corn);
        cotton = findViewById(R.id.cotton);
        rice = findViewById(R.id.rice);
        sugarcane = findViewById(R.id.sugarcane);
        tomato = findViewById(R.id.tomato);

        banana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showToast("You clicked my banana!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "banana");
                startActivity(intent);
            }

        });
        cabbage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showToast("You clicked my cabbage!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "cabbage");
                startActivity(intent);
            }
        });
        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showToast("You clicked my coffee!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "coffee");
                startActivity(intent);
            }
        });
        corn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showToast("You clicked my corn!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "corn");
                startActivity(intent);
            }
        });
        cotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showToast("You clicked my cotton!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "cotton");
                startActivity(intent);
            }
        });
        rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showToast("You clicked my rice!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "rice");
                startActivity(intent);
            }
        });
        sugarcane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showToast("You clicked my sugarcane!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "sugarcane");
                startActivity(intent);
            }
        });
        tomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showToast("You clicked my tomato!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("plantkey", "tomato");
                startActivity(intent);
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}