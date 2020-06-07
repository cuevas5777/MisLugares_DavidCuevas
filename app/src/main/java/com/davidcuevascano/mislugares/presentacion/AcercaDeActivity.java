package com.davidcuevascano.mislugares.presentacion;

import android.app.Activity;
import android.os.Bundle;

import com.davidcuevascano.mislugares.R;

/**
 * Clase para mostrar una breve descripción sobre nuestra aplicación y nuestra companía. Informamos al usuario
 * sobre nuestros objetivos de la aplicación y su ejecución.
 * @see android.app.Activity
 */
public class AcercaDeActivity extends Activity {

    /**
     * Inicializa los componentes de la actividad. El argumento Bundle
     * contiene el estado ya guardado de la actividad.
     * Si la actividad nunca ha existido, el valor del objeto Bundle es nulo.
     * <p>
     * muestra la configuración básica de la actividad, como declarar
     * la interfaz de usuario (definida en un archivo XML de diseño),
     * definir las variables de miembro y configurar parte de la IU
     * </p>
     *
     * @param savedInstanceState objeto Bundle que contiene el estado de la actividad.
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acercade);
    }
}
