package com.example.mislugares_davidcuevas.datos;

import com.example.mislugares_davidcuevas.modelo.GeoPunto;
import com.example.mislugares_davidcuevas.modelo.TipoLugar;

import java.io.Serializable;

public class LugaresFirebase implements Serializable {

    String nombre;
    String direccion;
    String url;
    int telefono;
    String comentario;
    GeoPunto posicion;
    TipoLugar tipo;
    float Valoracion;

    public LugaresFirebase(){

    }

    public LugaresFirebase(String nombre, String direccion, String url, int telefono, String comentario, GeoPunto posicion, TipoLugar tipo, float valoracion) {

        this.nombre = nombre;
        this.direccion = direccion;
        this.url = url;
        this.telefono = telefono;
        this.comentario = comentario;
        this.posicion = posicion;
        this.tipo = tipo;
        Valoracion = valoracion;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public GeoPunto getPosicion() {
        return posicion;
    }

    public void setPosicion(GeoPunto posicion) {
        this.posicion = posicion;
    }

    public TipoLugar getTipo() {
        return tipo;
    }

    public void setTipo(TipoLugar tipo) {
        this.tipo = tipo;
    }

    public float getValoracion() {
        return Valoracion;
    }

    public void setValoracion(float valoracion) {
        Valoracion = valoracion;
    }
}
