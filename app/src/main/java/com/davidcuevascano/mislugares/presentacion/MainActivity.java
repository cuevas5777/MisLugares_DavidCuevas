package com.davidcuevascano.mislugares.presentacion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.davidcuevascano.mislugares.R;
import com.davidcuevascano.mislugares.adaptadores.AdaptadorLugaresBD;
import com.davidcuevascano.mislugares.casos_uso.CasoUsoAlmacenamiento;
import com.davidcuevascano.mislugares.casos_uso.CasoUsoLocalizacion;
import com.davidcuevascano.mislugares.casos_uso.CasosUsoLugar;
import com.davidcuevascano.mislugares.datos.LugaresBD;
import com.davidcuevascano.mislugares.mapas.MapsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Clase MainActivity que es la encargada de mostrar la pantalla principal
 */
public class MainActivity extends AppCompatActivity {
    private LugaresBD lugares;
    private AdaptadorLugaresBD adaptador;
    private static final int MY_WRITE_REQUEST_CODE = 2;
    private CasosUsoLugar usoLugar;
    private CasoUsoAlmacenamiento usoAlmacenamiento;
    private RecyclerView recyclerView;

    static final int RESULTADO_PREFERENCIAS = 0;
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 1;
    private CasoUsoLocalizacion usoLocalizacion;
    private int _id = -1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String idFirebase;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private int CONEXIONGOOGLECORRECTA = 1;


    /**
     * M??todo para inicializar el layout, los listener y llenar las dem??s clases
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        Toast.makeText(MainActivity.this,"Pulsa el boton con el +1 si quieres introducir un lugar!",Toast.LENGTH_LONG).show();

        adaptador = ((Aplicacion) getApplication()).adaptador;
        lugares = ((Aplicacion) getApplication()).lugares;

        usoLugar = new CasosUsoLugar(this, lugares, adaptador);

        //permisos

        usoAlmacenamiento = new CasoUsoAlmacenamiento(MainActivity.this, MY_WRITE_REQUEST_CODE);
        usoAlmacenamiento.solicitarPermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "Necesita permisos de almacenamiento para a??adir fotograf??as",MY_WRITE_REQUEST_CODE);
        usoLocalizacion = new CasoUsoLocalizacion(this,
                SOLICITUD_PERMISO_LOCALIZACION);
        setSupportActionBar(toolbar);

        inicializarReciclerView();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken("991499786355-k71pmm1giih4bjru0hmbv67rtl1rnrbv.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        IniciarSesion();


        inicializarFirebase();



        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int pos =(Integer)(v.getTag());
                usoLugar.mostrar(pos, idFirebase);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /**
         * M??todo para inicializar los listener, en este el del floating button para que cuando lo pulsemos llame al m??todo de crear un nuevo Lugar
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Agregar un lugar en el mapa: ",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, MapsActivity.class);

                startActivity(i);

            }
        });

        /**
         * Nos permite identificar que gesto hemos realizado en la pantalla t??ctil.
         * <p>
         *     Sobre el vamos a sobreescribir el m??todo onSingleTapUp y a devolver true.
         *     Lo que conseguimos con esto es que el Listener s??lo reaccione, cuando hagamos un tapup.
         *     Hacemos click en la pantalla y levantamos el dedo.
         * </p>
         */
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });

        /**
         * Permite que la aplicaci??n intercepte eventos t??ctiles en progreso en el nivel de la jerarqu??a de vistas de RecyclerView
         * antes de que esos eventos t??ctiles sean considerados para el propio comportamiento de desplazamiento de RecyclerView
         * <p>
         *     Puede ser ??til para aplicaciones que desean implementar diversas formas de manipulaci??n gestual de vistas de elementos dentro de RecyclerView.
         *     OnItemTouchListeners puede interceptar una interacci??n t??ctil que ya est?? en progreso
         * </p>
         */
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }

            /**
             * Se usa para controlar que hacemos click, Tapup sobre la lista
             * <p>
             *     con el, obtenemos en child el item que queremos capturar a partir de la posici??n X e Y del motionEvent.
             *     Esta clase se utiliza para informar de eventos de movimiento (mouse, bol??grafo, dedo...).
             *     Los eventos de movimiento pueden contener movimientos absolutos o relativos y otros datos, seg??n el tipo de dispositivo.
             * </p>
             * @param recyclerView
             * @param motionEvent
             * @return verdadero o falso
             */
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                try {

                    /**
                     * Encuentra la primera vista, ??tem de la lista justo debajo del punto dado.
                     * Cada Item de la lista se muestra como un ViewHolder, es una vista en s?? (elementolista.xml)
                     */
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                        int pos = recyclerView.getChildAdapterPosition(child);
                        usoLugar.mostrar(pos, idFirebase);
                        Toast.makeText(MainActivity.this,"Seleccionado el lugar numero: "+ pos ,Toast.LENGTH_SHORT).show();

                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });


    }

    /**
     * M??todo para crear el men?? superior con el men?? establecido en el xml
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    /**
     * Gestionamos el MenuItem seleccionado por el usuario. Recogemos el id del menu (definido por el atributo android:id)
     *  en el recurso del men?? para realizar la accion correspondiente.
     *
     * @param item ID ??nico del elemento de men??
     * @return boolean donde controlamos que se ha escogido una opci??n v??lida del men??.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            LanzarPreferencias();
            return true;
        }
        /*if (id == R.id.menu_buscar) {
            lanzarVistaLugar();
            return true;
        }*/

        if (id == R.id.menu_acercaDe){
            LanzarAcercaDe();
            return  true;
        }
        if (id==R.id.menu_mapa) {
            LanzarMapa();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * M??todo para lanzar el Activity de Preferencias
     *
     */
    public void LanzarPreferencias(){
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivityForResult(i, RESULTADO_PREFERENCIAS);
    }

    /**
     * M??todo para lanzar la clase acerca de.
     */
    public void LanzarAcercaDe(){
        Intent acercaDe = new Intent(MainActivity.this, AcercaDeActivity.class);
        startActivity(acercaDe);
    }

    /**
     * M??todo para abrir un Di??logo para escribir el id del lugar que quieres visualizar
     *
     */
    /*public void lanzarVistaLugar(){
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
            .setTitle("Selecci??n de lugar")
            .setMessage("indica su id:")
            .setView(entrada)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int id = Integer.parseInt (entrada.getText().toString());
                    usoLugar.mostrar(id, idFirebase);
                }})
            .setNegativeButton("Cancelar", null)
            .show();
    }*/

    /**
     * M??todo para lanzar la clase que visualiza el mapa.
     */
    public void LanzarMapa(){
        Intent mapa = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(mapa);
    }

    /**
     * M??todo para inicializar la ReciclerView con su respectiva layout y adaptador
     * Ajustamos el tama??o a fijo
     * recyclerView.setHasFixedSize(true);
     * <p>
     *     Ponemos de LayoutManager un Linear
     *     recyclerView.setLayoutManager(new LinearLayoutManager(this));
     *     Y cargamos el adaptador que vamos a definir.
     *     recyclerView.setAdapter(adaptador);
     * </p>
     */
    public void inicializarReciclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);
    }


    /**
     * M??todo para controlar los permisos necesarios
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override public void onRequestPermissionsResult(int requestCode,
                                                     String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            usoLocalizacion.permisoConcedido();

    }

    /**
     * M??todo que activa la localizaci??n cuando vuelve a estar en primer plano
     */
    @Override protected void onResume() {
        super.onResume();
        usoLocalizacion.activar();
    }


    /**
     * M??todo que desactiva la localizaci??n cuando se pausa la app
     */
   @Override protected void onPause() {
        super.onPause();
        usoLocalizacion.desactivar();
    }



    /**
     * Resultado espec??fico cuando el usuario termina con la actividad subsiguiente y regresa a la actividad
     *
     * @param requestCode <p>
     *                          codigo de petici??n especificada por la segunda actividad
     *                          (se trata de RESULTADO_EDITAR si el usuario selecciona la edici??n del lugar,
 *                              RESULTADO_GALERIAS si el usuario importa nuevas fotos de su galeria y RESULTADO_FOTO
     *                          si el usuario realiza una foto con los permisos de la app)
     *                    </p>
     *
     * @param resultCode  <p>
     *                          codigo de resultado especificado por la segunda actividad
     *                          (se trata de RESULT_OK si se realiz?? la operaci??n de manera correcta
     *                           o de RESULT_CANCELED si se retir?? el usuario o fall?? la operaci??n por alg??n motivo)
     *                    </p>
     * @param data        <p>
     *                          Intent que proporciona los datos del resultado
 *                        </p>
     */
    @Override protected void onActivityResult(int requestCode, int resultCode,
                                              Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULTADO_PREFERENCIAS) {
            adaptador.setCursor(lugares.extraeCursor());
            adaptador.notifyDataSetChanged();
        }
        if(requestCode == CONEXIONGOOGLECORRECTA){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    public void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }


    private void IniciarSesion(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, CONEXIONGOOGLECORRECTA);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acceso = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Inicio de sesion completado",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acceso);
        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this,"Inicio de sesion fallido",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount googleSignInAccount) {
        //check if the account is null
        if (googleSignInAccount != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser usuario = mAuth.getCurrentUser();
                        obtenerIdFirebase(usuario);
                    } else {
                        Toast.makeText(MainActivity.this, "Fallo de conexion", Toast.LENGTH_SHORT).show();
                        obtenerIdFirebase(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "Acceso fallido", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerIdFirebase(FirebaseUser firebaseUser){
        GoogleSignInAccount cuentaGoogle = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(cuentaGoogle !=  null){
            String idGoogle = cuentaGoogle.getId();
            idFirebase = idGoogle;
        }
    }
}