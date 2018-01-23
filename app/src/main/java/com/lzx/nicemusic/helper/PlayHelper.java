package com.lzx.nicemusic.helper;

import android.content.Context;
import android.widget.Toast;

import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.nicemusic.network.RetrofitHelper;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lzx
 * @date 2018/1/22
 */

public class PlayHelper {

    public static void playMusic(Context context, MusicInfo musicInfo ) {
        RetrofitHelper.getMusicApi().playMusic(musicInfo.musicId)
                .map(responseBody -> {
                    String json = responseBody.string();
                    json = json.substring(1, json.length() - 2);
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject bitrate = jsonObject.getJSONObject("bitrate");
                    return bitrate.getString("file_link");
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file_link -> {
                    musicInfo.musicUrl = file_link;
                    MusicManager.get().playMusic(musicInfo);
                }, throwable -> Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT).show());
    }

}
