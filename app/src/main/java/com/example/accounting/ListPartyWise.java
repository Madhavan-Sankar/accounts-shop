package com.example.accounting;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class ListPartyWise extends AppCompatActivity {

    private static final String TAG = "DatabaseHelper";
    databasehelperparty mdatabasehelper;
    databasehelper db;
    EditText search;
    TextView title;
    private ListView mListView;
    ArrayList<String> last = new ArrayList<>();
    MyAdapter myAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listpartywise);
        title=findViewById(R.id.title);
        String mystring=new String("RECORD PARTYWISE");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        title.setText(content);
        search=findViewById(R.id.search);
        mListView = findViewById(R.id.listview);
        mdatabasehelper = new databasehelperparty(this);
        db = new databasehelper(this);
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
                myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,filteredname){
                };
                mListView.setAdapter(myAdapter);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = mListView.getItemAtPosition(i).toString();
                Bundle bundle = new Bundle();
                bundle.putString("sourceoforigin",name);
                Intent intent=new Intent(ListPartyWise.this,ListParty.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed()
    {
        Intent i = new Intent(ListPartyWise.this,MainActivity.class);
        startActivity(i);
    }
    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");
        Cursor data = mdatabasehelper.getData();
        while(data.moveToNext()){
            String name=data.getString(1);//1 is column name
            last.add(name);
        }
        Collections.sort(last);
        myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,last);
        mListView.setAdapter(myAdapter);
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
            View row = layoutInflater.inflate(R.layout.cardviewpartywise, parent, false);
            TextView name = row.findViewById(R.id.name);
            name.setText(rlast.get(position));
            TextView amount = row.findViewById(R.id.amount);
            Cursor data = db.getAmount(rlast.get(position));
            while(data.moveToNext()){
                if(data.getString(0)!=null)
                    amount.setText(data.getString(0));
            }
            return row;
        }
    }
}