package es.parroquiasanleandro.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.activitys.ActivitySeleccionarImagen;

public class ImagenServidorAdaptador extends RecyclerView.Adapter<ImagenServidorAdaptador.ViewHolder> {
    Context context;
    Activity activity;
    String idGrupo;
    List<String> imagenes;

    public ImagenServidorAdaptador(Context context, Activity activity, String idGrupo, List<String> imagenes) {
        this.context = context;
        this.activity = activity;
        this.idGrupo = idGrupo;
        this.imagenes = imagenes;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_imagen, parent, false);
        return new ImagenServidorAdaptador.ViewHolder(view);
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

        private final ImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);

        }

        public void asignarValores(String imagen) {
            Glide.with(context).load(Url.obtenerImagenAviso + idGrupo + "/img/" + imagen).into(imageView);
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("nombreImagen", imagen);
                activity.setResult(ActivitySeleccionarImagen.SELECION_IMAGEN_SERVIDOR, intent);
                activity.finish();
            });
        }
    }

}
