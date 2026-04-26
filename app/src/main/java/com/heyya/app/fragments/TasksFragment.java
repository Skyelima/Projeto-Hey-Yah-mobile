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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataManager = new MockDataManager(requireContext());

        recyclerView = view.findViewById(R.id.tasks_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        tvEmptyState = view.findViewById(R.id.tv_empty_state);

        // FAB — Criar Tarefa (UC3)
        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showTaskDialog(null));

        // Filter Chips
        ChipGroup chipGroup = view.findViewById(R.id.filter_chips);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = "all";
            } else {
                Chip chip = view.findViewById(checkedIds.get(0));
                if (chip != null && chip.getTag() != null) {
                    currentFilter = chip.getTag().toString();
                } else {
                    currentFilter = "all";
                }
            }
            loadTasks();
        });

        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
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

        adapter = new TaskAdapter(tasks, this);
        recyclerView.setAdapter(adapter);

        // Empty state
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(tasks.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    // UC3 / UC4 - Criar ou Editar Tarefa
    private void showTaskDialog(@Nullable Task editTask) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_task, null);

        EditText etTitle = dialogView.findViewById(R.id.et_task_title);
        EditText etDesc = dialogView.findViewById(R.id.et_task_desc);
        Spinner spCategory = dialogView.findViewById(R.id.sp_category);
        Spinner spPriority = dialogView.findViewById(R.id.sp_priority);
        TextView tvDeadline = dialogView.findViewById(R.id.tv_deadline);
        TextView tvWarning = dialogView.findViewById(R.id.tv_rn_warning);

        // Category Spinner
        String[] categories = {"estudo", "trabalho", "saude", "pessoal"};
        String[] catLabels = {"📚 Estudo", "💼 Trabalho", "❤️ Saúde", "🌟 Pessoal"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, catLabels);
        spCategory.setAdapter(catAdapter);

        // Priority Spinner
        String[] priorities = {"baixa", "media", "alta"};
        String[] priLabels = {"🟢 Baixa", "🟡 Média", "🔴 Alta"};
        ArrayAdapter<String> priAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, priLabels);
        spPriority.setAdapter(priAdapter);

        // Deadline date picker
        final String[] selectedDate = {MockDataManager.getTodayString()};
        tvDeadline.setText("📅 " + formatDate(selectedDate[0]));
        tvDeadline.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
                selectedDate[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                tvDeadline.setText("📅 " + formatDate(selectedDate[0]));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            picker.show();
        });

        // RN01 Eisenhower Check
        if (dataManager.getHighPriorityTodayCount() >= 3) {
            tvWarning.setVisibility(View.VISIBLE);
        }

        // Fill fields if editing (UC4)
        if (editTask != null) {
            etTitle.setText(editTask.getTitulo());
            etDesc.setText(editTask.getDescricao());
            selectedDate[0] = editTask.getPrazo() != null ? editTask.getPrazo() : MockDataManager.getTodayString();
            tvDeadline.setText("📅 " + formatDate(selectedDate[0]));
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

        // Build Dialog
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(editTask != null ? "✏️ Editar Tarefa" : "➕ Nova Tarefa")
                .setView(dialogView)
                .setPositiveButton(editTask != null ? "Salvar" : "Criar", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(), "O título não pode estar vazio", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String desc = etDesc.getText().toString().trim();
                    String cat = categories[spCategory.getSelectedItemPosition()];
                    String pri = priorities[spPriority.getSelectedItemPosition()];

                    if (editTask != null) {
                        // UC4 - Editar
                        editTask.setTitulo(title);
                        editTask.setDescricao(desc);
                        editTask.setCategoria(cat);
                        editTask.setPrioridade(pri);
                        editTask.setPrazo(selectedDate[0]);
                        dataManager.updateTask(editTask);
                        Toast.makeText(requireContext(), "✅ Tarefa atualizada!", Toast.LENGTH_SHORT).show();
                    } else {
                        // UC3 - Criar
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
    }

    @Override
    public void onTaskChecked(Task task) {
        dataManager.toggleTaskStatus(task.getId());
        String msg = task.isConcluida() ? "Tarefa reaberta" : "✅ Tarefa concluída! +XP";
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        loadTasks();
    }

    @Override
    public void onTaskEdit(Task task) {
        showTaskDialog(task);
    }

    @Override
    public void onTaskDelete(Task task) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("🗑️ Excluir Tarefa")
                .setMessage("Deseja remover \"" + task.getTitulo() + "\"?")
                .setPositiveButton("Excluir", (d, w) -> {
                    dataManager.deleteTask(task.getId());
                    Toast.makeText(requireContext(), "Tarefa excluída", Toast.LENGTH_SHORT).show();
                    loadTasks();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) return "Hoje";
        String[] parts = date.split("-");
        if (parts.length == 3) return parts[2] + "/" + parts[1];
        return date;
    }
}
