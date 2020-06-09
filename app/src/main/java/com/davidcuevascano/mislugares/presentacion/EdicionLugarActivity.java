package com.davidcuevascano.mislugares.presentacion;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.davidcuevascano.mislugares.R;
import com.davidcuevascano.mislugares.adaptadores.AdaptadorLugaresBD;
import com.davidcuevascano.mislugares.casos_uso.CasosUsoLugar;
import com.davidcuevascano.mislugares.datos.LugaresBD;
import com.davidcuevascano.mislugares.datos.LugaresFirebase;
import com.davidcuevascano.mislugares.mapas.MapsActivity;
import com.davidcuevascano.mislugares.modelo.Lugar;
import com.davidcuevascano.mislugares.modelo.TipoLugar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

/**
 * Clase para controlar la actividad del formulario de edicion_lugar, sus elementos
 * y el control de eventos. En el oncreate recogemos el id lugar correspondiente para apuntar desde
 * un cursor a la coleccion de datos desde el RecycleView. Una vez relaccionados los datos
 * realizamos las actualizaciones correspondientes. Este formulario tiene doble funcionalidad
 * ya que lo utilizamos tanto para edición del lugar como para crear uno nuevo. Controlamos
 * esta posibilidad mediante el id -1 que nos indica que no hemos seleccionado un cursor
 * desde la actividad principal sino que hemos seleccionado la opción de crear uno nuevo
 * @see androidx.appcompat.app.AppCompatActivity
 *
 */
public class EdicionLugarActivity extends AppCompatActivity {
    private LugaresBD lugares;
    private AdaptadorLugaresBD adaptador;
    private CasosUsoLugar usoLugar;
    private int pos;
    private Lugar lugar;
    private EditText nombre, direccion, telefono, url, comentario;
    private Spinner tipo;
    private int _id;
    private MapsActivity mapsActivity;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private UUID idFirebase;


    /**
     * Inicializa los componentes de la actividad. El argumento Bundle
     * contiene el estado ya guardado de la actividad.
     * Si la actividad nunca ha existido, el valor del objeto Bundle es nulo.
     * <p>
     *      Muestra la configuración básica de la actividad, como declarar
     *      la interfaz de usuario (definida en un archivo XML de diseño),
     *      definir las variables de miembro y configurar parte de la IU
     * </p>
     *
     * @param savedInstanceState objeto Bundle que contiene el estado de la actividad.
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edicion_lugar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adaptador = ((Aplicacion) getApplication()).adaptador;
        lugares = ((Aplicacion) getApplication()).lugares;
        usoLugar = new CasosUsoLugar(this, lugares, adaptador);

        mapsActivity = new MapsActivity();
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", -1);
        _id = extras.getInt("_id", -1);

        if (_id!=-1) lugar = lugares.elemento(_id);
        else         lugar = adaptador.lugarPosicion (pos);

        actualizaVistas();
        inicializarFirebase();

    }

    /**
     * Actualización de los componentes de la aplicación con las propiedades de la posición del lugar correspondiente.
     */
    public void actualizaVistas() {
        nombre = findViewById(R.id.nombree);
        nombre.setText(lugar.getNombre());

        direccion = findViewById(R.id.direccione);
        direccion.setText(lugar.getDireccion());
        telefono = findViewById(R.id.telefonoe);
        telefono.setText(Integer.toString(lugar.getTelefono()));
        url = findViewById(R.id.urle);
        url.setText(lugar.getUrl());
        comentario = findViewById(R.id.comentarioe);
        comentario.setText(lugar.getComentario());

        tipo = findViewById(R.id.tipoSpinner);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(adaptador);
        tipo.setSelection(lugar.getTipo().ordinal());
    }

    /**
     * Método implementado para gestionar el recurso de menú (definido en XML)
     * hacia el Menu proporcionado en la devolución de llamada.
     * <p>
     *      Cuando comienza la actividad, para mostrar los elementos de la barra de app.
     * </p>
     *
     * @param menu proporcionado en el XML para muestra los elementos de la barra.
     * @return boolean que devuelve true en el caso de que se haya podido cargar la barra correctamente.
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.edicion_lugar, menu);
        return true;
    }

    /**
     * Gestionamos el MenuItem seleccionado por el usuario. Recogemos el id del menu (definido por el atributo android:id)
     * en el recurso del menú para realizar la accion correspondiente.
     *
     * @param item ID único del elemento de menú
     * @return boolean donde controlamos que se ha escogido una opción válida del menú.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancelar:
                if (_id!=-1) lugares.borrar(_id);
                finish();
                return true;
            case R.id.guardar:
                if(nombre.getText().toString().matches("")){
                    new AlertDialog.Builder(this)
                            .setTitle("ERROR AL GUARDAR")
                            .setMessage("Necesita introducir nombre del lugar para poder guardar")
                            .setCancelable(false)
                            .setNegativeButton("Confirmar", null)
                            .show();
                    if (_id!=-1) lugares.borrar(_id);
                }else{
                    lugar.setNombre(nombre.getText().toString());
                    lugar.setTipo(TipoLugar.values()[tipo.getSelectedItemPosition()]);
                    lugar.setDireccion(direccion.getText().toString());
                    lugar.setTelefono(Integer.parseInt(telefono.getText().toString()));
                    lugar.setUrl(url.getText().toString());
                    lugar.setComentario(comentario.getText().toString());
                    if (_id==-1) _id = adaptador.idPosicion(pos);
                    usoLugar.guardar(_id, lugar);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }


}


