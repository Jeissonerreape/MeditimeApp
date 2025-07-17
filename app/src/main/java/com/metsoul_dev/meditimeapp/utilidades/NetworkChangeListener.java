package com.metsoul_dev.meditimeapp.utilidades;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.metsoul_dev.meditimeapp.D_NavigationDrawerActivity;
import com.metsoul_dev.meditimeapp.R;
import com.metsoul_dev.meditimeapp.admin.AdminMainActivity;

import java.util.Objects;

public class NetworkChangeListener extends BroadcastReceiver {


    private AlertDialog dialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConeectedToInternet(context)) {
            showNoConnectionDialog(context, intent);
        } else {
            dismissNoConnectionDialog(context);
            checkAndStartActivity(context);
        }
    }

    private void showNoConnectionDialog(Context context, Intent intent) {
        if (dialog == null || !dialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layoutDialog = LayoutInflater.from(context).inflate(R.layout.verificar_conexion_dialogo, null);
            builder.setView(layoutDialog);

            AppCompatButton btnRetry = layoutDialog.findViewById(R.id.btnRetry);

            // Mostrar el diálogo
            dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }

    private void dismissNoConnectionDialog(Context context) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //quiar if de correo
    private void checkAndStartActivity(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getEmail().equals("medicotiempo49@gmail.com")) {
                Toast.makeText(context, "Administrador: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                Log.d("NetworkChangeListener", "Usuario: " + currentUser.getEmail());
                Intent intent = new Intent(context, AdminMainActivity.class);
                context.startActivity(intent);

            }else{
                Toast.makeText(context, "Usuario logeado: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                Log.d("NetworkChangeListener", "Usuario: " + currentUser.getEmail());
                    Intent intent = new Intent(context, D_NavigationDrawerActivity.class);
                context.startActivity(intent);
            }

        } else {
            Log.d("NetworkChangeListener", "No hay sesión iniciada");
        }
    }
}
