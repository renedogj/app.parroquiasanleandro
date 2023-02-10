package es.parroquiasanleandro.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.MenuOption;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.Usuario;

public class MenuIncioAdaptador extends RecyclerView.Adapter<MenuIncioAdaptador.ViewHolder> {

    private final Context context;
    private final Activity activity;
    private final List<MenuOption> menuOptionList;
    private final int idFragmentActual;
    private final Usuario usuario;

    public MenuIncioAdaptador(Context context, Activity activity, List<MenuOption> menuOptionList, int idFragmentActual) {
        this.context = context;
        this.activity = activity;
        this.idFragmentActual = idFragmentActual;
        this.menuOptionList = menuOptionList;
        this.usuario = Usuario.recuperarUsuarioLocal(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_inicio, parent, false);
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

        private final CardView cardMenu;
        private final ImageView ivImagenMenu;
        private final TextView tvNombreMenu;

        public ViewHolder(View itemView) {
            super(itemView);

            cardMenu = itemView.findViewById(R.id.cardMenu);
            ivImagenMenu = itemView.findViewById(R.id.ivImagenMenu);
            tvNombreMenu = itemView.findViewById(R.id.tvNombreMenu);
        }

        public void asignarValoresArticulo(MenuOption menuOption) {
            Glide.with(context).load(menuOption.icono).into(ivImagenMenu);

            tvNombreMenu.setText(menuOption.nombre);
            ivImagenMenu.setContentDescription(menuOption.nombre);

            cardMenu.setOnClickListener(v -> {
                Log.e("MENNU OPTION", menuOption.id + " " + menuOption.nombre);
                Menu.seleccionarFragmentMenuId(menuOption.id, idFragmentActual, usuario, activity, context);
            });
        }
    }
}
