package com.xinmei365.emojsdk.domain;

/**
 * Created by xinmei on 15/12/15.
 */
public class EMCharacterEntity {


    public int mWordStart;
    public int mWordEnd;
    public CharSequence mWord;

    public CharacterType mCharType;

    public EMCharacterEntity(int wordStart, CharacterType type) {
        this.mWordStart = wordStart;
        this.mCharType = type;
    }

    public void setWord(CharSequence word) {

        this.mWord = word;
        this.mWordEnd = this.mWordStart + word.length();
    }

    public enum CharacterType {
        Normal,Space, Other, Emoj,Transfer
    }

    public EMCharacterEntity(int wordStart, String word) {
        this.mWordStart = wordStart;
        this.mWord = word;
    }

    public EMCharacterEntity(int mWordStart, int mWordEnd, String mWord) {
        this.mWordStart = mWordStart;
        this.mWordEnd = mWordEnd;
        this.mWord = mWord;
    }

    public EMCharacterEntity(int mWordStart, int mWordEnd, CharSequence mWord, CharacterType mCharType) {
        this.mWordStart = mWordStart;
        this.mWordEnd = mWordEnd;
        this.mWord = mWord;
        this.mCharType = mCharType;
    }


    @Override
    public String toString() {
        return "word=" + mWord + " start=" + mWordStart + " end=" + mWordEnd + " type" + mCharType;
    }
}
