package com.spareroom.myledger;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class InAccount extends AppCompatActivity {
    private TextView userName;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDat = FirebaseDatabase.getInstance();
    private ListView listView;
    private AdView mAdView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> optionsList = new ArrayList<>(4);
    private String[] header = new String[]{"BIll Number,", "Chalan Number,","Customer Name,","Amount,","Date,",
            "Credit or Debit,","Mode of Payment,","Payment Status,","Receipt Number\r\n"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_account);


        userName = findViewById(R.id.userName);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.showOptions);
        optionsList.add("PRINT NEW RECEIPT");
        optionsList.add("VIEW PENDING PAYMENTS");
        optionsList.add("GET EXCEL FILE");
        optionsList.add("VIEW ALL TRANSACTIONS");
        optionsList.add("EXIT");
        arrayAdapter = new ArrayAdapter<>(this,R.layout.listrow,R.id.textView,optionsList);
        listView.setAdapter(arrayAdapter);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (mDat==null) Toast.makeText(this, "Null", Toast.LENGTH_LONG).show();
        mDat.getReference("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName.setText("Username: "+dataSnapshot.child("username").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:startActivity(new Intent(InAccount.this,NewEntry.class));
                    break;

                    case 1:startActivity(new Intent(InAccount.this,Pending.class));
                    break;

                    case 2: produceExcel();
                    break;

                    case 3:startActivity(new Intent(InAccount.this,Transactions.class));
                    break;

                    case 4:finishAffinity();
                        System.exit(0);
                    break;
                }
            }
        });

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.logout){
            mAuth.signOut();
            finish();
            startActivity(new Intent(this,MainActivity.class).putExtra("app",1));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {}

    public void produceExcel() {

        File directory = new File(Environment.getExternalStorageDirectory() + "/My Ledger/Excel/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory , "My Ledger.csv");
        alterDocument(file);
        Uri uri;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(InAccount.this, InAccount.this.getPackageName()+".provider",file);
        }else{
            uri = Uri.fromFile(file);
        }

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/text");
        share.putExtra(Intent.EXTRA_STREAM,uri);

        startActivity(Intent.createChooser(share,"Share"));


    }

    private void alterDocument(final File file) {

            mDat.getReference().child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        final FileOutputStream fileOutputStream = new FileOutputStream(file);
                        for (String string : header)
                                fileOutputStream.write(string.getBytes());

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            String data = snapshot.child("billNo").getValue().toString() + ",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("chalanNo").getValue().toString() + ",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("customerName").getValue().toString()+",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("amount").getValue().toString() + ",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("date").getValue().toString() + ",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("creditOrDebit").getValue().toString() + ",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("modeOfPayment").getValue().toString() + ",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("paymentStatus").getValue().toString() + ",";
                            fileOutputStream.write(data.getBytes());
                            data = snapshot.child("receiptNo").getValue().toString() + "\r\n";
                            fileOutputStream.write(data.getBytes());

                        }

                    } catch (IOException e) {
                        Toast.makeText(InAccount.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }



}
