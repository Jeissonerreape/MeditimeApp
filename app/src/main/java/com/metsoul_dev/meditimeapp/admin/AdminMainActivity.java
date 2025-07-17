package com.metsoul_dev.meditimeapp.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.metsoul_dev.meditimeapp.B_Login;
import com.metsoul_dev.meditimeapp.R;
import com.metsoul_dev.meditimeapp.databinding.ActivityAdminMainBinding;

import java.util.Objects;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final Window window = getWindow();
            final WindowInsetsController insetsController = window.getInsetsController();

            if (insetsController != null) {
                // Establece los iconos oscuros para barra de navegación (fondo claro)
                insetsController.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        } else {
            // Para versiones anteriores
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();

            // Agrega la bandera para barra de navegación con iconos oscuros (fondo claro)
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            decorView.setSystemUiVisibility(flags);
        }





    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        DialogoGeneral();
    }

    @SuppressLint("SetTextI18n")
    private void DialogoGeneral() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogo_salir_extra, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        MaterialButton btnNo = dialogView.findViewById(R.id.btnNo);
        MaterialButton btnYes = dialogView.findViewById(R.id.btnYes);

        dialogTitle.setText("¿Cerrar Admin?");
        dialogMessage.setText("¿Estás seguro de Cerrar Sesion?");

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        btnYes.setOnClickListener(v -> {
            if (alertDialog.isShowing()) {

                alertDialog.dismiss();  // Cierra el diálogo antes de cerrar la app
            }
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, B_Login.class); // Reemplaza con tu actividad de login
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Finaliza la actual
        });
        btnNo.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

}