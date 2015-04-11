package com.frantzoe.gsb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

/**
 * Created by Lincoln on 4/6/2015.
 */

public class MyDataBase extends SQLiteOpenHelper {
    //Les valeurs seront inserees dans les tables a l'aide de la methode String.split ce qui explique les longues chaines avec pleins de virgules
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gsbdd";
    //DONNEES TABLE CLASSE
    public static final String TABLE_CLASSE = "classe";
    public static final String COL_CLASSE_ID = "_id";
    public static final String COL_CLASSE_DES = "_designation";
    public static final String COL_CLASSE_SYS = "_systeme";
    private static final String[] DONNEES_CLASSE = new String[]{
            "Antalgique,Neurologique",
            "Antiarythmiques,Cardiovasculaire",
            "Antibiotiques,Infectieux",
            "Anticoagulants,Cardiovasculaire",
            "Antihypertensseurs,Cardiovasculaire",
            "Antiviraux,Infectieux"
    };
    //DONNEES TABLE DCI
    public static final String TABLE_DCI = "dci";
    public static final String COL_DCI_ID = "_id";
    public static final String COL_DCI_DEN = "_denomination";
    public static final String COL_DCI_CLA = "_classe";
    public static final String COL_DCI_ANN = "_annee";
    public static final String[] DONNEES_DCI = new String[]{
            "Aciclovir,5,2000", "Amitriptyline,6,0",
            "Amlodipine,1,2007", "Amoxicilline,4,2007",
            "Clarithromycine,4,2000", "Dilitazem,3,2000",
            "Flecainide,3,2003", "Fluindione,2,0",
            "Ganciclovir,5,0", "Irbesartan,1,2012",
            "Linezolide,4,0", "Nefopam,6,2010",
            "Nicardipine,1,2006", "Ofloxacine,4,2003",
            "Olmesartan,1,0", "Oseltamivir,5,0",
            "Paracetamol,6,2000", "Warfarine,2,0"
    };
    //DONNEES TABLE PRINCEPS
    public static final String TABLE_PRINCEPS = "princeps";
    public static final String COL_PRINCEPS_ID = "_id";
    public static final String COL_PRINCEPS_NOM = "_nomcommercial";
    public static final String COL_PRINCEPS_DCI = "_dci";
    public static final String[] DONNEES_PRINCEPS = new String[]{
            "Acupan,12", "Alteis,15", "Amlor,3",
            "Aprovel,10", "Clamoxyl,4", "Coumadine,18",
            "Cymevan,9", "Doliprane,17", "Efferalgan,17",
            "Flecaine,7", "Laroxyl,2", "Loxen,13",
            "Oflocet,14", "Olmetec,15", "Previscan,8",
            "Tamiflu,16", "Tildiem,6", "Zeclar,5",
            "Zovirax,1", "Zyvoxid,11"
    };
    public static final String VIEW_PRRINCEPS_DCI = "medicaments";

    public MyDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //CREATION TABLE CLASSE
        database.execSQL("CREATE TABLE " + TABLE_CLASSE + "("
                + COL_CLASSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CLASSE_DES + " TEXT, "
                + COL_CLASSE_SYS + " TEXT);");
        //INSERTION TABLES CLASSE
        for (String DONNEES : DONNEES_CLASSE) {
            addClasses(database, DONNEES.split(",")[0], DONNEES.split(",")[1]);
        }
        //CREATION TABLE DCI
        database.execSQL("CREATE TABLE " + TABLE_DCI + "("
                + COL_DCI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DCI_DEN + " TEXT, "
                + COL_DCI_CLA + " INTEGER, "
                + COL_DCI_ANN + " INTEGER, "
                + "FOREIGN KEY (" + COL_DCI_CLA + ") REFERENCES " + TABLE_CLASSE + "(" + COL_CLASSE_ID + "));");
        //INSERTION TABLE DCI
        for (String DONNEES : DONNEES_DCI) {
            addDCI(database, DONNEES.split(",")[0], Integer.parseInt(DONNEES.split(",")[1]), Integer.parseInt(DONNEES.split(",")[2]));
        }
        //CREATION TABLE PRINCEPS
        database.execSQL("CREATE TABLE " + TABLE_PRINCEPS + "("
                + COL_PRINCEPS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PRINCEPS_NOM + " TEXT, "
                + COL_PRINCEPS_DCI + " INTEGER, "
                + "FOREIGN KEY (" + COL_PRINCEPS_DCI + ") REFERENCES " + TABLE_DCI + "(" + COL_DCI_ID + "));");
        //INSERTION TABLE PRINCEPS
        for (String DONNEES : DONNEES_PRINCEPS) {
            addPrinceps(database, DONNEES.split(",")[0], Integer.parseInt(DONNEES.split(",")[1]));
        }
        //CREATION VUE DE TOUS LES MEDICAMENTS
        database.execSQL(
                //Concatenation des indentifiants avec 'p' || _id pour princeps et 'd' || _id pour dci
                "CREATE VIEW "
                + VIEW_PRRINCEPS_DCI
                + " AS SELECT 'p' || "
                + COL_PRINCEPS_ID
                + " AS "
                + COL_PRINCEPS_ID
                + ", "
                + COL_PRINCEPS_NOM
                + " FROM "
                + TABLE_PRINCEPS
                + " UNION ALL SELECT 'd' || "
                + COL_DCI_ID
                + " AS "
                + COL_PRINCEPS_ID
                + ", "
                + COL_DCI_DEN
                + " AS "
                + COL_PRINCEPS_NOM
                + " FROM "
                + TABLE_DCI
                + " ORDER BY "
                + COL_PRINCEPS_NOM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Ajout d'une classe
    public void addClasses(SQLiteDatabase database, String designation, String systeme){
        ContentValues values = new ContentValues();
        values.put(COL_CLASSE_DES, designation);
        values.put(COL_CLASSE_SYS, systeme);
        database.insert(TABLE_CLASSE, null, values);
    }

    //Ajout d'un DCI
    public void addDCI(SQLiteDatabase database, String denomination, int classeid, int annnee){
        ContentValues values = new ContentValues();
        values.put(COL_DCI_DEN, denomination);
        if (annnee != 0) {values.put(COL_DCI_ANN, annnee);}
        values.put(COL_DCI_CLA, classeid);
        database.insert(TABLE_DCI, null, values);
    }

    //Ajout d'un princeps
    public void addPrinceps(SQLiteDatabase database, String nom, int dci) {
        ContentValues values = new ContentValues();
        values.put(COL_PRINCEPS_NOM, nom);
        values.put(COL_PRINCEPS_DCI, dci);
        database.insert(TABLE_PRINCEPS, null, values);
    }

    //Retourne tous les medicaments contenus dan la vue medicaments
    public Cursor allMedicaments() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + VIEW_PRRINCEPS_DCI, null);
    }

    //Retourne le princeps correspondant au nom passe en parametre
    public Princeps getPrinceps(String nom) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + TABLE_PRINCEPS
                + " WHERE "
                + COL_PRINCEPS_NOM
                + " = '" + nom + "'", null);
        cursor.moveToFirst();
        Princeps princeps = new Princeps(getDCI(cursor.getInt(cursor.getColumnIndex(COL_PRINCEPS_DCI))), cursor.getString(cursor.getColumnIndex(COL_PRINCEPS_NOM)));
        cursor.close();
        return princeps;
    }

    //Retourne la DCI correspondant à la denomination passee en parametre
    public Generique getGenerique(String denomination) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + TABLE_DCI
                + " WHERE "
                + COL_DCI_DEN + " = '" + denomination + "'", null);
        cursor.moveToFirst();
        Generique generique = new Generique(new DCI(denomination), cursor.getInt(cursor.getColumnIndex(COL_DCI_ANN)));
        cursor.close();
        return generique;
    }

    //Retourne la DCI correspondant à l'id passe en parametre
    public DCI getDCI (int id) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + TABLE_DCI
                + " WHERE "
                + COL_DCI_ID + " = " + id, null);
        cursor.moveToFirst();
        DCI dci = new DCI(cursor.getString(cursor.getColumnIndex(COL_DCI_DEN)));
        cursor.close();
        return dci;
    }

    //Retourne l'id de la DCI correspondant a la denomination passée en parametre
    public int getIdDCI (String denomination) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + TABLE_DCI
                + " WHERE "
                + COL_DCI_DEN + " = '" + denomination + "'", null);
        cursor.moveToFirst();
        int IdDCI = cursor.getInt(cursor.getColumnIndex(COL_DCI_ID));
        cursor.close();
        return IdDCI;
    }

    //Retourne l'id de la classe passée en parametre
    public int getIdClasse(String denom) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM "
                + TABLE_CLASSE
                + " WHERE "
                + COL_CLASSE_DES + " = '" + denom + "'", null);
        cursor.moveToFirst();
        int idClasse = cursor.getInt(cursor.getColumnIndex(COL_CLASSE_ID));
        cursor.close();
        return idClasse;
    }

    /*public int getNombreMedicaments(){
        SQLiteStatement sqLiteStatement = getReadableDatabase().compileStatement(
                "SELECT (SELECT COUNT(*) FROM "
                + TABLE_PRINCEPS
                + ") + (SELECT COUNT(*) FROM "
                + TABLE_DCI + ")");
        return (int) sqLiteStatement.simpleQueryForLong();
    }*/

    //Retourne la liste des DCI servant a alimenter le dialog lors de l'ajout d'un princeps
    public ArrayList<DCI> getListDCI() {
        ArrayList<DCI> dcis = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + TABLE_DCI
                + " ORDER BY "
                + COL_DCI_DEN, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dcis.add(new DCI(cursor.getString(cursor.getColumnIndex(COL_DCI_DEN))));
            cursor.moveToNext();
        }
        cursor.close();
        return dcis;
    }

    //Retourne la liste des princeps d'un medicament generique
    public String getPrincepsGenerique(String nom) {
        String result = "";
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + TABLE_PRINCEPS
                + " WHERE "
                + COL_PRINCEPS_DCI
                + " = (SELECT "
                + COL_DCI_ID
                + " FROM "
                + TABLE_DCI
                + " WHERE "
                + COL_DCI_DEN + " = '" + nom + "')", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result += cursor.getString(cursor.getColumnIndex(COL_PRINCEPS_NOM));
            if (!cursor.isLast()) {result += ", ";} else {result += ".";}
            cursor.moveToNext();
        }
        cursor.close();
        return result + "\n";
    }
    //Retourne la classe et le systeme d'une DCI
    public String getClasseDCI(String nom) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT * FROM "
                + TABLE_CLASSE
                + " WHERE "
                + COL_CLASSE_ID
                + " = (SELECT "
                + COL_DCI_CLA
                + " FROM "
                + TABLE_DCI
                + " WHERE "
                + COL_DCI_DEN + " = '" + nom + "')", null);
        cursor.moveToFirst();
        String classe = cursor.getString(cursor.getColumnIndex(COL_CLASSE_DES)) + "," + cursor.getString(cursor.getColumnIndex(COL_CLASSE_SYS));
        cursor.close();
        return classe;
    }

    //Retourne les valeurs corespondant a la chaine en cours de saisie dans la barre de recherche
    public Cursor getLesValeursFiltrees(String inputText) throws SQLException {
        Cursor cursor;
        SQLiteDatabase database = getReadableDatabase();
        if (inputText == null  ||  inputText.length () == 0)  {
            cursor = database.query(VIEW_PRRINCEPS_DCI, new String[]{COL_PRINCEPS_ID, COL_PRINCEPS_NOM}, null, null, null, null, null);
        } else {
            cursor = database.query(true, VIEW_PRRINCEPS_DCI, new String[] {COL_PRINCEPS_ID, COL_PRINCEPS_NOM},
                    COL_PRINCEPS_NOM + " LIKE '%" + inputText + "%'", null, null, null, null, null);
        }
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
