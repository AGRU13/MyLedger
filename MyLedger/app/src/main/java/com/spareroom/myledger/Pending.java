package com.spareroom.myledger;

import android.content.Intent;
import android.os.Bundle;
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

public class Pending extends AppCompatActivity {

    private ArrayList<ListInfo> pendingList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private Spinner search;
    private EditText searchEdit;
    private ArrayAdapter<String> arrayAdapter;
    private Button searchSelected;
    private AdView mAdView;
    private String[] options = new String[]{"Select ", "Search By Name", "Search By Bill Number", "Search By Date"};
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDat = FirebaseDatabase.getInstance();
    private int position, ss;
    private ListInfo listInfo = new ListInfo("No Pending Transactions", "", "", "", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        search = findViewById(R.id.searchOptions);
        recyclerView = findViewById(R.id.pendingList);
        searchEdit = findViewById(R.id.searchEdit);
        searchSelected = findViewById(R.id.searchSelected);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        listAdapter = new ListAdapter(this, pendingList);
        LinearLayoutManager l = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), l.getOrientation());
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(l);
        recyclerView.setAdapter(listAdapter);
        registerForContextMenu(recyclerView);


        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        search.setAdapter(arrayAdapter);

        mDat.getReference().child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String payment = snapshot.child("paymentStatus").getValue().toString();
                    if (payment.equals("PENDING")) {
                        String billNo = snapshot.child("billNo").getValue().toString();
                        String amount = snapshot.child("amount").getValue().toString();
                        String date = snapshot.child("date").getValue().toString();
                        String credit = snapshot.child("creditOrDebit").getValue().toString();
                        String name = snapshot.child("customerName").getValue().toString();
                        ListInfo ls = new ListInfo(billNo, amount, date, credit, name);
                        pendingList.add(ls);
                    }
                }
                if (pendingList.isEmpty())
                    pendingList.add(listInfo);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listAdapter.setOnItemClickListener(new ListAdapter.ClickListener() {
            @Override
            public void onItemLongClick(int position, View v) {
                Pending.this.position = position;
            }
        });


        search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ss = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (ss) {
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

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Query paymentQuery = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(pendingList.get(position).billRow);

        paymentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot paymentSnapshot : dataSnapshot.getChildren()) {
                    pendingList.clear();
                    paymentSnapshot.getRef().child("paymentStatus").setValue("PAID");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, InAccount.class));
    }

    public void searchByName() {
        pendingList.clear();
        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("customerName").equalTo(searchEdit.getText().toString().toUpperCase());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo, amount, date, credit, name);
                    pendingList.add(ls);
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void searchByBill() {
        pendingList.clear();
        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(searchEdit.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo, amount, date, credit, name);
                    pendingList.add(ls);
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void searchByDate() {
        pendingList.clear();
        Query query = mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("date").equalTo(searchEdit.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String billNo = snapshot.child("billNo").getValue().toString();
                    String amount = snapshot.child("amount").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String credit = snapshot.child("creditOrDebit").getValue().toString();
                    String name = snapshot.child("customerName").getValue().toString();
                    ListInfo ls = new ListInfo(billNo, amount, date, credit, name);
                    pendingList.add(ls);
                }

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

