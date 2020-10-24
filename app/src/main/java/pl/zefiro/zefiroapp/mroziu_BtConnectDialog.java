package pl.zefiro.zefiroapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

public class mroziu_BtConnectDialog {

    private Activity activity;
    private AlertDialog alertDialog;
    String TAG="Mroziu";

    mroziu_BtConnectDialog(Activity myActivity){
        activity=myActivity;
    }

    void startBtConnectDialog(){
        Log.d(TAG, "startBtConnectDialog: Start dialogu");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.mroziu_proces_dialog_bt_connect,null));
        builder.setCancelable(false);



        alertDialog =builder.create();
        alertDialog.show();
    }

    void dismissBtConnectDialog(){
        Log.d(TAG, "dismissBtConnectDialog: Koniec dialogu");
        alertDialog.dismiss();
    }
}
