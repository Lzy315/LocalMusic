package com.example.localmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public ImageView nextIv, playIv, lastIv;
    public TextView singerIv, songTv;
    public RecyclerView musicRv;
    //数据源
    public List<LocalMusicBean> mDatas;
    //创建适配器
    LocalMusicAdapter adapter;
    //    记录暂停音乐时进度条的位置
    int currentPausePositionInSong = 0;
    //    设置当前播放音乐的位置
    private int currentPlayPosition = -1;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mediaPlayer = new MediaPlayer();
        mDatas = new ArrayList<>();
        //创建适配器对象
        adapter = new LocalMusicAdapter(this, mDatas);
        musicRv.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);

//        加载本地数据
        loadLocalMusicData();
//        设置每一项的点击事件
        setEventListener();


    }


    private void loadLocalMusicData() {
        int id = 0;
//        加载本地音乐的MP3文件到集合中。
//        1.获取ContentResolver
        ContentResolver resolver = getContentResolver();
//        2.获取本地音乐存储的地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        3.开始查询
        Cursor cursor = resolver.query(uri, null, null, null, null);
//        4.遍历cursor

        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            id++;
            String sid = String.valueOf(id);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");  /*指定格式*/
            String time = sdf.format(new Date(duration));  /*Date(0L)当前的毫秒值转成日期对象,sdf.format将Date对象格式化为字符串*/
            if (time.equals("00:00")) {
                Log.d("MainActivity.this", "没法运行到这的哦");
                id--;
                continue;
            } else {
//                将一行当中的数据封装到对象当中
                LocalMusicBean localMusicBean = new LocalMusicBean(sid, song, singer, album, time, path);
                mDatas.add(localMusicBean);
            }
/*            //                将一行当中的数据封装到对象当中
            LocalMusicBean localMusicBean = new LocalMusicBean(sid, song, singer, album, time, path);
            mDatas.add(localMusicBean);*/
        }
//        数据源变化，提示适配器更新
        adapter.notifyDataSetChanged();

    }

    private void setEventListener() {
        /*设置每一项的点击事件*/

//        接口回调，接口的匿名内部类
        adapter.setOnItemClickLister(new LocalMusicAdapter.OnItemClickLister() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPlayPosition = position;
                LocalMusicBean musicBean = mDatas.get(position);
                playMusicMusicBean(musicBean);
            }
        });

    }

    private void playMusicMusicBean(LocalMusicBean musicBean) {
        /*根据传入对象播放音乐*/
        //             设置底部显示的歌手名称和歌曲名
        singerIv.setText(musicBean.getSinger());
        songTv.setText(musicBean.getSong());
        stopMusic();
//                重置多媒体播放器
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*点击播放按钮播放音乐，或者从暂停开始播放
     * 播放音乐有两种情况：
     * 1.从停止到播放。
     * 2.从暂停到播放*/
    private void playMusic() {
        /*播放音乐的函数*/
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
//            重头开始播放
            if (currentPausePositionInSong == 0) {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
//                从暂停开始播放
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
//            显示暂停的图标
            playIv.setImageResource(R.mipmap.icon_pause);
        }
    }

    private void stopMusic() {
        /*一首音乐播放完毕，停止播放音乐*/
        if (mediaPlayer != null) {
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
//            显示播放的图标
            playIv.setImageResource(R.mipmap.icon_play);
        }

    }

    private void pauseMusic() {
        /*暂停播放音乐*/
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            playIv.setImageResource(R.mipmap.icon_play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    private void initView() {
        nextIv = findViewById(R.id.local_music_bottom_iv_next);
        playIv = findViewById(R.id.local_music_bottom_iv_play);
        lastIv = findViewById(R.id.local_music_bottom_iv_last);
        singerIv = findViewById(R.id.local_music_bottom_tv_singer);
        songTv = findViewById(R.id.local_music_bottom_tv_song);
        musicRv = findViewById(R.id.local_music_rv);
//setOnClickListener() 方法为按钮注册一个监听器，点击按钮时就会执行监听器中的 onClick() 方法。
        nextIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_music_bottom_iv_last:
                if (currentPlayPosition == 0) {
                    Toast.makeText(this, "已经是第一首了，没有上一曲!", Toast.LENGTH_LONG).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition - 1;
                LocalMusicBean lastBean = mDatas.get(currentPlayPosition);
                playMusicMusicBean(lastBean);
                break;
            case R.id.local_music_bottom_iv_next:
                if (currentPlayPosition == mDatas.size() - 1) {
                    Toast.makeText(this, "已经是最后一曲了，没有下一曲!", Toast.LENGTH_LONG).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition + 1;
                LocalMusicBean nextBean = mDatas.get(currentPlayPosition);
                playMusicMusicBean(nextBean);
                break;
            case R.id.local_music_bottom_iv_play:
                if (currentPlayPosition == -1) {
//                    并没有选中要播放的音乐
                    Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                }
                if (mediaPlayer.isPlaying()) {
//                    此时处于播放的状态，需要暂停音乐
                    pauseMusic();
                } else {
//                    此时没有要播放的音乐，点击开始播放音乐
                    playMusic();
                }
                break;
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}