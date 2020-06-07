package com.davidcuevascano.mislugares.adaptadores;

import android.database.Cursor;

import com.davidcuevascano.mislugares.datos.LugaresBD;
import com.davidcuevascano.mislugares.modelo.Lugar;
import com.davidcuevascano.mislugares.modelo.RepositorioLugares;

/**
 * Clase para adaptar la base de datos a nuestra app
 * para que se guarden tanto los cambios que realicemos en los lugares
 * como cuando creamos un nuevo lugar
 */
public class AdaptadorLugaresBD extends AdaptadorLugares  {


    protected Cursor cursor;

    /**
     * Constructor para inicializar el cursor de la clase
     * @param lugares
     * @param cursor
     */
    public AdaptadorLugaresBD(RepositorioLugares lugares, Cursor cursor) {
        super(lugares);
        this.cursor = cursor;
    }


    public Cursor getCursor() {
        return cursor;
    }


    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Devuelve el lugar a partir de la posición
     *
     * @param posicion
     */
    public Lugar lugarPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        return LugaresBD.extraeLugar(cursor);
    }

    /**
     * Devuelve el ID del lugar dependiendo de la posición que ocupe este en la base de datos
     * @param posicion
     * @return id del lugar
     */
    public int idPosicion(int posicion) {
        cursor.moveToPosition(posicion);
        return cursor.getInt(0);
    }

    /**
     * Método que muestra la posición a partir del ID del lugar
     * @param id
     * @return posición del lugar
     */
    public int posicionId(int id) {
        int pos = 0;
        while (pos<getItemCount() && idPosicion(pos)!=id) pos++;
        if (pos>= getItemCount()){
            return -1;
        }
        else{
            return pos;
        }
    }

    /**
     * Método que actualiza los ViewHolder a partir de la posicion del elemento
     * @param holder
     * @param posicion
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Lugar lugar = lugarPosicion(posicion);
        holder.personaliza(lugar);
        holder.itemView.setTag(new Integer(posicion));
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos
     */
    @Override public int getItemCount() {
        return cursor.getCount();
    }
}
