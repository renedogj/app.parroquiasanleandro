package es.parroquiasanleandro.utils;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModel;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import es.parroquiasanleandro.Menu;

public class ItemViewModel extends ViewModel {
    int idFragmentActual;
    List<Integer> idsFragment = new ArrayList<>();
    String grupoActual;
    List<String> idsGrupos = new ArrayList<>();
    ActionBar actionBar;
    NavigationView navView;

    public NavigationView getNavView() {
        return navView;
    }

    public void setNavView(NavigationView navView) {
        this.navView = navView;
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public void setActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    public int getIdFragmentActual() {
        return idFragmentActual;
    }

    public List<Integer> getIdsFragment() {
        return idsFragment;
    }

    public String getGrupoActual() {
        return grupoActual;
    }

    public List<String> getIdsGrupos() {
        return idsGrupos;
    }

    public void setIdFragmentActual(int idFragmentActual) {
        this.idFragmentActual = idFragmentActual;
    }

    public void setIdsFragment(List<Integer> idsFragment) {
        this.idsFragment = idsFragment;
    }

    public void setGrupoActual(String grupoActual) {
        this.grupoActual = grupoActual;
    }

    public void setIdsGrupos(List<String> idsGrupos) {
        this.idsGrupos = idsGrupos;
    }

    public void addIdFragmentActual(){
        if(idsFragment.size() > 0){
            if((idsFragment.get(idsFragment.size() - 1)) != idFragmentActual){
                this.idsFragment.add(idFragmentActual);
            }
        }else{
            this.idsFragment.add(idFragmentActual);
        }
        Menu.asignarIconosMenu(navView, idFragmentActual);
    }

    public void addIdGrupo(){
        this.idsGrupos.add(grupoActual);
    }
}