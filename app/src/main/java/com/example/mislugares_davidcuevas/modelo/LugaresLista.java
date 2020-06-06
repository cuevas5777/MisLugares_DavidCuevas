package com.example.mislugares_davidcuevas.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase LugaresLista que implementa la clase RepositorioLugares
 */
public class LugaresLista implements RepositorioLugares {


    protected List<Lugar> listaLugares;

    /**
     * Constructor para definir un ArrayList de Lugar y llamar el método de Añadir ejemplos
     */
    public LugaresLista() {
        listaLugares = new ArrayList<Lugar>();

    }

    /**
     * Método para devolver el lugar de ese id
     *
     * @param id Id del Lugar
     * @return Lugar
     */
    public Lugar elemento(int id) {
        return listaLugares.get(id);
    }

    /**
     * Método para añadir un Lugar a la lista
     *
     * @param lugar Lugar
     */
    public void aniade(Lugar lugar) {
        listaLugares.add(lugar);
    }

    /**
     * Método para crear un nuevo Lugar
     *
     * @return Id del Lugar agregado
     */
    public int nuevo() {
        Lugar lugar = new Lugar();
        listaLugares.add(lugar);
        return listaLugares.size()-1;
    }

    /**
     * Método para borrar un Lugar de la lista
     *
     * @param id Id del Lugar a borrar
     */
    public void borrar(int id) {
        listaLugares.remove(id);
    }

    /**
     * Método para obtener el tamaño de la lista de lugares
     *
     * @return Tamaño de la lista
     */
    public int tamanyo() {
        return listaLugares.size();
    }

    /**
     * Método para actualizar el Lugar indicado
     *
     * @param id    Id del Lugar que quiere editar
     * @param lugar Objeto Lugar con los nuevos datos
     */
    public void actualiza(int id, Lugar lugar) {
        listaLugares.set(id, lugar);
    }


}