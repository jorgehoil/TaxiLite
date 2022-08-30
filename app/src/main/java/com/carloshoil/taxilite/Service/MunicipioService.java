package com.carloshoil.taxilite.Service;

import android.content.Context;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.carloshoil.taxilite.DAO.MunicipioDAO;
import com.carloshoil.taxilite.DataBase.Database;
import com.carloshoil.taxilite.Entitie.Municipio;

import java.util.List;

public class MunicipioService {

    Context context;

    public MunicipioService(Context context) {
        this.context = context;
    }

    public List<Municipio> obtenerTodos() {

        Database database = Database.getInstance(context);
        MunicipioDAO municipioDAO = database.municipioDAO();
        return municipioDAO.obtenerTodos();
    }


    public Municipio obtenerMuncipio(int iIdMunicipio) {
        Database database = Database.getInstance(context);
        MunicipioDAO municipioDAO = database.municipioDAO();
        return municipioDAO.obtenerMuncipio(iIdMunicipio);
    }

    public Municipio obtenerMuncipio(String cIdMunicipio) {
        Database database = Database.getInstance(context);
        MunicipioDAO municipioDAO = database.municipioDAO();
        return municipioDAO.obtenerMuncipio(cIdMunicipio);
    }

    public void Inserta(Municipio municipio) {
        Database database = Database.getInstance(context);
        MunicipioDAO municipioDAO = database.municipioDAO();
        municipioDAO.Inserta(municipio);
    }
    public void Inserta(List<Municipio> listMunicipios) {
        Database database=Database.getInstance(context);
        MunicipioDAO municipioDAO= database.municipioDAO();
        municipioDAO.Inserta(listMunicipios);
    }

    public void Elimina(Municipio municipio){
        Database database=Database.getInstance(context);
        MunicipioDAO municipioDAO= database.municipioDAO();
        municipioDAO.Elimina(municipio);
    }
    public void EliminaTodos(){
        Database database=Database.getInstance(context);
        MunicipioDAO municipioDAO= database.municipioDAO();
        municipioDAO.EliminarMunicipios();
    }



    public void Actualiza(Municipio municipio){
        Database database=Database.getInstance(context);
        MunicipioDAO municipioDAO= database.municipioDAO();
        municipioDAO.Actualiza(municipio);
    }
    public int ContarMunicipios()
    {
        Database database=Database.getInstance(context);
        MunicipioDAO municipioDAO= database.municipioDAO();
        return municipioDAO.contarMunicipios();
    }
}
