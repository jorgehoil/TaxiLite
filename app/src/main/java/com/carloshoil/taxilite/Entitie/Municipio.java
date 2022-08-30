package com.carloshoil.taxilite.Entitie;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Municipio {
    @PrimaryKey(autoGenerate = true)
    public int iIdMunicipio;

    @ColumnInfo(name= "cIdMunicipio")
    public String cIdMunicipio;

    @ColumnInfo(name="cNombreMunicipio")
    public String cNombreMunicipio;



}
