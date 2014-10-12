package com.valohyd.nextseries.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class OurSqliteBase extends SQLiteOpenHelper {

    private static final String TABLE_FAVORIS = "table_favoris"; // duplica du strings.xml car pas accessible ici 
    private static final String COL_ID = "id"; // idem ici
    private static final String COL_NOM_SERIE = "nom_serie";
    private static final String COL_NOTE = "note";
    private static final String COL_STATUS = "status";
    private static final String COL_BANNER = "banner";
    private static final String COL_CHAINE = "chaine";
    private static final String COL_NB_EP = "nb_ep";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_FAVORIS + 
            " (" + COL_ID + " INTEGER PRIMARY KEY, " 
            + COL_NOM_SERIE + " TEXT NOT NULL, " 
            + COL_NOTE + " TEXT,"
            + COL_STATUS + " TEXT,"
            + COL_BANNER + " TEXT,"
            + COL_CHAINE + " TEXT,"
            + COL_NB_EP + " TEXT"
            +");";

    public OurSqliteBase(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // créer la base de donnée
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // changement de version : reset de la db
        db.execSQL("DROP TABLE " + TABLE_FAVORIS + ";");
        onCreate(db);
    }

}
