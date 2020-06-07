package com.davidcuevascano.mislugares.modelo;

/**
 *Clase que guarda la longitud y latitud de un lugar.
 */
public class GeoPunto {

    private double longitud, latitud;

    /**
     *Geopunto con ambas variablas "latitud" y "longitud" en 0.0
     */
    static public GeoPunto SIN_POSICION = new GeoPunto(0.0,0.0);

    /**
     * Instantiates a new Geo punto.
     * @param longitud loongitud tipo double
     * @param latitud  latitud tipo double
     */
    public GeoPunto(double longitud, double latitud) {
        this.longitud= longitud;
        this.latitud= latitud;
    }

    public String toString() {
        return new String("longitud:" + longitud + ", latitud:"+ latitud);
    }


    /**
     * Calcula la distancia entre un Gepunto y el pasada por parametro.
     * @param punto Geopunto
     * @return double
     */
    public double distancia(GeoPunto punto) {
        final double RADIO_TIERRA = 6371000; // en metros
        double dLat = Math.toRadians(latitud - punto.latitud);
        double dLon = Math.toRadians(longitud - punto.longitud);
        double lat1 = Math.toRadians(punto.latitud);
        double lat2 = Math.toRadians(latitud);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) *
                        Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * RADIO_TIERRA;
    }

    /**
     * Punto sin posicion
     *
     * @return GeoPunto
     */
    public static GeoPunto puntoSinPosicion() {
        return SIN_POSICION;
    }


    public double getLongitud() {
        return longitud;
    }


    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    public double getLatitud() {
        return latitud;
    }


    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
}
