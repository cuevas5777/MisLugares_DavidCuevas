package com.davidcuevascano.mislugares.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.davidcuevascano.mislugares.R;
import com.davidcuevascano.mislugares.modelo.GeoPunto;
import com.davidcuevascano.mislugares.modelo.Lugar;
import com.davidcuevascano.mislugares.modelo.RepositorioLugares;
import com.davidcuevascano.mislugares.presentacion.Aplicacion;

/**
 * Clase para adaptar la información de los lugares para poder mostrarla en el RecycleView
 * @see androidx.recyclerview.widget.RecyclerView.Adapter
 */
public class AdaptadorLugares  extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> {

    protected View.OnClickListener onClickListener;

    protected RepositorioLugares lugares;

    /**
     * Recogemos la lista de lugares que pasamos como parámetro
     * @param lugares
     */
    public AdaptadorLugares(RepositorioLugares lugares) {
        this.lugares = lugares;
    }

    /**
     * Esta clase le va a dar el aspecto a nuestra lista.
     * <p>
     *      Internamente Android crea un ViewHolder para cada elemento de la lista, es decir, recorre la lista que hemos unido al adaptador.
     *      Accede a elementolista.xml e lo infla para mostrar por pantalla cada elemento de la lista.
     * </p>
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre,direccion;
        public ImageView foto;
        public RatingBar valoracion;
        public TextView distancia;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            direccion = itemView.findViewById(R.id.direccion);
            foto = itemView.findViewById(R.id.foto);
            valoracion= itemView.findViewById(R.id.valoracion);
            distancia=itemView.findViewById(R.id.distancia);
        }

        /**
         * El método personaliza rellena cada elemento de la lista
         * con los valores del lugar para el que se ha creado el ViewHolder.
         * <p>
         *     Cambiamos la foto con setImageResource.
         *     Le pasamos el icono que hemos introducido en la carpeta drawable anterioremente.
         * </p>
         * setScaleType
         * <p>
         *     Hace que la imagen se ajuste al tamaño que le hemos dado
         *     con el parámetro ImageView.ScaleType.FIT_END.
         * </p>
         * setRating
         * <p>
         *      Nos permite cambiar la valoración es un float entre 0 y el número máximo de estrella definido.
         * </p>
         *
         * @param lugar
         */
        public void personaliza(Lugar lugar) {
            nombre.setText(lugar.getNombre());
            direccion.setText(lugar.getDireccion());
            int id = R.drawable.otros;
            switch(lugar.getTipo()) {
                case RESTAURANTE:id = R.drawable.restaurante; break;
                case BAR:    id = R.drawable.bar;     break;
                case COPAS:   id = R.drawable.copas;    break;
                case ESPECTACULO:id = R.drawable.espectaculos; break;
                case HOTEL:   id = R.drawable.hotel;    break;
                case COMPRAS:  id = R.drawable.compras;   break;
                case EDUCACION: id = R.drawable.educacion;  break;
                case DEPORTE:  id = R.drawable.deporte;   break;
                case NATURALEZA: id = R.drawable.naturaleza; break;
                case GASOLINERA: id = R.drawable.gasolinera; break;  }
            foto.setImageResource(id);
            foto.setScaleType(ImageView.ScaleType.FIT_END);
            valoracion.setRating(lugar.getValoracion());

            GeoPunto pos=((Aplicacion) itemView.getContext().getApplicationContext())
                    .posicionActual;
            if (pos.equals(GeoPunto.SIN_POSICION) ||
                    lugar.getPosicion().equals(GeoPunto.SIN_POSICION)) {
                distancia.setText("... Km");
            } else {
                int d=(int) pos.distancia(lugar.getPosicion());
                if (d < 2000) distancia.setText(d + " m");
                else          distancia.setText(d / 1000 + " Km");
            }
        }
    }

    /**
     * Se ejecuta una vez por cada elemento de la lista que hemos unido a nuestro adaptador
     * <p>
     *     Es un metodo de ViewHolder que tenemos que sobreescribir.
     *     Se ejecuta una vez por cada elemento de la lista que hemos unido a nuestro adaptador
     * </p>
     * Usa la clase LayoutInflater para inflar con elementolista.xml.
     * Retorna un nuevo ViewHolder. Se ejecutará una vez por elemento de la lista.
     *
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elementolista, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Método de ViewHolder que sobreescribimos
     * <p>
     *     Por cada elemento de la lista se ejecuta una vez.
     *     Nos va a permitir personalizar cada elemento de la lista.
     *     Llamaremos al método personaliza que hemos creado para personalizarlo.
     * </p>
     *
     * @param holder
     * @param posicion
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Lugar lugar = lugares.elemento(posicion);
        holder.personaliza(lugar);
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos
     * @return numero de lugares
     */
    @Override public int getItemCount() {
        return lugares.tamanyo();
    }

    /**
     * Registra una devolución de llamada y se invoca cuando se haga clic en un elemento en este AdapterView.
     * @param onClickListener
     */
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener =  onClickListener;
    }


}

