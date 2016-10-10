package com.kw.app.medicine.base;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import io.rong.imlib.model.Message;

/**
 * @author wty
 * 点击播放
 **/
public class VoicePlayOnClickListener implements View.OnClickListener{

    AnimationDrawable anim;
    Message msg;
    public VoicePlayOnClickListener(AnimationDrawable anim, Message msg) {
        this.anim = anim;
        this.msg = msg;
    }

    @Override
    public void onClick(View v) {
    }

    public AnimationDrawable getAnim(){
        return anim;
    }
}
