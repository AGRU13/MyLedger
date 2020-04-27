package com.spareroom.myledger;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewEntry extends AppCompatActivity {
    private Spinner spinner , mode;
    private ArrayAdapter<String > adapter,adapter2;
    static EditText billNo,chalanNo,receiptNo,amount,date,name;
    private int cash=0,credit=0;
    private CheckBox pending;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDat = FirebaseDatabase.getInstance();
    private String[] items = new String[]{"CREDIT","DEBIT"};
    private String[] modes = new String[]{"CASH","CHECK"};
    private String payment = new String();
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,items);
        adapter2 = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,modes);
        spinner.setAdapter(adapter);
        mode.setAdapter(adapter2);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(dateFormat.format(calendar.getTime()));

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

        if (pending.isSelected())
            payment = "PAID";
        else payment = "PENDING";



    }

    public void confirm(View view){
        if (billNo.getText().toString().isEmpty()||chalanNo.getText().toString().isEmpty()||receiptNo.getText().toString().isEmpty()||amount.getText().toString().isEmpty()||date.getText().toString().isEmpty()){
            Toast.makeText(this, "ERROR!! FILL ALL THE ENTRIES", Toast.LENGTH_SHORT).show();
        }
        else{
            Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(billNo.getText().toString());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        Toast.makeText(NewEntry.this, "ERROR!! Bill Number already exists ", Toast.LENGTH_SHORT).show();
                    else {
                        Map<String,String> myMap = new HashMap<>();
                        myMap.put("billNo",billNo.getText().toString());
                        myMap.put("chalanNo",chalanNo.getText().toString());
                        myMap.put("receiptNo",receiptNo.getText().toString());
                        myMap.put("customerName",name.getText().toString().toUpperCase());
                        myMap.put("creditOrDebit",items[credit]);
                        myMap.put("modeOfPayment",modes[cash]);
                        myMap.put("amount",amount.getText().toString());
                        myMap.put("date",date.getText().toString());
                        myMap.put("paymentStatus",payment);
                        mDat.getReference().child(mAuth.getCurrentUser().getUid()).push().setValue(myMap);
                        startActivity(new Intent(NewEntry.this,Receipt.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,InAccount.class));
    }
}
