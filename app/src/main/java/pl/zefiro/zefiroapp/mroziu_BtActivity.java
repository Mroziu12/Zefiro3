package pl.zefiro.zefiroapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class mroziu_BtActivity extends AppCompatActivity implements mroziu_MyBtAdapter.OnCardListener {

    Context context1 = this;
    String TAG = "Mroziu";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mBluetoothSocket;
    Button btnSend, btnBtON;
    int index;

    BluetoothDevice deviceClicked;
    mroziu_BtSearchDialog btSearchDialog;
    mroziu_BtConnectDialog btConnectDialog;

    TextView tvWybraneUrzadzenie;
    RecyclerView recyclerView;


    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBTDevice;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    @Override
    public void onBackPressed() {

    }


    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());


                if(device.getName()!=null){

                    char[] stZtab ={'Z','e','f','i','r','o'};
                    //char[] stZtab ={'H','C','-','0'};
                    boolean flag=true;

                    for (int j=0;j<stZtab.length;j++){
                        if(stZtab[j]!=(device.getName().charAt(j))){
                            flag=false;
                            break;
                        }
                    }


                    if(flag){

                            mBTDevices.add(device);
                            mroziu_MyBtAdapter myAdapter = new mroziu_MyBtAdapter(context1, mBTDevices,mroziu_BtActivity.this);
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context1));
                            btSearchDialog.dismissBtSearchDialog();

                    }


                }else{
                    Log.d(TAG, "onReceive: Znaleziono Puste");
                }


            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mroziu_activity_bt);

        recyclerView = findViewById(R.id.recyclerView);
        btnBtON = findViewById(R.id.BtON);

        btSearchDialog= new mroziu_BtSearchDialog(mroziu_BtActivity.this);
        btConnectDialog= new mroziu_BtConnectDialog(mroziu_BtActivity.this);

        btnBtON.setOnClickListener(v -> szukajUrzadzen());

        // mDialog=new Dialog(this);

        // Intent intent = getIntent();
        // toSend=intent.getStringExtra(MainActivity.EXTRA_TEXT);

        mBTDevices = new ArrayList<>();

        btnSend = (Button) findViewById(R.id.btnSend);
        tvWybraneUrzadzenie = findViewById(R.id.tvWybraneUrzadzenie);


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //RObienie wsytkieog na wejsciu do Activit


        //--------------------------------------------
        enableDisableBT();


        checkBTPermissions();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWifiActivity();

            }
        });

    }

    private void szukajUrzadzen() {
        btSearchDialog.startBtSearchDialog();
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest

            Log.d(TAG, "szukajUrzadzen: sprawdziłem pwrmisje ");
            mBluetoothAdapter.startDiscovery();
            Log.d(TAG, "szukajUrzadzen: po disoevery");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            Log.d(TAG, "szukajUrzadzen: spraedzilem permisje");
            mBluetoothAdapter.startDiscovery();
            Log.d(TAG, "szukajUrzadzen: po start discovery");
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }


//    private void PokazDialog() {
//        Log.d(TAG, "PokazDialog: przed set content");
//        mDialog.setContentView(R.layout.dialog_return);
//        Log.d(TAG, "PokazDialog: przed buttonami itp");
//        ivWarning=(ImageView) mDialog.findViewById(R.id.pic_warning) ;
//        btnReturn = (Button) mDialog.findViewById(R.id.btnReturn);
//        tvTopic=(TextView) mDialog.findViewById(R.id.pop_topic);
//        tvMessage=(TextView) mDialog.findViewById(R.id.pop_text);
//
//        Log.d(TAG, "PokazDialog: przed get window");
//
//        btnReturn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMainActivity();
//
//            }
//        });
//
//
//        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        Log.d(TAG, "PokazDialog: przed show");
//        Log.d(TAG, "PokazDialog: pokazuje dialog:"+mDialog);
//        mDialog.show();
//        Log.d(TAG, "PokazDialog: po show");
//
//        Log.d(TAG, "PokazDialog: Jestem w Catchu 2 ");
//
//
//    }

    private void openWifiActivity() {
        if(mBTDevice!=null){
            Intent intent = new Intent(this, mroziu_WifiActivity.class);
            startActivity(intent);
        }else{
            Context context = getApplicationContext();
            CharSequence text = "Wybierz urządzenie Zefiro";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            Log.d(TAG, "enableDisableBT: 1");

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
            Log.d(TAG, "enableDisableBT: 2");
        }

    }


    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }




    @Override
    public void onCardClick(int position) {
        deviceClicked = mBTDevices.get(position);
        mBluetoothAdapter.cancelDiscovery();
        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(position).getName();
        Log.d(TAG, "polacz: tu 1?");
        tvWybraneUrzadzenie.setText("Wybrane urzadzenie: "+deviceName);
        Log.d(TAG, "polacz: tu 2?");
        String deviceAddress = mBTDevices.get(position).getAddress();
        Log.d(TAG, "polacz: tu 3?");

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(position).createBond();
            mBTDevice = mBTDevices.get(position);
            mBluetoothSocket=null;
            int counter = 0;
            do {
                try {
                    Log.d(TAG, "onItemClick: Tworzenie socketu:");
                    mBluetoothSocket = mBTDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                    Log.d(TAG, "onItemClick: uTworzono socket:"+mBluetoothSocket);
                    mBluetoothSocket.connect();
                    Log.d(TAG, "onItemClick: Udalo sie polaczyc z Socketem");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onItemClick: Nie udalo sie utworzyc polaczenia");
                }
                counter++;
            } while (!mBluetoothSocket.isConnected() && counter < 3);

            if(mBluetoothSocket.isConnected()){
                mroziu_SocketHandler.setSocket(mBluetoothSocket);
            }

            // mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }

//    private class Zadanie extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            btConnectDialog.startBtConnectDialog();
//        }
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            // put your code here
//            polacz();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final Void unused) {
//
//        }
//    }
    }



