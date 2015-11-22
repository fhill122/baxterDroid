package ivan.robocuphome2015.baxterdroid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Switch;

/**
 * to be used with a listenner to distinguish switched by program or user
 * Created by Ivan on 10/10/2015.
 */
public class ConnectSwitch extends Switch {
    protected Boolean checkedByProgram = false;

    public ConnectSwitch(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }
    public ConnectSwitch(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public void SwitchByProgram(boolean x){
        //check if state is changed
        if (x==this.isChecked())
            return;
        checkedByProgram = true;
        this.setChecked(x);
        Log.v("Switch", "toggled by program to "+x);
    }

    //have to be called in listener for both case
    boolean IfByProgram(){
        Boolean temp = checkedByProgram;
        checkedByProgram = false;
        Log.v("Switch", ""+temp);
        return temp;
    }

}
