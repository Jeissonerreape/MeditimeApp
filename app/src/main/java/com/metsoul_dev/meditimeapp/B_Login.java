package com.metsoul_dev.meditimeapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.metsoul_dev.meditimeapp.admin.AdminMainActivity;
import com.metsoul_dev.meditimeapp.databinding.ActivityLoginBinding;
import com.metsoul_dev.meditimeapp.utilidades.NetworkChangeListener;

import java.util.Objects;

public class B_Login extends AppCompatActivity {
    private ActivityLoginBinding binding;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "monitor01";

    private TextView Tusuario,Tcontra;

    private static final int REQUEST_CODE_PERMISOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.principal_naranja));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.principal_azul));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Verificar y solicitar el permiso si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hasReceiveBootPermission()) {
                // El permiso ya está concedido, puedes realizar acciones adicionales aquí
            } else {
                // Solicitar el permiso
                Log.d(TAG, "Permiso no concedido. Solicitando permiso...");
                requestReceiveBootPermission();
            }
        }


        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Tusuario = binding.editemail;
        Tcontra = binding.editcontra;

        binding.btningresar.setOnClickListener(v -> {
            if (!Tusuario.getText().toString().isEmpty() && !Tcontra.getText().toString().isEmpty()){
                iniciar_sesion();
            }else{

                Toast.makeText(B_Login.this, "Ingrese las email y pass", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnregistar.setOnClickListener(v -> {
            Intent intent = new Intent(this, C_RegistroJava.class);
            startActivity(intent);
            finishAffinity();
        });

        binding.txtrestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!esCorreoValido(Tusuario.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if (!Tusuario.getText().toString().isEmpty())
                    {
                        restablecerContra();
                    }else{
                        Toast.makeText(B_Login.this, "Error correo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
    private void  iniciar_sesion() {
        String email = Tusuario.getText().toString();
        String password = Tcontra.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            assert user != null;
                            if (user.getEmail().equals("medicotiempo49@gmail.com")){
                                Intent newinten = new Intent(B_Login.this, AdminMainActivity.class);
                                startActivity(newinten);
                                finishAffinity();
                            }else{
                                Intent newinten = new Intent(B_Login.this, D_NavigationDrawerActivity.class);
                                startActivity(newinten);
                                finishAffinity();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Fallo inicio sesion.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private boolean esCorreoValido(String correo) {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches();
    }
    private void restablecerContra(){
        String email = Tusuario.getText().toString().trim();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Showalertdialogo("Restablecimiento contraseña","Se ha enviado un correo para restablecer la contraseña:",email);

                        } else {

                            Toast.makeText(getApplicationContext(), "Error al restablecer la contraseña.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void Showalertdialogo(String titulo, String mensaje, String email) {
        View customView = getLayoutInflater().inflate(R.layout.dialog_message_01, null);
        TextView titleTextView = customView.findViewById(R.id.dialogTitle);
        TextView messageTextView = customView.findViewById(R.id.dialogMessage);

        Button button = customView.findViewById(R.id.dialogButton);
        titleTextView.setText(titulo);
        messageTextView.setText(mensaje + " " + email);
        AlertDialog.Builder builder = new AlertDialog.Builder(B_Login.this);
        builder.setView(customView);
        builder.setCancelable(false); // Impide que el diálogo se cierre al tocar fuera de él

        AlertDialog alertDialog = builder.create();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra el AlertDialog
                alertDialog.dismiss();

            }
        });

        alertDialog.show();
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

        dialogTitle.setText("¿Salir?");
        dialogMessage.setText("¿Estás seguro de que deseas salir?");

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        btnYes.setOnClickListener(v -> {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();  // Cierra el diálogo antes de cerrar la app
            }
            finishAffinity(); // Cierra la aplicación completamente
        });
        btnNo.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean hasReceiveBootPermission() {
        int permissionResult = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestReceiveBootPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_PERMISOS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISOS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permiso POST_NOTIFICATIONS concedido. Acciones adicionales...");
            // Puedes realizar acciones adicionales aquí después de que se concede el permiso
        } else {
            Log.d(TAG, "Permiso POST_NOTIFICATIONS no concedido. La funcionalidad relacionada no estará disponible.");
            // Puedes manejar la situación en la que el usuario no concede el permiso
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onStart() {
        super.onStart();

        //iniciar_sesionprouccion();
        //quitar despues de cambios y descomentar y linea 327
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);


    }

    @Override
    protected void onStop() {
        //desacomentar despues de cambios
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}