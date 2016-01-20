# emojier android sdk
A emoji sdk, you can define your own emoji and translate the text to it.

![sample](./images/sample.gif)

If you want to use the function you need to execute some steps.
###Preparation before integration
####Register and Apply AppKey
Click register on Emojier’s [official website](http://www.emojier.net/), then it will jump to the administration back-end registration page, as shown in the picture below: 

As a developer ,you need the AppKey And UserKey, you can find them in your dashboard.

![Alt text](./images/keyscreen.png)

###Download SDK
Download Emoji SDK from the  [official website](http://www.emojier.net/).
 By now, you have downloaded the SDK and are ready to learn how to use and integrate the SDK. Let’s get started!
####Import sdk
Add following line into your **build.gradle** 
```
dependencies {
    compile project(':emojsdk')
}
```

####Config Your AppKey
You need set the appkey before do anything.
```
/**
appKey:the appkey in your dashboard
*/
EMLogicManager.getInstance().setAppKey(String appKey);
```
###Config The UserKey
If you want use your own emoji, you need config the user key in your dashboard.
```

###Init The Config
We recommend do this in your SplashActivity.
```
   EMLogicManager.getInstance().init();
   DbOpenHelper.getInstance(Context context);
```
/**
userKey: which used for get your own emoji
*/
EMLogicManager.getInstance().setUserKey(String userKey);
```

###Convert one word to emoji
The most important method as follow, before you get the related emoji,you need set the **OnEMResponseListener**, if you get the real emoji, the **onEMRespSpanSb(EMCandiateEntity emCandiateEntity) ** would be called, in this callback method, you can write you code to display the real emoji,we supply the default implement of the callback ***DefaultEMResponse**, you must call the **super.onEMRespSpanSb(candiateEntity)**.
```
EMLogicManager.getInstance().setEMRespSpanListener(new DefaultEMResponse() {
   @Override
  public void onEMRespSpanSb(EMCandiateEntity canEntity){
         super.onEMRespSpanSb(candiateEntity);
         //do how you want to display the emoji
  }

  @Override
  public void onEMRespnSpanError(Exception exp) {
  }
});
//emKey:the word which you want to convert
//keyStart:the position of the word
EMLogicManager.getInstance().requestForEmoj(emKey,keyStart);
```

When you want to convert some word to releated emoji, you also need to know the position of the word. For Example:
```
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
```
when the real emoji return, will execute the callback method,
```
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
```
###Convert one sentence to emoji 
If you want to convert one sentence, you need to call the method of the **EMTranslateController**. Please focus the **IEMTranslateCallback**.
```
public interface IEMTranslateCallback {
    /**
    if all the key has been translated,will call this method.
    translatEntity:which contains the emoji spannable
    */
    void onEmojTransferSuccess(EMTranslatEntity translatEntity);
    void onEmojTransferError();
    void onEmptyMsgTranslate();
}
/**
 sentenceStr : the sentence which you want to translate. 
translateCallback:when all the emoji ready, the callback will be executed.
*/
EMTranslateController.getInstance().translateMsg(CharSequence sentenceStr,IEMTranslateCallback translateCallback);
```
Example:
```
private void convertSentence() {
    CharSequence str = mTestET.getText();
    EMTranslateController.getInstance().translateMsg(str, new IEMTranslateCallback() {
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
    });
}
```
###The recent emojis
When you want to get the emojis which you recent used, just call the follow method.
```
EMRecentManger.getInstance().getEMRecents()

Example:
```
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
```

###Get the real transfer content
If the message which you want to send that contains our emoji, then before you really sent, you need call the follow method which converts the emoji into our protocol message, so we can recoginze it later.
```
/**
content:the Spannable which contains emoji and text.
*/
public static CharSequence getTranslateTxt(SpannableStringBuilder content)
```
Example:
```
private CharSequence getTranslateMsg() {
  Editable editable = mTestET.getText();
  SpannableStringBuilder sb = (SpannableStringBuilder) editable;
  CharSequence translateMsg = SpanableUtil.getTranslateTxt(sb);
  return translateMsg;
}
```

###Process the receive message
when you receive the message which contains our emoji, you need call this method to convert the message to emoji.
```
/**
msgStr: the text message which need to be converted.
*/
public EMReceiveTxtEntity processReceiveContent(Context ctx, String msgStr)
```
Before call this method, you need set the callback so after all the emoji downloaded, you can repalce your text content.
```
/**
msgTranslateListener:the callback interface,you can reload the content which will display the emoji
*/
public void setOnReceiveMsgTranslateListener(IReceiveMsgTranslateListener msgTranslateListener)
```
Example:
```
private void processReceiveMsg(final String receiveMsg){
  EMRcivMsgController.getInstance().setOnReceiveMsgTranslateListener(new EMRcivMsgController.IReceiveMsgTranslateListener() {
    @Override
    public void onTranslateReceiveMsgSuccess() {
    //all the emoji has downloaded,reloaded the content
    EMReceiveTxtEntity emojTxtEntity = EMRcivMsgController.getInstance().processReceiveContent(MainActivity.this, receiveMsg);
    receiveMsagAdapter.addData(emojTxtEntity.mFinalSpanSB);
    }
  });
}
```

###Set the size for the emoji span
If you need different emoji size, you can call this method of **EMLogicManager**.
```
/**
emojSize: which control the real size of the emoji span, if you set -1, the default value used.
*/
public void setEmojiSize(int emojiSize);
```
###Config max emoji buffer
You can call this method to config the max emoji buffer, if exceed it will delete the reduancy emoji.
```
/**
maxVaule: the max size of the emoji buffer, unit is MB
*/
EMLogicManager.getInstance().setMaxEmojiBuffer(int maxVaule).
```
### Adv Link
It's the online emoji's property. For example, You send a emoji cookie to someone, who want to know where to buy it, he can just tap the emoji of cookie he has received, then he can open the link, and see the sale information on a website. If you want you emoji could be click, then you need call this method.
```
mTextView.setMovementMethod(EMLinkMovementMethod.getInstance());
```
### Clear all the emoji cache
If you want delete all the emojis which cached to local.you need call the method.
```
CommUtil.clearAllEmojImgs();
```





