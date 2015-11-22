package ivan.robocuphome2015.baxterdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;

import ivan.robocuphome2015.baxterdroid.R;

public class MainActivity extends Activity {

    protected static Socket socket;
    protected static EditText textIp,textPort;
    private static ConnectSwitch switchConnect;
    protected static String serverDataIn = null;
    protected static TextView historyField;
    protected ProgressBar progressBar;
    private SpeechRecognizer sr;
    protected static TextToSpeech t2s;
    private serverResponder responder;

    private MessageHandler messageHandler = new MessageHandler();
    class MessageHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1) {
                switchConnect.SwitchByProgram(true);
            }
            else if(msg.what==0){
                switchConnect.SwitchByProgram(false);
            }
            //data in
            else if(msg.what==2){
//                //todo call serverResponder instead
//                historyField.append("Server:" + serverDataIn + "\n");
//                t2s.speak(serverDataIn, TextToSpeech.QUEUE_FLUSH,null);
//                Log.w("serverDataIn", "new data");

                responder.getResponse(serverDataIn);

                Log.w("main", "finished processing data");

            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //history field
        historyField = (TextView)findViewById(R.id.historyField);

        //progress bar, set to off
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //speech recognizer
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new SpeechListener(this));

        //switch button
        switchConnect = (ConnectSwitch) findViewById(R.id.switch1);
        switchConnect.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                        if (view.isChecked()) {
                            // The toggle is enabled
                            //connect to server
                            if(!switchConnect.IfByProgram()) {
                                (new ClientThread(messageHandler)).start();
                            }
                            ((Button) findViewById(R.id.myButton)).setEnabled(true);
                        } else {
                            // The toggle is disabled
                            //todo
                            if(!switchConnect.IfByProgram() && socket != null) {
                                try {socket.close();}
                                catch (IOException e) {e.printStackTrace();}
                            }
                            ((Button) findViewById(R.id.myButton)).setEnabled(false);
                        }
                    }

                }
        );

        //editText for ip and port
        textIp = (EditText) findViewById(R.id.ip);
        textIp.addTextChangedListener(
                new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        switchConnect.SwitchByProgram(false);
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );
        textPort = (EditText) findViewById(R.id.port);
        textPort.addTextChangedListener(
                new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        switchConnect.SwitchByProgram(false);
                    }
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                }
        );
        //send button

        //textToSpeech
        t2s=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t2s.setLanguage(Locale.US);
                }
            }
        });

        //respond handler
        responder = new serverResponder(this);

    }

    public void onClickSend(View view) {
        EditText et = (EditText) findViewById(R.id.command);
        String str = et.getText().toString();

        sendCommand(str);
        //clear
        ((EditText) findViewById(R.id.command)).setText("");

    }

    protected static void sendCommand(String str){
        if (!switchConnect.isChecked()){return;}
        PrintWriter out = null;
        try {
            OutputStream outputStream= socket.getOutputStream();
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(outputStream)), true);
            out.println(str);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out == null){
                Log.e("send to server","send fail");
                switchConnect.SwitchByProgram(false);
            } else{
                historyField.append("Client:" + str + "\n");
            }
        }
    }


    //speech button callback, note: speech recognition can only be used from maint thread
    public void promptSpeechInput(View view){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        sr.startListening(intent);
        Log.i("Main", "start listening");
    }

}
