package ivan.robocuphome2015.baxterdroid;

import android.content.Context;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * respond to server data in;
 * rule:
 * 1. server command the android to get an answer:
 *      String serverDataIn = /ask/repeat/conform/ans1,ans2,ans3,...
 * 2. normal server respond to just let android speak
 *      String serverDataIn = string (not starting with %)
 *
 * ver 2 handle ans in server
 * format /ask will speak question, prompt speech input, send back the response
 *         ask will just speak question
 */
public class serverResponder {

    private MainActivity activity;
    private final int REPEAT_TIMES = 0;

    private String ask;
    private String repeat;
    private String confirm;
    private String[] expectedAns;


    public serverResponder(MainActivity activity){
        this.activity = activity;
    }

    // get the proper respond to send back for given server data_in
    public void getResponse(String serverDataIn){

        activity.historyField.append("Server:" + serverDataIn + "\n");

        String [] parts = serverDataIn.split("/");
        // rule 1
        if (serverDataIn.charAt(0) == '/'){
            //ver 2 ,todo: find when speech finishes

            ask = parts[1];
            int delay = Integer.parseInt(parts[2]);
//            int delay = 120*ask.length();
            activity.t2s.speak(ask, TextToSpeech.QUEUE_FLUSH,null);
            Log.e("serverResponder", "delay: "+delay);
            SystemClock.sleep(delay);
            activity.promptSpeechInput(activity.findViewById(R.id.buttonSpeak));
        } else {
            ask = parts[0];
            activity.t2s.speak(ask, TextToSpeech.QUEUE_FLUSH, null);
            //activity.sendCommand("received");
        }

    }

    //only used in getResponse method， return index of ans; return -1 if not found
    private int findAns(){
        Log.i("serverResponder", "start to find ans");
        Thread t = new Thread(){
            @Override
            public void run() {

                while(!SpeechListener.running){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.i("serverResponder", ""+SpeechListener.running);

                }
                Log.e("serverResponder", "running");
                while(SpeechListener.running){
//                    SystemClock.sleep(300);
                }
                Log.e("serverResponder", "finished");
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String speech_res = ((TextView)activity.findViewById(R.id.speechResult)).getText().toString();

        for (int i=0;i<expectedAns.length;i++){
            if (speech_res.toLowerCase().contains(expectedAns[i].toLowerCase())){
                return i;
            }
        }

        return -1;
    }
}
