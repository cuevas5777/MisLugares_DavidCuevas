package com.davidcuevascano.mislugares.modelo;

/**
 * Clase utilizada para manejar los datos de un Lugar
 *
 * @author David Cuevas Cano
 */
public class Lugar {
    private String nombre;
    private String direccion;
    private GeoPunto posicion;
    private String foto;
    private int telefono;
    private String url;
    private String comentario;
    private long fecha;
    private float valoracion;

    private TipoLugar tipo;

    /**
     * Constructor vacio de la clase Lugar
     * Inicializa {@link #fecha}, {@link #posicion} y {@link #tipo}
     */
    public Lugar() {
        fecha = System.currentTimeMillis();
        posicion =  GeoPunto.SIN_POSICION;
        tipo = TipoLugar.OTROS;
    }

    /**
     * Constructor de Lugar con los respectivos parámetros
     * @param nombre     Nombre del Lugar
     * @param direccion  Dirección del Lugar
     * @param longitud   Longitud del Lugar
     * @param latitud    Latitud del Lugar
     * @param tipo       Tipo que hace mención a la clase TipoLugar que es un enum
     * @param telefono   Teléfono del Lugar
     * @param url        Url para la web del Lugar
     * @param comentario Comentario del Lugar
     * @param valoracion Valoración del Lugar
     */
    public Lugar(String nombre, String direccion, double longitud,
                 double latitud, TipoLugar tipo, int telefono,
                 String url, String comentario, int valoracion)  {
        fecha = System.currentTimeMillis();
        posicion = new GeoPunto(longitud, latitud);
        this.tipo=tipo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.url = url;
        this.comentario = comentario;
        this.valoracion = valoracion;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    public GeoPunto getPosicion() {
        return posicion;
    }


    public void setPosicion(GeoPunto posicion) {
        this.posicion = posicion;
    }


    public String getFoto() {
        return foto;
    }


    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getTelefono() {
        return telefono;
    }


    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getComentario() {
        return comentario;
    }


    public void setComentario(String comentario) {
        this.comentario = comentario;
    }


    public long getFecha() {
        return fecha;
    }


    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public float getValoracion() {
        return valoracion;
    }


    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public TipoLugar getTipo() {
        return tipo;
    }

    public void setTipo(TipoLugar tipo) {
        this.tipo = tipo;
    }

    /**
     * Método toString para convertir un objeto en String en el formato que definimos
     *
     * @return String de Lugar
     */
    @Override
    public String toString() {
        return "Lugar{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", posicion=" + posicion +
                ", foto='" + foto + '\'' +
                ", telefono=" + telefono +
                ", url='" + url + '\'' +
                ", comentario='" + comentario + '\'' +
                ", fecha=" + fecha +
                ", valoracion=" + valoracion +
                ", tipo=" + tipo +
                '}';
    }
}
