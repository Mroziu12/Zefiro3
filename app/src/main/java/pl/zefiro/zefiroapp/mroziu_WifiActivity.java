package pl.zefiro.zefiroapp;

import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class mroziu_WifiActivity extends AppCompatActivity implements mroziu_MyWifiAdapter.OnCardWifiListener {
    String TAG = "Mroziu";
    private WifiManager wifiManager;
    Button btnSendWifi;
    TextView tvWybranaSiec;
    EditText etWifiPass;
    private List<ScanResult> results;
    RecyclerView recyclerView;
    Context context1 = this;
    String wifiName, wifiPass, toSend;
    byte b;


    Dialog mDialog, mDialogPos;
    Button btnReturn, btnNext;
    TextView tvTopic, tvMessage, tvTopicPos, tvMessagePos;
    ImageView ivWarning, ivDone;

    BluetoothSocket pobranyBluetoothSocket = mroziu_SocketHandler.getSocket();
    mroziu_WiFiSearchDialog mroziuWiFiSearchDialog;
    mroziu_WiFiCheckDialog wiFiCheckDialog;

    BroadcastReceiver wifiReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = (ArrayList<ScanResult>) wifiManager.getScanResults();
            Log.d(TAG, "onReceive: cos znaleziono");


            for (ScanResult scanResult : results) {

                mroziu_MyWifiAdapter myWifiAdapter = new mroziu_MyWifiAdapter(context1, results, mroziu_WifiActivity.this);
                recyclerView.setAdapter(myWifiAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context1));
            }
            mroziuWiFiSearchDialog.dismissWiFiSearchDialog();
            //wiFiCheckDialog.dismissWiFiCheckDialog();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mroziu_activity_wifi);

        Log.d(TAG, "onCreate: " + pobranyBluetoothSocket);
        etWifiPass = findViewById(R.id.etWifiPass);
        recyclerView = findViewById(R.id.recyclerViewWifi);
        btnSendWifi = findViewById(R.id.btnSendWifi);
        tvWybranaSiec = findViewById(R.id.tvWybranaSiec);

        mDialog = new Dialog(this);
        mDialogPos = new Dialog(this);

        mroziuWiFiSearchDialog = new mroziu_WiFiSearchDialog(mroziu_WifiActivity.this);
        wiFiCheckDialog = new mroziu_WiFiCheckDialog(mroziu_WifiActivity.this);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        btnSendWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiPass = null;
                String wifiPass = etWifiPass.getText().toString();
                toSend = "{name=\"" + wifiName + "\", pass=\"" + wifiPass + "\"}";
                if (wifiName != null && !wifiPass.equals("")) {
                    Log.d(TAG, "onClick: Wiadomosc:" + toSend);

                    wyslijWiadomosc();
                    new Zadanie().execute();

                } else if ((wifiName == null) && (wifiPass.equals(""))) {
                    Context context = getApplicationContext();
                    CharSequence text = "Wybierz Wi-Fi i Wprowadź hasło";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if ((wifiName == null) && !wifiPass.equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Wybierz Wi-Fi";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if (wifiName != null && wifiPass.equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Wprowadź hasło do Wi-Fi";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }


            }
        });
        Log.d(TAG, "onCreate: " + wifiManager);

        scanWifi();
        mroziuWiFiSearchDialog.startWiFiSearchDialog();
        //wiFiCheckDialog.startWiFiCheckDialog();
    }

    private void czytajWiadomosc() {
        Log.d(TAG, "czytajWiadomosc: weszlo do czytaj wiadomosc");
        InputStream inputStream = null;
        Log.d(TAG, "czytajWiadomosc: "+inputStream);
        try {
            Log.d(TAG, "czytajWiadomosc: weszlow w try");
            inputStream = pobranyBluetoothSocket.getInputStream();
            Log.d(TAG, "czytajWiadomosc: "+inputStream);
            Log.d(TAG, "czytajWiadomosc: czekam");
            inputStream.skip(inputStream.available());
            Log.d(TAG, "czytajWiadomosc: po czekaniu na akcje");
            b = (byte) inputStream.read();
            Log.d(TAG, "wyslijWiadomosc: odczytano: " + b);

            //---wsywietlanie---------------------



            //DIAAAAAAAAAAAAAAAAAAAAAAAAAALOG TUTAJ ALE W IFIE
            //---------------------------------
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void wyslijWiadomosc() {

        Log.d(TAG, "onClick: Wiadomosc:" + toSend);
        byte[] bytes = toSend.getBytes(Charset.defaultCharset());
        try {
            Log.d(TAG, "onClick: Wysylanie");
            OutputStream outputStream = pobranyBluetoothSocket.getOutputStream();
            outputStream.write(bytes);
            Log.d(TAG, "onClick: Wyslano");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onClick: Nie swy");
        }

//
    }

    private void scanWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        //arrayList.clear();
        Log.d(TAG, "scanWifi: przed Recieverem");
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Log.d(TAG, "scanWifi: przed wifscna");
        wifiManager.startScan();
        Log.d(TAG, "scanWifi: po wifsacn=");
        Log.d(TAG, "scanWifi: Scanning wifi...");
    }

    private void pokazDialogNegative() {
        Log.d(TAG, "PokazDialog: przed set content");
        mDialog.setContentView(R.layout.mroziu_dialog_check_neg);
        Log.d(TAG, "PokazDialog: przed buttonami itp");
        ivWarning = (ImageView) mDialog.findViewById(R.id.pic_warning_neg);
        btnReturn = (Button) mDialog.findViewById(R.id.btnReturn_neg);
        tvTopic = (TextView) mDialog.findViewById(R.id.pop_topic);
        tvMessage = (TextView) mDialog.findViewById(R.id.pop_text);

        Log.d(TAG, "PokazDialog: przed get window");

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();

            }
        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.d(TAG, "PokazDialog: przed show");
        Log.d(TAG, "PokazDialog: pokazuje dialog:" + mDialog);
        mDialog.show();
        Log.d(TAG, "PokazDialog: po show");

        Log.d(TAG, "PokazDialog: Jestem w Catchu 2 ");
    }

    private void openActivity2() {
        Intent intent = new Intent(this, mroziu_WifiActivity.class);
        startActivity(intent);
    }

    private void pokazDialogPositive() {
        Log.d(TAG, "PokazDialogPos: przed set content");
        mDialogPos.setContentView(R.layout.mroziu_dialog_check_pos);
        Log.d(TAG, "PokazDialogPos: przed buttonami itp");
        ivDone = (ImageView) mDialogPos.findViewById(R.id.pic_warning_pos);
        btnNext = (Button) mDialogPos.findViewById(R.id.btnReturn_pos);
        tvTopicPos = (TextView) mDialogPos.findViewById(R.id.pop_topic_pos);
        tvMessagePos = (TextView) mDialogPos.findViewById(R.id.pop_text_pos);

        Log.d(TAG, "PokazDialogPos: przed get window");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do logowania
                Context context = getApplicationContext();
                CharSequence text = "Tu przechodzi do nastenego Ativity";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        });

        mDialogPos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.d(TAG, "PokazDialogPos: przed show");
        Log.d(TAG, "PokazDialogPos: pokazuje dialog:" + mDialogPos);
        mDialogPos.show();
        Log.d(TAG, "PokazDialogPos: po show");

        Log.d(TAG, "PokazDialogPos: Jestem w Catchu 2 ");
    }

    @Override
    public void onCardWifiClick(int position) {

        wifiName = results.get(position).SSID;
        tvWybranaSiec.setText("Wybrana siec: " + wifiName);
        Log.d(TAG, "onCardWifiClick: Wi-Fi name = " + wifiName);
    }

    private class Zadanie extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            wiFiCheckDialog.startWiFiCheckDialog();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // put your code here
            czytajWiadomosc();
           // PokazDialog();
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused) {
            wiFiCheckDialog.dismissWiFiCheckDialog();
            if (b == 1) {
                pokazDialogPositive();
            } else {
                pokazDialogNegative();
            }
        }
    }
}
