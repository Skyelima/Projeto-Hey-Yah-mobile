package com.heyya.app.fragments;

import android.app.AlertDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.heyya.app.R;
import com.heyya.app.adapters.TaskAdapter;
import com.heyya.app.data.MockDataManager;
import com.heyya.app.models.Task;
import java.text.SimpleDateFormat;
import java.util.*;

public class TasksFragment extends Fragment implements TaskAdapter.TaskListener {

    private MockDataManager dataManager;
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
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

        // FAB
        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showTaskDialog(null));

        // Filter chips
        ChipGroup chipGroup = view.findViewById(R.id.filter_chips);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip chip = view.findViewById(checkedIds.get(0));
            if (chip != null) {
                currentFilter = chip.getTag().toString();
                loadTasks();
            }
        });

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
    }

    // UC3 / UC4 - Create or Edit Task Dialog
    private void showTaskDialog(@Nullable Task editTask) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_task, null);

        EditText etTitle = dialogView.findViewById(R.id.et_task_title);
        EditText etDesc = dialogView.findViewById(R.id.et_task_desc);
        Spinner spCategory = dialogView.findViewById(R.id.sp_category);
        Spinner spPriority = dialogView.findViewById(R.id.sp_priority);
        TextView tvDeadline = dialogView.findViewById(R.id.tv_deadline);
        TextView tvWarning = dialogView.findViewById(R.id.tv_rn_warning);

        // Spinners
        String[] categories = {"estudo", "trabalho", "saude", "pessoal"};
        String[] catLabels = {"📚 Estudo", "💼 Trabalho", "❤️ Saúde", "🌟 Pessoal"};
        spCategory.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, catLabels));

        String[] priorities = {"baixa", "media", "alta"};
        String[] priLabels = {"🟢 Baixa", "🟡 Média", "🔴 Alta"};
        spPriority.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, priLabels));

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

        // RN01 Check
        if (dataManager.getHighPriorityTodayCount() >= 3) {
            tvWarning.setVisibility(View.VISIBLE);
        }

        // If editing, fill fields
        if (editTask != null) {
            etTitle.setText(editTask.getTitulo());
            etDesc.setText(editTask.getDescricao());
            selectedDate[0] = editTask.getPrazo();
            tvDeadline.setText("📅 " + formatDate(editTask.getPrazo()));
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(editTask.getCategoria())) spCategory.setSelection(i);
            }
            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equals(editTask.getPrioridade())) spPriority.setSelection(i);
            }
        }

        new AlertDialog.Builder(requireContext(), R.style.Theme_HeyYa_Dialog)
                .setTitle(editTask != null ? "Editar Tarefa" : "Nova Tarefa")
                .setView(dialogView)
                .setPositiveButton(editTask != null ? "Salvar" : "Criar", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) return;

                    if (editTask != null) {
                        editTask.setTitulo(title);
                        editTask.setDescricao(etDesc.getText().toString().trim());
                        editTask.setCategoria(categories[spCategory.getSelectedItemPosition()]);
                        editTask.setPrioridade(priorities[spPriority.getSelectedItemPosition()]);
                        editTask.setPrazo(selectedDate[0]);
                        dataManager.updateTask(editTask);
                    } else {
                        Task newTask = new Task();
                        newTask.setTitulo(title);
                        newTask.setDescricao(etDesc.getText().toString().trim());
                        newTask.setCategoria(categories[spCategory.getSelectedItemPosition()]);
                        newTask.setPrioridade(priorities[spPriority.getSelectedItemPosition()]);
                        newTask.setPrazo(selectedDate[0]);
                        dataManager.addTask(newTask);
                    }
                    loadTasks();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onTaskChecked(Task task) {
        dataManager.toggleTaskStatus(task.getId());
        loadTasks();
    }

    @Override
    public void onTaskEdit(Task task) {
        showTaskDialog(task);
    }

    @Override
    public void onTaskDelete(Task task) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_HeyYa_Dialog)
                .setTitle("Excluir Tarefa")
                .setMessage("Remover \"" + task.getTitulo() + "\"?")
                .setPositiveButton("Excluir", (d, w) -> {
                    dataManager.deleteTask(task.getId());
                    loadTasks();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String formatDate(String date) {
        if (date == null) return "";
        String[] parts = date.split("-");
        if (parts.length == 3) return parts[2] + "/" + parts[1];
        return date;
    }
}
