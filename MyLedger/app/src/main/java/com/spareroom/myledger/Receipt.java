package com.spareroom.myledger;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Receipt extends AppCompatActivity {
    private EditText amount,shopName;
    private Button generate,done;
    private ImageButton whatsApp;
    private AdView mAdView;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        amount = findViewById(R.id.amountEdit);
        shopName = findViewById(R.id.shopEdit);
        generate = findViewById(R.id.generate);
        whatsApp = findViewById(R.id.whatsApp);
        done = findViewById(R.id.done);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        whatsApp.setVisibility(View.GONE);
        whatsApp.setEnabled(false);
        done.setEnabled(false);
        done.setVisibility(View.INVISIBLE);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount != null && shopName != null) {
                    File directory = new File(Environment.getExternalStorageDirectory() + "/My Ledger/");

                    if (!directory.exists()) {
                        directory.mkdir();
                    }

                    File pdf = new File(directory+"/receipt/");
                    if (!pdf.exists()) {
                        pdf.mkdir();
                    }


                    try {
                        Document document = new Document();
                        file = new File(pdf , NewEntry.receiptNo.getText().toString()+".pdf");
                        PdfWriter.getInstance(document, new FileOutputStream(file));
                        document.open();
                        document.setPageSize(PageSize.A4);
                        document.addCreationDate();
                        generate.setAlpha(0.3f);
                        generate.setEnabled(false);
                        disableInput(amount);
                        disableInput(shopName);
                        BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
                        float mHeadingFontSize = 20.0f;
                        float mValueFontSize = 26.0f;

                        BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

                        LineSeparator lineSeparator = new LineSeparator();
                        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

                        Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderDetailsTitleChunk = new Chunk("Bill Details", mOrderDetailsTitleFont);
                        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
                        mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
                        document.add(mOrderDetailsTitleParagraph);

                        Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
                        Chunk mOrderIdChunk = new Chunk("Receipt No:", mOrderIdFont);
                        Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
                        document.add(mOrderIdParagraph);

                        Font mOrderIdValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderIdValueChunk = new Chunk(NewEntry.receiptNo.getText().toString(), mOrderIdValueFont);
                        Paragraph mOrderIdValueParagraph = new Paragraph(mOrderIdValueChunk);
                        document.add(mOrderIdValueParagraph);

                        document.add(new Paragraph(""));
                        document.add(new Chunk(lineSeparator));
                        document.add(new Paragraph(""));

                        Font mOrderNameFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
                        Chunk mOrderNameChunk = new Chunk("Customer Name:", mOrderNameFont);
                        Paragraph mOrderNameParagraph = new Paragraph(mOrderNameChunk);
                        document.add(mOrderNameParagraph);

                        Font mOrderNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderNameValueChunk = new Chunk(NewEntry.name.getText().toString(), mOrderNameValueFont);
                        Paragraph mOrderNameValueParagraph = new Paragraph(mOrderNameValueChunk);
                        document.add(mOrderNameValueParagraph);

                        document.add(new Paragraph(""));
                        document.add(new Chunk(lineSeparator));
                        document.add(new Paragraph(""));

                        Font mOrderAmountFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
                        Chunk mOrderAmountChunk = new Chunk("Amount:", mOrderAmountFont);
                        Paragraph mOrderAmountParagraph = new Paragraph(mOrderAmountChunk);
                        document.add(mOrderAmountParagraph);

                        Font mOrderAmountValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderAmountValueChunk = new Chunk(amount.getText().toString(), mOrderAmountValueFont);
                        Paragraph mOrderAmountValueParagraph = new Paragraph(mOrderAmountValueChunk);
                        document.add(mOrderAmountValueParagraph);

                        document.add(new Paragraph(""));
                        document.add(new Chunk(lineSeparator));
                        document.add(new Paragraph(""));

                        Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
                        Chunk mOrderDateChunk = new Chunk("Order Date:", mOrderDateFont);
                        Paragraph mOrderDateParagraph = new Paragraph(mOrderDateChunk);
                        document.add(mOrderDateParagraph);

                        Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderDateValueChunk = new Chunk(NewEntry.date.getText().toString(), mOrderDateValueFont);
                        Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
                        document.add(mOrderDateValueParagraph);

                        document.add(new Paragraph(""));
                        document.add(new Chunk(lineSeparator));
                        document.add(new Paragraph(""));

                        Font mOrderShopFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
                        Chunk mOrderShopChunk = new Chunk("Shop Name :", mOrderShopFont);
                        Paragraph mOrderShopParagraph = new Paragraph(mOrderShopChunk);
                        document.add(mOrderShopParagraph);

                        Font mOrderShopValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                        Chunk mOrderShopValueChunk = new Chunk(shopName.getText().toString(), mOrderShopValueFont);
                        Paragraph mOrderShopValueParagraph = new Paragraph(mOrderShopValueChunk);
                        document.add(mOrderShopValueParagraph);

                        document.add(new Paragraph(""));
                        document.add(new Chunk(lineSeparator));
                        document.add(new Paragraph(""));

                        document.close();

                        Toast.makeText(Receipt.this, "Receipt Generated", Toast.LENGTH_LONG).show();

                        whatsApp.setVisibility(View.VISIBLE);
                        whatsApp.setEnabled(true);
                        done.setVisibility(View.VISIBLE);
                        done.setEnabled(true);

                    } catch (IOException | DocumentException ie) {
                        Toast.makeText(Receipt.this, ie.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(Receipt.this, "ERROR!! ALL THE BOXES MUST BE FILLED", Toast.LENGTH_SHORT).show();
            }
        });

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    uri = FileProvider.getUriForFile(Receipt.this, Receipt.this.getPackageName()+".provider",file);
                }else{
                    uri = Uri.fromFile(file);
                }

                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM,uri);

                startActivity(Intent.createChooser(share,"Share"));

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Receipt.this,InAccount.class));
            }
        });
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

    }
}
