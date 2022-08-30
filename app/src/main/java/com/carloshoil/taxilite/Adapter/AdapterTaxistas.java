package com.carloshoil.taxilite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carloshoil.taxilite.Entitie.Taxista;
import com.carloshoil.taxilite.Global.Utilities;
import com.carloshoil.taxilite.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class AdapterTaxistas extends RecyclerView.Adapter<AdapterTaxistas.ViewHolder> {
    Context context;
    List<Taxista> lstTaxista;
    DatabaseReference databaseReference;
    String cIdMunicipio;
    public AdapterTaxistas(Context context, List<Taxista> lstTaxista, String cIdMunicipio)
    {
        this.context=context;
        this.lstTaxista=lstTaxista;
        this.cIdMunicipio= cIdMunicipio;
        databaseReference=FirebaseDatabase.getInstance().getReference();
    }
    @NonNull
    @Override
    public AdapterTaxistas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_taxista,parent, false);
        return new AdapterTaxistas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTaxistas.ViewHolder holder, int position) {

        Taxista entTaxista= lstTaxista.get(position);
        if(entTaxista!=null)
        {
            holder.tvNombreTaxista.setText(entTaxista.cNombre);
            holder.btnLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Llamar(entTaxista);
                }
            });
            holder.btnMsj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   EnviarMensaje(entTaxista);
                }
            });
            Log.d("DEBUG", entTaxista.cUrlImage);
            if(!entTaxista.cUrlImage.isEmpty())
            {
                Glide.with(context)
                        .load(entTaxista.cUrlImage)
                        .into(holder.imFotoPerfil);
            }
            else
            {
                holder.imFotoPerfil.setImageDrawable(context.getResources().getDrawable(R.drawable.imgavatar));
            }



        }
    }
    private void Llamar(Taxista entTaxista)
    {
        if(Utilities.VerificaInternet(context))
        {
            HashMap<String, Object> updates= new HashMap<>();
            databaseReference.child("taxis/available"+ cIdMunicipio+"/"+entTaxista.cLLave+"/iViajes").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                    if(currentData.getValue()!=null)
                    {
                        return Transaction.success(currentData);
                    }
                    return  Transaction.abort();
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if(committed)
                    {
                        updates.put("taxis/available"+ cIdMunicipio+"/"+entTaxista.cLLave+"/iViajes", ServerValue.increment(1));
                        updates.put("taxistas/"+entTaxista.cLLave+"/iViajes", ServerValue.increment(1));
                        databaseReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + entTaxista.cTelefono));
                                if (intent.resolveActivity(context.getPackageManager()) != null) {
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                    else
                    {
                        Utilities.MostrarMensaje(context,"Usuario no disponible", "El usuario seleccionado ya no está disponible");
                    }
                }
            });
        }
        else
        {
            Toast.makeText(context, "Verifica tu conexión a internet", Toast.LENGTH_SHORT).show();
        }



    }
    private void EnviarMensaje(Taxista entTaxista)
    {
        if(Utilities.VerificaInternet(context))
        {
            HashMap<String, Object> updates= new HashMap<>();
            databaseReference.child("taxis/available"+ cIdMunicipio+"/"+entTaxista.cLLave+"/iViajes").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                    if(currentData.getValue()!=null)
                    {
                        return Transaction.success(currentData);
                    }
                    return  Transaction.abort();
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if(committed)
                    {
                        updates.put("taxis/available"+ cIdMunicipio+"/"+entTaxista.cLLave+"/iViajes", ServerValue.increment(1));
                        updates.put("taxistas/"+entTaxista.cLLave+"/iViajes", ServerValue.increment(1));
                        databaseReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String uri = "whatsapp://send?phone=" +"+52"+ entTaxista.cTelefono + "&text=" + "Por favor, ¿puedes venir por mí?, estoy en ";
                                intent.setData(Uri.parse(uri));
                                try{
                                    context.startActivity(intent);
                                }
                                catch(Exception ex)
                                {
                                    Utilities.MostrarMensaje(context, "Error", "Se ha presentado un error, verifica que tengas instalado WhatsApp");
                                }
                            }
                        });
                    }
                    else
                    {
                        Utilities.MostrarMensaje(context,"Usuario no disponible", "El usuario seleccionado ya no está disponible");
                    }
                }
            });
        }
        else
        {
            Toast.makeText(context, "Verifica tu conexión a internet", Toast.LENGTH_SHORT).show();
        }

    }

    public void EliminarTaxista(String cKey)
    {
        int iPosition=0;
        int iEliminar=0;
        boolean lEliminar=false;
        for(Taxista entTaxista: lstTaxista)
        {
            if(entTaxista.cLLave.equals(cKey))
            {
                iEliminar=iPosition;
                lEliminar=true;
            }
            else
            {
                iPosition++;
            }

        }
        if(lEliminar)
        {
            lstTaxista.remove(iEliminar);
            notifyItemRemoved(iEliminar);
        }

    }
    public void AgregarTaxista(Taxista entTaxista)
    {
        int iPosition=0;
        int iPositionAgregado=0;
        boolean lExiste=false;
        for(Taxista taxista: lstTaxista)
        {
            if(taxista.cLLave.equals(entTaxista.cLLave))
            {
                lExiste=true;
                iPositionAgregado=iPosition;
            }
            iPosition++;
        }
        if(lExiste)
        {
            Log.d("DEBUG", "lExiste" +lExiste+"__"+ entTaxista.cNombre);
            lstTaxista.remove(iPositionAgregado);
            lstTaxista.add(iPositionAgregado,entTaxista);
            notifyItemChanged(iPositionAgregado);
        }
        else
        {
            Log.d("DEBUG", "lExiste" +lExiste+"__"+ entTaxista.cNombre);
            lstTaxista.add(lstTaxista.size(),entTaxista);
            notifyItemInserted(lstTaxista.size());
        }

    }
    public void LimpiarLista()
    {
        lstTaxista.clear();
        notifyDataSetChanged();
    }
    public int ObtenerTotal()
    {
        return lstTaxista.size();
    }


    @Override
    public int getItemCount() {
        return lstTaxista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imFotoPerfil;
        Button btnLlamar;
        Button btnMsj;
        TextView tvNombreTaxista;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imFotoPerfil=itemView.findViewById(R.id.imFotoPerfil);
            btnLlamar=itemView.findViewById(R.id.btnLlamar);
            btnMsj=itemView.findViewById(R.id.btnMsj);
            tvNombreTaxista=itemView.findViewById(R.id.tvNombreTaxista);
        }
    }
    //Chumayel, Teabo, Cantamayec, Mani, Tipikal, Mama, Tekit, Chapab, Akil
}
