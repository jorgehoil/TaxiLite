package com.carloshoil.taxilite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.taxilite.Global.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ActivityTelefono extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String cVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    EditText edNumeroTelefono, edCodigo;
    Button btnVerificarTelefono, btnVerificarCodigo;
    TextView tvTelefono, tvCodigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefono);
        Init();
    }

    private void Init() {
        mAuth= FirebaseAuth.getInstance();
        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                IniciaSesion(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Utilities.MostrarMensaje(ActivityTelefono.this, "Error", "Se ha producido un error al iniciar sesión, si has ingresado un código, verifica que sea correcto");
                btnVerificarCodigo.setEnabled(true);
                btnVerificarTelefono.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                cVerificationId=s;
                mResendToken=forceResendingToken;
                btnVerificarCodigo.setEnabled(true);
                btnVerificarTelefono.setEnabled(true);
                OcultaMuestraControles(false);

            }
        };
        edCodigo=findViewById(R.id.edCodigo);
        edNumeroTelefono=findViewById(R.id.edNumeroTelefono);
        btnVerificarCodigo=findViewById(R.id.btnVerificarCodigo);
        btnVerificarTelefono=findViewById(R.id.btnVerificarTelefono);
        tvTelefono=findViewById(R.id.tvTelefono);
        tvCodigo=findViewById(R.id.tvCodigo);
        btnVerificarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificaTelefono(edNumeroTelefono.getText().toString());
            }
        });
        btnVerificarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificaCodigo(edCodigo.getText().toString());
            }
        });

    }
    private void VerificaTelefono(String cNumero)
    {
        if(ValidaTelefono(cNumero))
        {
            cNumero="+52"+cNumero;
            btnVerificarTelefono.setEnabled(false);
            PhoneAuthOptions phoneAuthOptions= PhoneAuthOptions.newBuilder()
                    .setPhoneNumber(cNumero)
                    .setCallbacks(mCallbacks)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
        }


    }
    private boolean ValidaTelefono(String cTelefono){
        if(cTelefono.trim().isEmpty())
        {
            Toast.makeText(this, "Ingresa tu número telefónico", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cTelefono.length()!=10)
        {
            Toast.makeText(this, "Debes ingresar 10 dígito", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    private void IniciaSesion(PhoneAuthCredential phoneAuthCredential)
    {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    Utilities.GuardarPreferencias("cEstatus", "COMP", ActivityTelefono.this);
                    Intent i= new Intent(ActivityTelefono.this, RegistroActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Utilities.MostrarMensaje(ActivityTelefono.this, "Error", "Se ha presentado un error al iniciar sesión, intenta de nuevo");
                }
            }
        });

    }

    private void OcultaMuestraControles(boolean lTelefono)
    {

        if(lTelefono)
        {
            edCodigo.setVisibility(View.INVISIBLE);
            btnVerificarCodigo.setVisibility(View.INVISIBLE);
            tvCodigo.setVisibility(View.INVISIBLE);
            edNumeroTelefono.setVisibility(View.VISIBLE);
            tvTelefono.setVisibility(View.VISIBLE);
            btnVerificarTelefono.setVisibility(View.VISIBLE);
        }
        else
        {
            edCodigo.setVisibility(View.VISIBLE);
            btnVerificarCodigo.setVisibility(View.VISIBLE);
            tvCodigo.setVisibility(View.VISIBLE);
            edNumeroTelefono.setVisibility(View.INVISIBLE);
            tvTelefono.setVisibility(View.INVISIBLE);
            btnVerificarTelefono.setVisibility(View.INVISIBLE);
        }

    }

    private void VerificaCodigo(String cCodigo)
    {
        if(!cCodigo.trim().isEmpty())
        {
            btnVerificarCodigo.setEnabled(false);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(cVerificationId, cCodigo);
            IniciaSesion(credential);
        }
        else {
            Toast.makeText(this, "Ingresa el código que has recibido por SMS", Toast.LENGTH_SHORT).show();
        }

    }
}