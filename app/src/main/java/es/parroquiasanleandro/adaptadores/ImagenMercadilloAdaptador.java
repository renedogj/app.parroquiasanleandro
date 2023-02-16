package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.activitys.ActivityArticulo;

public class ImagenMercadilloAdaptador extends RecyclerView.Adapter<ImagenMercadilloAdaptador.ViewHolder> {
    Context context;
    String idArticulo;
    List<String> imagenes;
    boolean isListadoArticulos = true;

    public ImagenMercadilloAdaptador(Context context, String idArticulo, List<String> imagenes) {
        this.context = context;
        this.idArticulo = idArticulo;
        this.imagenes = imagenes;
    }

    public ImagenMercadilloAdaptador(Context context, String idArticulo, List<String> imagenes, boolean isListadoArticulos) {
        this.context = context;
        this.idArticulo = idArticulo;
        this.imagenes = imagenes;
        this.isListadoArticulos = isListadoArticulos;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_imagen_articulo, parent, false);
        return new ImagenMercadilloAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String imagen = imagenes.get(position);
        holder.asignarValores(imagen);
    }

    @Override
    public int getItemCount() {
        return imagenes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private CardView cardImagenArticulo;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            cardImagenArticulo = itemView.findViewById(R.id.cardImagenArticulo);

            if(!isListadoArticulos){
                cardImagenArticulo.setRadius(50);
            }
        }

        public void asignarValores(String imagen) {
            Glide.with(context).load(Url.urlImagenesMercardillo + imagen).into(imageView);

            if(isListadoArticulos){
                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ActivityArticulo.class);
                    intent.putExtra("idArticulo", idArticulo);
                    context.startActivity(intent);
                });
            }
        }
    }
}
