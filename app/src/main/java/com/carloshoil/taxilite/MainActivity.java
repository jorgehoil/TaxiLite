package com.carloshoil.taxilite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.taxilite.Adapter.AdapterTaxistas;
import com.carloshoil.taxilite.DataBase.Database;
import com.carloshoil.taxilite.Entitie.Municipio;
import com.carloshoil.taxilite.Entitie.Taxista;
import com.carloshoil.taxilite.Global.Utilities;
import com.carloshoil.taxilite.Service.MunicipioService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
        DatabaseReference databaseReference;
        DatabaseReference availableTaxis;
        ChildEventListener childEventListener;
        Spinner spinnerPueblos;
        RecyclerView recyclerViewTaxis;
        AdapterTaxistas adapterTaxistas;
        List<String> lstMunicipiosIDS= new ArrayList<>();
        TextView tvTotalTaxis;
        Button btnActualizarMunicipios;
        String cIdMunicipio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Init();
        setAdapter();


    }

    @Override
    protected void onResume() {
        Log.d("DEBUG", "------- OnResume --------");
        if(availableTaxis!=null&&childEventListener!=null)
        {
            Log.d("DEBUG", "------- Se consulta --------");
            availableTaxis.orderByChild("iViajes").limitToFirst(3).addChildEventListener(childEventListener);
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.d("DEBUG", "------- Pause --------");
        if(availableTaxis!=null&&childEventListener!=null)
        {
            Log.d("DEBUG", "------- Se remueve --------");
            availableTaxis.removeEventListener(childEventListener);
            adapterTaxistas.LimpiarLista();
            ajustaConteoTaxis(0);
        }
        super.onPause();
    }

    private void ajustaConteoTaxis(int iTaxis)
    {
        Log.d("DEBUG", "------- Ajusta Conteo Taxis --------");
        if(iTaxis>=1)
        {
            tvTotalTaxis.setBackgroundColor(Color.rgb(30,110,42));
            tvTotalTaxis.setText(iTaxis+ (iTaxis>1?" taxis": " taxi")+ (iTaxis>1?" disponibles": " disponible"));
        }
        else
        {
            tvTotalTaxis.setText("Sin taxis disponibles");
            tvTotalTaxis.setBackgroundColor(Color.RED);
        }
    }
    private void Init() {
        Log.d("DEBUG", "------- Init --------");
        cIdMunicipio=Utilities.RecuperaPreferencia("cIdMunicipio", this);
        childEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("DEBUG", "CHILD-ADDED");
                Taxista taxista= new Taxista(snapshot.getKey(),
                        snapshot.child("cNombre").getValue()==null?"":snapshot.child("cNombre").getValue().toString(),
                        snapshot.child("cTelefono").getValue()==null?"":snapshot.child("cTelefono").getValue().toString(),
                        snapshot.child("cUrlImagen").getValue()==null?"":snapshot.child("cUrlImagen").getValue().toString());
                adapterTaxistas.AgregarTaxista(taxista);
                ajustaConteoTaxis(adapterTaxistas.ObtenerTotal());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("DEBUG", "CHILD-CHANGED");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUG", "CHILD-REMOVED");
                if(snapshot.getValue()!=null)
                {
                    adapterTaxistas.EliminarTaxista(snapshot.getKey());
                    ajustaConteoTaxis(adapterTaxistas.ObtenerTotal());
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("DEBUG", "CHILD-MOVED");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DEBUG", "ERROR");
            }
        };
        databaseReference= FirebaseDatabase.getInstance().getReference();
        btnActualizarMunicipios=findViewById(R.id.btnActualizarMunicipios);
        recyclerViewTaxis=findViewById(R.id.recycleTaxis);
        spinnerPueblos=findViewById(R.id.spinnerPuebls);
        tvTotalTaxis=findViewById(R.id.tvTotalTaxis);
        PreparaSpinner();
        spinnerPueblos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AjustaMunicipio(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnActualizarMunicipios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ObtenerTotalMunicipios();
            }
        });
    }

    private void AjustaMunicipio(int position) {
        Log.d("DEBUG", "------- AjustaMunicipio --------");
        ajustaConteoTaxis(0);
        String cMunicipio=lstMunicipiosIDS.get(position);
        if(availableTaxis!=null&&childEventListener!=null)
        {
            availableTaxis.removeEventListener(childEventListener);
        }
        adapterTaxistas.LimpiarLista();
        availableTaxis=databaseReference.child("taxis").child("available"+cMunicipio);
        availableTaxis.orderByChild("iViajes").limitToFirst(3).addChildEventListener(childEventListener);
        Utilities.GuardarPreferencias("cIdMunicipio", cMunicipio,this);

    }

    public void setAdapter()
    {
        Log.d("DEBUG", "------- setAdapter--------");
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewTaxis.setLayoutManager(linearLayoutManager);
        adapterTaxistas=new AdapterTaxistas(this,new ArrayList<>(),Utilities.RecuperaPreferencia("cIdMunicipio", this));
        recyclerViewTaxis.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerViewTaxis.setAdapter(adapterTaxistas);
    }
    public void VerificarCantidadMunicipios(int iCantidadLocal)
    {
        Log.d("DEBUG", "------- VerificarCantidadMunicipios --------");
        databaseReference.child("totalmunicipios").child("iTotal").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {

                    int iMunicipiosActual= task.getResult()==null?0:task.getResult().getValue(Integer.class);
                    if(iMunicipiosActual>0)
                    {
                        if(iCantidadLocal!=iMunicipiosActual) {
                            DescargaMunicipios();
                        }
                        else
                        {
                            spinnerPueblos.setEnabled(true);
                            btnActualizarMunicipios.setEnabled(true);
                            Toast.makeText(MainActivity.this, "La lista de municipios se encuentra actualizado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    spinnerPueblos.setEnabled(true);
                    btnActualizarMunicipios.setEnabled(true);
                    Utilities.MostrarMensaje(MainActivity.this, "Error", "Se ha presentado un error al actualizar municipios");
                }

            }
        });
    }
    public void DescargaMunicipios()
    {
        Log.d("DEBUG", "------- Descargar Municipios--------");
        databaseReference.child("municipios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    ActualizarMunicipios(task.getResult());
                }
                else
                {
                    Utilities.MostrarMensaje(MainActivity.this, "Error", "Se ha presentado un error al actualizar los municipios, intenta de nuevo");
                    btnActualizarMunicipios.setEnabled(true);
                    spinnerPueblos.setEnabled(true);
                }
            }
        });

    }
    public void ActualizarMunicipios(DataSnapshot data)
    {
        Log.d("DEBUG", "------- Actualizar Municipios--------");
        Handler handler= new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                spinnerPueblos.setEnabled(true);
                btnActualizarMunicipios.setEnabled(true);
               if(msg.what==1)
               {
                   Toast.makeText(MainActivity.this, "Se han actualizado correctamente los municipios", Toast.LENGTH_SHORT).show();
                   PreparaSpinner();
               }
               else
               {
                   Toast.makeText(MainActivity.this, "Se ha producido un error al actualizar", Toast.LENGTH_SHORT).show();
               }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Database database= Database.getInstance(MainActivity.this);
                database.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        Message message= new Message();
                        try {
                            List<Municipio> municipios= new ArrayList<>();
                            Municipio entMunicipio;
                            for(DataSnapshot municipio: data.getChildren())
                            {
                                entMunicipio= new Municipio();
                                entMunicipio.cIdMunicipio=municipio.getKey()==null?"":municipio.getKey();
                                entMunicipio.cNombreMunicipio=municipio.child("cNombre").getValue()==null?"":municipio.child("cNombre").getValue().toString();
                                Log.d("DEBUG", entMunicipio.cIdMunicipio+ entMunicipio.cNombreMunicipio);
                                municipios.add(entMunicipio);
                            }
                            MunicipioService municipioService= new MunicipioService(MainActivity.this);
                            municipioService.EliminaTodos();
                            municipioService.Inserta(municipios);
                            message.what=1;
                            message.obj="¡Correcto!";
                            handler.sendMessage(message);

                        }catch (Exception ex)
                        {
                            message.what=0;
                            message.obj="Se ha presentado un error"+ ex.getMessage();
                            handler.sendMessage(message);
                            Log.e("ERROR", "Error al actualizar - "+ ex.getMessage());
                        }
                    }
                });
            }
        }).start();


    }
    public void CargaSpinner(List<Municipio> lstMunicipios)
    {
        lstMunicipiosIDS.clear();
        Log.d("DEBUG", "------- CargaSpinner--------");
        List<String> lstMunicipiosString= new ArrayList<>();
        int iPosition=0;
        for(Municipio municipio:lstMunicipios)
        {
            lstMunicipiosIDS.add(municipio.cIdMunicipio);
            lstMunicipiosString.add(municipio.cNombreMunicipio);
        }
        spinnerPueblos.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstMunicipiosString));
        if(cIdMunicipio.isEmpty())
        {
            spinnerPueblos.setSelection(0);
            AjustaMunicipio(0);
        }
        else
        {
            for(String cIdMunicipiop:lstMunicipiosIDS)
            {
                if(cIdMunicipiop.equals(cIdMunicipio))
                {
                    spinnerPueblos.setSelection(iPosition);
                }
                iPosition++;
            }

        }


    }
    public void PreparaSpinner()
    {
        Log.d("DEBUG", "------- PrepararSpinner--------");
        spinnerPueblos.setEnabled(false);
        btnActualizarMunicipios.setEnabled(false);
        Handler handler= new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                List<Municipio> lstMunicipio= new ArrayList<>();
                if(msg.what==1)
                {
                    if(msg.obj!=null)
                    {
                        lstMunicipio= (ArrayList)(msg.obj);
                    }
                    if(lstMunicipio.size()==0)
                    {
                        DescargaMunicipios();
                    }
                    else
                    {
                        CargaSpinner(lstMunicipio);
                        spinnerPueblos.setEnabled(true);
                        btnActualizarMunicipios.setEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Se ha presentado un error al cargar muncipios", Toast.LENGTH_SHORT).show();
                    spinnerPueblos.setEnabled(true);
                    btnActualizarMunicipios.setEnabled(true);
                }

            }
        };
        new Thread(() -> {
            Message message= new Message();
            try{
                List<Municipio> lstMunicipio=new ArrayList<>();
                MunicipioService municipioService= new MunicipioService(MainActivity.this);
                lstMunicipio=municipioService.obtenerTodos();
                message.what=1;
                message.obj=lstMunicipio;

            }catch(Exception ex)
            {
                Log.e("ERROR", "Se ha presentado el error" + ex.getMessage());
                message.what=0;
                message.obj=null;
            }
            handler.sendMessage(message);

        }).start();




    }
    public void ObtenerTotalMunicipios()
    {
        Log.d("DEBUG", "------- ObtenerTotalMunicipios --------");
        spinnerPueblos.setEnabled(false);
        btnActualizarMunicipios.setEnabled(false);
        Handler handler= new Handler(Looper.myLooper())
        {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==1)
                {
                    int iResultado= (Integer) msg.obj;
                    VerificarCantidadMunicipios(iResultado);
                }
                else
                {
                    btnActualizarMunicipios.setEnabled(true);
                    spinnerPueblos.setEnabled(true);
                    Utilities.MostrarMensaje(MainActivity.this, "Error", "Se ha presentado un error al actualizar, inténtalo de nuevo");
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message= new Message();
                int iMunicipiosDB=0;
                try{
                    MunicipioService municipioService= new MunicipioService(MainActivity.this);
                    iMunicipiosDB=municipioService.ContarMunicipios();
                    message.what=1;
                    message.obj=iMunicipiosDB;
                }
                catch (Exception ex)
                {
                    message.what=0;
                    message.obj=0;
                    Log.e("ERROR", "Se ha presentado el siguiente error "+ ex.getMessage());
                }
                handler.sendMessage(message);
            }
        }).start();
    }
}