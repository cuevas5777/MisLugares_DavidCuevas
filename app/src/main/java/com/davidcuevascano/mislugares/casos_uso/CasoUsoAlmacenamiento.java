package com.davidcuevascano.mislugares.casos_uso;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


/**
 * Clase que va a encargarse de los distintos permisos de almacenamiento
 * <p>
 *      Estos son los permisos de escritura y de lectura para poder guardar archivos desde la app en el movil
 *      o para ver los archivos que necesite una operacion que este realizando la aplicacion
 * </p>
 * En esta clase, vamos a controlar la peticion de los dichos permisos.
 * @see androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
 */
public class CasoUsoAlmacenamiento implements ActivityCompat.OnRequestPermissionsResultCallback {


    private static final String TAG = "Mis_Lugares";
    private Activity actividad;
    private int codigoPermiso;


    /**
     * Constructor para solicitar permisos de lectura y escritura
     * <p>
     *     Solicitamos los permisos llamando al método solicitar permisos.
     *     Eso quiere decir que cuando la clase casoUsoAlmacenamiento se instancia,
     *     lo primero que hará es solicitar los permisos para lectura y escritura en almacenamiento externo.
     * </p>
     * @param actividad
     * @param codigoPermiso
     */
    public CasoUsoAlmacenamiento(Activity actividad, int codigoPermiso) {
        this.actividad = actividad;
        this.codigoPermiso = codigoPermiso;
        solicitarPermiso(Manifest.permission.READ_EXTERNAL_STORAGE,
                "Necesita permisos de almacenamiento para añadir fotografías",1);
        solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "Necesita permisos de almacenamiento para añadir fotografías",2);

    }


    /**
     * Método que genera un dialogo para pedir que se acepten los permisos de almacenamiento de la app
     * El método shouldShowRequestPermissionRationale:
     * <p>
     *     Si este metodo devolviese verdadero,
     *     quiere decir que al usuario nunca le ha solicitado el permiso la aplicación,
     *     o se le ha solicitado pero ha indicado que se pregunte cada vez acerca del permiso.
     * </p>
     * <p>
     *      Si este metodo devolviese falso, significa que ya se ha solicitado y el usuario ha contestado que no se vuelva a preguntar.
     *      En este caso, se le debería indicar al usuario que debe dar los permisos manualmente
     * </p>
     *
     * @param permiso
     * @param justificacion
     * @param requestCode
     */
    public  void solicitarPermiso(final String permiso, String justificacion, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }})
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

    /**
     * Método que genera un dialogo para pedir que se acepten los permisos de almacenamiento de la app
     * El método shouldShowRequestPermissionRationale:
     *<p>
     *     Si este metodo devolviese verdadero,
     *     quiere decir que al usuario nunca le ha solicitado el permiso la aplicación,
     *     o se le ha solicitado pero ha indicado que se pregunte cada vez acerca del permiso.
     *</p>
     *<p>
     *      Si este metodo devolviese falso, significa que ya se ha solicitado y el usuario ha contestado que no se vuelva a preguntar.
     *      En este caso, se le debería indicar al usuario que debe dar los permisos manualmente
     * </p>
     *
     * @param permiso
     * @param justificacion
     * @param requestCode
     * @param fragment
     */
    public  void solicitarPermisoFragment(final String permiso, String justificacion, final int requestCode, final Fragment fragment) {
        if (fragment.shouldShowRequestPermissionRationale(permiso)){
            new AlertDialog.Builder(fragment.getActivity())
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            fragment.requestPermissions(
                                    new String[]{permiso}, requestCode);
                        }})
                    .show();
        } else {
            fragment.requestPermissions(new String[]{permiso}, requestCode);
        }
    }


    /**
     * Metodo que si devuelve true significa que la app tiene permisos de lectura concedidos
     * READ_EXTERNAL_STORAGE: este permiso lo solicitamos para hacer fotografias.
     * Con el metodo de activityCompact requestPermissions solicitamos los permisos que necesitamos.
     *
     * @return true o false
     */
    public boolean hayPermisoAlmacenamiento() {
        return (ActivityCompat.checkSelfPermission(
                actividad, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }


    /**
     * Metodo que si devuelve true significa que la app tiene permisos de escritura concedidos
     * WRITE_EXTERNAL_STORAGE: este permiso lo solicitamos para escribir en almacenamiento, hacer una foto y almacenarla en un content provider.
     * Con el metodo de activityCompact requestPermissions solicitamos los permisos que necesitamos.
     *
     * @return true o false
     */
    public boolean hayPermisoAlmacenamientoEscritura() {
        return (ActivityCompat.checkSelfPermission(
                actividad, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Funciona muy parecido a startActivityForResult, en el sentido de que mandamos un requestcode,
     * y en la respuesta podemos comprobar si los permisos han sido efectivamente concedidos.
     *<p>
     *     Dspues de solicitar un permiso se llama a este método en el que se puede comprobar si el
     *     permiso en cuestion ha sido solicidado o no
     *</p>
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == codigoPermiso
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this.actividad, "Permiso de lectura concedido",Toast.LENGTH_LONG).show();
    }
}


