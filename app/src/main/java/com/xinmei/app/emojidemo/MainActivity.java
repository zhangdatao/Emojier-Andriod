package com.xinmei.app.emojidemo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xinmei365.emojsdk.contoller.EMRcivMsgController;
import com.xinmei365.emojsdk.contoller.EMRecentManger;
import com.xinmei365.emojsdk.contoller.EMTranslateController;
import com.xinmei365.emojsdk.contoller.IEMTranslateCallback;
import com.xinmei365.emojsdk.domain.EMCandiateEntity;
import com.xinmei365.emojsdk.domain.EMReceiveTxtEntity;
import com.xinmei365.emojsdk.domain.EMTranslatEntity;
import com.xinmei365.emojsdk.utils.SpanableUtil;
import com.xinmei365.emojsdk.utils.StringUtil;
import com.xinmei365.emojsdk.view.DefaultEMResponse;
import com.xinmei365.emojsdk.view.EMLogicManager;

import java.util.ArrayList;
import java.util.Vector;


public class MainActivity extends Activity implements View.OnClickListener {

    private EditText mTestET;
    private Button mSendBt;
    private LinearLayout mRecentLL;
    private Button mRecentBT;
    private Button mConvertBT;
    private Button mBackslashBT;
    private ListView mList;
    private int emStart = -1;
    private LinearLayout mToolBarLL;
    private ReceiveMsagAdapter receiveMsagAdapter;
    private ArrayList<CharSequence> mData = new ArrayList<>();
    private EMRcivMsgController.IReceiveMsgTranslateListener mReceiveMsgListener = new EMRcivMsgController.IReceiveMsgTranslateListener() {
        @Override
        public void onTranslateReceiveMsgSuccess(EMReceiveTxtEntity receTxtEntity) {
            receiveMsagAdapter.addData(receTxtEntity.mFinalSpanSB);
        }
    };
    private IEMTranslateCallback mTranslateCallback = new IEMTranslateCallback() {
        @Override
        public void onEmojTransferSuccess(EMTranslatEntity translatEntity) {

            if (translatEntity != null) {
                mTestET.setMovementMethod(LinkMovementMethod.getInstance());
                mTestET.setText(translatEntity.mSpanSb, TextView.BufferType.SPANNABLE);
                mTestET.setSelection(translatEntity.mSpanSb.length());
            }
        }

        @Override
        public void onEmojTransferError() {
            Log.d("emoji_error","translate error");
        }

        @Override
        public void onEmptyMsgTranslate() {
            Log.d("emoji_error","translate empty message");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EMLogicManager.getInstance().setAppKey("testappkey1");
        EMLogicManager.getInstance().init(this.getApplicationContext());

        initViews();
        initData();
        setListener();
    }

    private void initViews() {
        mTestET = (EditText)findViewById(R.id.mTestET);
        mSendBt = (Button)findViewById(R.id.mSendBt);
        mRecentLL = (LinearLayout)findViewById(R.id.mRecentLL);
        mToolBarLL = (LinearLayout)findViewById(R.id.mToolBarLL);
        mRecentBT = (Button)findViewById(R.id.mRecentBT);
        mConvertBT = (Button)findViewById(R.id.mConvertBT);
        mBackslashBT = (Button)findViewById(R.id.mBackslashBT);
        mList = (ListView)findViewById(R.id.mList);
    }

    private void initData() {
        receiveMsagAdapter = new ReceiveMsagAdapter(this,mData);
        mList.setAdapter(receiveMsagAdapter);
    }

    private void setListener() {
        mSendBt.setOnClickListener(this);
        mRecentBT.setOnClickListener(this);
        mConvertBT.setOnClickListener(this);
        mBackslashBT.setOnClickListener(this);
        mRecentLL.setOnClickListener(this);

        mTestET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int editIndex = mTestET.getSelectionStart();
                String content = s.toString();
                if (StringUtil.isNullOrEmpty(content)) {
                    emStart = -1;
                    mRecentLL.removeAllViews();
                    return;
                }
                int contentLength = content.length();
                char lastChar = content.charAt(contentLength - 1);
                if (lastChar == '\\') {
                    emStart = start;
                }
                if (contentLength >= 1 && start <= contentLength && emStart >= 0 && start > emStart) {

                    if (lastChar == ' ' || lastChar == ',' || lastChar == '.' || lastChar == '?' || lastChar == '!') {
                        emStart = -1;
                    }

                    String emKey = content.substring(emStart + 1, editIndex);
                    //get the real emoj
                    getEmoj(emKey, emStart);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mSendBt:
                sendTextMsg();
                break;
            case R.id.mRecentBT:
                getRecentEmojis();
                break;
            case R.id.mConvertBT:
                convertSentence();
                break;
            case R.id.mBackslashBT:
                insertBackslash();
                break;
            case R.id.mRecentLL:
                mRecentLL.removeAllViews();
                break;
        }
    }

    private void insertBackslash() {
        int selStart = mTestET.getSelectionStart();
        selStart = selStart < 0 ? 0 : selStart;
        mTestET.getText().insert(selStart, "\\");
    }

    private void convertSentence() {
        CharSequence str = mTestET.getText();
        EMTranslateController.getInstance().translateMsg(str, mTranslateCallback );
    }

    private void getRecentEmojis() {
        mRecentLL.removeAllViews();
        mRecentLL.setVisibility(View.VISIBLE);
        Vector<Spannable> emRecents = EMRecentManger.getInstance().getEMRecents();
        for (final CharSequence emojCharSeq : emRecents) {
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(30, 0, 0, 0);
            tv.setLayoutParams(layoutParams);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setText(emojCharSeq, TextView.BufferType.SPANNABLE);
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selectionStart = mTestET.getSelectionStart() <= 0 ? 0 : mTestET.getSelectionStart();
                    String inputStr = mTestET.getText().toString();
                    if (inputStr.length() <= 0 || selectionStart >= inputStr.length() || selectionStart <= 0) {
                        mTestET.append(emojCharSeq);
                    } else if (selectionStart < inputStr.length()) {
                        Editable inputEditable = mTestET.getEditableText();
                        inputEditable.insert(selectionStart, emojCharSeq);
                    }
                    mTestET.setSelection(selectionStart + 1);
                    mRecentLL.removeAllViews();
                }
            });
            mRecentLL.addView(tv);
        }
    }

    private void sendTextMsg() {
        final CharSequence sBuilder = getTranslateMsg();

        processReceiveMsg(sBuilder.toString());
        mTestET.setText("");
    }
    private CharSequence getTranslateMsg() {
        Editable editable = mTestET.getText();
        SpannableStringBuilder sb = (SpannableStringBuilder) editable;
        CharSequence translateMsg = SpanableUtil.getTranslateTxt(sb);
        return translateMsg;
    }


    private void processReceiveMsg(final String receiveMsg){
        EMRcivMsgController.getInstance().setOnReceiveMsgTranslateListener(mReceiveMsgListener);
        EMRcivMsgController.getInstance().processReceiveContent(receiveMsg);
    }

    private void getEmoj(String emKey, int keyStart) {
        EMLogicManager.getInstance().setEMRespSpanListener(new DefaultEMResponse() {
            @Override
            public void onEMRespSpanSb(final EMCandiateEntity candiateEntity) {
                super.onEMRespSpanSb(candiateEntity);
                mRecentLL.setVisibility(View.VISIBLE);
                for (final SpannableStringBuilder spanBuilder : candiateEntity.mEMSpans) {
                    Button tv = new Button(MainActivity.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(30, 0, 0, 0);

                    tv.setLayoutParams(layoutParams);
                    tv.setText(spanBuilder, TextView.BufferType.SPANNABLE);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("xxx", "onclick");
                            int spanStartIndex = candiateEntity.mEMStart <= 0 ? 0 : candiateEntity.mEMStart;
                            int spanEndIndex = candiateEntity.mEMKey.length() + spanStartIndex + 1;

                            mTestET.getText().replace(spanStartIndex, spanEndIndex, spanBuilder);
                            mTestET.setSelection(spanStartIndex + 1);
                            emStart = -1;
                            mRecentLL.removeAllViews();
                        }
                    });
                    mRecentLL.addView(tv);
                }
            }

            @Override
            public void onEMRespnSpanError(Exception exp) {
            }
        });
        EMLogicManager.getInstance().requestForEmoj(emKey,keyStart);
    }

}
