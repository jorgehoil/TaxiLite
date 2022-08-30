package com.carloshoil.taxilite.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.carloshoil.taxilite.Entitie.Municipio;

import java.util.List;

@Dao
public interface MunicipioDAO {
    @Query("SELECT*FROM municipio")
    List<Municipio> obtenerTodos();

    @Query("SELECT*FROM municipio WHERE iIdMunicipio =:arg0")
    Municipio obtenerMuncipio(int arg0);

    @Query("SELECT*FROM municipio WHERE cIdMunicipio =:cIdMunicipio")
    Municipio obtenerMuncipio(String cIdMunicipio);

    @Query("DELETE FROM municipio")
    public abstract void EliminarMunicipios();

    @Insert
    void Inserta(Municipio ... municipio);

    @Insert
    public abstract void Inserta(List<Municipio> list);
    @Delete
    void Elimina(Municipio municipio);

    @Update
    void Actualiza(Municipio municipio);

    @Query("SELECT COUNT(*) FROM Municipio")
     int contarMunicipios();





}
