package com.davidcuevascano.mislugares.datos;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.davidcuevascano.mislugares.modelo.GeoPunto;
import com.davidcuevascano.mislugares.modelo.Lugar;
import com.davidcuevascano.mislugares.modelo.RepositorioLugares;
import com.davidcuevascano.mislugares.modelo.TipoLugar;
import com.davidcuevascano.mislugares.presentacion.Aplicacion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

/**
 * Clase que crea  la base de datos si no existe y que contiene
 * operaciones basicas con la base dedatos SQLite
 */
public class LugaresBD extends SQLiteOpenHelper implements RepositorioLugares {


    Context contexto;

    /**
     * Inilizacion de la clase
     * @param contexto  Contexto
     */
    public LugaresBD(Context contexto) {
        super(contexto, "lugares", null, 1);
        this.contexto = contexto;
    }

    /**
     * Crea la base de datos si no esta creada ya.
     * @param bd base datos
     */
    @Override public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("CREATE TABLE lugares ("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "nombre TEXT, " +
                "direccion TEXT, " +
                "longitud REAL, " +
                "latitud REAL, " +
                "tipo INTEGER, " +
                "foto TEXT, " +
                "telefono INTEGER, " +
                "url TEXT, " +
                "comentario TEXT, " +
                "fecha BIGINT, " +
                "valoracion REAL)");
    }

    /**
     * Actualizar base de datos a una nueva version
     * @param db base de datos
     * @param oldVersion Version vieja de la base de datos.
     * @param newVersion Version nueva de la base de datos.
     */
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion,
                                    int newVersion) {
    }

    /**
     * Extrae lugar de un cursor
     * @param cursor the cursor
     */
    public static Lugar extraeLugar(Cursor cursor) {
        Lugar lugar = new Lugar();
        lugar.setNombre(cursor.getString(1));
        lugar.setDireccion(cursor.getString(2));
        lugar.setPosicion(new GeoPunto(cursor.getDouble(3),
                cursor.getDouble(4)));
        lugar.setTipo(TipoLugar.values()[cursor.getInt(5)]);
        lugar.setFoto(cursor.getString(6));
        lugar.setTelefono(cursor.getInt(7));
        lugar.setUrl(cursor.getString(8));
        lugar.setComentario(cursor.getString(9));
        lugar.setFecha(cursor.getLong(10));
        lugar.setValoracion(cursor.getFloat(11));
        return lugar;
    }

    /**
     * Extrae el cursor.
     * SharedPreferences
     * <p>
     *     Interfaz para acceder y modificar datos de preferencias devueltos por Context
     *     Las modificaciones a las preferencias deben pasar por un Editor de objetod
     *     para garantizar que los valores de preferencia permanezcan en un estado y control consistentes cuando se comprometan con el almacenamiento.
     *     Los objetos que se devuelven de los distintos getmétodos deben ser tratados como inmutables por la aplicación.
     * </p>
     * @return Cursor
     */
    public Cursor extraeCursor() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(contexto);
        String consulta;
        switch (pref.getString("orden", "0")) {
            case "0":
                consulta = "SELECT * FROM lugares ";
                break;
            case "1":
                consulta = "SELECT * FROM lugares ORDER BY valoracion DESC";
                break;
            default:
                double lon = ((Aplicacion) contexto.getApplicationContext())
                        .posicionActual.getLongitud();
                double lat = ((Aplicacion) contexto.getApplicationContext())
                        .posicionActual.getLatitud();
                consulta = "SELECT * FROM lugares ORDER BY " +
                        "(" + lon + "-longitud)*(" + lon + "-longitud) + " +
                        "(" + lat + "-latitud )*(" + lat + "-latitud )";
                break;
        }
        consulta += " LIMIT "+pref.getString("maximo","12");
        SQLiteDatabase bd = getReadableDatabase();
        return bd.rawQuery(consulta, null);
    }

    /**
     * Recibe identificador y devuelve el lugar correspondiente a él.
     * @param id identificador de lugar
     * @return
     */
    @Override public Lugar elemento(int id) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM lugares WHERE _id = " + id, null);
        try {
            if (cursor.moveToNext())
                return extraeLugar(cursor);
            else
                throw new SQLException("Error al acceder al elemento _id = " + id);
        } catch (Exception e) {
            throw e;
        } finally {
            if (cursor!=null) cursor.close();
        }
    }

    /**
     * Crea un nuevo lugar vacio y devuelve su identificador correspondiente
     * @return  tipo int ,  identificador del nuevo lugar.
     */
    @Override public int nuevo() {
        int _id = -1;
        Lugar lugar = new Lugar();
        getWritableDatabase().execSQL("INSERT INTO lugares (nombre, " +
                "direccion, longitud, latitud, tipo, foto, telefono, url, " +
                "comentario, fecha, valoracion) VALUES ('', '',  " +
                lugar.getPosicion().getLongitud() + ","+
                lugar.getPosicion().getLatitud() + ", "+ lugar.getTipo().ordinal()+
                ", '', 0, '', '', " + lugar.getFecha() + ", 0)");
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT _id FROM lugares WHERE fecha = " + lugar.getFecha(), null);
        if (c.moveToNext()) _id = c.getInt(0);
        c.close();
        return _id;
    }


    /**
     * Recibe el lugar que va actualizar y el id que lo identifica en la base de datos
     * para poder actualizar.
     * @param id identificador del lugar
     * @param lugar Lugar que recibe con los nuevos datos.
     */
    @Override public void actualiza(int id, Lugar lugar) {
        getWritableDatabase().execSQL("UPDATE lugares SET" +
                "   nombre = '" + lugar.getNombre() +
                "', direccion = '" + lugar.getDireccion() +
                "', longitud = " + lugar.getPosicion().getLongitud() +
                " , latitud = " + lugar.getPosicion().getLatitud() +
                " , tipo = " + lugar.getTipo().ordinal() +
                " , foto = '" + lugar.getFoto() +
                "', telefono = " + lugar.getTelefono() +
                " , url = '" + lugar.getUrl() +
                "', comentario = '" + lugar.getComentario() +
                "', fecha = " + lugar.getFecha() +
                " , valoracion = " + lugar.getValoracion() +
                " WHERE _id = " + id);
    }

    /**
     * @param lugar
     */
    public void aniade(Lugar lugar) {

    }

    /**
     * Borrar Lugar  al que corresponde el id pasado por parametro
     * @param id int , correspondiente al identificador del lugar
     */
    public void borrar(int id) {
        getWritableDatabase().execSQL("DELETE FROM lugares WHERE _id = " + id);
    }

    /**
     * Retorna el numero de lugares que tiene la base de datos.
     * @return tamanyo tipo int.
     */
    @Override
    public int tamanyo() {
        return 0;
    }

}
