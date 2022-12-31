package es.parroquiasanleandro.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.MenuOption;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;

public class MenuIncioAdaptador extends RecyclerView.Adapter<MenuIncioAdaptador.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<MenuOption> menuOptionList;
    private int idFragmentActual;
    private Usuario usuario;
    private FragmentManager fragmentManager;
    private ActionBar actionBar;
    private NavigationView navView;

    public MenuIncioAdaptador(Context context, Activity activity, List<MenuOption> menuOptionList, int idFragmentActual, FragmentManager fragmentManager, ActionBar actionBar, NavigationView navView) {
        this.context = context;
        this.activity = activity;
        this.idFragmentActual = idFragmentActual;
        this.menuOptionList = menuOptionList;
        this.usuario = Usuario.recuperarUsuarioLocal(context);
        this.fragmentManager = fragmentManager;
        this.actionBar = actionBar;
        this.navView = navView;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_inicio_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        MenuOption menuOption = menuOptionList.get(position);
        holder.asignarValoresArticulo(menuOption);
    }

    @Override
    public int getItemCount() {
        return menuOptionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardMenu;
        private ImageView ivImagenMenu;
        private TextView tvNombreMenu;

        public ViewHolder(View itemView) {
            super(itemView);

            cardMenu = itemView.findViewById(R.id.cardMenu);
            ivImagenMenu = itemView.findViewById(R.id.ivImagenMenu);
            tvNombreMenu = itemView.findViewById(R.id.tvNombreMenu);
        }

        public void asignarValoresArticulo(MenuOption menuOption) {
            //if(articulo.imagenes.get(0) != null){
            Glide.with(context).load(menuOption.icono).into(ivImagenMenu);
            //}
            tvNombreMenu.setText(menuOption.nombre);
            ivImagenMenu.setContentDescription(menuOption.nombre);

            cardMenu.setOnClickListener(v -> {
                //Toast.makeText(context, menuOption.nombre, Toast.LENGTH_SHORT).show();
                Menu.seleccionarFragmentMenuId(menuOption.id, idFragmentActual, usuario, activity, context, fragmentManager, actionBar, navView);
            });
        }
    }
}
