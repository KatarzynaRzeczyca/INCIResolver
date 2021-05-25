package com.example.inciresolver.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBProvider extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "INCI.db";
    public static final String INCI_TABLE_NAME = "ingredients";
    //public static final String INCI_COLUMN_ID = "id";
    public static final String INCI_COLUMN_NAME = "name";
    public static final String INCI_COLUMN_DESCRIPTION = "description";
    public static final String INCI_COLUMN_QUALITY = "quality";

    public DBProvider(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text, description text, quality text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertIngredient(String name, String description, String quality) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("description", description);
        contentValues.put("quality", quality);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public Ingredient getIngredientByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where name='"+name+"'", null );
        if(res.getCount() < 1){
            return new Ingredient();
        }
        res.moveToFirst();
        String inciName = res.getString(res.getColumnIndex(DBProvider.INCI_COLUMN_NAME));
        String inciDescription = res.getString(res.getColumnIndex(DBProvider.INCI_COLUMN_DESCRIPTION));
        String inciQuality = res.getString(res.getColumnIndex(DBProvider.INCI_COLUMN_QUALITY));
        Ingredient ingredient = new Ingredient(inciName, inciDescription, inciQuality);
        return ingredient;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, INCI_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String type, String description, String quality) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("description", description);
        contentValues.put("quality", quality);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(INCI_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
