package ivan.robocuphome2015.baxterdroid;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ivan on 11/10/2015.
 * part of code adapted from: http://www.truiton.com/2014/06/android-speech-recognition-without-dialog-custom-activity/
 * API: http://developer.android.com/reference/android/speech/package-summary.html
 */
public class SpeechListener implements RecognitionListener {
    private final String TAG = "SpeechListener";
    private TextView resultText;
    private ProgressBar progressBar;
    private MainActivity activity;
    //todo fix this
    protected static boolean ros_command = false;
    protected static boolean running = false;

    public SpeechListener(MainActivity activity){
        this.activity = activity;
        resultText = (TextView) activity.findViewById(R.id.speechResult);
        progressBar=activity.progressBar;
    }

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        running = true;
        Log.d(TAG, "onReadyForSpeech");
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public void onBeginningOfSpeech()
    {
        Log.d(TAG, "onBeginningOfSpeech");
    }
    @Override
    public void onRmsChanged(float rmsdB)
    {
        Log.v(TAG, "onRmsChanged");
    }
    @Override
    public void onBufferReceived(byte[] buffer)
    {
        Log.d(TAG, "onBufferReceived");
    }
    @Override
    public void onEndOfSpeech()
    {
        Log.d(TAG, "onEndofSpeech");
    }
    @Override
    public void onError(int error)
    {
        String errorMessage = getErrorText(error);
        Log.e(TAG, "error " + errorMessage);
        progressBar.setVisibility(View.INVISIBLE);
        resultText.setText("Say again...");
        MainActivity.t2s.speak("Sorry missed that", TextToSpeech.QUEUE_FLUSH, null);
        running = false;
        activity.sendCommand("error");
    }
    @Override
    public void onResults(Bundle results)
    {
        String str = new String();
        Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++)
        {
            Log.d(TAG, "result " + data.get(i));
            str += data.get(i);
        }
        progressBar.setVisibility(View.INVISIBLE);
        resultText.setText(String.valueOf(data.get(0)));
        running = false;
        activity.sendCommand((String) data.get(0));
    }
    @Override
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(TAG, "onPartialResults");
    }
    @Override
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(TAG, "onEvent " + eventType);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
