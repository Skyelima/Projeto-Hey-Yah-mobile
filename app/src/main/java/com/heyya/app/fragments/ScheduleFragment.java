package com.heyya.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.heyya.app.R;
import com.heyya.app.data.MockDataManager;
import com.heyya.app.models.UserData;

public class ScheduleFragment extends Fragment {

    private MockDataManager dataManager;
    private MaterialCardView selectedCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MockDataManager(requireContext());

        // Schedule cards
        setupCard(view, R.id.card_12x36, "12x36");
        setupCard(view, R.id.card_5x2, "5x2");
        setupCard(view, R.id.card_6x1, "6x1");
        setupCard(view, R.id.card_plantao, "plantao");
        setupCard(view, R.id.card_flexivel, "flexivel");

        refreshStatus(view);
    }

    private void setupCard(View root, int cardId, String schedule) {
        MaterialCardView card = root.findViewById(cardId);
        card.setOnClickListener(v -> {
            // Deselect previous
            if (selectedCard != null) {
                selectedCard.setStrokeColor(0xFF2A2A3E);
                selectedCard.setCardElevation(2);
            }
            // Select new
            card.setStrokeColor(0xFF6C63FF);
            card.setCardElevation(8);
            selectedCard = card;

            dataManager.setSchedule(schedule);
            refreshStatus(root);
        });

        // Restore selection
        UserData user = dataManager.getUserData();
        if (schedule.equals(user.getEscala())) {
            card.setStrokeColor(0xFF6C63FF);
            card.setCardElevation(8);
            selectedCard = card;
        }
    }

    private void refreshStatus(View view) {
        UserData user = dataManager.getUserData();
        TextView statusText = view.findViewById(R.id.tv_schedule_status);

        if (user.getEscala() != null) {
            String text = "✅ Escala configurada: " + user.getEscalaLabel() +
                    "\n\nA IA vai adaptar sugestões conforme seu regime de trabalho.";
            if (user.isPlantao()) {
                text += "\n\n⚠️ RN02: Em dias de plantão, tarefas cognitivas pesadas serão bloqueadas após a 8ª hora.";
            }
            statusText.setText(text);
        } else {
            statusText.setText("Nenhuma escala configurada. Selecione uma opção acima.");
        }
    }
}
