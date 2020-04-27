package com.spareroom.myledger;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditDetails extends AppCompatActivity {
    private Spinner spinner , mode;
    private EditText billNo,chalanNo,receiptNo,amount,date,name;
    private ArrayAdapter<String > adapter,adapter2;
    private int cash=0,credit=0;
    private CheckBox pending;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDat = FirebaseDatabase.getInstance();
    private String[] items = new String[]{"CREDIT","DEBIT"};
    private String[] modes = new String[]{"CASH","CHECK"};
    private String payment;
    private Button confirm;
    private String key;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        spinner = findViewById(R.id.dbCd);
        mode = findViewById(R.id.paymentMethod);
        billNo = findViewById(R.id.bill);
        chalanNo = findViewById(R.id.chalanNo);
        receiptNo = findViewById(R.id.receiptNO);
        amount = findViewById(R.id.debitEdit);
        date = findViewById(R.id.dateEdit);
        pending = findViewById(R.id.checkBox);
        confirm = findViewById(R.id.confirm);
        name = findViewById(R.id.nameEdit);

        disableInput(billNo);
        
        pending.setEnabled(true);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,items);
        adapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,modes);
        spinner.setAdapter(adapter);
        mode.setAdapter(adapter2);

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");

        mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    billNo.setText(snapshot.child("billNo").getValue().toString());
                    chalanNo.setText(snapshot.child("chalanNo").getValue().toString());
                    receiptNo.setText(snapshot.child("receiptNo").getValue().toString());
                    name.setText(snapshot.child("customerName").getValue().toString());
                    date.setText(snapshot.child("date").getValue().toString());
                    amount.setText(snapshot.child("amount").getValue().toString());
                    if (snapshot.child("creditOrDebit").getValue().toString().equals("CREDIT"))
                        spinner.setSelection(0);
                    else spinner.setSelection(1);

                    if (snapshot.child("modeOfPayment").getValue().toString().equals("CASH"))
                        mode.setSelection(0);
                    else mode.setSelection(1);

                    if (snapshot.child("paymentStatus").getValue().toString().equals("PAID"))
                        pending.setChecked(true);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                credit = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cash = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


    }

    public void confirm(View view){
        if (billNo.getText().toString().isEmpty()||chalanNo.getText().toString().isEmpty()||receiptNo.getText().toString().isEmpty()||amount.getText().toString().isEmpty()||date.getText().toString().isEmpty()){
            Toast.makeText(this, "ERROR!! FILL ALL THE ENTRIES", Toast.LENGTH_SHORT).show();
        }
        else{
            if (pending.isChecked())
                payment = "PAID";
            else payment ="PENDING" ;

            final Map<String,String> myMap = new HashMap<>();
            myMap.put("billNo",billNo.getText().toString());
            myMap.put("chalanNo",chalanNo.getText().toString());
            myMap.put("receiptNo",receiptNo.getText().toString());
            myMap.put("customerName",name.getText().toString().toUpperCase());
            myMap.put("creditOrDebit",items[credit]);
            myMap.put("modeOfPayment",modes[cash]);
            myMap.put("amount",amount.getText().toString());
            myMap.put("date",date.getText().toString());
            myMap.put("paymentStatus",payment);
            mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                       snapshot.getRef().setValue(myMap);
                       recreate();
                   }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            startActivity(new Intent(EditDetails.this,Transactions.class));
        }
    }


    public void disableInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        editText.setTextIsSelectable(false);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return true;
            }
        });
        editText.setAlpha(0.4f);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,Transactions.class));
    }
}
