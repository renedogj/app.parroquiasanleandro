package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Categoria;
import es.parroquiasanleandro.R;

public class CategoriaAdaptador extends RecyclerView.Adapter<CategoriaAdaptador.ViewHolder> {
    private Context context;
    private List<Categoria> categorias;

    public CategoriaAdaptador(Context context, List<Categoria> categorias) {
        this.context = context;
        this.categorias = categorias;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria_articulo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.asignarValoresArticulo(categoria);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardCategoria;
        private LinearLayout lnlytCategoria;
        private TextView tvNombreCategoria;

        public ViewHolder(View itemView) {
            super(itemView);

            cardCategoria = itemView.findViewById(R.id.cardCategoria);
            lnlytCategoria = itemView.findViewById(R.id.lnlytCategoria);
            tvNombreCategoria = itemView.findViewById(R.id.tvNombreCategoria);
        }

        public void asignarValoresArticulo(Categoria categoria) {
            tvNombreCategoria.setText(categoria.getNombre());

            /*lnlytCategoria.setOnClickListener(v -> {
                Toast.makeText(context, categoria.getNombre(), Toast.LENGTH_SHORT).show();
            });*/

            cardCategoria.setOnClickListener(v -> {
                //Intent intent = new Intent(context, ActivityArticulo.class);
                //intent.putExtra("idArticulo", categoria.id + "");
                //context.startActivity(intent);
            });
        }
    }
}
