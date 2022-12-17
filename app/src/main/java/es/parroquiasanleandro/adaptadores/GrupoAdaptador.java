package es.parroquiasanleandro.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.utils.ItemViewModel;

public class GrupoAdaptador extends RecyclerView.Adapter<GrupoAdaptador.ViewHolder> {

    private final Context context;
    private final List<Grupo> grupos;
    private final Usuario usuario;
    public String grupoPadre;
    public List<Grupo> gruposNivel = new ArrayList<>();
    public RecyclerView rvGrupos;

    private final ItemViewModel vmIds;

    public GrupoAdaptador(Context context, List<Grupo> grupos, @NotNull String grupoPadre, RecyclerView rvGrupos, ItemViewModel vmIds) {
        this.context = context;
        this.grupos = grupos;
        this.grupoPadre = grupoPadre;
        this.rvGrupos = rvGrupos;
        usuario = Usuario.recuperarUsuarioLocal(context);
        obtenerGruposNivel();
        this.vmIds = vmIds;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grupo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        gruposNivel.get(position).setPosicion(position);
        Grupo grupo = gruposNivel.get(position);

        holder.asignarValores(grupo);
    }

    @Override
    public int getItemCount() {
        return gruposNivel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout linearLayoutGrupo;
        private final TextView tvNombreGrupo;
        private final CardView cardGrupoBoton;
        private final CardView cardImgGrupo;
        private final TextView tvBotonSeguir;
        private final ImageView imgGrupo;

        public Grupo grupo;
        public boolean grupoGuardado;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayoutGrupo = itemView.findViewById(R.id.linearLayoutGrupo);
            tvNombreGrupo = itemView.findViewById(R.id.tvNombreGrupo);
            cardGrupoBoton = itemView.findViewById(R.id.cardGrupoBoton);
            tvBotonSeguir = itemView.findViewById(R.id.tvBotonSeguir);
            cardImgGrupo = itemView.findViewById(R.id.cardImgGrupo);
            imgGrupo = itemView.findViewById(R.id.imgGrupo);
        }

        public void asignarValores(Grupo grupo) {
            this.grupo = grupo;
            Log.d("GRUPO", grupo.imagen);
            Glide.with(context).load(Url.obtenerImagenAviso + grupo.key +"/img/" + grupo.imagen).into(imgGrupo);
            tvNombreGrupo.setText(grupo.nombre);
            linearLayoutGrupo.setBackgroundColor(Color.parseColor(grupo.color));
            cardImgGrupo.setBackgroundColor(Color.parseColor(grupo.color));
            imgGrupo.setBackgroundColor(Color.parseColor(grupo.color));

            checkGrupo(comprobarSiGrupoGuardado(grupo));

            cardGrupoBoton.setOnClickListener(v -> {
                if (grupoGuardado){
                    grupo.dessuscribirGrupo(context, usuario.getId());
                    grupoGuardado = false;
                    usuario.removeGrupos(grupo);
                }else{
                    grupo.suscribirGrupo(context, usuario.getId());
                    grupoGuardado = true;
                    usuario.addGrupo(grupo);
                    chekGruposPadre(grupo);
                }
                checkGrupo(grupoGuardado);
            });

            linearLayoutGrupo.setOnClickListener(v -> {
                if(existenSubniveles(grupo)) {
                    vmIds.setGrupoActual(grupo.key);
                    vmIds.addIdGrupo();
                    GrupoAdaptador grupoAdaptador = new GrupoAdaptador(context, grupos, grupo.key, rvGrupos,vmIds);
                    rvGrupos.setAdapter(grupoAdaptador);
                }
            });
        }

        public boolean comprobarSiGrupoGuardado(Grupo grupo){
            for (Grupo grupoAux : usuario.getGrupos()){
                if(grupoAux.key.equals(grupo.key) && grupoAux.nombre.equals(grupo.nombre)){
                    return true;
                }
            }
            return false;
        }

        public void checkGrupo(Boolean chek){
            grupoGuardado = chek;
            if(grupoGuardado){
                cardGrupoBoton.setCardBackgroundColor(Color.parseColor("#aa00dd"));
                tvBotonSeguir.setTextColor(Color.parseColor("#FFFDF1F1"));
            }else{
                cardGrupoBoton.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                tvBotonSeguir.setTextColor(Color.parseColor("#FF5A5046"));
            }
        }
    }


    public void chekGruposPadre(Grupo grupo) {
        List<String> categoriasKey = new ArrayList<>();
        for (int i = 1; i <= grupo.key.length(); i++) {
            if (!categoriasKey.contains(grupo.key.substring(0, i))) {
                categoriasKey.add(grupo.key.substring(0, i));
            }
        }
        for(Grupo grupo1 : grupos){
            if(categoriasKey.contains(grupo1.key)){
                grupo1.suscribirGrupo(context, usuario.getId());
                usuario.addGrupo(grupo1);
            }
        }
    }

    public void obtenerGruposNivel(){
        for(Grupo grupo : grupos){
            if(grupo.key.length() == grupoPadre.length()+1){
                if(grupo.key.startsWith(grupoPadre)) {
                    gruposNivel.add(grupo);
                }
            }
        }
    }

    public boolean existenSubniveles(Grupo grupoPadre){
        for(Grupo grupo : grupos){
            if(grupo.key.length() == grupoPadre.key.length()+1){
                if(grupo.key.startsWith(grupoPadre.key)) {
                    return true;
                }
            }
        }
        return false;
    }
}