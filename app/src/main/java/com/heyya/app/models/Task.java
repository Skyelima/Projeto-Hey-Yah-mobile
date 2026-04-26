package com.heyya.app.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task implements Serializable {
    private int id;
    private String titulo;
    private String descricao;
    private String prazo;
    private String categoria; // estudo, trabalho, saude, pessoal
    private String prioridade; // alta, media, baixa
    private String status; // pendente, concluida
    private long criadoEm;

    public Task() {
        this.criadoEm = System.currentTimeMillis();
        this.status = "pendente";
    }

    public Task(int id, String titulo, String descricao, String prazo,
                String categoria, String prioridade) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prazo = prazo;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.status = "pendente";
        this.criadoEm = System.currentTimeMillis();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPrazo() { return prazo; }
    public void setPrazo(String prazo) { this.prazo = prazo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCriadoEm() { return criadoEm; }
    public void setCriadoEm(long criadoEm) { this.criadoEm = criadoEm; }

    public boolean isConcluida() { return "concluida".equals(status); }

    public void toggleStatus() {
        status = isConcluida() ? "pendente" : "concluida";
    }

    public boolean isHoje() {
        String hoje = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return hoje.equals(prazo);
    }

    public String getCategoriaEmoji() {
        switch (categoria) {
            case "estudo": return "📚";
            case "trabalho": return "💼";
            case "saude": return "❤️";
            case "pessoal": return "🌟";
            default: return "📋";
        }
    }

    public int getPrioridadeColor() {
        switch (prioridade) {
            case "alta": return 0xFFFF5252;
            case "media": return 0xFFFFB74D;
            case "baixa": return 0xFF00E676;
            default: return 0xFF8888A0;
        }
    }
}
