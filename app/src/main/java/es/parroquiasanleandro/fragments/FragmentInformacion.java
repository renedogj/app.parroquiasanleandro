package es.parroquiasanleandro.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.parroquiasanleandro.Menu;
import es.parroquiasanleandro.R;
import es.parroquiasanleandro.activitys.ActivityNavigation;
import es.parroquiasanleandro.utils.ItemViewModel;

public class FragmentInformacion extends Fragment {
    private ItemViewModel viewModel;

    private TextView tvWeb;
    private ImageView imgFacebook;
    private ImageView imgYoutube;
    private ImageView imgInstagram;
    private ImageView imgTwitter;
    private ImageView imgEnlace;

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
        imgFacebook = view.findViewById(R.id.imgFacebook);
        imgYoutube = view.findViewById(R.id.imgYoutube);
        imgInstagram = view.findViewById(R.id.imgInstagram);
        imgTwitter = view.findViewById(R.id.imgTwitter);
        imgEnlace = view.findViewById(R.id.imgEnlace);

        tvWeb.setPaintFlags(tvWeb.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        imgFacebook.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.facebook.com/parroquiasanleandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.facebook.katana");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/parroquiasanleandro")));
            }
        });

        imgYoutube.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.youtube.com/ParroquiaSanLeandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.youtube");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/ParroquiaSanLeandro")));
            }
        });

        imgInstagram.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://instagram.com/_u/psanleandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.instagram.android");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/psanleandro")));
            }
        });

        imgTwitter.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://twitter.com/psanleandro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setType("application/twitter");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //No encontró la aplicación, abre la versión web.
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/psanleandro")));
            }
        });

        imgEnlace.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.parroquiasanleandro.es/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityNavigation.actionBar.setTitle(Menu.INFORMACION);
        viewModel.setIdFragmentActual(Menu.FRAGMENT_INFORMACION);
        viewModel.addIdFragmentActual();
    }
}