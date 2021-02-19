package com.example.accounting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditParty extends AppCompatActivity {
    EditText name;
    Button edit,delete;
    private String selectedName;
    private int selectedId;
    databasehelperparty mdatabasehelperparty;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editparty);
        name=findViewById(R.id.name);
        edit=findViewById(R.id.edit);
        delete=findViewById(R.id.delete);
        mdatabasehelperparty=new databasehelperparty(this);
        Intent recieveIntent = getIntent();
        selectedId=recieveIntent.getIntExtra("id",-1);
        selectedName=recieveIntent.getStringExtra("name");
        name.setText(selectedName);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updated = name.getText().toString().trim();
                if(updated.equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(), "Please enter a valid name!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mdatabasehelperparty.updateName(updated,selectedId,selectedName);
                    Toast.makeText(getApplicationContext(),"Updated Successfully!!",Toast.LENGTH_SHORT).show();
                    Intent i =new Intent(EditParty.this,AddNewEntry.class);
                    startActivity(i);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdatabasehelperparty.deleteName(selectedId,selectedName);
                Toast.makeText(getApplicationContext(),"Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                Intent i =new Intent(EditParty.this,AddNewEntry.class);
                startActivity(i);
            }
        });
    }
}
