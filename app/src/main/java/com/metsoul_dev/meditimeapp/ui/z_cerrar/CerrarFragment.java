package com.metsoul_dev.meditimeapp.ui.z_cerrar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.metsoul_dev.meditimeapp.B_Login;
import com.metsoul_dev.meditimeapp.databinding.FragmentCerrarBinding;

public class CerrarFragment extends Fragment {

    private FragmentCerrarBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCerrarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mostrarDialogoDeConfirmacion();

        return root;
    }

    private void mostrarDialogoDeConfirmacion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro Desea Cerrar sesión?")
                .setCancelable(false)
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Cerrar sesión con Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Ir al LoginActivity y limpiar el back stack
                    Intent intent = new Intent(requireActivity(), B_Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    requireActivity().finish(); // Cerrar actividad actual
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Volver atrás o al fragmento anterior
                    requireActivity().onBackPressed();
                })
                .show();
    }
}
