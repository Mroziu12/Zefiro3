package pl.zefiro.zefiroapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static pl.zefiro.zefiroapp.DataManager.LOGIN;
import static pl.zefiro.zefiroapp.DataManager.LOGIN_DEF;
import static pl.zefiro.zefiroapp.DataManager.PASSWORD;
import static pl.zefiro.zefiroapp.DataManager.PASSWORD_DEF;
import static pl.zefiro.zefiroapp.DataManager.SHARED_PREFS;

public class Login extends AppCompatActivity {

    private static final String TAG = "KS:Login";

    EditText et_login, et_pass;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login);

        autoLogin();

        et_login = findViewById(R.id.et_login);
        et_pass = findViewById(R.id.et_pass);

        findViewById(R.id.but_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, et_login.getText().toString() + "<=>" + et_pass.getText().toString());
                new Connection().execute();
            }
        });



    }

    private void autoLogin() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String login1 = sp.getString(LOGIN,LOGIN_DEF);
        String pass1 = sp.getString(PASSWORD,PASSWORD_DEF);

        if(login1 != LOGIN_DEF && pass1 != PASSWORD_DEF ) {
            Log.i(TAG, "autologownie: " + login1 + " " + pass1);
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }else {
            Log.i(TAG, "brak autologowania");
        }


    }

    @Override
    public void onBackPressed() {

    }

    public class Connection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void ... Void) {
            
            OkHttpClient client = new OkHttpClient();
            
            RequestBody formBody = new FormBody.Builder()
                    .add("id", et_login.getText().toString())
                    .add("password", et_pass.getText().toString())
//                    .add("email", "eve.holt@reqres.in")
//                    .add("password", "pistol")
                    .build();
            Request request = new Request.Builder()
                    .url("https://zefiro.pl/app/appdata.php")
//                    .url("https://reqres.in/api/register")
                    .post(formBody)
                    .build();
            
            Response response = null;
            try {
                Log.d(TAG, "doInBackground: przed respone");
                response = client.newCall(request).execute();
                Log.d(TAG, "doInBackground: przed ranie srting");
                res = response.body().string();
//                Log.i(TAG, '\"' + res + "\" CODE:" + response.code());
                Log.d(TAG, "doInBackground: przed ifem");
                if(res.toCharArray()[0] =='1') {
                    Log.i(TAG, "Login succes");
                    Log.d(TAG, "doInBackground: przed sp");
                    SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    Log.d(TAG, "doInBackground: przed editor");
                    editor.putString(PASSWORD, et_pass.getText().toString() );
                    editor.putString(LOGIN, et_login.getText().toString());
                    editor.apply();
                    Log.d(TAG, "doInBackground: przed new activity");
                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                }else {
//                    Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Login failed");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "doInBackground: po try");

            runOnUiThread(new Runnable() {
                public void run() {
                    if(res.toCharArray()[0] !='1') Toast.makeText(getApplicationContext(), "Złe hasło", Toast.LENGTH_SHORT).show();
                }
            });

            return null;
        }


    }
}
