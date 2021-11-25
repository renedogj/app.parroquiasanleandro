package com.parroquiasanleandro;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends ViewModel {
    int idFragmentActual;
    List<Integer> idsFragment = new ArrayList<>();
    String categoriaActual;
    List<String> idsCategorias = new ArrayList<>();

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