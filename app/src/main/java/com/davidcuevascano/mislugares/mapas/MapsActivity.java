package com.davidcuevascano.mislugares.mapas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.davidcuevascano.mislugares.R;
import com.davidcuevascano.mislugares.adaptadores.AdaptadorLugaresBD;
import com.davidcuevascano.mislugares.casos_uso.CasoUsoLocalizacion;
import com.davidcuevascano.mislugares.casos_uso.CasosUsoLugar;
import com.davidcuevascano.mislugares.datos.LugaresBD;
import com.davidcuevascano.mislugares.modelo.GeoPunto;
import com.davidcuevascano.mislugares.modelo.Lugar;
import com.davidcuevascano.mislugares.presentacion.Aplicacion;
import com.davidcuevascano.mislugares.presentacion.VistaLugarActivity;
import com.google.android.gms.maps.model.Marker;

import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;


import java.util.ArrayList;
import java.util.List;


/**
 * FragmentActivity sobre el que vamos a cargar nuestro mapa de la API de Google Maps
 * @author David Cuevas Cano
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{//, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private MapView mapView;
    private AdaptadorLugaresBD adaptador;
    private CasoUsoLocalizacion usoLocalizacion;
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 1;
    private Context contexto;
    private CasosUsoLugar usoLugar;
    private LugaresBD lugares;
    private Marker m;
    private Lugar lugar;
    private int _id = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.mapa);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        mapboxMap.setStyle(String.valueOf(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")));

    }

    /*@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }*/

















    /*private GoogleMap maps;
    private AdaptadorLugaresBD adaptador;
    private CasoUsoLocalizacion usoLocalizacion;
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 1;
    private Context contexto;
    private CasosUsoLugar usoLugar;
    private LugaresBD lugares;
    private Marker m;
    private Lugar lugar;
    private int _id = -1;

    /**
     * Inicializa los componentes de la actividad.
     * El argumento Bundle contiene el estado ya guardado de la actividad.
     *
     * @param savedInstanceState objeto Bundle que contiene el estado de la actividad.
     */
    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
        adaptador = ((Aplicacion) getApplication()).adaptador;
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        usoLocalizacion = new CasoUsoLocalizacion(this, SOLICITUD_PERMISO_LOCALIZACION);
        contexto = (Aplicacion) getApplication();
        lugares = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this, lugares, adaptador);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            _id = extras.getInt("_id", -1);
        }
    }

    /**
     * Este m??todo se va a llamar cuando el mapa este listo para usarse y Obtengamos un objeto GoogleMap no nulo
     *
     * @param googleMap
     */
    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        maps = googleMap;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(contexto);

        maps.setMapType(Integer.parseInt(pref.getString("tipoMap", "1")));
        maps.setOnMarkerDragListener(this);
        maps.setOnMapLongClickListener(this);
        if (usoLocalizacion.hayPermisoLocalizacion()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            maps.setMyLocationEnabled(true);
        }
        maps.getUiSettings().setZoomControlsEnabled(true);
        maps.getUiSettings().setCompassEnabled(true);
        if (_id >= 0) {
            Lugar lugar = adaptador.lugarPosicion(_id);
            GeoPunto p = lugar.getPosicion();
            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(p.getLatitud(), p.getLongitud()), 12));
            Bitmap iGrande = BitmapFactory.decodeResource(
                    getResources(), lugar.getTipo().getRecurso());
            Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                    iGrande.getWidth() / 7, iGrande.getHeight() / 7, false);
            m = maps.addMarker(new MarkerOptions()
                    .position(new LatLng(p.getLatitud(), p.getLongitud()))
                    .draggable(true)
                    .title(lugar.getNombre()).snippet(lugar.getDireccion())
                    .icon(BitmapDescriptorFactory.fromBitmap(icono)));
            maps.setOnInfoWindowClickListener(this);
            maps.setOnMarkerDragListener(this);
        } else {
            if (adaptador.getItemCount() > 0) {
                GeoPunto p = adaptador.lugarPosicion(0).getPosicion();
                maps.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(p.getLatitud(), p.getLongitud()), 12));
            }
            for (int n = 0; n < adaptador.getItemCount(); n++) {
                Lugar lugar = adaptador.lugarPosicion(n);
                GeoPunto p = lugar.getPosicion();
                if (p != null && p.getLatitud() != 0) {

                    Bitmap iGrande = BitmapFactory.decodeResource(
                            getResources(), lugar.getTipo().getRecurso());
                    Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                            iGrande.getWidth() / 7, iGrande.getHeight() / 7, false);
                    maps.addMarker(new MarkerOptions()
                            .position(new LatLng(p.getLatitud(), p.getLongitud()))
                            .title(lugar.getNombre()).snippet(lugar.getDireccion())
                            .icon(BitmapDescriptorFactory.fromBitmap(icono)));
                }
            }
            maps.setOnInfoWindowClickListener(this);
            maps.setOnMapLongClickListener(this);
        }
    }

    /**
     * Evento que lanza una actividad {@link VistaLugarActivity} cuando se pulsa algun lugar marcado en el mapa
     *
     * @param marker
     */
    /*
    @Override
    public void onInfoWindowClick(Marker marker) {
        for (int id=0; id<adaptador.getItemCount(); id++){
            if (adaptador.lugarPosicion(id).getNombre()
                    .equals(marker.getTitle())){
                Intent intent = new Intent(this, VistaLugarActivity.class);
                intent.putExtra("pos", id);
                startActivity(intent);
                break;
            }
        }

        /**
         * Cuando pulsas al marcador si detecta que el t??tulo es Agregar Lugar llama al m??todo sobrecargado de crear
         * un lugar nuevo con el GeoPunto del marcador
         */
    /*
        if (marker.getTitle().equals("Agregar Lugar")) {
            usoLugar.nuevo(new GeoPunto(marker.getPosition().longitude, marker.getPosition().latitude));
            finish();
        }
    }


    /**
     * M??todo para agregar un marcador en el mapa con el nombre Agregar Lugar, si se pulsa en otro punto se cambia a esa nueva ubicaci??n
     * @param punto
     */
    /*
    @Override
    public void onMapLongClick(LatLng punto) {
        if (m != null) {
            m.setPosition(punto);
        } else {
            m = maps.addMarker(new MarkerOptions()
                    .position(punto)
                    .title("Agregar Lugar")
                    .icon(BitmapDescriptorFactory.defaultMarker())
                    .draggable(true));
        }
    }

    /**
     * Se llama cuando un marcador comienza a ser arrastrado. Se puede acceder a la ubicaci??n del marcador a trav??s de getPosition();
     * <p>
     *     Esta posici??n puede ser diferente a la posici??n anterior al inicio del arrastre porque el marcador aparece sobre el punto de contacto.
     * </p>
     *
     * @param marker
     */
    /*
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    /**
     * Se llama repetidamente mientras se arrastra un marcador.
     * Se puede acceder a la ubicaci??n del marcador a trav??s de getPosition().
     * @param marker
     */
    /*
    @Override
    public void onMarkerDrag(Marker marker) {

    }

    /**
     *Se llama cuando un marcador ha terminado de ser arrastrado, puede accederse a la ubicacion del marcador a trav??s de getPosition().
     * @param marker
     */
    /*
    @Override
    public void onMarkerDragEnd(Marker marker) {
        for (int id=0; id<adaptador.getItemCount(); id++){
            if (adaptador.lugarPosicion(id).getNombre()
                    .equals(marker.getTitle())){
                lugar = adaptador.lugarPosicion(id);
                lugar.setPosicion(new GeoPunto(marker.getPosition().longitude, marker.getPosition().latitude));
                usoLugar.guardar(adaptador.idPosicion(id), lugar);

            }
        }
    }
    */
}
