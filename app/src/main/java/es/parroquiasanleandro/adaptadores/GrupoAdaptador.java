package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.utils.ItemViewModel;

public class GrupoAdaptador extends RecyclerView.Adapter<GrupoAdaptador.ViewHolder> {
    private final Context context;
    private final ItemViewModel viewModel;

    private final List<Grupo> grupos;
    private final Usuario usuario;
    public String grupoPadre;
    public List<Grupo> gruposNivel = new ArrayList<>();
    public RecyclerView rvGrupos;


    public GrupoAdaptador(Context context, List<Grupo> grupos, @NotNull String grupoPadre, RecyclerView rvGrupos, ItemViewModel viewModel) {
        this.context = context;
        this.grupos = grupos;
        this.grupoPadre = grupoPadre;
        this.rvGrupos = rvGrupos;
        usuario = Usuario.recuperarUsuarioLocal(context);
        obtenerGruposNivel();
        this.viewModel = viewModel;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grupo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Grupo grupo = gruposNivel.get(position);
        holder.asignarValores(grupo);
    }

    @Override
    public int getItemCount() {
        return gruposNivel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout linearLayoutContenedorGrupo;
        private final LinearLayout linearLayoutGrupo;
        private final TextView tvNombreGrupo;
        private final CardView cardGrupoBoton;
        private final TextView tvMasGrupos;
        private final TextView tvBotonSeguir;
        private final ImageView imgGrupo;

        public Grupo grupo;
        public boolean grupoGuardado;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayoutContenedorGrupo = itemView.findViewById(R.id.linearLayoutContenedorGrupo);
            linearLayoutGrupo = itemView.findViewById(R.id.linearLayoutGrupo);
            tvNombreGrupo = itemView.findViewById(R.id.tvNombreGrupo);
            cardGrupoBoton = itemView.findViewById(R.id.cardGrupoBotonSeguir);
            tvMasGrupos = itemView.findViewById(R.id.tvMasGrupos);
            tvBotonSeguir = itemView.findViewById(R.id.tvBotonSeguir);
            imgGrupo = itemView.findViewById(R.id.imgGrupo);
        }

        public void asignarValores(Grupo grupo) {
            this.grupo = grupo;

            linearLayoutContenedorGrupo.setBackgroundColor(Color.parseColor(grupo.color));
            Glide.with(context).load(Url.obtenerImagenAviso + grupo.id + "/img/" + grupo.imagen).into(imgGrupo);
            tvNombreGrupo.setText(grupo.nombre);

            grupoGuardado = grupo.isGrupoGuardado(usuario);
            checkGrupo(grupoGuardado);

            if (grupo.existenSubniveles(grupos)) {
                tvMasGrupos.setPaintFlags(tvMasGrupos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tvMasGrupos.setVisibility(View.VISIBLE);
                tvMasGrupos.setOnClickListener(v -> {
                    viewModel.setGrupoActual(grupo.id);
                    viewModel.addIdGrupoActual();
                    GrupoAdaptador grupoAdaptador = new GrupoAdaptador(context, grupos, grupo.id, rvGrupos, viewModel);
                    rvGrupos.setAdapter(grupoAdaptador);
                });
            } else {
                tvMasGrupos.setVisibility(View.GONE);
            }

            cardGrupoBoton.setOnClickListener(v -> {
                if (grupoGuardado) {
                    grupo.eliminarGrupoSeguido(context, usuario.getId());
                    grupoGuardado = false;
                    usuario.removeGrupo(grupo);
                } else {
                    grupo.seguirGrupo(context, usuario.getId());
                    grupoGuardado = true;
                    usuario.addGrupo(grupo);
                    grupo.chekGruposPadre(context, grupos, usuario);
                }
                checkGrupo(grupoGuardado);
            });

            linearLayoutGrupo.setOnClickListener(v -> {
                viewModel.setGrupoActual(grupo.id);
                viewModel.addIdGrupoActual();
                Menu.iniciarFragmentInfoGrupo();
            });
        }

        public void checkGrupo(Boolean chek) {
            if (chek) {
                cardGrupoBoton.setCardBackgroundColor(Color.parseColor("#FF3F888F"));
                //cardGrupoBoton.setCardBackgroundColor(R.color.primary_color);
                //tvBotonSeguir.setTextColor(Color.parseColor("#FFFDF1F1"));
                //tvBotonSeguir.setTextColor(R.color.negro);
                tvBotonSeguir.setText("Siguiendo");
            } else {
                //cardGrupoBoton.setCardBackgroundColor(R.color.blanco);
                //cardGrupoBoton.setBackgroundResource(R.color.blanco);
                cardGrupoBoton.setCardBackgroundColor(Color.parseColor("#FFF5F5F5"));
                //tvBotonSeguir.setTextColor(Color.parseColor("#FF5A5046"));
                //tvBotonSeguir.setTextColor(R.color.negro);
                tvBotonSeguir.setText("Seguir");
            }
        }
    }

    public void obtenerGruposNivel() {
        for (Grupo grupo : grupos) {
            if (grupo.id.length() == grupoPadre.length() + 1) {
                if (grupo.id.startsWith(grupoPadre)) {
                    gruposNivel.add(grupo);
                }
            }
        }
    }
}