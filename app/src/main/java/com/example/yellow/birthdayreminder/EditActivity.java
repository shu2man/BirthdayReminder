package com.example.yellow.birthdayreminder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Yellow on 2017-12-10.
 */

public class EditActivity extends Activity {
    private static final String DB_NAME="Contacts_db";
    private static final String TABLE_NAME="Contacts";
    private static final int DB_VERSION=2;

    private MyDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db=new MyDB(this,DB_NAME,null,DB_VERSION);
    }

    public void checkAndSave(View view){
        TextView ntv=(TextView)findViewById(R.id.name_edit);
        EditText bet=findViewById(R.id.birthday_edit);
        EditText get=findViewById(R.id.gift_edit);

        String name=ntv.getText().toString();
        String birthday=bet.getText().toString();
        String gift=get.getText().toString();

        db.insert(name,birthday,gift);
    }
}
