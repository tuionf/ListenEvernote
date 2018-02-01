package com.example.tuionf.listenevernote;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.Locale;

/**
 * @author tuionf
 * @date 2018/1/18
 * @email 596019286@qq.com
 * @explain
 */

public class MyApplication extends Application {

    private static final String CONSUMER_KEY = "tuionf-2348";
    private static final String CONSUMER_SECRET = "01c44e619b727bda";
    private static final String TOKEN = "S=s1:U=93ad8:E=168a18b7ba5:C=16149da4db8:P=1cd:A=en-devtoken:V=2:H=e62b43cf8b06ac33408385b22a2f6b20";
    private static final String URL = "https://sandbox.evernote.com/shard/s1/notestore";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5a605c63");

        //Set up the Evernote singleton session, use EvernoteSession.getInstance() later
        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .buildForSingleUser(TOKEN,URL)
//                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();

//        registerActivityLifecycleCallbacks(new LoginChecker());

    }
}
