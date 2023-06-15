package com.dam.juanmaseatitserver;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Clase principal e inicial de la aplicación
 */
public class MainActivity extends AppCompatActivity {
    // Atributos de clase
    Button btnSignIn;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        btnSignIn.setOnClickListener(view -> {
            Intent signIn = new Intent(MainActivity.this, SignIn.class);
            startActivity(signIn);
        });
    }
}