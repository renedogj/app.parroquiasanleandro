package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.List;

import es.parroquiasanleandro.Grupo;
import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Url;
import es.parroquiasanleandro.Usuario;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentInfoGrupo extends Fragment {
    private Context context;
    private ItemViewModel viewModel;
    //public static FragmentManager fragmentManager;

    private LinearLayout linearLayoutContenedorGrupo;
    private LinearLayout linearLayoutGrupo;
    private TextView tvNombreGrupo;
    private CardView cardGrupoBoton;
    private TextView tvMasGrupos;
    private TextView tvBotonSeguir;
    private ImageView imgGrupo;
    private TextView tvTextoGrupo;

    private List<Grupo> grupos;
    private Grupo grupo;
    private Usuario usuario;
    private boolean grupoGuardado = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_grupo, container, false);

        linearLayoutContenedorGrupo = view.findViewById(R.id.linearLayoutContenedorGrupo);
        linearLayoutGrupo = view.findViewById(R.id.linearLayoutGrupo);
        tvNombreGrupo = view.findViewById(R.id.tvNombreGrupo);
        cardGrupoBoton = view.findViewById(R.id.cardGrupoBotonSeguir);
        tvMasGrupos = view.findViewById(R.id.tvMasGrupos);
        tvBotonSeguir = view.findViewById(R.id.tvBotonSeguir);
        imgGrupo = view.findViewById(R.id.imgGrupo);
        tvTextoGrupo = view.findViewById(R.id.tvTextoGrupo);

        usuario = Usuario.recuperarUsuarioLocal(context);
        grupos = Grupo.recuperarGruposDeLocal(context);
        grupo = Grupo.recuperarGrupoDeLocal(context, viewModel.getGrupoActual());
        if(grupo.equals(new Grupo())){
            Toast.makeText(context, "Se ha producido un error al recuperar el grupo", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }else{
            linearLayoutContenedorGrupo.setBackgroundColor(Color.parseColor(grupo.color));
            Glide.with(context).load(Url.obtenerImagenAviso + grupo.id +"/img/" + grupo.imagen).into(imgGrupo);
            tvNombreGrupo.setText(grupo.nombre);

            if(!grupo.texto.equals("null")){
                tvTextoGrupo.setText(grupo.texto);
            }
            grupoGuardado = grupo.isGrupoGuardado(usuario);
            checkGrupo(grupoGuardado);

            if(grupo.existenSubniveles(grupos)){
                tvMasGrupos.setPaintFlags(tvMasGrupos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tvMasGrupos.setVisibility(View.VISIBLE);
                        /*tvMasGrupos.setOnClickListener(v -> {
                            if(grupo.existenSubniveles(grupos)) {
                                viewModel.setGrupoActual(grupo.id);
                                viewModel.addIdGrupoActual();
                                //GrupoAdaptador grupoAdaptador = new GrupoAdaptador(context, grupos, grupo.id, rvGrupos, viewModel);
                                //rvGrupos.setAdapter(grupoAdaptador);
                            }
                        });*/
            }else{
                tvMasGrupos.setVisibility(View.GONE);
            }
        }

        cardGrupoBoton.setOnClickListener(v -> {
            if (grupoGuardado){
                grupo.eliminarGrupoSeguido(context, usuario.getId());
                grupoGuardado = false;
                usuario.removeGrupo(grupo);
            }else{
                grupo.seguirGrupo(context, usuario.getId());
                grupoGuardado = true;
                usuario.addGrupo(grupo);
                grupo.chekGruposPadre(context, grupos, usuario);
            }
            checkGrupo(grupoGuardado);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setIdFragmentActual(Menu.FRAGMENT_INFO_GRUPO);
        viewModel.addIdFragmentActual();
    }

    public void checkGrupo(Boolean chek){
        if(chek){
            cardGrupoBoton.setCardBackgroundColor(Color.parseColor("#FF3F888F"));
            //cardGrupoBoton.setCardBackgroundColor(R.color.primary_color);
            //tvBotonSeguir.setTextColor(Color.parseColor("#FFFDF1F1"));
            //tvBotonSeguir.setTextColor(R.color.negro);
            tvBotonSeguir.setText("Siguiendo");
        }else{
            //cardGrupoBoton.setCardBackgroundColor(R.color.blanco);
            //cardGrupoBoton.setBackgroundResource(R.color.blanco);
            cardGrupoBoton.setCardBackgroundColor(Color.parseColor("#FFF5F5F5"));
            //tvBotonSeguir.setTextColor(Color.parseColor("#FF5A5046"));
            //tvBotonSeguir.setTextColor(R.color.negro);
            tvBotonSeguir.setText("Seguir");
        }
    }
}
