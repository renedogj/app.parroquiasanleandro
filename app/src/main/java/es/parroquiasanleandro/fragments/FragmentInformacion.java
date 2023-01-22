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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentInformacion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInformacion extends Fragment {
    private Context context;

    private ItemViewModel viewModel;

    public FragmentInformacion() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_informacion,container,false);
    }



    @Override
    public void onResume() {
        super.onResume();
        viewModel.setIdFragmentActual(Menu.FRAGMENT_INFORMACION);
        viewModel.addIdFragmentActual();
    }
}