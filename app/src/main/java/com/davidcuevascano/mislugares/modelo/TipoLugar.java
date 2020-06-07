package com.davidcuevascano.mislugares.modelo;


import com.davidcuevascano.mislugares.R;

/**
 * Listado de los Tipos con su respectivo icono
 */
public enum TipoLugar {
    OTROS ("Otros", R.drawable.otros),
    RESTAURANTE ("Restaurante", R.drawable.restaurante),
    BAR ("Bar", R.drawable.bar),
    COPAS ("Copas", R.drawable.copas),
    ESPECTACULO ("Espectáculo", R.drawable.espectaculos),
    HOTEL ("Hotel", R.drawable.hotel),
    COMPRAS ("Compras", R.drawable.compras),
    EDUCACION ("Educación", R.drawable.educacion),
    DEPORTE ("Deporte",  R.drawable.deporte),
    NATURALEZA ("Naturaleza", R.drawable.naturaleza),
    GASOLINERA ("Gasolinera", R.drawable.gasolinera);

    private final String texto;
    private final int recurso;

    /**
     * @param texto
     * @param recurso
     */
    TipoLugar(String texto, int recurso) {
        this.texto = texto;
        this.recurso = recurso;
    }

    public String getTexto() {
        return texto;
    }

    public int getRecurso() {
        return recurso;
    }

    /**
     * Getter para obtener un array de los Nombres
     *
     * @return Array de nombres
     */
    public static String[] getNombres() {
        String[] resultado = new String[TipoLugar.values().length];
        for (TipoLugar tipo : TipoLugar.values()) {
            resultado[tipo.ordinal()] = tipo.texto;
        }
        return resultado;
    }
}
