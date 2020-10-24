package pl.zefiro.zefiroapp;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class mroziu_MyWifiAdapter extends RecyclerView.Adapter<mroziu_MyWifiAdapter.MyViewHolder> {
    String TAG = "RecList";
    Context context;
    List<ScanResult> names;
    private OnCardWifiListener onCardWifiListener;

    public mroziu_MyWifiAdapter(Context ct, List<ScanResult> name1, OnCardWifiListener onCardWifiListener){
        context=ct;
        names=name1;
        this.onCardWifiListener=onCardWifiListener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Wywolano mnie");
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =  inflater.inflate(R.layout.mroziu_my_row,parent,false);

        return new MyViewHolder(view,onCardWifiListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        String nazwaUrzadzenia=names.get(position).SSID;
        holder.myText.setText(nazwaUrzadzenia);
        Log.d(TAG, "onBindViewHolder: holder:"+holder);

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: size:"+names.size());
        return names.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myText;
        OnCardWifiListener onCardWifiListener;

        public MyViewHolder(@NonNull View itemView, OnCardWifiListener onCardWifiListener) {
            super(itemView);
            myText = itemView.findViewById(R.id.tvdeviceName);
            this.onCardWifiListener= onCardWifiListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCardWifiListener.onCardWifiClick(getAdapterPosition());
        }
    }

    public interface OnCardWifiListener{
        void onCardWifiClick(int position);
    }
}
