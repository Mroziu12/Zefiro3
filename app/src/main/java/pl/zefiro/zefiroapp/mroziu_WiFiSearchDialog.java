package pl.zefiro.zefiroapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class mroziu_WiFiSearchDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    mroziu_WiFiSearchDialog(Activity myActivity){
        activity=myActivity;
    }

    void startWiFiSearchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.mroziu_proces_dialog_wifi_search,null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissWiFiSearchDialog(){
        alertDialog.dismiss();
    }
}
