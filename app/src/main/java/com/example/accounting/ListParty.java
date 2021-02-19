package com.example.accounting;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListParty extends AppCompatActivity {

    private static final String TAG = "DatabaseHelper";
    databasehelper mdatabasehelper;
    EditText search;
    TextView total;
    String sourceoforigin;
    private ListView mListView;
    ArrayList<String> last = new ArrayList<>();
    MyAdapter myAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listparty);
        Bundle bundle = getIntent().getExtras();
        sourceoforigin = bundle.getString("sourceoforigin");
        search=findViewById(R.id.search);
        total = findViewById(R.id.total);
        mListView = (ListView) findViewById(R.id.listview);
        mdatabasehelper = new databasehelper(this);
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
                    if(sourceoforigin.equalsIgnoreCase("mainactivity") || i.toLowerCase().contains(sourceoforigin.toLowerCase())) {
                        if (i.toLowerCase().contains(searchArray.toLowerCase())) {
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
        Cursor data = mdatabasehelper.getData();
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
                String name = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),"Hii"+name,Toast.LENGTH_SHORT).show();
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
            ImageButton delete= row.findViewById(R.id.delete);
            TextView date = row.findViewById(R.id.date);
            TextView name = row.findViewById(R.id.name);
            final TextView amount = row.findViewById(R.id.amount);

            // now set our resources on views
            String[] res=rlast.get(position).split(",/");
            final int delid = Integer.parseInt(res[0]);
            final String delname;
            date.setText(res[1]);
            delname = res[2];
            name.setText(res[2]);
            amount.setText(res[3]);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(ListParty.this);
                        alertDialog2.setTitle("Confirm Delete...");
                        alertDialog2.setMessage("Delete "+delname+" with amount Rs. "+ amount.getText() +"?..");
                        alertDialog2.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(),"Deleted "+delname+" Successfully!!",Toast.LENGTH_SHORT).show();
                                        mdatabasehelper.deleteName(delid,delname);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("sourceoforigin",sourceoforigin);
                                        Intent i=new Intent(ListParty.this,ListParty.class);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                    }
                        });
                        alertDialog2.setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }});
                    alertDialog2.show();
                }
            });
            return row;
        }
    }
}