package com.dien.informatics;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChapterRecyclerAdapter extends RecyclerView.Adapter<ChapterRecyclerAdapter.ChapterVH> {

    private final Context mContext;
    private final List<Chapter> chapterList;
    private @IdRes
    final List<Integer> imgList;
    private ChapterSaved chapterSaved;
    private final Presenter presenter;

    public ChapterRecyclerAdapter(Context mContext, List<Chapter> chapterList, @IdRes List<Integer> imgList, ChapterSaved chapterSaved, Presenter presenter) {
        this.mContext = mContext;
        this.chapterList = chapterList;
        this.imgList = imgList;
        this.chapterSaved = chapterSaved;
        this.presenter = presenter;
    }

    public void setChapterSaved(ChapterSaved chapterSaved) {
        this.chapterSaved = chapterSaved;
    }

    @NonNull
    @Override
    public ChapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_chapter_layout, parent, false);
        return new ChapterVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterVH holder, int position) {
        boolean isEnable = true;
        if (holder.getAdapterPosition() > chapterSaved.getIndex()) {
            holder.tvChapter.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.itemView.setEnabled(false);
            isEnable = false;
        } else {
            holder.tvChapter.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.itemView.setEnabled(true);
        }
        holder.tvChapter.setText(chapterList.get(holder.getAdapterPosition()).getName());
        holder.rcv_lesson.setLayoutManager(new LinearLayoutManager(mContext));
        holder.rcv_lesson.setItemAnimator(new DefaultItemAnimator());
        LessonRecyclerAdapter adapter = new LessonRecyclerAdapter(mContext,
                chapterList.get(holder.getAdapterPosition()), chapterSaved, presenter, isEnable);
        holder.rcv_lesson.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        if (chapterList == null || chapterList.size() == 0) {
            return 0;
        }
        return chapterList.size();
    }

    static class ChapterVH extends RecyclerView.ViewHolder {

        private final RecyclerView rcv_lesson;
        private final TextView tvChapter;

        public ChapterVH(@NonNull View itemView) {
            super(itemView);
            rcv_lesson = itemView.findViewById(R.id.rcv_lesson);
            tvChapter = itemView.findViewById(R.id.tv_chapter);
        }
    }
}
