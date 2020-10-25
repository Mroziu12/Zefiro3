package pl.zefiro.zefiroapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

import static pl.zefiro.zefiroapp.DataManager.LED_COLOR;
import static pl.zefiro.zefiroapp.DataManager.LED_COLOR_DEF;
import static pl.zefiro.zefiroapp.DataManager.LOGIN;
import static pl.zefiro.zefiroapp.DataManager.LOGIN_DEF;
import static pl.zefiro.zefiroapp.DataManager.PASSWORD;
import static pl.zefiro.zefiroapp.DataManager.SELECTED_BUT;
import static pl.zefiro.zefiroapp.DataManager.SELECTED_BUT_DEF;
import static pl.zefiro.zefiroapp.DataManager.SHARED_PREFS;

public class Settings extends AppCompatActivity {
    int DeafultColor;
    Button but_colorPicker;
    Button but_hardReset;
    Button but_saveall;
    EditText pass1, pass2;

    TextView tv;
    String res;

    private static HashMap<Button, Integer> buttonList = new HashMap<>();
    private static final String TAG = "KS:Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_settings);

        but_colorPicker = findViewById(R.id.id_settings_pickColor);
        but_saveall = findViewById(R.id.id_settings_but_saveall);
        tv  = findViewById(R.id.id_settings_tv_type_light);
        but_hardReset = findViewById(R.id.id_settings_hardReset);
        pass1 = findViewById(R.id.id_settings_et_pass1);
        pass2 = findViewById(R.id.id_settings_et_pass2);

        setStartsData();

//        findViewById(R.id.id_setttings_location).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GPSTracker g = new GPSTracker(getApplicationContext());
//                Location l = g.getLocation();
//                if(l != null) {
//                    double lat = l.getLatitude();
//                    double lon = l.getLongitude();
//                    Toast.makeText(getApplicationContext(), "lat:" + lat + " lon: " + lon , Toast.LENGTH_LONG).show();
//                    tv.setText("lat:" + lat + " \nlon: " + lon );
//                }
//            }
//        });

        final Button but_1 = findViewById(R.id.id_settings_but_1);
        final Button but_2 = findViewById(R.id.id_settings_but_2);
        final Button but_3 = findViewById(R.id.id_settings_but_3);
        final Button but_4 = findViewById(R.id.id_settings_but_4);
        final Button but_5 = findViewById(R.id.id_settings_but_5);
//        final Button but_6 = findViewById(R.id.id_settings_but_6);

        buttonList.put(but_1, 0);
        buttonList.put(but_2, 0);
        buttonList.put(but_3, 0);
        buttonList.put(but_4, 0);
        buttonList.put(but_5, 0);
//        buttonList.put(but_6, 0);

        onClickButList(buttonList);

        but_colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        but_saveall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveall();
                Toast.makeText(getApplicationContext(), "Zapisano", Toast.LENGTH_SHORT).show();
            }
        });

        but_hardReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Connection1().execute();
                Toast.makeText(getApplicationContext(), "Zresetowano", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
                System.exit(1);
            }
        });

    }

    private void saveall() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(LED_COLOR, DeafultColor);
        editor.putString(PASSWORD, pass1.getText().toString());
        editor.putInt(SELECTED_BUT, getSelectedButton(buttonList).getId());
        editor.apply();
        new Connection().execute();
    }

    public class Connection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void ... Void) {
            OkHttpClient client = new OkHttpClient();
            SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String deviceID = sp.getString(LOGIN,LOGIN_DEF);

            int intColor = sp.getInt(LED_COLOR, LED_COLOR_DEF);
            String hexColor = Integer.toHexString(intColor).substring(2);

            Log.i(TAG, deviceID + " " + checkIfPass() + " " + hexColor + " " + getModeName(buttonList));

            RequestBody formBody = new FormBody.Builder()
                    .add("id", deviceID)
                    .add("color", hexColor)
                    .add("mode", getModeName(buttonList))
                    .add("pass", checkIfPass())
                    .build();
            Request request = new Request.Builder()
                    .url("https://zefiro.pl/app/settingsdata.php")
                    .post(formBody)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
                Log.i(TAG,  '\"' + res + "\" CODE:" + response.code());
//                Log.i(TAG, getButtonName(buttonList1) + " " + getButtonName(buttonList2));

            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    if(res.toCharArray()[0] !='1') Toast.makeText(getApplicationContext(), "Złe hasło", Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }


    }

    public class Connection1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void ... Void) {
            OkHttpClient client = new OkHttpClient();
            SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String deviceID = sp.getString(LOGIN,LOGIN_DEF);

            int intColor = sp.getInt(LED_COLOR, LED_COLOR_DEF);
            String hexColor = Integer.toHexString(intColor).substring(2);
            Log.i(TAG, deviceID + " ffffff 0 1234");

            RequestBody formBody = new FormBody.Builder()
                    .add("id", deviceID)
                    .add("color", "ffffff")
                    .add("mode", "0")
                    .add("pass","1234")
                    .build();
            Request request = new Request.Builder()
                    .url("https://zefiro.pl/app/settingsdata.php")
                    .post(formBody)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();

                Log.i(TAG, '\"' + res + "\" CODE:" + response.code());
                if(res.toCharArray()[0] =='1') {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear().commit();
//
                }else {
                    BadResSerOnChangeSettings();

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    if(res.toCharArray()[0] !='1') Toast.makeText(getApplicationContext(), "Złe hasło", Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }


    }



    private String checkIfPass() {
        if(pass1.getText().toString() == "") return "";
        if(pass1.getText().toString() == pass2.getText().toString()) return pass1.getText().toString();
        if(pass1.getText().toString().toCharArray().length != pass2.getText().toString().toCharArray().length) return "";
        boolean czy = true;
        for(int i=0; i< pass1.getText().toString().toCharArray().length ;i++) {
            if(pass1.getText().toString().toCharArray()[i] != pass2.getText().toString().toCharArray()[i]) czy = false;
        }
        if(czy == true) return pass1.getText().toString();
        if(czy == false) BadNewPass();
        return "";
    }

    private void BadNewPass() {
        Log.i(TAG, "Bład w podawaniu hasła, hasła nie są takie same");
    }
    private void BadResSerOnChangeSettings() {
        Log.i(TAG, "błąd przy basie danych ");
    }

    private void setStartsData() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        DeafultColor = sp.getInt(LED_COLOR, LED_COLOR_DEF);
        but_colorPicker.setBackgroundColor(DeafultColor);

        Button but = findViewById(sp.getInt(SELECTED_BUT, SELECTED_BUT_DEF));
        but.setBackgroundResource(R.drawable.but_scroll_bg_pressed);
    }

    @Override
    public void onBackPressed(){
        saveall();
        super.onBackPressed();

    }

    public void onClickButList(final HashMap<Button, Integer> buttonList){
        for(final Button but : buttonList.keySet()) {
            but.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    for(Button but : buttonList.keySet()){
                        buttonList.replace(but, 0);
                       but.setBackgroundResource(R.drawable.but_scroll_bg);
                    }
                    buttonList.replace(but, 1);
                    but.setBackgroundResource(R.drawable.but_scroll_bg_pressed);

                }
            });
        }
    }

    public Button getSelectedButton(HashMap<Button, Integer> buttonList) {
        Button returned = (Button)buttonList.keySet().toArray()[0];
        for(Button but : buttonList.keySet()) {
            if(buttonList.get(but) == 1) returned = but;
        }
        return returned;
    }

    private String getModeName(HashMap<Button, Integer> buttonList) {
        Button but = getSelectedButton(buttonList);
        switch (but.getText().toString()) {
            case "Stały":
                return "1";
            case "Tęcz 1":
                return "2";
            case "Tęcza 2":
                return "3";
            case "Fala random":
                return "4";
            case "Fade":
                return "5";
        }
        return "1";
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, DeafultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                DeafultColor = color;
                but_colorPicker.setBackgroundColor(color);
            }
        });
        colorPicker.show();
    }
}
