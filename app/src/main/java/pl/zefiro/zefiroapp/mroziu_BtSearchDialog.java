package pl.zefiro.zefiroapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class mroziu_BtSearchDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    mroziu_BtSearchDialog(Activity myActivity){
        activity=myActivity;
    }

    void startBtSearchDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.mroziu_proces_dialog_bt_search,null));
        builder.setCancelable(true);

        alertDialog =builder.create();
        alertDialog.show();
    }

    void dismissBtSearchDialog(){
        alertDialog.dismiss();
    }
}
