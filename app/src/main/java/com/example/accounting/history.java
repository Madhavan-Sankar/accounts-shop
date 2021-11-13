package com.example.accounting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class history extends AppCompatActivity {
    private static final String TAG = "DatabaseHelper";
    databasehelperhistory mdatabasehelperhistory;
    TextView title;
    Button clear;
    private ListView mListView;
    ArrayList<String> last = new ArrayList<>();
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        title=findViewById(R.id.title);
        clear=findViewById(R.id.clear);
        String mystring=new String("TRANSACTION");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        title.setText(content);
        mListView=findViewById(R.id.listview);
        mdatabasehelperhistory = new databasehelperhistory(this);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdatabasehelperhistory.clear();
                Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();
                Intent  i = new Intent(history.this,MainActivity.class);
                startActivity(i);
            }
        });
        populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");
        Cursor data = mdatabasehelperhistory.getData();
        while(data.moveToNext()){
            String dateandname = data.getString(1);//1 is column name
            Long amount = data.getLong(2);//0 is column name
            last.add(data.getString(0) + ",/" + dateandname + ",/" + String.valueOf(amount));
        }
        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, last);
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
            View row = layoutInflater.inflate(R.layout.history_cardview, parent, false);
            TextView dateandname = row.findViewById(R.id.dateandname);
            final TextView added = row.findViewById(R.id.added);
            final TextView deleted = row.findViewById(R.id.deleted);

            // now set our resources on views
            String[] res=rlast.get(position).split(",/");
            String name[] = res[1].split("#");
            dateandname.setText(name[0]+"\n"+name[1]);

            if(name[2].equals("ADDED"))
                added.setText(res[2]);
            else if(name[2].equals("DELETED"))
                deleted.setText(res[2]);
            else {
                added.setText(res[2]+" (UPDATED) ");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.weight = 50f;
                Button button = new Button(getApplicationContext());
                button.setLayoutParams(params);
                deleted.setVisibility(View.GONE);

            }
            return row;
        }
    }

    @Override
    public void onBackPressed() {
        Intent  i = new Intent(history.this,MainActivity.class);
        startActivity(i);
    }
}