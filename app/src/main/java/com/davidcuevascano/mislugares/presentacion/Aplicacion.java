package com.davidcuevascano.mislugares.presentacion;

import android.app.Application;

import com.davidcuevascano.mislugares.adaptadores.AdaptadorLugaresBD;
import com.davidcuevascano.mislugares.datos.LugaresBD;
import com.davidcuevascano.mislugares.modelo.GeoPunto;

/**
 * Clase para mantener el estado global de la aplicación.
 * Proporcionamos nuestra propia implementación creando una subclase (Application) y especificando el nombre completo.
 * La clase de aplicación se instancia antes que cualquier otra clase cuando se crea el proceso para su paquete.
 * @see android.app.Application
 */

public class Aplicacion extends Application {

    /**
     * La coleccion de Lugares almacenados en la BDD.
     */
    public LugaresBD lugares;

    /**
     * Metodo para poder recuperar nuestro adaptador desde cualquier parte de la aplicación.
     */
    public AdaptadorLugaresBD adaptador;


    /**
     * La posicionActual del lugar, clase GeoPunto que almacena la longitud y la latitud para el posicionamiento.
     */
    public GeoPunto posicionActual = new GeoPunto(0.0, 0.0);


    /**
     * Inicializa los componentes de la actividad.
     * Recoge los los lugares de la BD y el adaptador
     */
    @Override public void onCreate() {
        super.onCreate();
        lugares = new LugaresBD(this);
        adaptador= new AdaptadorLugaresBD(lugares, lugares.extraeCursor());

    }
}


