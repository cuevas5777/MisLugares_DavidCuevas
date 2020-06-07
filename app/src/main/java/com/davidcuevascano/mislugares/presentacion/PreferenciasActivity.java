package com.davidcuevascano.mislugares.presentacion;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que simplemente muestra {@link PreferenciasFragment}
 * Devuelve el FragmentManager para interactuar con fragmentos asociados con la actividad de este fragmento.
 */
public class PreferenciasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new PreferenciasFragment())
            .commit();
    }

}
