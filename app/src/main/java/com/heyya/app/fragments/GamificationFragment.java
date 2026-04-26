package com.heyya.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.heyya.app.R;
import com.heyya.app.data.MockDataManager;
import com.heyya.app.models.UserData;

public class GamificationFragment extends Fragment {

    private MockDataManager dataManager;

    private static final String[][] BADGES = {
        {"🎯", "Primeira Tarefa", "Crie sua primeira tarefa"},
        {"⭐", "5 Concluídas", "Conclua 5 tarefas"},
        {"🌟", "10 Concluídas", "Conclua 10 tarefas"},
        {"⚙️", "Organizado", "Configure sua escala"},
        {"🔥", "3 Seguidas", "Complete 3 no mesmo dia"},
        {"🤖", "Tech Savvy", "Use sugestões da IA"},
        {"🌈", "Equilibrado", "Tarefas em todas as categorias"},
        {"👑", "Veterano", "Alcance nível 5"},
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gamification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MockDataManager(requireContext());
        refreshGamification(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) refreshGamification(getView());
    }

    private void refreshGamification(View view) {
        UserData user = dataManager.getUserData();

        ((TextView) view.findViewById(R.id.gamif_level)).setText(String.valueOf(user.getNivel()));
        ((TextView) view.findViewById(R.id.gamif_title)).setText(user.getNivelTitulo());
        ((TextView) view.findViewById(R.id.gamif_points)).setText(user.getPontos() + " pontos XP");

        ProgressBar xpBar = view.findViewById(R.id.gamif_xp_bar);
        xpBar.setProgress(user.getXpProgress());

        ((TextView) view.findViewById(R.id.gamif_xp_text)).setText(user.getXpInLevel() + " / 100 XP");

        // Badges
        GridLayout badgesGrid = view.findViewById(R.id.badges_grid);
        badgesGrid.removeAllViews();

        boolean[] earned = evaluateBadges(user);

        for (int i = 0; i < BADGES.length; i++) {
            View badgeView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_badge, badgesGrid, false);

            ((TextView) badgeView.findViewById(R.id.badge_icon)).setText(BADGES[i][0]);
            ((TextView) badgeView.findViewById(R.id.badge_name)).setText(BADGES[i][1]);
            ((TextView) badgeView.findViewById(R.id.badge_desc)).setText(BADGES[i][2]);

            if (!earned[i]) {
                badgeView.setAlpha(0.3f);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            badgeView.setLayoutParams(params);

            badgesGrid.addView(badgeView);
        }
    }

    private boolean[] evaluateBadges(UserData user) {
        int catCount = dataManager.getCategoryCount();
        return new boolean[]{
            user.getTotalCreated() >= 1,  // Primeira Tarefa
            user.getTotalDone() >= 5,      // 5 Concluídas
            user.getTotalDone() >= 10,     // 10 Concluídas
            user.getEscala() != null,      // Organizado
            user.getTodayDone() >= 3,      // 3 Seguidas
            user.isAiUsed(),               // Tech Savvy
            catCount >= 4,                 // Equilibrado
            user.getNivel() >= 5,          // Veterano
        };
    }
}
