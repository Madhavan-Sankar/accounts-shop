package com.example.accounting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.SharedMemory;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    MyAdapter myAdapter;
    ArrayList<String> last = new ArrayList<>();
    EditText search;
    TextView title;
    Long amt;
    String date,time;
    databasehelper mdatabasehelper;
    databasehelperparty mdatabasehelperparty;
    databasehelperhistory mdatabasehelperhistory;
    public void dateandtime()
    {
        Date full = Calendar.getInstance().getTime();
        String full1 = full.toString();
        time = full1.split(" ")[3];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        date = mdformat.format(calendar.getTime());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        title=findViewById(R.id.title);
        String mystring=new String("ADD ENTRY");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        title.setText(content);
        listView=findViewById(R.id.listnames);
        search=findViewById(R.id.search);
        mdatabasehelper=new databasehelper(this);
        mdatabasehelperparty=new databasehelperparty(this);
        mdatabasehelperhistory = new databasehelperhistory(this);
        populateListView();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchArray = s.toString();
                ArrayList<String> filteredname = new ArrayList<>();
                for(String i : last){
                    if (i.toUpperCase().contains(searchArray.toUpperCase())) {
                        if(!filteredname.contains(i)){
                            filteredname.add(i);
                        }
                    }
                }
                Collections.sort(filteredname);
                myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,filteredname);
                listView.setAdapter(myAdapter);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Main2Activity.this,MainActivity.class);
        startActivity(i);
    }

    void addData(String date, String name, Long amt)
    {
        boolean res=mdatabasehelper.addData(date,name,amt);
        if(res)
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        boolean res1 = mdatabasehelperhistory.addData(date+"#"+name+"#ADDED",amt);
        if(res1)
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    private void populateListView() {
        Cursor data = mdatabasehelperparty.getData();
        while(data.moveToNext()){
            //String name = data.getString(1);//0 is column name
            last.add(data.getString(1));
        }
        Collections.sort(last);
        myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,last);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String name = adapterView.getItemAtPosition(i).toString();
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Main2Activity.this);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View v = inflater.inflate(R.layout.addamt, null);  // this line
                alertDialog2.setView(v);
                AlertDialog alertDialog = alertDialog2.create();
                alertDialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                final TextView pname = v.findViewById(R.id.pname);
                pname.setText(name);
                Button submit = v.findViewById(R.id.submit);
                final EditText amount = v.findViewById(R.id.amt);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dateandtime();
                        if(TextUtils.isEmpty(amount.getText().toString()))
                        {
                            Toast.makeText(getApplicationContext(),"Please enter the amount!!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            amt = Long.parseLong(String.valueOf(amount.getText()));
                            addData(date, name, amt);
                            Intent i = new Intent(Main2Activity.this, Main2Activity.class);
                            startActivity(i);
                        }
                    }
                });
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rlast;

        public MyAdapter(Context c, int resource,ArrayList<String> last) {
            super(c, resource,last);
            this.context = c;
            rlast=last;
        }
        @SuppressLint("WrongViewCast")
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.cardviewparty, parent, false);
            TextView name= row.findViewById(R.id.name);
            name.setText(rlast.get(position));
            return row;
        }
    }

}