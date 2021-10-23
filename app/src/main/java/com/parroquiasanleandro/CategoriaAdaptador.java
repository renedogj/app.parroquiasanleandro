package com.parroquiasanleandro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAdaptador extends RecyclerView.Adapter<CategoriaAdaptador.ViewHolder> {

    private Context context;
    private List<Categoria> categorias;
    private Usuario usuario;
    private List<CategoriaAdaptador.ViewHolder> viewHolders = new ArrayList<>();

    public CategoriaAdaptador(Context context, List<Categoria> categorias, Usuario usuario) {
        this.context = context;
        this.categorias = categorias;
        this.usuario = usuario;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categoria_item, parent, false);
        CategoriaAdaptador.ViewHolder viewHolder = new CategoriaAdaptador.ViewHolder(view);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        categorias.get(position).posicion = position;
        Categoria categoria = categorias.get(position);
        holder.asignarValoresCategoria(categoria);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private SwitchCompat switchCategoria;

        public ViewHolder(View itemView) {
            super(itemView);
            switchCategoria = itemView.findViewById(R.id.switchCategoria);
        }

        public void asignarValoresCategoria(Categoria categoria) {
            switchCategoria.setText(categoria.nombre);

            switchCategoria.setChecked(comprobarSiCategoriaGuardada(categoria));
            /*if (categoria.key.equals("0")){
                switchCategoria.setClickable(false);
                switchCategoria.setChecked(true);
                categoria.guardarCategoria(context, usuario.uid);
            }*/
            switchCategoria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        categoria.guardarCategoria(context, usuario.uid);
                        chekCategoriasPadre(categoria);
                    } else {
                        categoria.eliminarCategoria(context, usuario.uid);
                    }
                }
            });
        }

        public boolean comprobarSiCategoriaGuardada(Categoria categoria){
            for (Categoria categoriaAux : usuario.categorias){
                if(categoriaAux.key.equals(categoria.key) && categoriaAux.nombre.equals(categoria.nombre)){
                    return true;
                }
            }
            return false;
        }

        public void checkCategoria(Boolean chek){
            switchCategoria.setChecked(chek);
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
                viewHolders.get(categoria1.posicion).checkCategoria(true);
                categoria.guardarCategoria(context, usuario.uid);
            }
        }
    }
}