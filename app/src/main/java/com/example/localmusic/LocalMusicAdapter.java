package com.example.localmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.LocalMusicViewHolder> {

    Context context;
    List<LocalMusicBean> mDatas; //用于接收数据源
    OnItemClickLister onItemClickLister;

    public void setOnItemClickLister(OnItemClickLister onItemClickLister) {
        this.onItemClickLister = onItemClickLister;
    }
//接口回调
    public interface OnItemClickLister {
        public void OnItemClick(View view, int position);
    }

    public LocalMusicAdapter(Context context, List<LocalMusicBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
//    把View直接封装在ViewHolder中，负责每个Item的布局
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_music, parent, false);  //获取item_local_music布局，转换成View
        LocalMusicViewHolder holder = new LocalMusicViewHolder(view);
        return holder;
    }

//    适配渲染数据到View中
    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, int position) {
        /*从position中获取数据，并展示出来*/
        LocalMusicBean musicBean = mDatas.get(position);
        holder.idTv.setText(musicBean.getId());  //显示歌曲ID
        holder.songTv.setText(musicBean.getSong());  //显示歌曲名称
        holder.singerTv.setText(musicBean.getSinger());
        holder.albumTv.setText(musicBean.getAlbum());
        holder.timeTv.setText(musicBean.getDuration());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLister.OnItemClick(v,position);
            }
        });

    }

    @Override
    public int getItemCount() {

        return mDatas.size();
    }


    //必须有个类继承内部类 RecyclerView.ViewHolder
    class LocalMusicViewHolder extends RecyclerView.ViewHolder {
        TextView idTv, songTv, singerTv, albumTv, timeTv;

        public LocalMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            idTv = itemView.findViewById(R.id.item_local_music_num);
            songTv = itemView.findViewById(R.id.item_local_music_song);
            singerTv = itemView.findViewById(R.id.item_local_music_singer);
            albumTv = itemView.findViewById(R.id.item_local_music_album);
            timeTv = itemView.findViewById(R.id.item_local_music_durtion);

        }
    }
}
