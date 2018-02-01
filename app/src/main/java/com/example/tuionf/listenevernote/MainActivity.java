package com.example.tuionf.listenevernote;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.User;
import com.example.tuionf.listenevernote.Task.GetNoteHtmlTask;
import com.example.tuionf.listenevernote.Task.GetUserTask;
import com.example.tuionf.listenevernote.fragment.NoteFragment;
import com.example.tuionf.listenevernote.fragment.NotebookFragment;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SpeechSynthesizer mTts;
    private static final String TAG = "MainActivity";

    private EditText editText;
    private Toolbar mToolbar;
    private EvernoteNoteStoreClient noteStoreClient;
    private List<Notebook > notebookList = new ArrayList<Notebook>();
    private List<NoteRef> mNoteRefList = new ArrayList<>();

    private NavigationView navigation;
    private TextView userName;
    private DrawerLayout drawerLayout;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(this);
        editText = (EditText) findViewById(R.id.et);

        navigation = (NavigationView) findViewById(R.id.navigation);


        initParameter();
        getYinxiangToken();

        setToolBar();
//        registerForContextMenu(mListView);

        if (savedInstanceState == null) {
            selectedItem = R.id.mn_note;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NoteFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            new GetUserTask().start(this);
        }
        setNavigationView();

    }


    private void setToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar.setTitle("首页");//设置标题
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        //菜单按钮可用
        actionBar.setHomeButtonEnabled(true);
        //回退按钮可用
        actionBar.setDisplayHomeAsUpEnabled(true);
        //将drawlayout与toolbar绑定在一起
        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        abdt.syncState();//初始化状态
        //设置drawlayout的监听事件 打开/关闭

        drawerLayout.setDrawerListener(abdt);
        //actionbar中的内容进行初始化
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.main_green));
    }

    private void setNavigationView() {
        //NavigationView初始化
        navigation.setItemIconTintList(null);
        View headerView = navigation.getHeaderView(0);
        userName = headerView.findViewById(R.id.tv_login);
        navigation.getMenu().findItem(selectedItem).setChecked(true);

        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectedItem = item.getItemId();
                item.setChecked(true);
                switch(item.getItemId()){
                    case R.id.mn_note:
                        navigation.getMenu().findItem(R.id.mn_notebook).setChecked(false);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new NoteFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                        break;
                    case R.id.mn_notebook:
                        navigation.getMenu().findItem(R.id.mn_note).setChecked(false);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new NotebookFragment())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @TaskResult
    public void onGetUser(User user) {
        if (user != null) {
            userName.setText(user.getUsername());
        }
    }





    private void getYinxiangToken() {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            YinxiangActivity.launch(this);
        }

        noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
            @Override
            public void onSuccess(List<Notebook> result) {
                for (Notebook notebook : result) {
                    notebookList.add(notebook);
                }

            }

            @Override
            public void onException(Exception exception) {
                Log.e(TAG, "onException: " );
            }
        });

    }

    private void initParameter() {
        mTts = SpeechSynthesizer.createSynthesizer(this,mTtsInitListener);
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

    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.e(TAG, "onInit: "+code );
        }
    };

    /**
     * 合成回调监听
     * */
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            Log.e(TAG, "onSpeakBegin: "+"开始播放" );
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {
            Log.e(TAG, "onSpeakPaused: "+"暂停播放" );
        }

        @Override
        public void onSpeakResumed() {
            Log.e(TAG, "onSpeakResumed: "+"继续播放" );
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    @Override
    public void onClick(View v) {
        final String strTextToSpeech = editText.getText().toString();
        mTts.startSpeaking( strTextToSpeech, mSynListener );
    }



    @TaskResult(id = "html")
    public void onGetNoteContentHtml(String html, GetNoteHtmlTask task) {
        startActivity(ViewHtmlActivity.createIntent(this, task.getNoteRef(), html));
    }


}
