package com.dam.juanmaseatitserver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Clase dedicada a la pantalla inicial de la aplicación, que se muestra durante breves momentos
 */
public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int duracion_Splash = 3000;

        // El handler ejecutará el código en el tiempo que indiquemos
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
        }, duracion_Splash);
    }
}