package com.heyya.app.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.heyya.app.R;
import com.heyya.app.models.Task;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskListener {
        void onTaskChecked(Task task);
        void onTaskEdit(Task task);
        void onTaskDelete(Task task);
    }

    private final List<Task> tasks;
    private final TaskListener listener;

    public TaskAdapter(List<Task> tasks, TaskListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.title.setText(task.getTitulo());
        holder.desc.setText(task.getDescricao());
        holder.category.setText(task.getCategoriaEmoji() + " " + task.getCategoria().toUpperCase());
        holder.priority.setText(task.getPrioridade().toUpperCase());
        holder.priority.setTextColor(task.getPrioridadeColor());

        if (task.getPrazo() != null) {
            String[] parts = task.getPrazo().split("-");
            holder.deadline.setText("📅 " + parts[2] + "/" + parts[1]);
            holder.deadline.setVisibility(View.VISIBLE);
        } else {
            holder.deadline.setVisibility(View.GONE);
        }

        // Done state
        if (task.isConcluida()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(0.5f);
            holder.checkView.setBackgroundResource(R.drawable.bg_check_done);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(1.0f);
            holder.checkView.setBackgroundResource(R.drawable.bg_check);
        }

        // Listeners
        holder.checkView.setOnClickListener(v -> listener.onTaskChecked(task));
        holder.btnEdit.setOnClickListener(v -> listener.onTaskEdit(task));
        holder.btnDelete.setOnClickListener(v -> listener.onTaskDelete(task));
    }

    @Override
    public int getItemCount() { return tasks.size(); }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, category, priority, deadline;
        View checkView;
        ImageButton btnEdit, btnDelete;

        TaskViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.task_title);
            desc = v.findViewById(R.id.task_desc);
            category = v.findViewById(R.id.task_category);
            priority = v.findViewById(R.id.task_priority);
            deadline = v.findViewById(R.id.task_deadline);
            checkView = v.findViewById(R.id.task_check);
            btnEdit = v.findViewById(R.id.btn_edit);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}
