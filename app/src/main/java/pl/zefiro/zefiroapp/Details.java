package pl.zefiro.zefiroapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static pl.zefiro.zefiroapp.DataManager.LOGIN;
import static pl.zefiro.zefiroapp.DataManager.LOGIN_DEF;
import static pl.zefiro.zefiroapp.DataManager.SHARED_PREFS;

public class Details extends AppCompatActivity {

    private static final String TAG = "KS:Details";
    TextView tv;

    private static HashMap<Button, Integer> buttonList1 = new HashMap<>();
    private static HashMap<Button, Integer> buttonList2 = new HashMap<>();
    private static List<String> DataList;

    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_details);

         final Button but_temp = findViewById(R.id.id_but_scroll_temp);
         final Button but_hum = findViewById(R.id.id_but_scroll_hum);
         final Button but_press = findViewById(R.id.id_but_scroll_press);
         final Button but_pm1 = findViewById(R.id.id_but_scroll_pm1);
         final Button but_pm25 = findViewById(R.id.id_but_scroll_pm25);
         final Button but_pm10 = findViewById(R.id.id_but_scroll_pm10);

         buttonList1.put(but_temp, 0);
         buttonList1.put(but_hum, 0);
         buttonList1.put(but_press, 0);
         buttonList1.put(but_pm1, 0);
         buttonList1.put(but_pm25, 0);
         buttonList1.put(but_pm10, 0);

         Button but_day = findViewById(R.id.id_but_scroll_day);
         Button but_week = findViewById(R.id.id_but_scroll_week);
         Button but_month = findViewById(R.id.id_but_scroll_month);
         Button but_half = findViewById(R.id.id_but_scroll_halfYear);
         Button but_year = findViewById(R.id.id_but_scroll_year);

         buttonList2.put(but_day, 0);
         buttonList2.put(but_week, 0);
         buttonList2.put(but_month, 0);
         buttonList2.put(but_half, 0);
         buttonList2.put(but_year, 0);

        tv = findViewById(R.id.id_tv);

        onClickButList(buttonList1);
        onClickButList(buttonList2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id_targetButton = (int) getIntent().getSerializableExtra("type");
            findViewById(id_targetButton).performClick();
        }
        but_day.performClick();

        graphGenerate();

    }

    private void graphGenerate() {
        getDataFromBase();
    }

    private void getDataFromBase() {
        new Connection().execute();
    }


    public class Connection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void ... Void) {

            OkHttpClient client = new OkHttpClient();

            SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String deviceID = sp.getString(LOGIN,LOGIN_DEF);

            RequestBody formBody = new FormBody.Builder()
                    .add("type", getButtonName(buttonList1))
                    .add("time", getButtonName(buttonList2))
                    .add("id", deviceID)
                    .build();
            Request request = new Request.Builder()
                    .url("https://zefiro.pl/app/chartdata.php")
                    .post(formBody)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                res = response.body().string();
                Log.i(TAG,  getButtonName(buttonList1) + " " + getButtonName(buttonList2) + " " + '\"' + res + "\" CODE:" + response.code());



            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            DataList  = new ArrayList<String>();
            res = res.replace("{", "");
            res = res.replace("}", "");
            String[] list = res.split(", ");
            for(int i=0; i<list.length ;i++) {
                DataList.add(list[i]);
            }

            GraphView graph = (GraphView) findViewById(R.id.id_details_graph1);
            graph.removeAllSeries();

            DataPoint[] dataPoints = new DataPoint[DataList.size()];

            for(int i=0; i<DataList.size() ;i++) {
                dataPoints[i] = new DataPoint(i+1, Double.parseDouble(DataList.get(i)));
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

            graph.getViewport().setYAxisBoundsManual(false);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(30);

            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(30);

            // enable scaling and scrolling
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(false);

            graph.addSeries(series);
        }


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
                        but.setBackgroundResource(R.drawable.but_scroll_bg);
                    }
                    buttonList.replace(but, 1);
                    but.setBackgroundResource(R.drawable.but_scroll_bg_pressed);
                    tv.setText(getSelectedButton(buttonList1).getText() + " <=> " + getSelectedButton(buttonList2).getText());
                    graphGenerate();

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

    private String getButtonName(final HashMap<Button, Integer> buttonList) {
        Button tarBut = getSelectedButton(buttonList);
        switch (tarBut.getText().toString()) {
            case "PM 1":
                return "pm1";
            case "PM 2.5":
                return "pm25";
            case "PM 10":
                return "pm10";
            case "WILGOTNOŚĆ":
                return "humidity";
            case "TEMP":
                return "temp";
            case "CIŚNIENIE":
                return "pressure";

            case "Dzień":
                return "1";
            case "Tydzień":
                return "7";
            case "7":
                return "1";
            case "Miesiąc":
                return "30";
            case "Pół roku":
                return "180";
            case "Rok":
                return "365";

        }
        return tarBut.getText().toString();
    }







}
