package com.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.parroquiasanleandro.Categoria;
import com.parroquiasanleandro.utils.ItemViewModel;
import com.parroquiasanleandro.R;
import com.parroquiasanleandro.Usuario;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAdaptador extends RecyclerView.Adapter<CategoriaAdaptador.ViewHolder> {

    private final Context context;
    private final List<Categoria> categorias;
    private final Usuario usuario;
    public String categoriaPadre;
    public List<Categoria> categoriasNivel = new ArrayList<>();
    public RecyclerView rvCategorias;

    private final ItemViewModel vmIds;

    public CategoriaAdaptador(Context context, List<Categoria> categorias, @NotNull String categoriaPadre, RecyclerView rvCategorias,ItemViewModel vmIds) {
        this.context = context;
        this.categorias = categorias;
        this.categoriaPadre = categoriaPadre;
        this.rvCategorias = rvCategorias;
        usuario = Usuario.recuperarUsuarioLocal(context);
        obtenerCategoriasNivel();
        this.vmIds = vmIds;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categoria_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        categoriasNivel.get(position).setPosicion(position);
        Categoria categoria = categoriasNivel.get(position);

        holder.asignarValores(categoria);
    }

    @Override
    public int getItemCount() {
        return categoriasNivel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout linearLayoutCategoria;
        private final TextView tvNombreCategoria;
        private final CardView cardCategoriaBoton;
        private final CardView cardImgCategoria;
        private final TextView tvBotonSeguir;
        private final ImageView imgCategoria;

        public Categoria categoria;
        public boolean categoriaGuardada;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayoutCategoria = itemView.findViewById(R.id.linearLayoutCategoria);
            tvNombreCategoria = itemView.findViewById(R.id.tvNombreCategoria);
            cardCategoriaBoton = itemView.findViewById(R.id.cardCategoriaBoton);
            tvBotonSeguir = itemView.findViewById(R.id.tvBotonSeguir);
            cardImgCategoria = itemView.findViewById(R.id.cardImgCategoria);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
        }

        public void asignarValores(Categoria categoria) {
            this.categoria = categoria;

            tvNombreCategoria.setText(categoria.nombre);
            linearLayoutCategoria.setBackgroundColor(Color.parseColor(categoria.color));
            cardImgCategoria.setBackgroundColor(Color.parseColor(categoria.color));
            imgCategoria.setBackgroundColor(Color.parseColor(categoria.color));

            FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child(categoria.key).child("imagenPredeterminada.jpg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).into(imgCategoria);
                        }
                    });

            checkCategoria(comprobarSiCategoriaGuardada(categoria));

            cardCategoriaBoton.setOnClickListener(v -> {
                if (categoriaGuardada){
                    categoria.dessuscribirCategoria(context, usuario.uid);
                    categoriaGuardada = false;
                    usuario.removeCategoria(categoria);
                }else{
                    categoria.suscribirCategoria(context, usuario.uid);
                    categoriaGuardada = true;
                    usuario.addCategoria(categoria);
                    chekCategoriasPadre(categoria);
                }
                checkCategoria(categoriaGuardada);
            });

            linearLayoutCategoria.setOnClickListener(v -> {
                if(existenSubniveles(categoria)) {
                    vmIds.setCategoriaActual(categoria.key);
                    vmIds.addIdCategoria();
                    CategoriaAdaptador categoriaAdaptador = new CategoriaAdaptador(context, categorias, categoria.key, rvCategorias,vmIds);
                    rvCategorias.setAdapter(categoriaAdaptador);
                }
            });
        }

        public boolean comprobarSiCategoriaGuardada(Categoria categoria){
            for (Categoria categoriaAux : usuario.getCategorias()){
                if(categoriaAux.key.equals(categoria.key) && categoriaAux.nombre.equals(categoria.nombre)){
                    return true;
                }
            }
            return false;
        }

        public void checkCategoria(Boolean chek){
            categoriaGuardada = chek;
            if(categoriaGuardada){
                cardCategoriaBoton.setCardBackgroundColor(Color.parseColor("#aa00dd"));
                tvBotonSeguir.setTextColor(Color.parseColor("#FFFDF1F1"));
            }else{
                cardCategoriaBoton.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                tvBotonSeguir.setTextColor(Color.parseColor("#FF5A5046"));
            }
        }
    }


    public void chekCategoriasPadre(Categoria categoria) {
        List<String> categoriasKey = new ArrayList<>();
        for (int i = 1; i <= categoria.key.length(); i++) {
            if (!categoriasKey.contains(categoria.key.substring(0, i))) {
                categoriasKey.add(categoria.key.substring(0, i));
            }
        }
        for(Categoria categoria1: categorias){
            if(categoriasKey.contains(categoria1.key)){
                categoria1.suscribirCategoria(context, usuario.uid);
                usuario.addCategoria(categoria1);
            }
        }
    }

    public void obtenerCategoriasNivel(){
        for(Categoria categoria: categorias){
            if(categoria.key.length() == categoriaPadre.length()+1){
                if(categoria.key.startsWith(categoriaPadre)) {
                    categoriasNivel.add(categoria);
                }
            }
        }
    }

    public boolean existenSubniveles(Categoria categoriaPadre){
        for(Categoria categoria: categorias){
            if(categoria.key.length() == categoriaPadre.key.length()+1){
                if(categoria.key.startsWith(categoriaPadre.key)) {
                    return true;
                }
            }
        }
        return false;
    }
}