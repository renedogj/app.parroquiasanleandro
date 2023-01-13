package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Articulo;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.activitys.ActivityArticulo;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_articulo, parent, false);
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
        private RecyclerView rvImagenesArticulo;
        private TextView tvNombreArticulo;
        private TextView tvPrecio;

        public ViewHolder(View itemView) {
            super(itemView);

            cardArticulo = itemView.findViewById(R.id.cardArticulo);
            rvImagenesArticulo = itemView.findViewById(R.id.rvImagenesArticulo);
            tvNombreArticulo = itemView.findViewById(R.id.tvNombreArticulo);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }

        public void asignarValoresArticulo(Articulo articulo) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvImagenesArticulo.setLayoutManager(linearLayoutManager);

            ImagenMercadilloAdaptador imagenMercadilloAdaptador = new ImagenMercadilloAdaptador(context, articulo.id, articulo.imagenes);
            rvImagenesArticulo.setAdapter(imagenMercadilloAdaptador);

            tvNombreArticulo.setText(articulo.nombre);
            tvPrecio.setText(articulo.precio + "€");

            rvImagenesArticulo.setOnClickListener(v -> {
                Toast.makeText(context, articulo.nombre, Toast.LENGTH_SHORT).show();
            });

            cardArticulo.setOnClickListener(v -> {
                Intent intent = new Intent(context, ActivityArticulo.class);
                intent.putExtra("idArticulo", articulo.id);
                context.startActivity(intent);
            });
        }
    }
}
