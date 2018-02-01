package com.example.tuionf.listenevernote;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * @author tuionf
 * @date 2018/1/27
 * @email 596019286@qq.com
 * @explain
 */

public class TextToVoice {

    private static final String TAG = "TextToVoice";
    private static SpeechSynthesizer mTts;

    private static void init(Context context){
        mTts = SpeechSynthesizer.createSynthesizer(context,mTtsInitListener);
        mTts.setParameter(SpeechConstant.PARAMS,null);

        mTts.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD );
        mTts.setParameter( SpeechConstant.VOICE_NAME, "xiaoyan" );
        mTts.setParameter( SpeechConstant.SPEED, "50" );
        //合成语速
        mTts.setParameter( SpeechConstant.SPEED, "50" );
        //合成音调
        mTts.setParameter( SpeechConstant.PITCH, "50" );
        //合成音量
        mTts.setParameter( SpeechConstant.VOLUME, "50" );
        //设置播放器音频流类型
        mTts.setParameter( SpeechConstant.STREAM_TYPE, "3" );
        //设置合成音频打断音乐
        mTts.setParameter( SpeechConstant.KEY_REQUEST_FOCUS, "true" );

        mTts.setParameter( SpeechConstant.AUDIO_FORMAT, "wav" );
        mTts.setParameter( SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    public static void toVoice(Context context,String text,SynthesizerListener mSynListener){
        init(context);
        mTts.startSpeaking(text,mSynListener);
    }

    private static InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.e(TAG, "onInit: "+code );
        }
    };
}
