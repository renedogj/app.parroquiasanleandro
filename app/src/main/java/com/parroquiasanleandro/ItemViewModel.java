package com.parroquiasanleandro;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends ViewModel {
    int idFragmentActual;
    List<Integer> idsFragment = new ArrayList<>();

    public void addIdFragmentActual(){
        this.idsFragment.add(idFragmentActual);
    }
}