package com.davidcuevascano.mislugares.casos_uso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.davidcuevascano.mislugares.adaptadores.AdaptadorLugares;
import com.davidcuevascano.mislugares.modelo.GeoPunto;
import com.davidcuevascano.mislugares.presentacion.Aplicacion;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Clase para los permisos de localización
 * LocationListener Se utiliza para recibir notificaciones del LocationManager cuando la ubicación ha cambiado.
 * <p>
 *     es el manejador de localizaciones de Android. Esta clase proporciona acceso a los servicios de ubicación del sistema.
 *     Estos servicios permiten que las aplicaciones obtengan actualizaciones periódicas de la ubicación geográfica del dispositivo
 *     o que activen una aplicación especificada  (con un intent) cuando el dispositivo entra en la proximidad de una ubicación geográfica determinada.
 * </p>
 */
public class CasoUsoLocalizacion implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "MisLugares";
    private Activity actividad;
    private int codigoPermiso;
    private LocationManager manejadorLoc;

    /**
     * Es una clase de datos que representa una ubicación geográfica.
     * Una ubicación puede consistir en una latitud, longitud, time-stamp y otra información como rumbo (bearing)
     * direccion geográfica (norte, sur, etc.), altitud y velocidad.
     */
    private Location mejorLoc;
    private GeoPunto posicionActual;
    private AdaptadorLugares adaptador;

    /**
     * Tiempo en milisegundos.
     */
    private static final long DOS_MINUTOS = 2 * 60 * 1000;


    /**
     * Constructor para solicitar los permisos de localizacion y a la vez, inicializar la posición actual y la mejor posición conocida
     * @param actividad
     * @param codigoPermiso
     */
    public CasoUsoLocalizacion(Activity actividad, int codigoPermiso) {
        this.actividad = actividad;
        this.codigoPermiso = codigoPermiso;
        manejadorLoc = (LocationManager) actividad.getSystemService(LOCATION_SERVICE);
        posicionActual = ((Aplicacion) actividad.getApplication())
                .posicionActual;
        adaptador = ((Aplicacion) actividad.getApplication()).adaptador;
        ultimaLocalizazion();
        solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                "Necesita permisos de localizacion",3, actividad);
    }

    /**
     * Mñetodo que si devuelve true es que la app tiene permisos de localizacion concedidos
     *
     * @return true o false
     */
    public boolean hayPermisoLocalizacion() {
        return (ActivityCompat.checkSelfPermission(
                actividad, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Método que guarda la ultima localización conocida del terminal
     */
    @SuppressLint("MissingPermission")
    void ultimaLocalizazion(){
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER));
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER));
            } else  {
                solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                        "Sin el permiso localización no puedo mostrar la distancia"+
                                " a los lugares.", codigoPermiso, actividad);
            }
        }
    }

    /**
     * Método que genera un dialogo para pedir que se acepten los permisos de localización de la app
     *
     * shouldShowRequestPermissionRationale
     * <p>
     *      Obtiene si debe mostrar la interfaz de usuario con justificación para solicitar un permiso.
     *      Debe hacer esto solo si no tiene el permiso y el contexto en el que se solicita, no comunica claramente al usuario.
     *</p>
     *
     * requestPermissions
     * <p>
     *      Este método puede iniciar una actividad que permita al usuario elegir qué permisos otorgar y cuáles rechazar.
     *      Por lo tanto, debe estar preparado para que su actividad se detenga y se reanude.
     *      Además, otorgar algunos permisos puede requerir un reinicio de su aplicación
     *</p>
     * @param permiso
     * @param justificacion
     * @param requestCode
     * @param actividad
     */
    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }}).show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

    /**
     * Fuerza la búsqueda de una nueva localización, activa los proovedores,
     * y comunica al adaptador que ha habido cambios en la localización actual.
     * Esto lo usaremos para que actualice el Recyclerview con las nuevas distancias
     * <p>
     *     adaptador.notifyDataSetChanged();, actualiza los datos del adaptador
     * </p>
     */
    public void permisoConcedido() {
        ultimaLocalizazion();
        activarProveedores();
        adaptador.notifyDataSetChanged();
    }

    /**
     * Método que activa los proveedores de GPS e Internet
     * <p>
     *      Activa tanto el proveedore GPS como el de telefonía.
     *      Si el proveedor está disponible, pide actualizaciones cada 20  segundos para el GPS, diez segundos para el proveedor de red telefónica.
     *      Sino hay permisos de localización, se solicitan.
     * </p>
     */
    @SuppressLint("MissingPermission")
    private void activarProveedores() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                /**
                 * Al metodo manejadorLoc.requestLocationUpdates, le proporcionamos el proveedor de red o GPS ,
                 * un tiempo de espera de 10 segundos para Network y 20 para GPS, la distancia mínima en metros, y un LocationListener para esperar la respuesta
                 * en este caso nuestra propia clase CasoUsoLocalizacion es un LocationListener
                 */
                manejadorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        20 * 1000, 5, this);
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                manejadorLoc.requestLocationUpdates(LocationManager
                        .NETWORK_PROVIDER, 10 * 1000, 10, this);
            }
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no puedo mostrar la distancia"+
                            " a los lugares.", codigoPermiso, actividad);
        }
    }

    /**
     * Método que actualiza la localización si el terminal se ha movido
     *
     * notifyDataSetChanged
     * <p>
     *     Este evento no especifica qué ha cambiado el conjunto de datos,
     *     lo que obliga a los observadores a suponer que todos los elementos
     *     y la estructura existentes pueden dejar de ser válidos
     * </p>
     * @param location
     */
    @Override public void onLocationChanged(Location location) {
        Log.d(TAG, "Nueva localización: "+location);
        actualizaMejorLocaliz(location);
        adaptador.notifyDataSetChanged();
    }

    /**
     * Método que activa los proveedores si estos estan deshabilitados
     * @param proveedor
     */
    @Override public void onProviderDisabled(String proveedor) {
        Log.d(TAG, "Se deshabilita: "+proveedor);
        activarProveedores();
    }

    /**
     * Método que activa los proveedores para que no dejen de estas habilitados
     * @param proveedor
     */
    @Override public void onProviderEnabled(String proveedor) {
        Log.d(TAG, "Se habilita: "+proveedor);
        activarProveedores();
    }

    /**
     * Método que comprueba que la ubicación del terminal ha cambiado
     * @param proveedor
     * @param estado
     * @param extras
     */
    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        Log.d(TAG, "Cambia estado: "+proveedor);
        activarProveedores();
    }

    /**
     * Método que actualiza a la mejor localización del terminal cada 2 minutos
     * <p>
     *     Actualiza la localizacion hacia una localización nueva si la precisión es menor que dos veces la precisión de la mejor localización que hemos obtenido anteriormente
     *     y el tiempo de obtención  entre ambas localizaciones (timestamp) es mayor de dos minutos.
     * </p>
     * @param localiz
     */
    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz != null && (mejorLoc == null
                || localiz.getAccuracy() < 2*mejorLoc.getAccuracy()
                || localiz.getTime() - mejorLoc.getTime() > DOS_MINUTOS)) {
            Log.d(TAG, "Nueva mejor localización");
            mejorLoc = localiz;
            posicionActual.setLatitud(
                    localiz.getLatitude());
            posicionActual.setLongitud(
                    localiz.getLongitude());
        }
    }

    /**
     * Método que activa los permisos de localización y proveedores
     */
    public void activar() {
        if (hayPermisoLocalizacion()) activarProveedores();
    }


    /**
     * Método que borra las localizaciones para el ahorro de datos
     *
     * removeUpdates
     * <p>
     *     Elimina las actualizaciones de ubicación para lo especificado LocationListener.
     *     Después de esta llamada, el oyente ya no recibirá actualizaciones de ubicación.
     * </p>
     */
    public void desactivar() {
        if (hayPermisoLocalizacion()) manejadorLoc.removeUpdates(this);
    }

    /**
     * Devuelve si el permiso de localización es concedido
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == codigoPermiso
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this.actividad, "Permiso de localización concedido",Toast.LENGTH_LONG).show();
    }
}
