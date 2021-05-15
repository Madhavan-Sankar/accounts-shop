package com.example.accounting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class AddNewEntry extends AppCompatActivity {

    Button b;
    EditText text,search;
    ListView mListView;
    ArrayList<String> last = new ArrayList<>();
    databasehelperparty mdatabasehelperparty;
    MyAdapter myAdapter;
    private static final String TAG = "DatabaseHelperParty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        b = findViewById(R.id.submit1);
        text = findViewById(R.id.partyname);
        search=findViewById(R.id.search);
        mListView=findViewById(R.id.listnames);
        mdatabasehelperparty = new databasehelperparty(this);
        populateListView();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=text.getText().toString().trim().toLowerCase();
                ArrayList<String> removeduplicate = new ArrayList<>();
                Cursor data = mdatabasehelperparty.getData();
                while(data.moveToNext()){
                    removeduplicate.add(data.getString(1));
                }
                if(removeduplicate.contains(name))
                {
                    Toast.makeText(AddNewEntry.this, "Duplicate Entry!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    addData(name);
                    Intent i = new Intent(AddNewEntry.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });
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
                    if (i.toLowerCase().contains(searchArray.toLowerCase())) {
                        if(!filteredname.contains(i)){
                            filteredname.add(i);
                        }
                    }
                }
                Collections.sort(filteredname);
                myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,filteredname);
                mListView.setAdapter(myAdapter);
            }
        });
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");
        Cursor data = mdatabasehelperparty.getData();
        while(data.moveToNext()){
            //String name = data.getString(1);//0 is column name
            last.add(data.getString(1));
        }
        Collections.sort(last);
        myAdapter = new MyAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,last);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String name = adapterView.getItemAtPosition(i).toString();
                Cursor data = mdatabasehelperparty.getItemID(name);
                int ItemId=-1;
                while(data.moveToNext())
                {
                    ItemId=data.getInt(0);
                }
                if(ItemId>0)
                {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(AddNewEntry.this);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View v = inflater.inflate(R.layout.editparty, null);  // this line
                    alertDialog2.setView(v);
                    AlertDialog alertDialog = alertDialog2.create();
                    alertDialog.show();
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    final EditText namepopup = v.findViewById(R.id.name);
                    Button edit = v.findViewById(R.id.edit);
                    Button delete = v.findViewById(R.id.delete);
                    namepopup.setText(name);
                    final int finalItemId = ItemId;
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String updated = namepopup.getText().toString().trim();


                            ArrayList<String> removeduplicate = new ArrayList<>();
                            Cursor data = mdatabasehelperparty.getData();
                            while(data.moveToNext()){
                                String dupname=data.getString(1);
                                if (!dupname.equalsIgnoreCase(name)) {
                                    removeduplicate.add(dupname);
                                }
                            }


                            if(updated.equalsIgnoreCase(""))
                            {
                                Toast.makeText(getApplicationContext(), "Please enter a valid name!!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if(removeduplicate.contains(updated))
                                {
                                    Toast.makeText(getApplicationContext(), "Name Already Exists!!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mdatabasehelperparty.updateName(updated, finalItemId,name);
                                    Toast.makeText(getApplicationContext(),"Updated Successfully!!",Toast.LENGTH_SHORT).show();
                                    Intent i =new Intent(AddNewEntry.this,AddNewEntry.class);
                                    startActivity(i);
                                }
                            }
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mdatabasehelperparty.deleteName(finalItemId,name);
                            Toast.makeText(getApplicationContext(),"Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                            Intent i =new Intent(AddNewEntry.this,AddNewEntry.class);
                            startActivity(i);
                        }
                    });
                    alertDialog.getWindow().setAttributes(layoutParams);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Failed to find ID!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Cursor data = mdatabasehelperparty.getItemID(name);
                int ItemId=-1;
                while(data.moveToNext())
                {
                    ItemId=data.getInt(0);
                }
                if(ItemId>0)
                {
                    Intent ii =new Intent(AddNewEntry.this,EditParty.class);
                    ii.putExtra("id",ItemId);
                    ii.putExtra("name",name);
                    startActivity(ii);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Failed to find ID!!",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AddNewEntry.this,MainActivity.class);
        startActivity(i);
    }

    void addData(String name)
    {
        boolean res=mdatabasehelperparty.addData(name);
        if(res)
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
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
