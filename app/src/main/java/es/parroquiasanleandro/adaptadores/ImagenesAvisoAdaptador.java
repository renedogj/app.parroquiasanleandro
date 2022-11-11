package es.parroquiasanleandro.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.R;

public class ImagenesAvisoAdaptador extends RecyclerView.Adapter<ImagenesAvisoAdaptador.ViewHolder> {

    private static final int SELECION_IMAGEN = 0;

    private Activity activity;
    Context context;
    List<StorageReference> imagenes;

    public ImagenesAvisoAdaptador(Context context, Activity activity, List<StorageReference> imagenes) {
        this.activity = activity;
        this.context = context;
        this.imagenes = imagenes;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.imagen_item, parent, false);
        return new ImagenesAvisoAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        StorageReference imagen = imagenes.get(position);
        holder.asignarValores(activity,imagen);
    }

    @Override
    public int getItemCount() {
        return imagenes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }

        public void asignarValores(Activity activity, StorageReference imagen) {
            imagen.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    Glide.with(context)
                            .load(task.getResult().toString())
                            .into(imageView);
                    Intent intent = new Intent();
                    intent.setData(task.getResult());
                    intent.putExtra("NombreImagen",imagen.getName());
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.setResult(SELECION_IMAGEN,intent);
                            activity.finish();
                        }
                    });
                }
            });

        }
    }
}
