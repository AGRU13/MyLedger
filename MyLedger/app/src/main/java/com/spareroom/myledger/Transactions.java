package com.spareroom.myledger;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Transactions extends AppCompatActivity {

    private ArrayList<ListInfo> transactionsList = new ArrayList<>(10);
    private RecyclerView recyclerView;
    private Spinner search;
    private EditText searchEdit;
    private ArrayAdapter<String> arrayAdapter;
    private AdView mAdView;
    private Button searchSelected;
    private String[] options = new String[]{"Select ","Search By Name","Search By Bill Number","Search By Date" };
    private ListAdapter listAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDat = FirebaseDatabase.getInstance();
    private int position,ss;
    private ListInfo listInfo = new ListInfo("Nothing To Show","","","","");

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        search = findViewById(R.id.searchOptions);
        recyclerView = findViewById(R.id.pendingList);
        searchEdit = findViewById(R.id.searchEdit);
        searchSelected = findViewById(R.id.searchSelected);
        listAdapter = new ListAdapter(this,transactionsList);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        LinearLayoutManager l = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),l.getOrientation());
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(l);
        recyclerView.setAdapter(listAdapter);
        registerForContextMenu(recyclerView);

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,options);
        search.setAdapter(arrayAdapter);

        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).limitToLast(20);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                search.setSelection(0);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo,amount,date,credit,name);
                    transactionsList.add(ls);
                }
                if (transactionsList.isEmpty())
                    transactionsList.add(listInfo);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ss=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        searchSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ss){
                    case 0:
                        search();
                        break;

                    case 1:
                        searchByName();
                        break;

                    case 2:
                        searchByBill();
                        break;

                    case 3:
                        searchByDate();
                        break;

                }
            }
        });

        listAdapter.setOnItemClickListener(new ListAdapter.ClickListener() {
            @Override
            public void onItemLongClick(int position, View v) {
                Transactions.this.position = position;
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.open)
            startActivity(new Intent(this,ShowDetails.class).putExtra("key",transactionsList.get(position).billRow));
        else if (item.getItemId()==R.id.edit)
            startActivity(new Intent(this,EditDetails.class).putExtra("key",transactionsList.get(position).billRow));
        else if (item.getItemId()==R.id.delete)
            deleteTrans(transactionsList.get(position).billRow);
        return false;
    }

    public void deleteTrans(final String  billNo){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(billNo).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren())
                            snapshot.getRef().removeValue();
                        transactionsList.clear();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,InAccount.class));
    }

    public void searchByName(){
        transactionsList.clear();
        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("customerName").equalTo(searchEdit.getText().toString().toUpperCase());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo,amount,date,credit,name);
                    transactionsList.add(ls);
                }
                if (transactionsList.isEmpty())
                    transactionsList.add(listInfo);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void searchByBill(){
        transactionsList.clear();
        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(searchEdit.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren() ){
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo,amount,date,credit,name);
                    transactionsList.add(ls);
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void searchByDate(){
        transactionsList.clear();
        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("date").equalTo(searchEdit.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo,amount,date,credit,name);
                    transactionsList.add(ls);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void search(){
        transactionsList.clear();
        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).limitToLast(20);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                search.setSelection(0);
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo,amount,date,credit,name);
                    transactionsList.add(ls);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
