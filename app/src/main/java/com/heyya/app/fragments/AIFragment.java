package com.heyya.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.heyya.app.R;
import com.heyya.app.data.MockDataManager;
import com.heyya.app.models.Task;
import com.heyya.app.models.UserData;
import java.util.ArrayList;
import java.util.List;

public class AIFragment extends Fragment {

    private MockDataManager dataManager;
    private LinearLayout suggestionsContainer;
    private Button btnGenerate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MockDataManager(requireContext());

        suggestionsContainer = view.findViewById(R.id.ai_suggestions_container);
        btnGenerate = view.findViewById(R.id.btn_generate_ai);

        btnGenerate.setOnClickListener(v -> generateSuggestions());
    }

    private void generateSuggestions() {
        btnGenerate.setEnabled(false);
        btnGenerate.setText("⏳ Processando...");

        // Mark AI as used for gamification
        UserData user = dataManager.getUserData();
        user.setAiUsed(true);
        dataManager.saveUserData(user);

        // Simulate API delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;

            btnGenerate.setEnabled(true);
            btnGenerate.setText("🤖 Gerar Sugestões Inteligentes");

            List<Task> pending = dataManager.getTasksByStatus("pendente");
            String schedule = user.getEscala() != null ? user.getEscalaLabel() : "Flexível";

            suggestionsContainer.removeAllViews();

            List<String[]> suggestions = new ArrayList<>();
            suggestions.add(new String[]{
                "📅 Cronograma Otimizado",
                "Com base nas suas " + pending.size() + " tarefas pendentes e escala " + schedule + ", recomendo: manhã para tarefas de alta prioridade, tarde para tarefas moderadas.",
                "Cronograma"
            });
            suggestions.add(new String[]{
                "😴 Janela de Descanso",
                "Detectei alta carga cognitiva. Sugiro uma pausa de 20 minutos às 15h para recuperação mental.",
                "Bem-estar"
            });

            long highCount = 0;
            for (Task t : pending) {
                if ("alta".equals(t.getPrioridade())) highCount++;
            }
            suggestions.add(new String[]{
                "📊 Reorganização de Prioridades",
                "Você tem " + highCount + " tarefas de alta prioridade. Considere delegar ou adiar as menos urgentes.",
                "Priorização"
            });
            suggestions.add(new String[]{
                "🎯 Foco Recomendado",
                pending.isEmpty() ? "Todas as tarefas concluídas! Hora de descansar." :
                    "Comece por \"" + pending.get(0).getTitulo() + "\" — é sua tarefa mais antiga pendente.",
                "Produtividade"
            });

            if (user.isPlantao()) {
                suggestions.add(new String[]{
                    "⚠️ Alerta de Escala (RN02)",
                    "Em dia de plantão, evite tarefas de alta densidade cognitiva após 8 horas de trabalho contínuo.",
                    "Regra RN02"
                });
            }

            for (String[] s : suggestions) {
                View card = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_ai_suggestion, suggestionsContainer, false);
                ((TextView) card.findViewById(R.id.ai_title)).setText(s[0]);
                ((TextView) card.findViewById(R.id.ai_text)).setText(s[1]);
                ((TextView) card.findViewById(R.id.ai_tag)).setText(s[2]);
                suggestionsContainer.addView(card);
            }
        }, 1500);
    }
}
