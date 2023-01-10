package es.parroquiasanleandro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.utils.ItemViewModel;


public class FragmentHorario extends Fragment {
    private Context context;

    private ItemViewModel vmIds;

    public FragmentHorario() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        vmIds = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_horario,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        vmIds.setIdFragmentActual(Menu.FRAGMENT_HORARIO);
        vmIds.addIdFragmentActual();
    }
}