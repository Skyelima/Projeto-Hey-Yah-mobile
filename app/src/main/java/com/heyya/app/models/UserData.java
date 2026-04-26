package com.heyya.app.models;

import java.io.Serializable;

public class UserData implements Serializable {
    private int pontos;
    private int nivel;
    private String escala; // 12x36, 5x2, 6x1, plantao, flexivel
    private boolean aiUsed;
    private int totalCreated;
    private int totalDone;
    private int todayDone;

    public UserData() {
        this.pontos = 0;
        this.nivel = 1;
        this.escala = null;
        this.aiUsed = false;
        this.totalCreated = 5;
        this.totalDone = 1;
        this.todayDone = 0;
    }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    public String getEscala() { return escala; }
    public void setEscala(String escala) { this.escala = escala; }

    public boolean isAiUsed() { return aiUsed; }
    public void setAiUsed(boolean aiUsed) { this.aiUsed = aiUsed; }

    public int getTotalCreated() { return totalCreated; }
    public void setTotalCreated(int totalCreated) { this.totalCreated = totalCreated; }

    public int getTotalDone() { return totalDone; }
    public void setTotalDone(int totalDone) { this.totalDone = totalDone; }

    public int getTodayDone() { return todayDone; }
    public void setTodayDone(int todayDone) { this.todayDone = todayDone; }

    public void addPoints(int points) {
        this.pontos += points;
        this.nivel = (this.pontos / 100) + 1;
    }

    public int getXpInLevel() { return pontos % 100; }
    public int getXpProgress() { return Math.min(getXpInLevel(), 100); }

    public String getNivelTitulo() {
        String[] titles = {"Iniciante", "Aprendiz", "Dedicado", "Focado", "Veterano", "Mestre", "Lendário"};
        int idx = Math.min(nivel - 1, titles.length - 1);
        return titles[idx];
    }

    public String getEscalaLabel() {
        if (escala == null) return "Não configurada";
        switch (escala) {
            case "12x36": return "12×36 (Plantão)";
            case "5x2": return "5×2 (Comercial)";
            case "6x1": return "6×1 (Intensiva)";
            case "plantao": return "Plantão Esporádico";
            case "flexivel": return "Flexível";
            default: return escala;
        }
    }

    public boolean isPlantao() {
        return "12x36".equals(escala) || "plantao".equals(escala);
    }
}
