package com.davidcuevascano.mislugares.modelo;

/**
 * Interfaz de lugares
 * @author David Cuevas Cano
 */
public interface RepositorioLugares {

    /**
     * Recibido el identificador de un Lugar devuelve el lugar corrspondiente
     *
     * @param id identificador de lugar
     * @return Tipo Lugar
     */
    Lugar elemento(int id); //Devuelve el elemento dado su id

    /**
     * Aniade un lugar
     *
     * @param lugar Tipo Lugar
     */
    void aniade(Lugar lugar); //Añade el elemento indicado

    /**
     * Crea un nuevo elemento.
     *
     * @return devuelve el identificador del elemento.
     */
    int nuevo(); //Añade un elemento en blanco y devuelve su id

    /**
     * Borrar el elemento cuyo identificador se ha pasado por parametro.
     *
     * @param id identificador del elemento a borrar.
     */
    void borrar(int id); //Elimina el elemento con el id indicado

    /**
     * Tamaño de elementos
     *
     * @return  Numero de elementos.
     */
    int tamanyo(); //Devuelve el número de elementos

    /**
     * Actualiza el elemento al que le correspone la id lugar
     *
     * @param id   identificador del elemento.
     * @param lugar Lugar con datos modificados.
     */
    void actualiza(int id, Lugar lugar); //Reemplaza un elemento
}