package es.parroquiasanleandro.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentInformacion extends Fragment {
    private ItemViewModel viewModel;

    private TextView tvWeb;

    public FragmentInformacion() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacion, container, false);

        tvWeb = view.findViewById(R.id.tvWeb);

        tvWeb.setPaintFlags(tvWeb.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setIdFragmentActual(Menu.FRAGMENT_INFORMACION);
        viewModel.addIdFragmentActual();
    }
}