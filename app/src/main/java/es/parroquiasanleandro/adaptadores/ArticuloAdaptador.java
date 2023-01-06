package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Articulo;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;

public class ArticuloAdaptador extends RecyclerView.Adapter<ArticuloAdaptador.ViewHolder> {
    private Context context;
    private List<Articulo> articulos;

    public ArticuloAdaptador(Context context, List<Articulo> articulos) {
        this.context = context;
        this.articulos = articulos;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.articulo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Articulo articulo = articulos.get(position);
        holder.asignarValoresArticulo(articulo);
    }

    @Override
    public int getItemCount() {
        return articulos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardArticulo;
        private ImageView ivImagenArticulo;
        private TextView tvNombreArticulo;
        private TextView tvPrecio;
        private TextView tvFlechaIzquierda;
        private TextView tvFlechaDerecha;
        private int indexImagenesArticulo = 0;

        public ViewHolder(View itemView) {
            super(itemView);

            cardArticulo = itemView.findViewById(R.id.cardArticulo);
            ivImagenArticulo = itemView.findViewById(R.id.ivImagenArticulo);
            tvNombreArticulo = itemView.findViewById(R.id.tvNombreArticulo);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvFlechaIzquierda = itemView.findViewById(R.id.tvFlechaIzquierda);
            tvFlechaDerecha = itemView.findViewById(R.id.tvFlechaDerecha);
        }

        public void asignarValoresArticulo(Articulo articulo) {
            ivImagenArticulo.setMinimumHeight(Math.round(ivImagenArticulo.getWidth()*1.5f));

            if(articulo.imagenes.size() > 1){
                tvFlechaIzquierda.setVisibility(View.VISIBLE);
                tvFlechaDerecha.setVisibility(View.VISIBLE);
                tvFlechaIzquierda.setOnClickListener(view -> {
                    indexImagenesArticulo -= 1;
                    if(indexImagenesArticulo < 0){
                        indexImagenesArticulo = articulo.imagenes.size()-1;
                    }
                    Glide.with(context).load(Url.urlImagenes + articulo.imagenes.get(indexImagenesArticulo)).into(ivImagenArticulo);

                });
                tvFlechaDerecha.setOnClickListener(view -> {
                    indexImagenesArticulo += 1;
                    if(indexImagenesArticulo >= articulo.imagenes.size()){
                        indexImagenesArticulo = 0;
                    }
                    Glide.with(context).load(Url.urlImagenes + articulo.imagenes.get(indexImagenesArticulo)).into(ivImagenArticulo);
                });
            }

            if(articulo.imagenes.get(0) != null){
                Glide.with(context).load(Url.urlImagenes + articulo.imagenes.get(0)).into(ivImagenArticulo);
            }
            tvNombreArticulo.setText(articulo.nombre);
            tvPrecio.setText(articulo.precio + "€");
            ivImagenArticulo.setContentDescription(articulo.nombre);

            cardArticulo.setOnClickListener(v -> {
                Toast.makeText(context,articulo.nombre,Toast.LENGTH_SHORT).show();
            });
        }
    }
}
