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
![Alt text](./images/textchanged.png)

when the real emoji return, will execute the callback method,
![Alt text](./imagesemojicallback.png)


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
We supply the simple example, hope useful for you.

![Alt text](./images/convert.png)

###The recent emojis
When you want to get the emojis which you recent used, just call the follow method.
```
EMRecentManger.getInstance().getEMRecents()
```
Follow is the simple example:
![Alt text](./images/getRecentEmoji.png)


###Get the real transfer content
If the message which you want to send that contains our emoji, then before you really sent, you need call the follow method which converts the emoji into our protocol message, so we can recoginze it later.
```
/**
content:the Spannable which contains emoji and text.
*/
public static CharSequence getTranslateTxt(SpannableStringBuilder content)
```
Follow is our simple example

![Alt text](./images/getTranslateMsg.png)

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
Follow is our example:

![Alt text](./images/processReciveMsg.png)

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





