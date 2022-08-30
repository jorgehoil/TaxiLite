package com.carloshoil.taxilite.Global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.carloshoil.taxilite.R;

public class Utilities {
    public static void GuardarPreferencias(String cClave, String cValor, Context context)
    {
        SharedPreferences sharedPreferences= context.getSharedPreferences(context.getString(R.string.name_filepreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(cClave, cValor);
        editor.commit();
    }
    public static String RecuperaPreferencia(String cClave, Context context)
    {
        String cRespuesta;
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getString(R.string.name_filepreferences), Context.MODE_PRIVATE);
        cRespuesta=sharedPreferences.getString(cClave, "");
        return cRespuesta;
    }
    public static boolean VerificaInternet(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public static void MostrarMensaje(Context context, String cTitulo, String cMensaje)
    {
        AlertDialog.Builder alert= new AlertDialog.Builder(context);
        alert.setTitle(cTitulo);
        alert.setMessage(cMensaje);
        alert.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

}


