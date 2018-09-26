package com.ayon.testnow;

import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by mugdha on 9/25/18.
 */

public class Qus {
    public String qus;
    public String ans1;
    public String ans2;
    public String ans3;
    public String ans4;
    public int rightAns;
    public boolean isRight = false;

    public Qus()
    {
        super();
    }
    public Qus(String qus, String ans1, String ans2, String ans3, String ans4, int rightAns) {
        this.qus = qus;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        this.ans4 = ans4;
        this.rightAns = rightAns;
    }

    public String getQus() {
        return qus;
    }

    public String getAns1() {
        return ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public String getAns3() {
        return ans3;
    }

    public String getAns4() {
        return ans4;
    }

    public int getRightAns() {
        return rightAns;
    }

    public boolean isRight() {
        return isRight;
    }
    public void submit(String tag){
        Log.d(TAG, "submit: "+tag);
        if(tag.equalsIgnoreCase(rightAns+""))
            isRight = true;
    }
}
