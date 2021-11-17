package com.parroquiasanleandro;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends ViewModel {
    int idFragmentActual;
    List<Integer> idsFragment = new ArrayList<>();

    public int getIdFragmentActual() {
        return idFragmentActual;
    }

    public List<Integer> getIdsFragment() {
        return idsFragment;
    }

    public void setIdFragmentActual(int idFragmentActual) {
        this.idFragmentActual = idFragmentActual;
    }

    public void setIdsFragment(List<Integer> idsFragment) {
        this.idsFragment = idsFragment;
    }

    public void addIdFragmentActual(){
        this.idsFragment.add(idFragmentActual);
    }
}