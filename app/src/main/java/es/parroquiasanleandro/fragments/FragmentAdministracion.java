package es.parroquiasanleandro.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentAdministracion extends Fragment {
    private ItemViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        Log.e("FRAGMENT ADMINISTRACIOn","AZ>aaaaaaaaaaaaaa");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_administracion, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityNavigation.actionBar.setTitle(Menu.ADMINISTRACION);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_ADMINISTRACION);
        viewModel.addIdFragmentActual();
    }
}