package com.metsoul_dev.meditimeapp.ui.micuenta;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.metsoul_dev.meditimeapp.B_Login;
import com.metsoul_dev.meditimeapp.D_NavigationDrawerActivity;
import com.metsoul_dev.meditimeapp.R;
import com.metsoul_dev.meditimeapp.databinding.FragmentSlideshowBinding;

public class MiCuentaFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private FirebaseFirestore db;
    private TextView textViewNombre;
    private TextView textViewTelefono,textViewCorreo,textViewciudad,textdepa;
    private FirebaseAuth mAuth;

    private boolean BanderaDescarga = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();

        binding.Closetcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "cerrar sesion", Toast.LENGTH_SHORT).show();
                DialogoCerrarsesion();
            }
        });

        binding.Atencioncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Atencion", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }


    @SuppressLint({"SetTextI18n","MissingInflatedId"})
    private void DialogoCerrarsesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialogo_salir_extra, null);
        // Obtén las referencias a las vistas del XML personalizado
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        MaterialButton btnNo = dialogView.findViewById(R.id.btnNo);
        MaterialButton btnYes = dialogView.findViewById(R.id.btnYes);
        btnNo.setText("Aceptar");
        btnYes.setText("Salir");

        dialogTitle.setText("¿Cerrar Sesion?");
        dialogMessage.setText("Mejor cierra la pestaña sin salir y estarás siempre conectado.");

        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // Configura el botón "Sí"
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Log.d("fireservicio", "sesion cerrada");

                Intent intent = new Intent(getContext(), B_Login.class);
                startActivity(intent);
                finishAffinity(getActivity());
            }
        });

        //alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar el diálogo
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public boolean onBackPressed() {
        DrawerLayout drawerLayout = ((D_NavigationDrawerActivity) requireActivity()).drawer;

        if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Si el drawer está cerrado, ábrelo
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else {
            // Si el drawer está abierto o no existe, realiza la navegación predeterminada (o personalizada)
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_navigation_drawer);
            navController.navigate(R.id.nav_view);
            return true;
        }
    }
}