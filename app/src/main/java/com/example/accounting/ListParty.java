package com.example.accounting;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class ListParty extends AppCompatActivity {

    private static final String TAG = "DatabaseHelper";
    databasehelper mdatabasehelper;
    databasehelperhistory mdatabasehelperhistory;
    EditText search;
    TextView total;
    String sourceoforigin;
    TextView title;
    private ListView mListView;
    ArrayList<String> last = new ArrayList<>();
    MyAdapter myAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listparty);
        title=findViewById(R.id.title);
        String mystring=new String("RECORDS");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        title.setText(content);
        Bundle bundle = getIntent().getExtras();
        sourceoforigin = bundle.getString("sourceoforigin");
        search=findViewById(R.id.search);
        total = findViewById(R.id.total);
        mListView =  findViewById(R.id.listview);
        mdatabasehelper = new databasehelper(this);
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
                    if(sourceoforigin.equalsIgnoreCase("mainactivity") || i.toUpperCase().contains(sourceoforigin.toUpperCase())) {
                        if (i.toUpperCase().contains(searchArray.toUpperCase())) {
                            if (!filteredname.contains(i)) {
                                filteredname.add(i);
                            }
                        }
                    }
                }
                int totalsum =0;
                for(String i:filteredname)
                {
                    String foramt[]=i.split(",/");
                    totalsum+=Integer.parseInt(foramt[foramt.length-1]);
                }
                total.setText(String.valueOf(totalsum));
                myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,filteredname){
                };
                mListView.setAdapter(myAdapter);
            }
        });
    }

    public void onBackPressed()
    {
        if(sourceoforigin.equalsIgnoreCase("mainactivity")) {
            Intent i = new Intent(ListParty.this, MainActivity.class);
            startActivity(i);
        }
        else
        {
            Intent i = new Intent(ListParty.this, ListPartyWise.class);
            startActivity(i);
        }
    }
    private void populateListView() {
        int totalsum=0;
        Log.d(TAG, "populateListView: Displaying data in the ListView.");
        final Cursor data = mdatabasehelper.getData();
        while(data.moveToNext()){
            String name = data.getString(2);//1 is column name
            if(sourceoforigin.equalsIgnoreCase("mainactivity") || name.equalsIgnoreCase(sourceoforigin)) {
                String date = data.getString(1);//0 is column name
                Long amount = data.getLong(3);//2 is column name
                last.add(data.getString(0) + ",/" + date + ",/" + name + ",/" + String.valueOf(amount));
                totalsum += Integer.parseInt(String.valueOf(amount));
            }
        }
        total.setText(String.valueOf(totalsum));
        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, last);
        myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,last);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String ret=adapterView.getItemAtPosition(i).toString();

                final String[] s = ret.split(",/");
                final Integer id=Integer.parseInt(s[0]);
                final String fname=s[2];

                android.app.AlertDialog.Builder alertDialog2 = new android.app.AlertDialog.Builder(ListParty.this);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View v = inflater.inflate(R.layout.editamount, null);  // this line
                alertDialog2.setView(v);
                AlertDialog alertDialog = alertDialog2.create();
                alertDialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                final TextView name = v.findViewById(R.id.pname);
                name.setText(fname);
                final EditText amt = v.findViewById(R.id.amt);
                amt.setText(s[3]);
                Button update = v.findViewById(R.id.update);
                Button delete = v.findViewById(R.id.delete);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String amount = amt.getText().toString();
                        if(amt.equals(""))
                        {
                            Toast.makeText(getApplicationContext(), "Please Enter a valid amount!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mdatabasehelper.updateAmount(amount,id);
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                            String date = mdformat.format(calendar.getTime());
                            boolean res1 = mdatabasehelperhistory.addData(date+"#"+name.getText().toString()+"#UPDATED",Long.parseLong(amount));
                            if(res1)
                                Toast.makeText(getApplicationContext(),"Updated "+name.getText()+" Successfully!!",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString("sourceoforigin",sourceoforigin);
                            Intent i=new Intent(ListParty.this,ListParty.class);
                            i.putExtras(bundle);
                            startActivity(i);
                        }
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mdatabasehelper.deleteName(id,name.getText().toString());
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
                        String date = mdformat.format(calendar.getTime());
                        boolean res1 = mdatabasehelperhistory.addData(date+"#"+name.getText().toString()+"#DELETED",Long.parseLong(s[3]));
                        if(res1)
                            Toast.makeText(getApplicationContext(),"Deleted "+name.getText()+" Successfully!!",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("sourceoforigin",sourceoforigin);
                        Intent i=new Intent(ListParty.this,ListParty.class);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
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
            View row = layoutInflater.inflate(R.layout.cardview, parent, false);
            TextView date = row.findViewById(R.id.date);
            final TextView name = row.findViewById(R.id.name);
            final TextView amount = row.findViewById(R.id.amount);

            // now set our resources on views
            String[] res=rlast.get(position).split(",/");
            date.setText(res[1]);
            name.setText(res[2]);
            amount.setText(res[3]);
            return row;
        }
    }
}