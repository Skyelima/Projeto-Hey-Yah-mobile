package com.heyya.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heyya.app.models.Task;
import com.heyya.app.models.UserData;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * MockDataManager — Simula persistência MongoDB (UC11)
 * Usa SharedPreferences + Gson como armazenamento local
 */
public class MockDataManager {

    private static final String PREFS_NAME = "heyya_prefs";
    private static final String KEY_TASKS = "tasks";
    private static final String KEY_USER = "user_data";
    private static final String KEY_LOGGED_IN = "logged_in";

    // Mock credentials
    public static final String MOCK_USERNAME = "admin";
    public static final String MOCK_PASSWORD = "1234";
    public static final String MOCK_NAME = "Admin";

    private final SharedPreferences prefs;
    private final Gson gson;

    public MockDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // ===== AUTENTICAÇÃO =====
    public boolean authenticate(String username, String password) {
        return MOCK_USERNAME.equals(username) && MOCK_PASSWORD.equals(password);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    // ===== TASKS (UC2-UC5) =====
    public List<Task> getTasks() {
        String json = prefs.getString(KEY_TASKS, null);
        if (json == null) {
            List<Task> defaults = createDefaultTasks();
            saveTasks(defaults);
            return defaults;
        }
        Type type = new TypeToken<List<Task>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void saveTasks(List<Task> tasks) {
        prefs.edit().putString(KEY_TASKS, gson.toJson(tasks)).apply();
    }

    // UC3 - Criar Tarefa
    public void addTask(Task task) {
        List<Task> tasks = getTasks();
        int maxId = 0;
        for (Task t : tasks) {
            if (t.getId() > maxId) maxId = t.getId();
        }
        task.setId(maxId + 1);
        tasks.add(task);
        saveTasks(tasks);

        UserData user = getUserData();
        user.setTotalCreated(user.getTotalCreated() + 1);
        saveUserData(user);
    }

    // UC4 - Editar Tarefa
    public void updateTask(Task updated) {
        List<Task> tasks = getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updated.getId()) {
                tasks.set(i, updated);
                break;
            }
        }
        saveTasks(tasks);
    }

    // UC5 - Excluir Tarefa
    public void deleteTask(int taskId) {
        List<Task> tasks = getTasks();
        tasks.removeIf(t -> t.getId() == taskId);
        saveTasks(tasks);
    }

    // Toggle status + gamificação (UC9)
    public void toggleTaskStatus(int taskId) {
        List<Task> tasks = getTasks();
        for (Task t : tasks) {
            if (t.getId() == taskId) {
                boolean wasDone = t.isConcluida();
                t.toggleStatus();
                if (!wasDone) {
                    UserData user = getUserData();
                    int points = 10;
                    if ("alta".equals(t.getPrioridade())) points = 25;
                    else if ("media".equals(t.getPrioridade())) points = 15;
                    user.addPoints(points);
                    user.setTotalDone(user.getTotalDone() + 1);
                    user.setTodayDone(user.getTodayDone() + 1);
                    saveUserData(user);
                }
                break;
            }
        }
        saveTasks(tasks);
    }

    // Filtros
    public List<Task> getTasksByStatus(String status) {
        List<Task> result = new ArrayList<>();
        for (Task t : getTasks()) {
            if (status.equals(t.getStatus())) result.add(t);
        }
        return result;
    }

    public List<Task> getTasksByCategory(String category) {
        List<Task> result = new ArrayList<>();
        for (Task t : getTasks()) {
            if (category.equals(t.getCategoria())) result.add(t);
        }
        return result;
    }

    public List<Task> getTodayTasks() {
        String hoje = getTodayString();
        List<Task> result = new ArrayList<>();
        for (Task t : getTasks()) {
            if (hoje.equals(t.getPrazo())) result.add(t);
        }
        return result;
    }

    // RN01 - Eisenhower: max 3 tarefas alta prioridade/dia
    public int getHighPriorityTodayCount() {
        String hoje = getTodayString();
        int count = 0;
        for (Task t : getTasks()) {
            if ("alta".equals(t.getPrioridade()) && hoje.equals(t.getPrazo()) && !t.isConcluida()) {
                count++;
            }
        }
        return count;
    }

    // ===== USER DATA =====
    public UserData getUserData() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return new UserData();
        return gson.fromJson(json, UserData.class);
    }

    public void saveUserData(UserData data) {
        prefs.edit().putString(KEY_USER, gson.toJson(data)).apply();
    }

    // UC1 - Configurar escala
    public void setSchedule(String schedule) {
        UserData user = getUserData();
        user.setEscala(schedule);
        saveUserData(user);
    }

    // ===== STATS =====
    public int getPendingCount() {
        int count = 0;
        for (Task t : getTasks()) {
            if (!t.isConcluida()) count++;
        }
        return count;
    }

    public int getDoneCount() {
        int count = 0;
        for (Task t : getTasks()) {
            if (t.isConcluida()) count++;
        }
        return count;
    }

    public int getCategoryCount() {
        List<String> cats = new ArrayList<>();
        for (Task t : getTasks()) {
            if (!cats.contains(t.getCategoria())) cats.add(t.getCategoria());
        }
        return cats.size();
    }

    // ===== HELPERS =====
    public static String getTodayString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    private List<Task> createDefaultTasks() {
        String hoje = getTodayString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        String amanha = sdf.format(cal.getTime());

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "Estudar Java Collections", "Revisar ArrayList, HashMap e TreeSet", hoje, "estudo", "alta"));
        tasks.add(new Task(2, "Reunião de equipe", "Alinhamento semanal do projeto UNICID", hoje, "trabalho", "media"));

        Task t3 = new Task(3, "Caminhada 30min", "Atividade física leve para descanso mental", hoje, "saude", "baixa");
        t3.setStatus("concluida");
        tasks.add(t3);

        tasks.add(new Task(4, "Implementar CRUD de Tarefas", "Finalizar UC2-UC5 do projeto Hey Ya!", hoje, "trabalho", "alta"));
        tasks.add(new Task(5, "Ler artigo sobre MongoDB", "Entender schema flexível para o projeto", amanha, "estudo", "media"));

        return tasks;
    }
}
