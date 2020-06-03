package com.example.mislugares_davidcuevas.casos_uso;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.example.mislugares_davidcuevas.adaptadores.AdaptadorLugaresBD;
import com.example.mislugares_davidcuevas.datos.LugaresBD;
import com.example.mislugares_davidcuevas.mapas.MapsActivity;
import com.example.mislugares_davidcuevas.modelo.GeoPunto;
import com.example.mislugares_davidcuevas.modelo.Lugar;
import com.example.mislugares_davidcuevas.presentacion.Aplicacion;
import com.example.mislugares_davidcuevas.presentacion.EdicionLugarActivity;
import com.example.mislugares_davidcuevas.presentacion.VistaLugarActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import static com.example.mislugares_davidcuevas.presentacion.VistaLugarActivity.RESULTADO_GALERIA;

/**
 * Clase donde se realiza las operaciones habituales de los lugares
 * hacia la base de datos SQLite(Guardado , borrado y Modificado) o
 * eventos que se realizan en las vistas de la aplicacion (Mostrar lugares ,
 * Mostrar Mapa , Llamada,visualizar web y etc)
 */
public class CasosUsoLugar {
    private Activity actividad;
    private LugaresBD lugares;
    private AdaptadorLugaresBD adaptador;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private UUID idFirebase;



    /**
     * Constructor de clase que recibe el contestodo de la actividad que lo usa , un Repositorio de Lugares
     * y el Adaptador de la Base e datos SQLite, inicializando las variables de esta clase.
     *
     * @see Activity
     * @see LugaresBD
     * @see AdaptadorLugaresBD
     *
     * @param actividad
     * @param lugares
     * @param adaptador
     */
    public CasosUsoLugar(Activity actividad, LugaresBD lugares, AdaptadorLugaresBD adaptador) {
        this.actividad = actividad;
        this.lugares = lugares;
        this.adaptador = adaptador;
    }


    /**
     * Recibe la posicion del lugar y envia un intent con esa posicion a
     * VistaLugarActivity para que se inicie la actividad y muestre ese lugar.
     * @param pos
     */
    public void mostrar(int pos, String idFirebase) {
        Intent i = new Intent(actividad, VistaLugarActivity.class);
        i.putExtra("pos", pos);
        i.putExtra("idFirebase", idFirebase);
        actividad.startActivity(i);
    }
    public void mostrar(int pos) {
        Intent i = new Intent(actividad, VistaLugarActivity.class);
        i.putExtra("pos", pos);
        actividad.startActivity(i);
    }


    /**
     * Metodo que recibe el id de un lugar y llama al metodo Lugares.borrar()
     * para eliminarlo de la base de datos, despues extrae el cursor de lugaresBD para
     * que el RecicleeView actualice los datos en el MainActivity.
     * @see LugaresBD#borrar(int)
     * @param id del lugar en la base de datos.
     */
    public void borrar(final int id) {
        new AlertDialog.Builder(actividad)
                .setTitle("Borrar lugar")
                .setMessage("¿Desea eliminar este lugar?")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        lugares.borrar(id);
                        adaptador.setCursor(lugares.extraeCursor());
                        adaptador.notifyDataSetChanged();
                        actividad.finish();

                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

    }

    /**
     * Metodo que recibe un ID del Lugar que se quiere editar y llamara a la actividad
     * EdicionLugarActivity para mostrar los campos modificables del lugar y sus datos ,
     * siempre y cuando el lugar no sea nuevo pues si no se mostraran vacios para que el
     * usuario los rellene.
     *
     * @param pos Corresponde al Identificador del lugar de la base de datos.
     * @param codidoSolicitud Para actualizar la vista de lugar a traves del onActivityResult de la actividad.
     */
    public void editar(int pos, int codidoSolicitud) {
        Intent i = new Intent(actividad, EdicionLugarActivity.class);
        i.putExtra("pos", pos);
        actividad.startActivityForResult(i, codidoSolicitud);
    }

    /**
     * Metodo recibe un Lugar y un ID , que guardara los cambios que se hayan realizado
     * en Lugar al que corresponda al ID que ha recibido.
     * @see LugaresBD#actualiza(int, Lugar)
     * @param id  identificador del lugar en la base datos SQLite
     * @param nuevoLugar Lugar con datos modificados que se van a guardar.
     */
    public void guardar(int id, Lugar nuevoLugar) {
        lugares.actualiza(id, nuevoLugar);
        adaptador.setCursor(lugares.extraeCursor());
        adaptador.notifyDataSetChanged();
    }

    /**
     * Actualiza posicion de lugar.
     * <p>
     *     Para ello nos ayudamos del metodo idPosicion de AdaptadorLugaresBD el cual mueve la posicion a la posicion actual del terminal
     * </p>
     *
     * @param pos
     * @param lugar
     */
    public void actualizaPosLugar(int pos, Lugar lugar) {
        int id = adaptador.idPosicion(pos);
        guardar(id, lugar);  //
    }

    /**
     * Compartir el lugar a traves de la aplicacion que el usuario eliga a traves de un intent.
     * @param lugar
     */
    public void compartir(Lugar lugar) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,
                lugar.getNombre() + " - " + lugar.getUrl());
        actividad.startActivity(i);
    }


    /**
     * Metodo que recibira el lugar y atraves de un intent abrira el dial de llamadas
     * para llamar al telefono de contacto del lugar.
     * @param lugar
     */
    public void llamarTelefono(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }


    /**
     * Metodo que Abrira la web del lugar en el navegador del telefono.
     * @param lugar
     */
    public void verPgWeb(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }


    /**
     * Muestra la ubicacion del lugar en google maps.
     * <p>
     *     Muestra la posicion del lugar basandose en la Latitud y Longitud que contenga dicho lugar
     * </p>
     * <p>
     *     Si el lugar no tiene posicion lo colocara en donde se encuentre el terminal en ese momento
     * </p>
     * @param lugar
     */
    public void verMapa(Lugar lugar) {
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        Uri uri = lugar.getPosicion() != GeoPunto.SIN_POSICION
                ? Uri.parse("geo:" + lat + ',' + lon)
                : Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        actividad.startActivity(new Intent("android.intent.action.VIEW", uri));
    }


    /**
     * Metodo que coloca una foto de la VistaLugarActivity y esta se actualiza si añadimos otra foto o la seleccionamos desde la galeria
     * @param pos
     * @param uri
     * @param imageView
     */
    public void ponerFoto(int pos, String uri, ImageView imageView) {
        Lugar lugar = adaptador.lugarPosicion(pos);
        lugar.setFoto(uri);
        visualizarFoto(lugar, imageView);
        actualizaPosLugar(pos, lugar);
    }


    /**
     * Metodo que abre la galeria del telefono para seleccionar una imagen y la URI es enviada
     * a traves de un intent
     * @param view
     */
    public void galeria(View view) {
        String action;
        if (android.os.Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat
            action = Intent.ACTION_OPEN_DOCUMENT;
        } else {
            action = Intent.ACTION_PICK;
        }
        Intent intent = new Intent(action,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        actividad.startActivityForResult(intent, RESULTADO_GALERIA);
    }


    /**
     * Visualiza la foto en la ImageView pasa por parametro.
     * @see VistaLugarActivity
     * @param lugar lugar
     * @param imageView  ImageView
     */
    public void visualizarFoto(Lugar lugar, ImageView imageView) {
        if (lugar.getFoto() != null && !lugar.getFoto().isEmpty()) {
            imageView.setImageBitmap(reduceBitmap(actividad, String.valueOf(Uri.parse(lugar.getFoto())), 1024,   1024));
        } else {
            imageView.setImageBitmap(null);
        }
    }


    /**
     * Abre la aplicacion de la camara del movil para tomar una foto , cuando
     * la foto ha sido tomada devuelve la URI de  la imagen.
     *
     * @param codidoSolicitud
     * @return Uri de la imagen
     */
    public Uri tomarFoto(int codidoSolicitud) {
        try {
            Uri uriUltimaFoto;
            File file = File.createTempFile(
                    "img_" + (System.currentTimeMillis()/ 1000), ".jpg" ,
                    actividad.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            if (Build.VERSION.SDK_INT >= 24) {
                uriUltimaFoto = FileProvider.getUriForFile(
                        actividad, "com.example.mislugares_davidcuevas.fileprovider", file);
            } else {
                uriUltimaFoto = Uri.fromFile(file);
            }
            Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra (MediaStore.EXTRA_OUTPUT, uriUltimaFoto);
            actividad.startActivityForResult(intent, codidoSolicitud);
            return uriUltimaFoto;
        } catch (IOException ex) {
            Toast.makeText(actividad, "Error al crear fichero de imagen",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * Decodificar mapas de bits grandes sin exceder el límite de memoria por aplicación al subir una versión de submuestreo más pequeña a la memoria.
     * <p>
     *     Al establecer la propiedad inJustDecodeBounds en true durante la decodificación, se evita la asignación de memoria,
     *     con lo cual se muestra null para el objeto de mapa de bits, pero se establecen outWidth, outHeight y outMimeType.
     *     Esta técnica permite leer las dimensiones y el tipo de los datos de la imagen antes de la construcción (y la asignación de memoria) del mapa de bits.
     * </p>
     *
     * @param contexto
     * @param uri
     * @param maxAncho
     * @param maxAlto
     * @return
     */
    private Bitmap reduceBitmap(Context contexto, String uri, int maxAncho, int maxAlto) {
        try {
            InputStream input = null;
            Uri u = Uri.parse(uri);
            if (u.getScheme().equals("http") || u.getScheme().equals("https")) {
                input = new URL(uri).openStream();
            } else {
                input = contexto.getContentResolver().openInputStream(u);
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(input, null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso de imagen no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Toast.makeText(contexto, "Error accediendo a imagen",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo que creara un nuevo lugar  y abrira EdicionLugar para que insertemos los datos iniciales
     */
    public void nuevo() {
        int id = lugares.nuevo();
        GeoPunto posicion = ((Aplicacion) actividad.getApplication())
                .posicionActual;
        if (!posicion.equals(GeoPunto.SIN_POSICION)) {
            Lugar lugar = lugares.elemento(id);
            lugar.setPosicion(posicion);
            lugares.actualiza(id, lugar);
        }
        Intent i = new Intent(actividad, EdicionLugarActivity.class);
        i.putExtra("_id", id);

        actividad.startActivity(i);
    }

    /**
     * Sobre carga del metodo que crea un nuevo lugar
     * <p>
     *     Este metodo permite crear un nuevo lugar a raiz de añadir una marca en el mapa,
     *     de esta manera guarda la posicion de la marca en relacion al nuevo id del lugar
     * </p>
     * @param posicion
     */
    public void nuevo(GeoPunto posicion) {
        int id = lugares.nuevo();
        if (!posicion.equals(GeoPunto.SIN_POSICION)) {
            Lugar lugar = lugares.elemento(id);
            lugar.setPosicion(posicion);
            lugares.actualiza(id, lugar);
        }
        Intent i = new Intent(actividad, EdicionLugarActivity.class);
        i.putExtra("_id", id);

        actividad.startActivity(i);

    }

    /**
     * Metodo que redirige al mapa al ser llamado desde VistaLugarActivity
     * <p>
     *     En el mapa solamente muestra la marca en el mapa mediante el id ya que este metodo es llamado en VistaLugarActivity para
     *     mostrar en el mapa la ubicacion exacta del lugar sin que aparezcan los demas lugares ya que se limita al id que recoge de la clase
     * </p>
     * @param id
     */
    public void mapa(int id) {
        Intent i = new Intent(actividad, MapsActivity.class);
        i.putExtra("_id", id);
        actividad.startActivity(i);
    }

}
