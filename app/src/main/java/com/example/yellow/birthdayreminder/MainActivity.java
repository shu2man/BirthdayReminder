package com.example.yellow.birthdayreminder;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String DB_NAME="Contacts_db";
    private static final String TABLE_NAME="Contacts";
    private static final int DB_VERSION=2;

    private boolean hasPermission=true;
    private static String[] PERMISSION_CONTACTS={Manifest.permission.READ_CONTACTS};

    private MyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new MyDB(this,DB_NAME,null,DB_VERSION);

        verifyPermission(this);

        //Toast.makeText(this,"on create",Toast.LENGTH_SHORT).show();
        initListView();
    }

    public String getPhoneNumber(String name){
        String phoneNumber="";
        int count=0;
        //query(Uri uri of target,String[] Colunms need to return as Cursor,String whereClause,
        //     String[] whereArgs,String OrderBy ColunmName+"ASC/DESC"
        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);//return
        if(cursor!=null){
            while(cursor.moveToNext()){
                String contact_name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if(contact_name.equals(name)){
                    Cursor phone=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+"=?",new String[]{name},null);
                    //可能有多个号码，遍历
                    while(phone!=null&&phone.moveToNext()){
                        if(count>=1) phoneNumber+="\n";
                        phoneNumber+= phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        count++;
                    }
                    if(phone!=null) phone.close();
                    return phoneNumber;
                }
            }
        }

        if(cursor!=null) cursor.close();
        if(phoneNumber.equals("")) return "貌似你还没有Ta的联系方式哦";
        else return phoneNumber;
    }

    public void goToEdit(View view){
        Intent intent=new Intent(this,EditActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void initListView(){
        Cursor cursor=db.getAllInfo();
        String[] from={"name","birthday","gift"};
        int[] to={R.id.name,R.id.birthday,R.id.gift};
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.list_item,cursor,from,to);
        final ListView lv=(ListView)findViewById(R.id.contacts_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TextView nametv=view.findViewById(R.id.name);
                TextView birthdaytv=view.findViewById(R.id.birthday);
                TextView gifttv=view.findViewById(R.id.gift);
                String name=nametv.getText().toString();
                String birthday=birthdaytv.getText().toString();
                String gift=gifttv.getText().toString();

                dialogDetail(name,birthday,gift);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView nametv=view.findViewById(R.id.name);
                String name=nametv.getText().toString();

                dialogDelete(name);
                return true;//true 不再执行点击事件
            }
        });
    }

    public void dialogDetail(final String name, String birthday, final String gift){
        LayoutInflater factor=LayoutInflater.from(MainActivity.this);
        View dialog=factor.inflate(R.layout.dialog_layout,null);
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(MainActivity.this);
        alertdialog.setView(dialog);

        final EditText nameet=dialog.findViewById(R.id.name_edit_dialog);
        final EditText birthdayet=dialog.findViewById(R.id.birthday_edit_dialog);
        final EditText giftet=dialog.findViewById(R.id.gift_edit_dialog);
        TextView numbertv=dialog.findViewById(R.id.phone_number_dialog);
        nameet.setText(name);
        birthdayet.setText(birthday);
        giftet.setText(gift);
        numbertv.setText(getPhoneNumber(name));

        alertdialog.setPositiveButton("确认修改",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateContact(name,nameet.getText().toString(),birthdayet.getText().toString(),giftet.getText().toString());
            }
        });
        alertdialog.setNegativeButton("放弃修改",null);

        alertdialog.show();
    }
    public void dialogDelete(final String name){
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("确定将"+name+"从列表中删除吗？");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.delete(name);
                initListView();
                Toast.makeText(MainActivity.this,name+"已从列表中删除",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("取消",null);

        dialog.show();
    }

    public void updateContact(String name,String newName,String newBirthday,String newGift){
        if((!name.equals(newName))&&db.isNameDuplicated(newName)) {
            Toast.makeText(MainActivity.this,"姓名重复，无法修改",Toast.LENGTH_SHORT).show();
        }
        else{
            db.update(name,newName,newBirthday,newGift);
            initListView();
        }
    }

    public void verifyPermission(Activity activity){
        try{
            int permission= ActivityCompat.checkSelfPermission(activity,"android.permission.READ_CONTACTS");
            if(permission!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,PERMISSION_CONTACTS,1);
            }
            else hasPermission=true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permission[],int[] grantResults){
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //permission granted
        }
        else{
            //permission not set
        }
    }

}
