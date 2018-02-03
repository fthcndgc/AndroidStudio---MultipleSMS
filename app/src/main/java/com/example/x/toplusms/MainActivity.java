package com.example.x.toplusms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    Button gonder,rehber;
    EditText numara,mesaj;
    String telefonnumarasi,mesajiniz;
    private final int PICK = 1;
    String rehbernumara;
    private static final int REQUEST_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int smsokuma = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int rehberokuma = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> izinler = new ArrayList<String>(); // Alınmamış izinleri listeye ekleyeceğiz.
        if(smsokuma != PackageManager.PERMISSION_GRANTED){
            izinler.add(Manifest.permission.SEND_SMS);
        }
        if(rehberokuma != PackageManager.PERMISSION_GRANTED){
            izinler.add(Manifest.permission.READ_CONTACTS);
        }
        if(!izinler.isEmpty()){
            ActivityCompat.requestPermissions(this, izinler.toArray(new String[izinler.size()]), REQUEST_PERMISSION);
        }

        gonder = (Button)findViewById(R.id.gonder);
        numara = (EditText)findViewById(R.id.telefon);
        mesaj = (EditText)findViewById(R.id.mesaj);
        rehber = (Button)findViewById(R.id.rehber);

        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telefonnumarasi = numara.getText().toString();
                mesajiniz = mesaj.getText().toString();
                String[] numaralar = telefonnumarasi.split("-");
                for (int i = 0; i<numaralar.length;i++)
                {
                    android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
                    sms.sendTextMessage(numaralar[i].toString(),null,mesajiniz,null,null);
                    Toast.makeText(getApplicationContext(),"Mesajiniz " + telefonnumarasi + " numarali kisiye gonderilmistir.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        rehber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

        if (requestCode == PICK && intent != null)
        {
            Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);
            cursor.moveToNext();
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String  name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCur = managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);

            if (phoneCur.moveToFirst()) {
                //name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String noBir = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }

            if (phoneCur.moveToNext())
            {
                rehbernumara = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (numara.getText().toString().trim() == "")
                    numara.setText(rehbernumara+"-");
                else
                    numara.setText(numara.getText()+rehbernumara + "-");
            }
        }
    }
}