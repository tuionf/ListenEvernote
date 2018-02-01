package com.example.tuionf.listenevernote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.helper.Cat;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rwondratschek
 */
@SuppressWarnings("FieldCanBeLocal")
public class ViewHtmlActivity extends AppCompatActivity implements View.OnClickListener{

    //-----
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符
    private static final String regEx_w = "<w[^>]*?>[\\s\\S]*?<\\/w[^>]*?>";//定义所有w标签
    //-----

    private static final Cat CAT = new Cat("ViewHtmlActivity");

    private static final String KEY_NOTE = "KEY_NOTE";
    private static final String KEY_HTML = "KEY_HTML";

    private static final String TAG = "ViewHtmlActivity";

    public static Intent createIntent(Context context, NoteRef note, String html) {
        Intent intent = new Intent(context, ViewHtmlActivity.class);
        intent.putExtra(KEY_NOTE, note);
        intent.putExtra(KEY_HTML, html);
        return intent;
    }

    private NoteRef mNoteRef;
    private String mHtml;

    private EvernoteHtmlHelper mEvernoteHtmlHelper;
    private Button voiceBtn;
    private String str;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_html);

        mNoteRef = getIntent().getParcelableExtra(KEY_NOTE);
        mHtml = getIntent().getStringExtra(KEY_HTML);
        voiceBtn = (Button) findViewById(R.id.voice);
        voiceBtn.setOnClickListener(this);
//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        mToolbar.setTitle("首页");//设置标题
//        setSupportActionBar(mToolbar);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(getResources().getColor(R.color.tb_text));

//        setSupportActionBar(toolbar);

//        if (!isTaskRoot()) {
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        getSupportActionBar().setTitle(mNoteRef.getTitle());

        final WebView webView = (WebView) findViewById(R.id.webView);

        if (savedInstanceState == null) {
            String data = "<html><head></head><body>" + mHtml + "</body></html>";

            webView.setWebViewClient(new WebViewClient() {

                @SuppressWarnings("deprecation")
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    try {
                        Response response = getEvernoteHtmlHelper().fetchEvernoteUrl(url);
                        WebResourceResponse webResourceResponse = toWebResource(response);
                        if (webResourceResponse != null) {
                            return webResourceResponse;
                        }

                    } catch (Exception e) {
                        CAT.e(e);
                    }

                    return super.shouldInterceptRequest(view, url);
                }
            });

            webView.loadDataWithBaseURL("", data, "text/html", "UTF-8", null);

            str = delHTMLTag(data);
            Log.e("hhp", "onCreate:--- "+str);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected WebResourceResponse toWebResource(Response response) throws IOException {
        if (response == null || !response.isSuccessful()) {
            return null;
        }

        String mimeType = response.header("Content-Type");
        String charset = response.header("charset");
        return new WebResourceResponse(mimeType, charset, response.body().byteStream());
    }

    protected EvernoteHtmlHelper getEvernoteHtmlHelper() throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        if (mEvernoteHtmlHelper == null) {
            EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

            if (mNoteRef.isLinked()) {
                mEvernoteHtmlHelper = clientFactory.getLinkedHtmlHelper(mNoteRef.loadLinkedNotebook());
            } else {
                mEvernoteHtmlHelper = clientFactory.getHtmlHelperDefault();
            }
        }

        return mEvernoteHtmlHelper;
    }

    @Override
    public void onClick(View v) {
        TextToVoice.toVoice(this,str,mSynListener);
    }

    /**
     * @param htmlStr
     * @return 删除Html标签
     */
    public static String delHTMLTag(String htmlStr) {
        Pattern p_w = Pattern.compile(regEx_w, Pattern.CASE_INSENSITIVE);
        Matcher m_w = p_w.matcher(htmlStr);
        htmlStr = m_w.replaceAll(""); // 过滤script标签


        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签


        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签


        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签


        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签


        htmlStr = htmlStr.replaceAll(" ", ""); //过滤
        return htmlStr.trim(); // 返回文本字符串
    }

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
}
