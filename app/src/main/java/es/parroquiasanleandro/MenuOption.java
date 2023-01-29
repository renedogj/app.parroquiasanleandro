package es.parroquiasanleandro;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.parroquiasanleandro.fragments.FragmentAvisosParroquiales;
import es.parroquiasanleandro.fragments.FragmentCalendario;
import es.parroquiasanleandro.fragments.FragmentGrupos;
import es.parroquiasanleandro.fragments.FragmentHorario;
import es.parroquiasanleandro.fragments.FragmentInformacion;
import es.parroquiasanleandro.fragments.FragmentInicio;
import es.parroquiasanleandro.fragments.FragmentMercadillo;
import es.parroquiasanleandro.fragments.FragmentPerfil;

public class MenuOption {
    public String nombre;
    public int id;
    public int icono;
    public int icono_black;
    public Class fragementClass;

    public MenuOption() {
    }

    public MenuOption(String nombre, int id, int icono, int icono_black, Class fragementClass) {
        this.nombre = nombre;
        this.id = id;
        this.icono = icono;
        this.icono_black = icono_black;
        this.fragementClass = fragementClass;
    }

    public MenuOption(String nombre, int id, int icono, Class fragementClass) {
        this.nombre = nombre;
        this.id = id;
        this.icono = icono;
        this.fragementClass = fragementClass;
    }

    //Map para obtener la información de los items
    //Todos los items tienen que estar aquí
    @NonNull
    public static Map<Integer, MenuOption> obtenerMapMenuOptions() {
        Map<Integer, MenuOption> menuItemMap = new ArrayMap<Integer, MenuOption>();
        menuItemMap.put(Menu.FRAGMENT_HORARIO, new MenuOption(Menu.HORARIO, Menu.FRAGMENT_HORARIO, R.drawable.ic_reloj, FragmentHorario.class));
        menuItemMap.put(Menu.FRAGMENT_GRUPOS, new MenuOption(Menu.GRUPOS, Menu.FRAGMENT_GRUPOS, R.drawable.ic_grupos, FragmentGrupos.class));
        menuItemMap.put(Menu.FRAGMENT_PERFIL, new MenuOption(Menu.PERFIL, Menu.FRAGMENT_PERFIL, R.drawable.ic_user, FragmentPerfil.class));
        menuItemMap.put(Menu.FRAGMENT_INICIO, new MenuOption(Menu.INICIO, Menu.FRAGMENT_INICIO, R.drawable.ic_home, FragmentInicio.class));
        menuItemMap.put(Menu.FRAGMENT_AVISOS, new MenuOption(Menu.AVISOS, Menu.FRAGMENT_AVISOS, R.drawable.ic_bell, FragmentAvisosParroquiales.class));
        menuItemMap.put(Menu.FRAGMENT_CALENDARIO, new MenuOption(Menu.CALENDARIO, Menu.FRAGMENT_CALENDARIO, R.drawable.ic_calendar, FragmentCalendario.class));
        menuItemMap.put(Menu.FRAGMENT_MERCADILLO, new MenuOption(Menu.MERCADILLO, Menu.FRAGMENT_MERCADILLO, R.drawable.ic_mercadillo, FragmentMercadillo.class));
        menuItemMap.put(Menu.FRAGMENT_INFORMACION, new MenuOption(Menu.INFORMACION, Menu.FRAGMENT_INFORMACION, R.drawable.ic_informacion, FragmentInformacion.class));
        return menuItemMap;
    }

    //Lista para tener ordenados los items
    //Los items que se muestran en la pantalla de inicio tienen que estar aquí
    @NonNull
    public static List<MenuOption> obtenerListMenuOptions() {
        List<MenuOption> menuItemList = new ArrayList<>();
        menuItemList.add(new MenuOption(Menu.AVISOS, Menu.FRAGMENT_AVISOS, R.drawable.ic_bell, FragmentAvisosParroquiales.class));
        menuItemList.add(new MenuOption(Menu.CALENDARIO, Menu.FRAGMENT_CALENDARIO, R.drawable.ic_calendar, FragmentCalendario.class));
        menuItemList.add(new MenuOption(Menu.HORARIO, Menu.FRAGMENT_HORARIO, R.drawable.ic_reloj, FragmentHorario.class));
        menuItemList.add(new MenuOption(Menu.INFORMACION, Menu.FRAGMENT_INFORMACION, R.drawable.ic_informacion, FragmentInformacion.class));
        menuItemList.add(new MenuOption(Menu.GRUPOS, Menu.FRAGMENT_GRUPOS, R.drawable.ic_grupos, FragmentGrupos.class));
        menuItemList.add(new MenuOption(Menu.MERCADILLO, Menu.FRAGMENT_MERCADILLO, R.drawable.ic_mercadillo, FragmentMercadillo.class));
        return menuItemList;
    }
}
