package pl.zefiro.zefiroapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import static pl.zefiro.zefiroapp.DataManager.LOGIN;
import static pl.zefiro.zefiroapp.DataManager.LOGIN_DEF;
import static pl.zefiro.zefiroapp.DataManager.PASSWORD;
import static pl.zefiro.zefiroapp.DataManager.PASSWORD_DEF;
import static pl.zefiro.zefiroapp.DataManager.SHARED_PREFS;

public class MainActivity extends AppCompatActivity {
    String TAG="Mroziu";
    Context context = this;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnected(context)){
            Log.d(TAG, "onClick: sprawdzono True");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showCustomDialog();
                }
            },3000);
            Log.d(TAG, "onClick: Pokazano dialog");
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isLogged()){
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                    }else{
                        Intent intent1 = new Intent(MainActivity.this, mroziu_WelcomeActivity.class);
                        startActivity(intent1);
                    }
                }
            },3000);

        }
    }

    private boolean isLogged() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String login1 = sp.getString(LOGIN,LOGIN_DEF);
        String pass1 = sp.getString(PASSWORD,PASSWORD_DEF);

        if(login1 != LOGIN_DEF && pass1 != PASSWORD_DEF ) {
            Log.i(TAG, "autologownie: " + login1 + " " + pass1);
            return true;

        }else {
            Log.i(TAG, "brak autologowania");
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        if (!isConnected(context)){
//            Log.d(TAG, "onClick: sprawdzono True");
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    showCustomDialog();
//                }
//            },3000);
//
//            Log.d(TAG, "onClick: Pokazano dialog");
//        }else{
//            Intent intent = new Intent(MainActivity.this, mroziu_WelcomeActivity.class);
//            Log.d(TAG, "openMainActivity: dziala");
//            startActivity(intent);
//        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Polaccz sie z internetem aby kontynuować")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });

        AlertDialog dialog =builder.create();
        dialog.show();
        //dialog.show();
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifiConn !=null && wifiConn.isConnected() || dataConn!=null && dataConn.isConnected()){
            return true;
        }else{
            return false;
        }
    }
}