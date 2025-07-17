package com.metsoul_dev.meditimeapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.metsoul_dev.meditimeapp.databinding.ActivityRegistroJavaBinding;

import java.util.HashMap;
import java.util.Map;

public class C_RegistroJava extends AppCompatActivity {

    private ActivityRegistroJavaBinding bindingR; // Declarar el objeto binding

    private TextView Tcorreo,Tcontra, Tname, Ttel, Tcedula, Tedad;


    private ProgressDialog progressDialog;
    private MaterialCheckBox checkBox;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "monitor01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bindingR = ActivityRegistroJavaBinding.inflate(getLayoutInflater());
        setContentView(bindingR.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Tcorreo = bindingR.edittextcorreore;
        Tcontra = bindingR.editcontraregistro;
        Tname = bindingR.edittextnamere;
        Ttel = bindingR.edittexttelre;
        checkBox = bindingR.checkpoliticas;
        Tcedula = bindingR.edittextcedula;
        Tedad = bindingR.editetxtedad;

        bindingR.btncancelarRegistro.setOnClickListener(view -> {
            onBackPressed();
        });
        bindingR.btnregistarre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar el correo electrónico
                if (!esCorreoValido(Tcorreo.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if (!Tcorreo.getText().toString().isEmpty()
                            && !Tcontra.getText().toString().isEmpty()
                            && !Tname.getText().toString().isEmpty()
                            && !Ttel.getText().toString().isEmpty()
                            && !Tedad.getText().toString().isEmpty()
                            && !Tcedula.getText().toString().isEmpty()){
                        if (checkBox.isChecked()){
                            agregar_usuarioIMG();

                        }else{
                            Toast.makeText(C_RegistroJava.this, "Acepta politicas y tratamiento de datos", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(C_RegistroJava.this, "Error datos incompletos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void agregar_usuarioIMG() {
        // [START create_user_with_email]
        String email = Tcorreo.getText().toString();
        String cedula = Tcedula.getText().toString();
        String password = Tcontra.getText().toString();
        String name = Tname.getText().toString();
        String phone = Ttel.getText().toString();
        String edad = Tedad.getText().toString();

        // Mostrar ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando usuario...");
        progressDialog.setCancelable(false); // No permitir que el usuario cancele
        progressDialog.setCanceledOnTouchOutside(false); // No cerrar al tocar fuera
        progressDialog.show();


        mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                String userId = user.getUid();

                                guardarDatosEnFirestoreIMG(userId, name, phone, password, email , cedula, edad);
                                sendEmailVerification(user);


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Fallo al registro de usuarios.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }});

    }

    private void guardarDatosEnFirestoreIMG(String userId, String name, String phone, String password, String email, String cedula, String edad) {
        // Crear un mapa con los datos del usuario
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", name);
        userData.put("dni", cedula);
        userData.put("telefono", phone);
        userData.put("correo", email);
        userData.put("edad", edad);
        userData.put("password", password);
        userData.put("userId", userId);

        DocumentReference userRef = db.collection("usuarios").document(userId);

        // Guardar los datos en Firestore
        userRef.set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Datos del usuario guardados en Firestore con éxito.");
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al guardar datos del usuario en Firestore", e);

                    }
                });
    }


    private void sendEmailVerification(FirebaseUser user) {
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(C_RegistroJava.this);
                        builder.setMessage("Verificacion enviada al correo: "+Tcorreo.getText().toString());
                        builder.setTitle("Verificacion");
                        builder.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), B_Login.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        });
                        builder.show();


                    }
                });
        // [END send_email_verification]
    }

    private boolean esCorreoValido(String correo) {
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent newinten = new Intent(C_RegistroJava.this, B_Login.class);
        startActivity(newinten);
        finishAffinity();
    }
}