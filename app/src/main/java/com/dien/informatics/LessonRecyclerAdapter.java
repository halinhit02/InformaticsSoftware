package com.dien.informatics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LessonRecyclerAdapter extends RecyclerView.Adapter<LessonRecyclerAdapter.LessonViewHolder> {

    private final Context context;
    private final Chapter currentChapter;
    private final ChapterSaved chapterSaved;
    private final boolean isEnable;
    private final Presenter presenter;

    public LessonRecyclerAdapter(Context context, Chapter currentChapter, ChapterSaved chapterSaved, Presenter presenter, boolean isEnable) {
        this.context = context;
        this.currentChapter = currentChapter;
        this.chapterSaved = chapterSaved;
        this.presenter = presenter;
        this.isEnable = isEnable;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_lesson_layout, parent, false);
        return new LessonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        if (!isEnable || chapterSaved.getLessonIndex() < holder.getAdapterPosition() && chapterSaved.getKey().equals(currentChapter.getName())) {
            holder.tvLesson.setTextColor(context.getResources().getColor(R.color.gray));
            holder.itemView.setEnabled(false);
        } else {
            holder.tvLesson.setTextColor(context.getResources().getColor(R.color.black));
            holder.itemView.setEnabled(true);
        }
        String lesson = currentChapter.getLessons().get(holder.getAdapterPosition());
        String document = currentChapter.getDocuments().get(holder.getAdapterPosition());
        if (document.contains("Contest"))
            holder.imgIcon.setImageResource(R.drawable.task_icon);
        else if (document.contains("Certificate"))
            holder.imgIcon.setImageResource(R.drawable.certificate_icon);
        else if (lesson.contains("-") || lesson.contains("HSG"))
            holder.imgIcon.setImageResource(R.drawable.link_icon);
        else
            holder.imgIcon.setImageResource(R.drawable.pdf_icon);
        holder.tvLesson.setText(currentChapter.getLessons().get(holder.getAdapterPosition()));
        holder.itemView.setOnClickListener(view -> presenter.onItemClicked(currentChapter, holder.getAdapterPosition(), holder.itemView));
    }

    @Override
    public int getItemCount() {
        if (chapterSaved.getLesson() == null || currentChapter.getDocuments().size() == 0)
            return 0;
        else
            return currentChapter.getLessons().size();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgIcon;
        private final TextView tvLesson;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.iv_icon);
            tvLesson = itemView.findViewById(R.id.tv_lesson);
        }
    }
}
