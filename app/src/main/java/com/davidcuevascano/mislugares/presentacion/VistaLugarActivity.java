package com.davidcuevascano.mislugares.presentacion;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.davidcuevascano.mislugares.R;
import com.davidcuevascano.mislugares.adaptadores.AdaptadorLugaresBD;
import com.davidcuevascano.mislugares.casos_uso.CasoUsoAlmacenamiento;
import com.davidcuevascano.mislugares.casos_uso.CasosUsoLugar;
import com.davidcuevascano.mislugares.datos.LugaresBD;
import com.davidcuevascano.mislugares.datos.LugaresFirebase;
import com.davidcuevascano.mislugares.modelo.Lugar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Clase para controlar la actividad del formulario de vista_lugar, sus elementos
 * y el control de eventos. En el oncreate recogemos el id lugar correspondiente para apuntar desde
 * un cursor a la coleccion de datos desde el RecycleView.
 * @see androidx.appcompat.app.AppCompatActivity
 *
 */
public class VistaLugarActivity extends AppCompatActivity {
    private LugaresBD lugares;
    private AdaptadorLugaresBD adaptador;
    private CasosUsoLugar usoLugar;
    private int pos;
    private Lugar lugar;

    public final static int RESULTADO_GALERIA = 2;

    final static int RESULTADO_FOTO = 3;
    private static final int MY_READ_REQUEST_CODE = 1;
    private static final int MY_WRITE_REQUEST_CODE = 2;
    private ImageView foto;
    private CasoUsoAlmacenamiento usoAlmacenamiento;
    private Uri uriUltimaFoto;

    final static int RESULTADO_EDITAR = 1;

    public int _id;

    private Activity actividad;

    public final Calendar c = Calendar.getInstance();

    int mes = c.get(Calendar.MONTH);

    int dia = c.get(Calendar.DAY_OF_MONTH);

    int anio = c.get(Calendar.YEAR);

    int hora = c.get(Calendar.HOUR_OF_DAY);

    int minuto = c.get(Calendar.MINUTE);

    int segundos = c.get(Calendar.SECOND);

    ImageButton icono_hora, icono_fecha;

    TextView txtFecha;

    TextView txtHora;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String idFirebase;
    LugaresFirebase lugaresFirebase = new LugaresFirebase();

    /**
     * Inicializa los componentes de la actividad. El argumento Bundle
     * contiene el estado ya guardado de la actividad.
     * Si la actividad nunca ha existido, el valor del objeto Bundle es nulo.
     * <p>
     *      muestra la configuraci??n b??sica de la actividad, como declarar
     *      la interfaz de usuario (definida en un archivo XML de dise??o),
     *      definir las variables de miembro y configurar parte de la IU
     * </p>
     *
     * @param savedInstanceState objeto Bundle que contiene el estado de la actividad.
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        adaptador = ((Aplicacion) getApplication()).adaptador;

        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", -1) ;
        _id = extras.getInt("_id",-1);
        idFirebase = extras.getString("idFirebase");
        if (_id!=-1){
            lugar = lugares.elemento(_id);
        }
        else{
            lugar = adaptador.lugarPosicion(pos);
        }
        lugares = ((Aplicacion) getApplication()).lugares;
        lugares.actualiza(_id,lugar);
        usoLugar = new CasosUsoLugar(this, lugares, adaptador);
        lugar = adaptador.lugarPosicion (pos);
        foto = (ImageView) findViewById(R.id.foto);
        actualizaVistas();
        setSupportActionBar(toolbar);
        inicializarListener();
        usoAlmacenamiento = new CasoUsoAlmacenamiento(this, MY_WRITE_REQUEST_CODE);

        if (extras != null) {
            pos = extras.getInt("pos", 0);
        }
        else  {
            pos = 0;
        }
        _id = adaptador.idPosicion(pos);

        if (lugar.getTelefono() == 0) {
            findViewById(R.id.telefono).setVisibility(View.GONE);
        } else {
            findViewById(R.id.telefono).setVisibility(View.VISIBLE);
            TextView telefono = findViewById(R.id.telefono);
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_Loc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.mapa(pos);
            }
        });
        inicializarFirebase();

        lugaresFirebase.setTipo(lugar.getTipo());
        lugaresFirebase.setDireccion(lugar.getDireccion());
        lugaresFirebase.setTelefono(lugar.getTelefono());
        lugaresFirebase.setUrl(lugar.getUrl());
        lugaresFirebase.setComentario(lugar.getComentario());
        lugaresFirebase.setPosicion(lugar.getPosicion());
        lugaresFirebase.setValoracion(lugar.getValoracion());
        lugaresFirebase.setFecha(lugar.getFecha());

        databaseReference.child("Lugares").child(String.valueOf(idFirebase)).child(lugar.getNombre()).setValue(lugaresFirebase);
    }

    /**
     * M??todo implementado para gestionar el recurso de men?? (definido en XML)
     * hacia el Menu proporcionado en la devoluci??n de llamada.
     * <p>
     *      Cuando comienza la actividad, para mostrar los elementos de la barra de app.
     * </p>
     *
     * @param menu proporcionado en el XML para muestra los elementos de la barra.
     * @return boolean que devuelve true en el caso de que se haya podido cargar la barra correctamente.
     */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }

    /**
     * Gestionamos el MenuItem seleccionado por el usuario. Recogemos el id del menu (definido por el atributo android:id)
     * en el recurso del men?? para realizar la accion correspondiente.
     *
     * @param item ID ??nico del elemento de men??
     * @return boolean donde controlamos que se ha escogido una opci??n v??lida del men??.
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.accion_compartir:
                usoLugar.compartir(lugar);
                return true;

            case R.id.accion_llegar:
                usoLugar.verMapa(lugar);
                return true;

            case R.id.accion_editar:
                usoLugar.editar(pos, RESULTADO_EDITAR);
                return true;

            case R.id.accion_borrar:
                int _id = adaptador.idPosicion(pos);
                databaseReference.child("Lugares").child(idFirebase).child(lugar.getNombre()).removeValue();
                usoLugar.borrar(_id);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Actualizaci??n de los componentes de la aplicaci??n con las propiedades de la posici??n del lugar correspondiente.
     * Se a??ade un listener para controlar la modificaci??n del ratingBar de la valoraci??n del lugar proporcionada por el usuario
     */
    public void actualizaVistas() {
        TextView nombre = findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());
        ImageView logo_tipo = findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());
        TextView tipo = findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());
        TextView direccion = findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion());
        TextView telefono = findViewById(R.id.telefono);
        telefono.setText(Integer.toString(lugar.getTelefono()));
        TextView url = findViewById(R.id.url);
        url.setText(lugar.getUrl());
        TextView comentario = findViewById(R.id.comentario);
        comentario.setText(lugar.getComentario());
        TextView fecha = findViewById(R.id.fecha);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));
        TextView hora = findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));
        RatingBar valoracion = findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
        new RatingBar.OnRatingBarChangeListener() {
            @Override public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {
                lugar.setValoracion(valor);
                usoLugar.actualizaPosLugar(pos, lugar);
                pos = adaptador.posicionId(_id);
            }
        });
        usoLugar.visualizarFoto(lugar, foto);
    }


    /**
     * Inicializa cada uno de los listener correspondientes a los componentes de la actividad
     * Cada uno gestiona un evento en el caso de que el usuario haga click en ver el mapa, acceder a la
     * URL de la p??gina web, acceso a la galeria, a ver la foto, llamar por tel??fono y eliminar la foto
     * gestionado en los casos de uso de la clase CasosUsoLugar.
     */
    public void inicializarListener () {
        LinearLayout lmap = findViewById(R.id.LinearMapa);
        LinearLayout lweb = findViewById(R.id.LinearWeb);
        LinearLayout ltelefono = findViewById(R.id.LinearTelefono);
        ImageView galeria = (ImageView) findViewById(R.id.galeria);
        final ImageView camara = (ImageView) findViewById(R.id.camara);
        final ImageView eliminarFoto = (ImageView) findViewById(R.id.eliminarfoto);

        /**
         * Acceso al mapa mediante el objeto View, que representa el manejo de eventos de la interfaz de usuario.
         *
         * @param view lugar que accedemos desde la ventana principal recogido en el oncreate. Mediante ??l accedemos
         *             a la latitud y longitud triangular la posici??n
         */
        lmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.verMapa(lugar);
            }
        });

        /**
         * Accedemos a la p??gina web del lugar y abrimos su URL en el navegador
         *
         * @param view lugar que accedemos desde la ventana principal recogido en el oncreate. Mediante ??l accedemos
         *             a la URL del sitio.
         */
        lweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.verPgWeb(lugar);
            }
        });

        /**
         * Recogemos el tel??fono de la base de datos y abrimos una ventana para poder llamarlo
         *
         * @param view lugar que accedemos desde la ventana principal recogido en el oncreate. Mediante ??l accedemos
         *             al tel??fono del lugar con el objeto View.
         */
        ltelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.llamarTelefono(lugar);
            }
        });

        /**
         * Accedemos a la galeria del dispositivo con los permisos necesarios y actualizamos la foto del sitio.
         *
         * @param view lugar que accedemos desde la ventana principal recogido en el oncreate. Mediante ??l accedemos
         *             y modificamos la foto.
         */
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.galeria(foto);

            }
        });

        /**
         * Solicitados los permisos de acceso a c??mara. Mostramos la c??mara de fotos del m??vil para hacer una foto en tiempo real
         * y almacenarla
         *
         * @param view lugar que accedemos desde la ventana principal recogido en el oncreate. Mediante ??l accedemos
         *             al registro de la foto.
         */
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usoAlmacenamiento.hayPermisoAlmacenamientoEscritura()) {
                uriUltimaFoto = usoLugar.tomarFoto(RESULTADO_FOTO);
                }else {
                    Toast.makeText(getBaseContext(), "No hay permisos de almacenamiento, no se puede tomar la foto", Toast.LENGTH_LONG).show();
                    usoAlmacenamiento = new CasoUsoAlmacenamiento(VistaLugarActivity.this, MY_WRITE_REQUEST_CODE);
                }
            }
        });

        /**
         * Requiere permisos de lectura y escritura para poder eliminar la foto relaccionada al sitio.
         *
         * @param view lugar que accedemos desde la ventana principal recogido en el oncreate. Mediante ??l accedemos
         *             y eliminamos la foto.
         */
        eliminarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usoLugar.ponerFoto(pos, "", foto);
            }
        });



        icono_fecha = findViewById(R.id.icono_fecha);
        icono_hora = findViewById(R.id.icono_hora);



        icono_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lugar lugar = adaptador.lugarPosicion(pos);

                pos = adaptador.posicionId(_id);
                obtenerFecha();
            }
        });

        icono_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lugar lugar = adaptador.lugarPosicion(pos);

                pos = adaptador.posicionId(_id);
                obtenerHora();
            }
        });

        txtFecha = findViewById(R.id.fecha);
        txtHora = findViewById(R.id.hora);

    }

    /**
     * Resultado espec??fico cuando el usuario termina con la actividad subsiguiente y regresa a la actividad
     *
     * @param requestCode c??digo de petici??n especificada por la segunda actividad
     *                    (se trata de RESULTADO_EDITAR si el usuario selecciona la edici??n del lugar,
     *                    RESULTADO_GALERIAS si el usuario importa nuevas fotos de su galeria y RESULTADO_FOTO
     *                    si el usuario realiza una foto con los permisos de la app)
     * @param resultCode  c??digo de resultado especificado por la segunda actividad
     *                    (se trata de RESULT_OK si se realiz?? la operaci??n de manera correcta
     *                    o de RESULT_CANCELED si se retir?? el usuario o fall?? la operaci??n por alg??n motivo)
     * @param data        Intent que proporciona los datos del resultado
     */
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULTADO_EDITAR) {
            lugar = lugares.elemento(_id);
            pos = adaptador.posicionId(_id);
            actualizaVistas();
        }else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                usoLugar.ponerFoto(pos, data.getDataString(), foto);
            } else {
                Toast.makeText(this, "Foto no cargada",Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK && uriUltimaFoto!=null) {
                lugar.setFoto(uriUltimaFoto.toString());
                usoLugar.ponerFoto(pos, lugar.getFoto(), foto);
            } else {
                Toast.makeText(this, "Error en captura", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Obtenemos la fecha con un cuadro de di??logo es un tipo de ventana emergente
     * que solicita al usuario de la aplicaci??n.
     * <p>
     *      Permite modificar el a??o, el mes y el d??a.
     *      Mediante DatePickerDialog seleccionamos la fecha en milisegundos con un objeto de tipo Long.
     *      Finalmente, mostramos el di??logo llamando al m??todo show(). Este m??todo utiliza dos par??metros:
     *      el manejador de fragments y una etiqueta que identificar?? el cuadro de di??logo.
     * </p>
     */
    public void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();

            cal.setTimeInMillis(lugar.getFecha());

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            lugar.setFecha(cal.getTimeInMillis());

            usoLugar.actualizaPosLugar(pos, lugar);
            txtFecha.setText(DateFormat.getDateInstance()
                    .format(new Date(cal.getTimeInMillis())));
            anio = year;
            mes = month;
            dia = dayOfMonth;
            }
        },anio, mes, dia);

        recogerFecha.getDatePicker();
        recogerFecha.show();
    }

    /**
     * Obtenemos la hora y los minutos con un cuadro de di??logo es un tipo de ventana emergente
     * que solicita al usuario de la aplicaci??n.
     * <p>
     *      Permite modificar la hora y los minutos.
     *      Mediante TimePickerDialog seleccionamos la hora en milisegundos con un objeto de tipo Long.
     *      Finalmente, mostramos el di??logo llamando al m??todo show(). Este m??todo utiliza dos par??metros:
     *      el manejador de fragments y una etiqueta que identificar?? el cuadro de di??logo.
     * </p>
     */
    public void obtenerHora(){
        final TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendario = Calendar.getInstance();
            calendario.setTimeInMillis(lugar.getFecha());
            calendario.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendario.set(Calendar.MINUTE, minute);
            lugar.setFecha(calendario.getTimeInMillis());
            usoLugar.actualizaPosLugar(pos, lugar);

            txtHora.setText(DateFormat.getTimeInstance().format(
                    new Date(calendario.getTimeInMillis())));
            hora = hourOfDay;
            minuto = minute;

            }

        }, hora, minuto, true);
        recogerHora.show();
    }

    public void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

}
