package com.xinmei365.emojsdk.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xinmei365.emojsdk.LibApp;
import com.xinmei365.emojsdk.domain.Constant;
import com.xinmei365.emojsdk.domain.EMCharacterEntity;
import com.xinmei365.emojsdk.domain.EmojEntity;
import com.xinmei365.emojsdk.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by xinmei on 15/11/18.
 */
public class EMDBMagager {

 //   private static final String TAG = EMDBMagager.class.getSimpleName();
    private static EMDBMagager mInstance = null;
    private final DbOpenHelper dbHelper;


    private static final int RECENT_EMOJ_COUNT = 10;


    private EMDBMagager() {
        dbHelper = DbOpenHelper.getInstance(LibApp.getContext());
    }

    public synchronized static EMDBMagager getInstance() {
        if (mInstance == null) {
            mInstance = new EMDBMagager();
        }
        return mInstance;
    }

    public Map<String, ArrayList<EMCharacterEntity>> filterTranslateWord(ArrayList<EMCharacterEntity> emTransEntries) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<EMCharacterEntity> needTransArr = new ArrayList<EMCharacterEntity>();
        ArrayList<EMCharacterEntity> needJoinArr = new ArrayList<EMCharacterEntity>();
        int count = emTransEntries.size();
        for (int i = 0; i < count; i++) {
            EMCharacterEntity entry = emTransEntries.get(i);

            EMCharacterEntity needTransEntry = new EMCharacterEntity(entry.mWordStart, EMCharacterEntity.CharacterType.Normal);
            CharSequence finalStr = entry.mWord;
            needTransEntry.setWord(finalStr);


            if (entry.mCharType == EMCharacterEntity.CharacterType.Normal) {
                String word = entry.mWord.toString();
                ArrayList<String> cacheWords = getCacheWords(db, word);


                if (cacheWords.size() > 0) {
                    if (i == count - 1) { // the last one, add it directly and return
                        needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;
                        needTransArr.add(needTransEntry);
                        needJoinArr.add(needTransEntry);
                    } else if (i < count - 1) { // not the last
                        EMCharacterEntity secondEntry = emTransEntries.get(i + 1);
                        if (secondEntry.mCharType == EMCharacterEntity.CharacterType.Emoj) {

                            //James0xfffc, such form, need set james type to transfer and put into array
                            needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;

                            needTransArr.add(needTransEntry);
                            needJoinArr.add(needTransEntry);
                            needJoinArr.add(secondEntry);
                            i++;
                        }else if (secondEntry.mCharType == EMCharacterEntity.CharacterType.Other){
                            needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;

                            needTransArr.add(needTransEntry);
                            needJoinArr.add(needTransEntry);
                            needJoinArr.add(secondEntry);
                            i++;
                        }else if (secondEntry.mCharType == EMCharacterEntity.CharacterType.Space) {
                            //James Bond
                            finalStr = finalStr + secondEntry.mWord.toString();
                            needTransEntry.setWord(finalStr);

                            //follow by whitespace
                            int maxCount = 8;
                            for (int j = 1; j < maxCount; j++) {
                                if (i + 1 + j <= count - 1) {
                                    EMCharacterEntity thirdEntry = emTransEntries.get(i + j + 1);
                                    if (thirdEntry.mCharType == EMCharacterEntity.CharacterType.Other ||
                                            thirdEntry.mCharType == EMCharacterEntity.CharacterType.Emoj) {
                                        if (finalStr.toString().endsWith(" ")) {
                                            finalStr = finalStr.subSequence(0,finalStr.length() - 1);

                                            needTransEntry.setWord(finalStr);
                                            needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;

                                            needTransArr.add(needTransEntry);

                                            needJoinArr.add(needTransEntry);
                                            needJoinArr.add(emTransEntries.get(i + j));
                                            needJoinArr.add(emTransEntries.get(i + 1 + j));

                                            i = i + j + 1 ;
                                            j = maxCount;
                                        }else {
                                            needTransEntry.setWord(finalStr);
                                            needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;

                                            needTransArr.add(needTransEntry);

                                            needJoinArr.add(needTransEntry);
                                            needJoinArr.add(emTransEntries.get(i + 1 + j));

                                            i = i + j + 1 ;
                                            j = maxCount;
                                        }
                                    } else if (thirdEntry.mCharType == EMCharacterEntity.CharacterType.Space) {
                                        finalStr = finalStr.toString() + thirdEntry.mWord;
                                        needTransEntry.setWord(finalStr);

                                    } else if (thirdEntry.mCharType == EMCharacterEntity.CharacterType.Normal) {
                                        String totalStr = finalStr.toString() + thirdEntry.mWord;
                                        String tempTotalStr = totalStr.toLowerCase();
                                        if (cacheWords.contains(tempTotalStr)) {
                                            //matched
                                            needTransEntry.setWord(totalStr);
                                            needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;

                                            needTransArr.add(needTransEntry);
                                            needJoinArr.add(needTransEntry);

                                            i = i + j + 1; //loop from next word
                                            j = maxCount;
                                        } else {
                                            boolean isHit = false;
                                            //no matched, possible contain certain element of cache word
                                            for (String cacheWord : cacheWords) {
                                                if (cacheWord.contains(tempTotalStr)) {
                                                    isHit = true;
                                                    break;
                                                }
                                            }
                                            if (!isHit) {
                                                //Not found, word must be preceding by whitespace
                                                if (finalStr.toString().endsWith(" ")) {
                                                    finalStr = finalStr.subSequence(0,finalStr.length() - 1);

                                                    needTransEntry.setWord(finalStr);
                                                    needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;

                                                    needTransArr.add(needTransEntry);

                                                    needJoinArr.add(needTransEntry);
                                                    needJoinArr.add(emTransEntries.get(i + j));

                                                    i = i + j; //loop from next word
                                                    j = maxCount;
                                                }
                                            } else {
                                                // find it, loop to find the next one
                                                finalStr = totalStr;
                                                needTransEntry.setWord(finalStr);
                                            }
                                        }
                                    }
                                }
                            }

                            if (StringUtil.isNullOrEmpty(finalStr.toString()) && !needTransArr.contains(needTransEntry)) {
                                needTransEntry.setWord(finalStr);

                                needTransEntry.mCharType = EMCharacterEntity.CharacterType.Translate;
                                needTransArr.add(needTransEntry);
                                needJoinArr.add(needTransEntry);
                            }
                        }
                    }
                } else {
                    //can't find result, add it to concat array directly
                    needJoinArr.add(entry);
                }
            } else {
                needJoinArr.add(entry);
            }
        }

        for (EMCharacterEntity entry : needTransArr) {
            Log.d("transfer", entry.toString());
        }
        for (EMCharacterEntity entry : needJoinArr) {
            Log.d("join", entry.toString());
        }


        Map<String, ArrayList<EMCharacterEntity>> map = new HashMap<>();
        map.put(Constant.KEY_EMOJ_TRANSLATE_ASSEMBLE_ARR, needTransArr);
        map.put(Constant.KEY_EMOJ_ALL_ASSEMBLE_ARR, needJoinArr);

        return map;

    }

    private ArrayList<String> getCacheWords(SQLiteDatabase db, String word) {
        ArrayList<String> cacheWords = new ArrayList<>();
        String querySql;
        Cursor cursor;
        querySql = "SELECT * FROM candiate_emoj WHERE emoj_tag like ?";
        cursor = db.rawQuery(querySql, new String[]{word + "%"});
        while (cursor.moveToNext()) {
            cacheWords.add(cursor.getString(cursor.getColumnIndex("emoj_tag")).toLowerCase());
        }
        return cacheWords;
    }



    public void cacheEmojTag(String emojTag, String emojJson) {
        if (!StringUtil.isNullOrEmpty(emojJson)) {
            try {
                JSONObject jsonObj = new JSONObject(emojJson);
                if (jsonObj.optInt("status_code") != 200 || jsonObj.optInt("cand_count") == 0) {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if (emojTag.contains("\\")) {
            emojTag = emojTag.substring(1, emojTag.length());
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CandiateEmojDao.COLUMN_NAME_EMOJTAG, emojTag);
        if (!StringUtil.isNullOrEmpty(emojJson))
            values.put(CandiateEmojDao.COLUMN_NAME_EMOJ_CONTENT, emojJson);

        if (db.isOpen()) {
            db.replace(CandiateEmojDao.TABLE_NAME, null, values);
        }
        closeDB(dbHelper);
    }

    public String queryEmojByTag(String emojTag) {
        if (StringUtil.isNullOrEmpty(emojTag)) return null;
        if (emojTag.contains("\\")) {
            emojTag = emojTag.substring(1, emojTag.length());
        }

        String emojTagContent = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String querySql = "SELECT * FROM candiate_emoj WHERE emoj_tag=?";
        Cursor cursor = db.rawQuery(querySql, new String[]{emojTag});
        while (cursor.moveToNext()) {
            emojTagContent = cursor.getString(cursor.getColumnIndex("emoj_content"));
        }
        closeDB(dbHelper);
        return emojTagContent;
    }

    public void cacheEmojIdProperty(String emojId, String emojProperty) {
        if (StringUtil.isNullOrEmpty(emojId)) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EmojIDPropertyDao.COLUMN_NAME_EMOJTID, emojId);
        values.put(EmojIDPropertyDao.COLUMN_NAME_EMOJ_PROPERTY, emojProperty);

        if (db.isOpen()) {
            db.replace(EmojIDPropertyDao.TABLE_NAME, null, values);
        }
        closeDB(dbHelper);
    }

    public String getEmojPropertyById(String emojId) {
        if (StringUtil.isNullOrEmpty(emojId)) return null;

        String emojProperty = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String querySql = "SELECT * FROM emoj_id_property WHERE emoj_id=?";
        Cursor cursor = db.rawQuery(querySql, new String[]{emojId});
        while (cursor.moveToNext()) {
            emojProperty = cursor.getString(cursor.getColumnIndex("emoj_property"));
        }
        closeDB(dbHelper);
        return emojProperty;
    }


    /**
     * save emoji to local
     */
    public void cacheEmojToLocalDB(EmojEntity emojEntity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String querySql = "SELECT * FROM recentEmoj";
        Cursor cursor = db.rawQuery(querySql, null);
        if (cursor != null && cursor.getCount() > RECENT_EMOJ_COUNT) {
            int needDel = cursor.getCount() - RECENT_EMOJ_COUNT;
            String deleteSql = "DELETE FROM recentEmoj WHERE timestamp IN " +
                    "(SELECT timestamp FROM recentEmoj LIMIT " + needDel + ") ";
            db.execSQL(deleteSql);
        }
        if (emojEntity.mEmojType == 1) {
            String keyId = emojEntity.mEmojUnicode.substring(2, emojEntity.mEmojUnicode.length() - 1);
            String[] emojKeyAndID = keyId.split("_");
            String emojId = emojKeyAndID[1];
            String sql = "SELECT * FROM recentEmoj WHERE emojUnicode like ?";
            Cursor queryCursor = db.rawQuery(sql, new String[]{"%" + emojId + "%"});
            if (queryCursor.moveToNext()) {
                String emojUnicode = queryCursor.getString(cursor.getColumnIndex(RecentEmojDao.COLUMN_NAME_unicode));
                if (!StringUtil.isNullOrEmpty(emojUnicode) && emojUnicode.contains(emojId)) {
                    String delSql = "DELETE FROM recentEmoj WHERE emojUnicode=\'" + emojUnicode + "\'";
                    db.execSQL(delSql);
                }
            }
        } else {
            String sql = "SELECT * FROM recentEmoj WHERE emojUnicode=?";
            Cursor queryCursor = db.rawQuery(sql, new String[]{emojEntity.mEmojUnicode});
            if (queryCursor != null && queryCursor.getCount() >= 1) {
                String delSql = "DELETE FROM recentEmoj WHERE emojUnicode=\'" + emojEntity.mEmojUnicode + "\'";
                db.execSQL(delSql);
            }
        }


        ContentValues values = new ContentValues();
        values.put(RecentEmojDao.COLUMN_NAME_TIMESTAMP, emojEntity.mRecentUseTimestamp);
        values.put(RecentEmojDao.COLUMN_NAME_ID, emojEntity.mEmojId);
        values.put(RecentEmojDao.COLUMN_NAME_unicode, emojEntity.mEmojUnicode);
        values.put(RecentEmojDao.COLUMN_NAME_CategoryId, emojEntity.mEmojCategoryId);
        values.put(RecentEmojDao.COLUMN_NAME_CategoryName, emojEntity.mEmojCategoryName);
        values.put(RecentEmojDao.COLUMN_NAME_EMOJ_TYPE, emojEntity.mEmojType);

        if (db.isOpen()){
            db.insert(RecentEmojDao.TABLE_NAME, null, values);
        }

        if (db.isOpen()){
            db.close();
        }

    }


    public Vector<EmojEntity> queryRecentEomjs() {
        Vector<EmojEntity> recentEmojs = new Vector<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String querySql = "SELECT * FROM recentEmoj";
        Cursor cursor = db.rawQuery(querySql, null);
        boolean lastCursor = cursor.moveToLast();
        if (!lastCursor) return null;
        String emojID = cursor.getString(cursor.getColumnIndex(RecentEmojDao.COLUMN_NAME_unicode));
        int emojType = cursor.getInt(cursor.getColumnIndex(RecentEmojDao.COLUMN_NAME_EMOJ_TYPE));
        EmojEntity lastEmoj = new EmojEntity(emojID, emojType);
        recentEmojs.add(lastEmoj);
        while (cursor.moveToPrevious()) {
            emojID = cursor.getString(cursor.getColumnIndex(RecentEmojDao.COLUMN_NAME_unicode));
            emojType = cursor.getInt(cursor.getColumnIndex(RecentEmojDao.COLUMN_NAME_EMOJ_TYPE));
            EmojEntity emojEntity = new EmojEntity(emojID, emojType);
            recentEmojs.add(emojEntity);
        }
        return recentEmojs;
    }

    private void closeDB(DbOpenHelper db) {
        db.closeDB();
    }
}
