package com.heyya.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.heyya.app.R;
import com.heyya.app.data.MockDataManager;
import com.heyya.app.models.Task;
import com.heyya.app.models.UserData;
import java.util.List;
import java.util.Random;

public class DashboardFragment extends Fragment {

    private MockDataManager dataManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            dataManager = new MockDataManager(requireContext());
            refreshDashboard(view);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            try { refreshDashboard(getView()); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void refreshDashboard(View view) {
        List<Task> tasks = dataManager.getTasks();
        UserData user = dataManager.getUserData();
        List<Task> todayTasks = dataManager.getTodayTasks();
        int done = dataManager.getDoneCount();
        int pending = dataManager.getPendingCount();

        // Stats
        setText(view, R.id.stat_total, String.valueOf(tasks.size()));
        setText(view, R.id.stat_done, String.valueOf(done));
        setText(view, R.id.stat_pending, String.valueOf(pending));
        setText(view, R.id.stat_points, String.valueOf(user.getPontos()));

        // Tarefas do dia
        LinearLayout todayContainer = view.findViewById(R.id.today_tasks_container);
        if (todayContainer != null) {
            todayContainer.removeAllViews();

            if (todayTasks.isEmpty()) {
                TextView empty = new TextView(requireContext());
                empty.setText("📭 Nenhuma tarefa para hoje");
                empty.setTextColor(0xFF8888A0);
                empty.setTextSize(14);
                empty.setPadding(0, 64, 0, 64);
                empty.setGravity(android.view.Gravity.CENTER);
                todayContainer.addView(empty);
            } else {
                for (Task t : todayTasks) {
                    View taskView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_mini_task, todayContainer, false);

                    TextView title = taskView.findViewById(R.id.mini_task_title);
                    TextView meta = taskView.findViewById(R.id.mini_task_meta);
                    TextView priority = taskView.findViewById(R.id.mini_task_priority);
                    View checkView = taskView.findViewById(R.id.mini_task_check);

                    if (title != null) title.setText(t.getTitulo());
                    if (meta != null) meta.setText(t.getCategoriaEmoji() + " " + capitalize(t.getCategoria()));
                    if (priority != null) {
                        priority.setText(t.getPrioridade().toUpperCase());
                        priority.setTextColor(t.getPrioridadeColor());
                    }

                    if (t.isConcluida()) {
                        if (title != null) {
                            title.setPaintFlags(title.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                            title.setAlpha(0.5f);
                        }
                        if (checkView != null) checkView.setBackgroundResource(R.drawable.bg_check_done);
                    }

                    final int taskId = t.getId();
                    taskView.setOnClickListener(v -> {
                        dataManager.toggleTaskStatus(taskId);
                        refreshDashboard(view);
                    });

                    todayContainer.addView(taskView);
                }
            }
        }

        // XP Bar
        ProgressBar xpBar = view.findViewById(R.id.xp_progress);
        if (xpBar != null) xpBar.setProgress(user.getXpProgress());
        setText(view, R.id.xp_text, user.getXpInLevel() + " / 100 XP");
        setText(view, R.id.level_text, "Nível " + user.getNivel());

        // AI Tip
        String[] tips = {
            "Com base na sua escala, sugiro blocos de estudo de 45min seguidos de 15min de pausa.",
            "Você tem tarefas de alta prioridade hoje. Foque nelas antes das 14h.",
            "Padrão detectado: Você é mais produtivo entre 9h e 12h.",
            "Dica: Divida tarefas grandes em subtarefas de 25min (Pomodoro).",
            "Atenção RN01: Evite acumular mais de 3 tarefas urgentes."
        };
        setText(view, R.id.ai_tip, tips[new Random().nextInt(tips.length)]);
    }

    private void setText(View root, int id, String text) {
        TextView tv = root.findViewById(id);
        if (tv != null) tv.setText(text);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
