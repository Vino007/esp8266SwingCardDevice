package com.example.vino.learnsqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    private Button btnCreatePerson;
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCreatePerson= (Button) findViewById(R.id.btnCreatePerson);
        dbHelper=new MyDatabaseHelper(this,"person.db",null,1);
        btnCreatePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("name","zhangsan");
                values.put("age",22);

                db.insert("person",null,values);
            }
        });
    }


}
