package com.heyya.app.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.heyya.app.R;
import com.heyya.app.adapters.TaskAdapter;
import com.heyya.app.data.MockDataManager;
import com.heyya.app.models.Task;
import java.util.*;

public class TasksFragment extends Fragment implements TaskAdapter.TaskListener {

    private MockDataManager dataManager;
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            dataManager = new MockDataManager(requireContext());

            recyclerView = view.findViewById(R.id.tasks_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            tvEmptyState = view.findViewById(R.id.tv_empty_state);

            // FAB — Criar Tarefa (UC3)
            View fab = view.findViewById(R.id.fab_add_task);
            if (fab != null) {
                fab.setOnClickListener(v -> showTaskDialog(null));
            }

            // Filter Chips - usando listeners individuais por segurança
            setupChipFilter(view, R.id.chip_all, "all");
            setupChipFilter(view, R.id.chip_pending, "pendente");
            setupChipFilter(view, R.id.chip_done, "concluida");
            setupChipFilter(view, R.id.chip_estudo, "estudo");
            setupChipFilter(view, R.id.chip_trabalho, "trabalho");
            setupChipFilter(view, R.id.chip_saude, "saude");

            loadTasks();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Erro ao carregar tarefas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupChipFilter(View root, int chipId, String filter) {
        Chip chip = root.findViewById(chipId);
        if (chip != null) {
            chip.setOnClickListener(v -> {
                currentFilter = filter;
                loadTasks();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        try {
            if (dataManager == null || recyclerView == null) return;

            List<Task> tasks;
            switch (currentFilter) {
                case "pendente":
                    tasks = dataManager.getTasksByStatus("pendente");
                    break;
                case "concluida":
                    tasks = dataManager.getTasksByStatus("concluida");
                    break;
                case "estudo":
                case "trabalho":
                case "saude":
                    tasks = dataManager.getTasksByCategory(currentFilter);
                    break;
                default:
                    tasks = dataManager.getTasks();
            }

            if (tasks == null) tasks = new ArrayList<>();

            adapter = new TaskAdapter(tasks, this);
            recyclerView.setAdapter(adapter);

            // Empty state
            if (tvEmptyState != null) {
                if (tasks.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UC3 / UC4 - Criar ou Editar Tarefa
    private void showTaskDialog(@Nullable Task editTask) {
        try {
            View dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_task, null);

            EditText etTitle = dialogView.findViewById(R.id.et_task_title);
            EditText etDesc = dialogView.findViewById(R.id.et_task_desc);
            Spinner spCategory = dialogView.findViewById(R.id.sp_category);
            Spinner spPriority = dialogView.findViewById(R.id.sp_priority);
            TextView tvDeadline = dialogView.findViewById(R.id.tv_deadline);
            TextView tvWarning = dialogView.findViewById(R.id.tv_rn_warning);

            // Category Spinner
            String[] categories = {"estudo", "trabalho", "saude", "pessoal"};
            String[] catLabels = {"📚 Estudo", "💼 Trabalho", "❤️ Saúde", "🌟 Pessoal"};
            spCategory.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, catLabels));

            // Priority Spinner
            String[] priorities = {"baixa", "media", "alta"};
            String[] priLabels = {"🟢 Baixa", "🟡 Média", "🔴 Alta"};
            spPriority.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, priLabels));

            // Deadline
            final String[] selectedDate = {MockDataManager.getTodayString()};
            tvDeadline.setText("📅 " + formatDate(selectedDate[0]));
            tvDeadline.setOnClickListener(v -> {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                    selectedDate[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                    tvDeadline.setText("📅 " + formatDate(selectedDate[0]));
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            });

            // RN01
            if (dataManager.getHighPriorityTodayCount() >= 3 && tvWarning != null) {
                tvWarning.setVisibility(View.VISIBLE);
            }

            // Preencher se editando (UC4)
            if (editTask != null) {
                etTitle.setText(editTask.getTitulo());
                etDesc.setText(editTask.getDescricao());
                if (editTask.getPrazo() != null) {
                    selectedDate[0] = editTask.getPrazo();
                    tvDeadline.setText("📅 " + formatDate(selectedDate[0]));
                }
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equals(editTask.getCategoria())) {
                        spCategory.setSelection(i);
                        break;
                    }
                }
                for (int i = 0; i < priorities.length; i++) {
                    if (priorities[i].equals(editTask.getPrioridade())) {
                        spPriority.setSelection(i);
                        break;
                    }
                }
            }

            // Dialog
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(editTask != null ? "✏️ Editar Tarefa" : "➕ Nova Tarefa")
                    .setView(dialogView)
                    .setPositiveButton(editTask != null ? "Salvar" : "Criar", (dialog, which) -> {
                        String title = etTitle.getText().toString().trim();
                        if (title.isEmpty()) {
                            Toast.makeText(requireContext(), "Título obrigatório!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String desc = etDesc.getText().toString().trim();
                        String cat = categories[spCategory.getSelectedItemPosition()];
                        String pri = priorities[spPriority.getSelectedItemPosition()];

                        if (editTask != null) {
                            editTask.setTitulo(title);
                            editTask.setDescricao(desc);
                            editTask.setCategoria(cat);
                            editTask.setPrioridade(pri);
                            editTask.setPrazo(selectedDate[0]);
                            dataManager.updateTask(editTask);
                            Toast.makeText(requireContext(), "✅ Tarefa atualizada!", Toast.LENGTH_SHORT).show();
                        } else {
                            Task newTask = new Task();
                            newTask.setTitulo(title);
                            newTask.setDescricao(desc);
                            newTask.setCategoria(cat);
                            newTask.setPrioridade(pri);
                            newTask.setPrazo(selectedDate[0]);
                            dataManager.addTask(newTask);
                            Toast.makeText(requireContext(), "✅ Tarefa criada!", Toast.LENGTH_SHORT).show();
                        }
                        loadTasks();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Erro ao abrir dialog: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskChecked(Task task) {
        try {
            dataManager.toggleTaskStatus(task.getId());
            Toast.makeText(requireContext(),
                    task.isConcluida() ? "Tarefa reaberta" : "✅ Concluída! +XP",
                    Toast.LENGTH_SHORT).show();
            loadTasks();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void onTaskEdit(Task task) {
        showTaskDialog(task);
    }

    @Override
    public void onTaskDelete(Task task) {
        try {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("🗑️ Excluir Tarefa")
                    .setMessage("Remover \"" + task.getTitulo() + "\"?")
                    .setPositiveButton("Excluir", (d, w) -> {
                        dataManager.deleteTask(task.getId());
                        Toast.makeText(requireContext(), "Tarefa excluída", Toast.LENGTH_SHORT).show();
                        loadTasks();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) return "Hoje";
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) return parts[2] + "/" + parts[1];
        } catch (Exception e) { /* ignore */ }
        return date;
    }
}
