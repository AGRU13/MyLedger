package com.spareroom.myledger;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowDetails extends AppCompatActivity {
    private TextView showBill,showChalan,showReceipt,showDate,credit,showAmount,showMethod,showStatus,showName;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        Bundle bundle = getIntent().getExtras();
        showBill = findViewById(R.id.showBill);
        showChalan = findViewById(R.id.showChalan);
        showReceipt = findViewById(R.id.showReceipt);
        showDate =findViewById(R.id.showDate);
        showAmount = findViewById(R.id.showAmount);
        showMethod = findViewById(R.id.showMode);
        showStatus = findViewById(R.id.doneOrNot);
        credit = findViewById(R.id.showAmount1);
        showName = findViewById(R.id.showName);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDat = FirebaseDatabase.getInstance();
        mDat.getReference().child(mAuth.getCurrentUser().getUid()).orderByChild("billNo").equalTo(bundle.getString("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    showBill.setText(snapshot.child("billNo").getValue().toString());
                    showChalan.setText(snapshot.child("chalanNo").getValue().toString());
                    showReceipt.setText(snapshot.child("receiptNo").getValue().toString());
                    showName.setText(snapshot.child("customerName").getValue().toString());
                    showDate.setText(snapshot.child("date").getValue().toString());
                    credit.setText("AMOUNT "+snapshot.child("creditOrDebit").getValue().toString()+"ED : ");
                    showAmount.setText(snapshot.child("amount").getValue().toString());
                    showMethod.setText(snapshot.child("modeOfPayment").getValue().toString());
                    showStatus.setText("PAYMENT "+snapshot.child("paymentStatus").getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
