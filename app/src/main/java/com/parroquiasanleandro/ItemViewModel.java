package com.parroquiasanleandro;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModel;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends ViewModel {
    int idFragmentActual;
    List<Integer> idsFragment = new ArrayList<>();
    String categoriaActual;
    List<String> idsCategorias = new ArrayList<>();
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

    public String getCategoriaActual() {
        return categoriaActual;
    }

    public List<String> getIdsCategorias() {
        return idsCategorias;
    }

    public void setIdFragmentActual(int idFragmentActual) {
        this.idFragmentActual = idFragmentActual;
    }

    public void setIdsFragment(List<Integer> idsFragment) {
        this.idsFragment = idsFragment;
    }

    public void setCategoriaActual(String categoriaActual) {
        this.categoriaActual = categoriaActual;
    }

    public void setIdsCategorias(List<String> idsCategorias) {
        this.idsCategorias = idsCategorias;
    }

    public void addIdFragmentActual(){
        this.idsFragment.add(idFragmentActual);
    }

    public void addIdCategoria(){
        this.idsCategorias.add(categoriaActual);
    }
}