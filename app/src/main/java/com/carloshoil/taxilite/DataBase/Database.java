package com.carloshoil.taxilite.DataBase;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.carloshoil.taxilite.DAO.MunicipioDAO;
import com.carloshoil.taxilite.Entitie.Municipio;

@androidx.room.Database(entities = {Municipio.class}, version= 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static final String DB_NAME="taxiliteDB.db";
    private static volatile Database instance;
    public static synchronized Database getInstance(Context context)
    {
        if(instance==null)
        {
            instance=create(context);
        }
        return  instance;
    }
    private static Database create(Context context)
    {
        return Room.databaseBuilder(context,Database.class, DB_NAME).build();
    }
    public abstract MunicipioDAO municipioDAO();

}
