package ivan.robocuphome2015.baxterdroid;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Ivan on 13/10/2015.
 */
public class ClientThread extends Thread{
    private final int timeout = 800;//millisecond
    private static int total_n = 0;
    private Handler messageHandler;

    public ClientThread(Handler messageHandler){
        super();
        total_n+=1;
//        this.socket=socket;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        Log.e("ServerConnect", this.getId() + "new thread@" + total_n);
//        EditText etIp = (EditText) findViewById(R.id.ip);
//        EditText etPort = (EditText) findViewById(R.id.port);
        String SERVER_IP = MainActivity.textIp.getText().toString();
        int SERVER_PORT = Integer.parseInt(MainActivity.textPort.getText().toString());
        MainActivity.socket = null;
        //create socket
        try {
            messageHandler.sendEmptyMessage(0);//switch off
            messageHandler.sendEmptyMessage(3);//grey out switch
            //InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            //will keep trying to connect until timeout
            MainActivity.socket = new Socket();
            MainActivity.socket.connect(new InetSocketAddress(SERVER_IP,SERVER_PORT),timeout);

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (MainActivity.socket != null) {
//                    messageHandler.sendEmptyMessage(1);
            }
            messageHandler.sendEmptyMessage(4);//renable switch
        }

        //listening to it
        SystemClock.sleep(50);
        if (MainActivity.socket != null ) {
            Log.d("ServerConnect", "start reading response");
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(MainActivity.socket.getInputStream()));
                if (in!=null){
                    // switch on
                    messageHandler.sendEmptyMessage(1);
                    while ((MainActivity.serverDataIn = in.readLine()) != null) {
                        // update history
                        messageHandler.sendEmptyMessage(2);
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
        //ending...
        messageHandler.sendEmptyMessage(0);
        Log.e("ServerConnect", this.getId() + "thread closing...@" + total_n);
        total_n -= 1;
    }
}
