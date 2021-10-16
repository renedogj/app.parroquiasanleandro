package com.parroquiasanleandro;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActivityAvisosParroquiales extends AppCompatActivity {

    private final Context context = ActivityAvisosParroquiales.this;

    private RecyclerView rvAvisos;

    List<Aviso> avisos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avisos_parroquiales);

        rvAvisos = findViewById(R.id.rvAvisos);

        rvAvisos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        //linearLayoutManager.setStackFromEnd(true);
        rvAvisos.setLayoutManager(linearLayoutManager);

        avisos = new ArrayList<>();

        /*FirebaseStorage.getInstance().getReference().child("ImagenesAvisos").child("2020058181014.jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                //ImageView.setImageURI(uri);
                Glide.with(context)
                        .load(uri.toString())
                        .into(ivImagenAviso);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle any errors
            }
        });*/

        FirebaseDatabase.getInstance().getReference().child("Avisos").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {
                Aviso aviso = dataSnapshot.getValue(Aviso.class);
                if (aviso != null) {
                    aviso.key = dataSnapshot.getKey();
                }
                avisos.add(aviso);

                AvisoAdaptador avisoAdaptador = new AvisoAdaptador(context, avisos);
                rvAvisos.setAdapter(avisoAdaptador);
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Log.d("DATABASE ERROR", databaseError.getMessage());
            }
        });
    }
}