package com.example.accounting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button displaylist,entry,party,backup,restore,partywisedisplay;
    TextView amount;
    String date,time;
    private static final int wesc=1;
    String partyname="partylist.csv";
    String transactions="transactions.csv";
    databasehelper mdatabasehelper;
    databasehelperparty mdatabasehelperparty;
    int total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateandtime();
        amount=findViewById(R.id.finalamt);
        displaylist=findViewById(R.id.display);
        partywisedisplay=findViewById(R.id.displaypartywise);
        entry=findViewById(R.id.entry);
        party=findViewById(R.id.party);
        backup=findViewById(R.id.backup);
        restore=findViewById(R.id.restore);
        //restore.setVisibility(View.GONE);        //This is for hiding the restore button for preventing Data Redundancy
        //backup.setVisibility(View.GONE)
        mdatabasehelper=new databasehelper(this);
        mdatabasehelperparty = new databasehelperparty(this);
        total = populateListView();
        amount.setText(String.valueOf(total));
        //display full list
        displaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("sourceoforigin","mainactivity");
                Intent i=new Intent(MainActivity.this,ListParty.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        //display party wise list
        partywisedisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,ListPartyWise.class);
                startActivity(i);
            }
        });
        //add / edit party
        party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,AddNewEntry.class);
                startActivity(i);
            }
        });
        //create entry
        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(i);
            }
        });
        //backup data
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    backupData();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                }
            }
        });
        //restore data
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    restoreData();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
        }
        else
        {

        }
    }

    public void onBackPressed()
    {

    }
    private void backupData(){
        //for party names
        try {
            StringBuilder build = new StringBuilder();
            Cursor data1 = mdatabasehelperparty.getData();
            while(data1.moveToNext()){
                //String name = data.getString(1);//0 is column name
                build.append(data1.getString(1)+"\n");
            }
            File dir = new File(Environment.getExternalStorageDirectory()+"/Accounting Sales");
            dir.mkdirs();
            String filedir=dir.toString()+"/"+partyname;
            FileWriter fw = new FileWriter(filedir);
            BufferedWriter bw = new BufferedWriter(fw);
            Toast.makeText(getApplicationContext(),"Saved PartyNames",Toast.LENGTH_SHORT).show();
            bw.write(String.valueOf(build));
            bw.close();
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //for entries
        try {
            StringBuilder build1 = new StringBuilder();
            Cursor data2 = mdatabasehelper.getData();
            while(data2.moveToNext()){
                String date = data2.getString(1);//0 is column name
                String name=data2.getString(2);//1 is column name
                Long amount=data2.getLong(3);//2 is column name
                build1.append(date+","+name+","+String.valueOf(amount)+"\n");
            }
            File dir = new File(Environment.getExternalStorageDirectory()+"/Accounting Sales");
            dir.mkdirs();
            String filedir=dir.toString()+"/"+transactions;
            FileWriter fw = new FileWriter(filedir);
            BufferedWriter bw = new BufferedWriter(fw);
            Toast.makeText(getApplicationContext(),"Saved Transactions",Toast.LENGTH_SHORT).show();
            bw.write(String.valueOf(build1));
            bw.close();
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void restoreData()
    {
        //for partynames
        try {

            File dir = new File(Environment.getExternalStorageDirectory() + "/Accounting Sales");
            String filedir = dir.toString() + "/" + partyname;
            FileReader fr = new FileReader(filedir);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sbforpartyname = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sbforpartyname.append(line+",");
            }
            br.close();
            String[] lines = sbforpartyname.toString().split(",");
            for(String s: lines){
                ArrayList<String> removeduplicate = new ArrayList<>();
                Cursor data = mdatabasehelperparty.getData();
                while(data.moveToNext()){
                    removeduplicate.add(data.getString(1));
                }
                if(removeduplicate.contains(s))
                {
                }
                else {
                    mdatabasehelperparty.addData(s);
                }
            }
            Toast.makeText(getApplicationContext(), "Retrieved Party List", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //for entries
        try {

            File dir = new File(Environment.getExternalStorageDirectory() + "/Accounting Sales");
            String filedir = dir.toString() + "/" + transactions;
            FileReader fr = new FileReader(filedir);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sbfortransactions = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sbfortransactions.append(line+"//");
            }
            br.close();
            String[] lines = sbfortransactions.toString().split("//");
            for(String s: lines){
                String[] inputsplit = s.split(",");
                mdatabasehelper.addData(inputsplit[0],inputsplit[1],Long.valueOf(inputsplit[2]));
            }
            Toast.makeText(getApplicationContext(), "Retrieved Transactions List", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Intent i = new Intent(MainActivity.this,MainActivity.class);
        startActivity(i);
    }

    private int populateListView() {
        Cursor data = mdatabasehelper.getData();
        while (data.moveToNext()) {
            total+=data.getLong(3);//1 is column name
        }
        return total;
    }
    public void dateandtime()
    {
        Date full = Calendar.getInstance().getTime();
        String full1 = full.toString();
        time = full1.split(" ")[3];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        date = mdformat.format(calendar.getTime());
    }
}