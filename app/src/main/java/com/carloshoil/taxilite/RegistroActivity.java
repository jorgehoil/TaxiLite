package com.carloshoil.taxilite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carloshoil.taxilite.Global.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button btnGuardar;
    DatabaseReference databaseReferenceUser;
    EditText edNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Init();
        RecuperaNombre();
    }

    public void RecuperaNombre()
    {
        databaseReferenceUser.child(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    String cNombre=task.getResult().child("cNombre").getValue()==null?"":task.getResult().child("cNombre").getValue().toString();
                    if(cNombre.isEmpty())
                    {
                       btnGuardar.setEnabled(true);
                    }
                    else
                    {
                        Toast.makeText(RegistroActivity.this, "Ya existe una cuenta con este número de teléfono", Toast.LENGTH_SHORT).show();
                        Intent i= new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                else
                {
                    btnGuardar.setEnabled(true);
                }
            }
        });
    }
    public void Init()
    {
        edNombre=findViewById(R.id.edNombreUsuario);
        firebaseAuth=FirebaseAuth.getInstance();
        btnGuardar=findViewById(R.id.btnGuardarNombre);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     Guardar();
            }
        });
        databaseReferenceUser= FirebaseDatabase.getInstance().getReference().child("users");
        btnGuardar.setEnabled(false);
    }

    private void Guardar() {
        if(!edNombre.getText().toString().trim().isEmpty())
        {
            btnGuardar.setEnabled(false);
            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("cNombre", edNombre.getText().toString());
            databaseReferenceUser.child(firebaseAuth.getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegistroActivity.this, "¡Se ha registrado correctamente!", Toast.LENGTH_SHORT).show();
                        Intent i= new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        btnGuardar.setEnabled(true);
                        Utilities.MostrarMensaje(RegistroActivity.this, "Error", "Se ha presentado un error al guarda, intenta de nuevo");
                    }

                }
            });
        }
        else
        {
            Toast.makeText(this, "Debes ingresar tu nombre para continuar", Toast.LENGTH_SHORT).show();
        }



    }
}