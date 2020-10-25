package pl.zefiro.zefiroapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class mroziu_WelcomeActivity extends AppCompatActivity {

    Button btnNext;
    String TAG="Mroziu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mroziu_activity_welcome);
        btnNext=findViewById(R.id.btn_welcome_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }
    @Override
    public void onBackPressed() {

    }

    private void openMainActivity() {
        Log.d(TAG, "openMainActivity: dziala");
                Intent intent = new Intent(mroziu_WelcomeActivity.this, mroziu_BtActivity.class);
        Log.d(TAG, "openMainActivity: dziala");
                startActivity(intent);
        Log.d(TAG, "openMainActivity: dziala");
    }


}