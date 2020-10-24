package pl.zefiro.zefiroapp;

import android.bluetooth.BluetoothSocket;

public class mroziu_SocketHandler {
    private static BluetoothSocket socket;

    public static synchronized BluetoothSocket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(BluetoothSocket socket){
        mroziu_SocketHandler.socket = socket;
    }
}
