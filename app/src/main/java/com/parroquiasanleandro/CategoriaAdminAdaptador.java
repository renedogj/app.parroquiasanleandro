package com.parroquiasanleandro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class CategoriaAdminAdaptador extends RecyclerView.Adapter<CategoriaAdminAdaptador.ViewHolder> {

    private Context context;
    private Categoria[] categorias;

    public CategoriaAdminAdaptador(Context context, Categoria[] categorias) {
        this.context = context;
        this.categorias = categorias;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_categoria_item, parent, false);
        return new CategoriaAdminAdaptador.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Categoria categoria = categorias[position];
        holder.asignarValoresCategoria(categoria);
    }

    @Override
    public int getItemCount() {
        return categorias.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSpinCategoriaAdministra;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSpinCategoriaAdministra = itemView.findViewById(R.id.tvSpinCategoriaAdministra);
        }

        public void asignarValoresCategoria(Categoria categoria) {
            tvSpinCategoriaAdministra.setText(categoria.nombre);
        }
    }
}
