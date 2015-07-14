package com.example.vino.learnsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Joker on 2015/6/26.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_PERSON="create table person( " +
            "id INTEGER primary key autoincrement," +
            "name varchar(20)," +
            "age int," +
            "gender varchar(10))";

    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PERSON);
        Toast.makeText(mContext,"create person",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
