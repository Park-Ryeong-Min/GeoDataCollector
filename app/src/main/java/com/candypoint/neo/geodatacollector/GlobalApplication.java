package com.candypoint.neo.geodatacollector;

import android.app.Application;
import android.util.Log;

import com.candypoint.neo.geodatacollector.Configures.URL;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by myown on 2018. 3. 30..
 */

public class GlobalApplication extends Application {

    private Socket mSocket;
    {
        try{
            mSocket = IO.socket(URL.SERVER_URL);
            mSocket.connect();
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
        Log.d("SOCKET_CONNECTION", "socket connected");
    }

    public Socket getSocket(){
        return mSocket;
    }
}
