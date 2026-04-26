package com.heyya.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MockDataManager(requireContext());
        refreshDashboard(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) refreshDashboard(getView());
    }

    private void refreshDashboard(View view) {
        List<Task> tasks = dataManager.getTasks();
        UserData user = dataManager.getUserData();
        List<Task> todayTasks = dataManager.getTodayTasks();
        int done = dataManager.getDoneCount();
        int pending = dataManager.getPendingCount();

        // Stats
        ((TextView) view.findViewById(R.id.stat_total)).setText(String.valueOf(tasks.size()));
        ((TextView) view.findViewById(R.id.stat_done)).setText(String.valueOf(done));
        ((TextView) view.findViewById(R.id.stat_pending)).setText(String.valueOf(pending));
        ((TextView) view.findViewById(R.id.stat_points)).setText(String.valueOf(user.getPontos()));

        // Today tasks
        LinearLayout todayContainer = view.findViewById(R.id.today_tasks_container);
        todayContainer.removeAllViews();

        if (todayTasks.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("📭 Nenhuma tarefa para hoje");
            empty.setTextColor(0xFF8888A0);
            empty.setPadding(0, 48, 0, 48);
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

                title.setText(t.getTitulo());
                meta.setText(t.getCategoriaEmoji() + " " + capitalize(t.getCategoria()));
                priority.setText(t.getPrioridade().toUpperCase());
                priority.setTextColor(t.getPrioridadeColor());

                if (t.isConcluida()) {
                    title.setPaintFlags(title.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    title.setAlpha(0.5f);
                    checkView.setBackgroundResource(R.drawable.bg_check_done);
                }

                taskView.setOnClickListener(v -> {
                    dataManager.toggleTaskStatus(t.getId());
                    refreshDashboard(view);
                });

                todayContainer.addView(taskView);
            }
        }

        // XP Bar
        ProgressBar xpBar = view.findViewById(R.id.xp_progress);
        TextView xpText = view.findViewById(R.id.xp_text);
        TextView levelText = view.findViewById(R.id.level_text);
        xpBar.setProgress(user.getXpProgress());
        xpText.setText(user.getXpInLevel() + " / 100 XP");
        levelText.setText("Nível " + user.getNivel());

        // AI Tip
        String[] tips = {
            "Com base na sua escala, sugiro blocos de estudo de 45min seguidos de 15min de pausa.",
            "Você tem tarefas de alta prioridade hoje. Foque nelas antes das 14h.",
            "Padrão detectado: Você é mais produtivo entre 9h e 12h.",
            "Dica: Divida tarefas grandes em subtarefas de 25min (Pomodoro).",
            "Atenção RN01: Evite acumular mais de 3 tarefas urgentes."
        };
        ((TextView) view.findViewById(R.id.ai_tip)).setText(tips[new Random().nextInt(tips.length)]);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
