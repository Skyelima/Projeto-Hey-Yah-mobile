<p align="center">
  <img src="mascot.png" width="180" alt="Hey Ya! Mascot"/>
</p>

<h1 align="center">Hey Ya! 🐧</h1>
<h3 align="center">Gestão Inteligente de Rotina — Android App</h3>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/SDK-35-blue" />
  <img src="https://img.shields.io/badge/Material-3-6C63FF" />
  <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow" />
</p>

<p align="center">
  <b>Aplicativo mobile para estudantes, freelancers e profissionais de escala</b><br/>
  <sub>Organização inteligente com IA, gamificação e CRUD completo de tarefas</sub>
</p>

---

## 📖 Sobre o Projeto

**Hey Ya!** é um aplicativo Android nativo desenvolvido em **Java** como projeto acadêmico da **UNICID**, focado em ajudar pessoas com rotinas variáveis a organizarem suas tarefas de forma inteligente.

O app utiliza conceitos de **Engenharia de Software** como Casos de Uso, Regras de Negócio e Arquitetura em camadas, combinados com uma interface moderna em **Material Design 3** no estilo *Quiet Luxury* (dark mode premium).

> 🐧 **Nosso mascote** é um pinguim simpático que representa organização, disciplina e fofura!

---

## ✨ Funcionalidades

### 📋 Gestão de Tarefas (UC2-UC5)
- **Criar** tarefas com título, descrição, categoria, prioridade e prazo
- **Editar** qualquer propriedade de uma tarefa existente
- **Excluir** tarefas com confirmação
- **Concluir/Reabrir** tarefas com um toque
- **Filtrar** por: Todas · Pendentes · Concluídas · Estudo · Trabalho · Saúde

### ⚙️ Configuração de Escala (UC1)
| Escala | Descrição |
|--------|-----------|
| 🏥 12×36 | 12 horas de trabalho, 36 de descanso |
| 🏢 5×2 | 5 dias úteis, 2 de folga |
| ⚡ 6×1 | 6 dias de trabalho, 1 folga semanal |
| 🚑 Plantão | Plantões esporádicos, escala variável |
| 🎯 Flexível | Freelancer / horários flexíveis |

### 📊 Dashboard Inteligente (UC6)
- Estatísticas em tempo real (total, feitas, pendentes, XP)
- Lista de tarefas do dia com toggle rápido
- Barra de progresso XP e nível atual
- Dica da IA gerada automaticamente

### 🤖 Sugestões de IA (UC8 / UC10)
- Cronograma otimizado baseado na sua escala
- Análise de carga cognitiva e sugestão de pausas
- Reorganização de prioridades
- Foco recomendado na tarefa mais urgente
- Alertas de escala para plantão (RN02)

### 🏆 Gamificação (UC9)
| Badge | Nome | Como desbloquear |
|-------|------|-------------------|
| 🎯 | Primeira Tarefa | Crie sua primeira tarefa |
| ⭐ | 5 Concluídas | Conclua 5 tarefas |
| 🌟 | 10 Concluídas | Conclua 10 tarefas |
| ⚙️ | Organizado | Configure sua escala |
| 🔥 | 3 Seguidas | Complete 3 no mesmo dia |
| 🤖 | Tech Savvy | Use sugestões da IA |
| 🌈 | Equilibrado | Tarefas em todas as categorias |
| 👑 | Veterano | Alcance nível 5 |

**Sistema de XP:**
- Tarefa baixa prioridade: **+10 XP**
- Tarefa média prioridade: **+15 XP**
- Tarefa alta prioridade: **+25 XP**
- Cada **100 XP** = sobe 1 nível

### 🔐 Autenticação Mockada (UC11)
```
Usuário: admin
Senha:   1234
```

---

## 📐 Regras de Negócio

| Regra | Descrição |
|-------|-----------|
| **RN01** — Eisenhower | Alerta quando há 3+ tarefas de alta prioridade no mesmo dia (prevenção de burnout) |
| **RN02** — Consciência de Escala | Em dias de plantão/12x36, bloqueia tarefas cognitivas pesadas após 8h contínuas |
| **RN03** — Privacidade | Nenhuma PII é enviada à IA — apenas contexto de tarefas e escala são processados |

---

## 🏗️ Arquitetura

```
📦 com.heyya.app
├── 📄 LoginActivity.java          → Tela de login mockado
├── 📄 MainActivity.java           → Navigation Drawer + Fragment Manager
│
├── 📁 models/
│   ├── 📄 Task.java               → Modelo de tarefa (CRUD)
│   └── 📄 UserData.java           → Dados do usuário (XP, nível, escala)
│
├── 📁 data/
│   └── 📄 MockDataManager.java    → SharedPreferences como mock MongoDB
│
├── 📁 adapters/
│   └── 📄 TaskAdapter.java        → RecyclerView adapter para tarefas
│
└── 📁 fragments/
    ├── 📄 DashboardFragment.java   → Estatísticas + tarefas do dia
    ├── 📄 TasksFragment.java       → CRUD completo com filtros
    ├── 📄 ScheduleFragment.java    → Seleção de escala de trabalho
    ├── 📄 AIFragment.java          → Sugestões mockadas de IA
    └── 📄 GamificationFragment.java → Badges, XP e níveis
```

### Layouts XML
```
📦 res/layout/
├── activity_login.xml        → Tela de login
├── activity_main.xml         → Drawer + Toolbar + Container
├── nav_header.xml            → Header do menu lateral
├── fragment_dashboard.xml    → Dashboard com cards
├── fragment_tasks.xml        → Lista de tarefas + filtros + botão criar
├── fragment_schedule.xml     → Cards de escala selecionáveis
├── fragment_ai.xml           → Sugestões de IA
├── fragment_gamification.xml → Nível, XP e grid de badges
├── dialog_task.xml           → Modal de criar/editar tarefa
├── item_task.xml             → Card de tarefa no RecyclerView
├── item_mini_task.xml        → Mini tarefa no dashboard
├── item_ai_suggestion.xml    → Card de sugestão de IA
└── item_badge.xml            → Card de badge
```

---

## 🎨 Design System — Quiet Luxury

O app segue o design system **Quiet Luxury** com dark mode premium:

| Token | Valor | Uso |
|-------|-------|-----|
| `bg_primary` | `#0A0A0F` | Fundo principal |
| `bg_secondary` | `#12121A` | Fundo toolbar/drawer |
| `bg_card` | `#1E1E2E` | Cards |
| `accent_primary` | `#6C63FF` | Botões, links, destaques |
| `accent_secondary` | `#00D9FF` | XP, info, secondary CTAs |
| `text_primary` | `#E8E8ED` | Texto principal |
| `text_secondary` | `#8888A0` | Texto secundário |
| `success` | `#00E676` | Concluído |
| `warning` | `#FFB74D` | Atenção |
| `error` | `#FF5252` | Erro / Alta prioridade |

---

## 🚀 Como Rodar

### Pré-requisitos
- **Android Studio** Panda (2024.3+) ou superior
- **JDK 17**
- Emulador ou dispositivo com **Android 10+** (API 29)

### Passo a passo
```bash
# 1. Clone o repositório
git clone https://github.com/Skyelima/Projeto-Hey-Yah-mobile.git

# 2. Abra no Android Studio
#    File → Open → selecione a pasta clonada

# 3. Aguarde o Gradle Sync

# 4. Rode no emulador ou dispositivo
#    Clique ▶️ Run (Shift + F10)
```

### Login
```
👤 Usuário: admin
🔑 Senha:   1234
```

---

## 📚 Casos de Uso Implementados

| UC | Nome | Status |
|----|------|--------|
| UC1 | Configurar Ciclo de Trabalho | ✅ |
| UC2 | Gerenciar Tarefas | ✅ |
| UC3 | Criar Tarefa | ✅ |
| UC4 | Editar Tarefa | ✅ |
| UC5 | Excluir Tarefa | ✅ |
| UC6 | Visualizar Dashboard | ✅ |
| UC8 | Gerar Sugestões de IA | ✅ (mock) |
| UC9 | Sistema de Gamificação | ✅ |
| UC10 | Cronograma Inteligente | ✅ (mock) |
| UC11 | Persistência Local | ✅ (SharedPreferences) |

---

## 🧰 Tecnologias

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 17 | Linguagem principal |
| Android SDK | 35 | Target API |
| Material Design 3 | 1.12.0 | Componentes de UI |
| Gson | 2.10.1 | Serialização JSON |
| SharedPreferences | — | Persistência local (mock MongoDB) |
| RecyclerView | 1.3.2 | Listas performáticas |
| AppCompat | 1.7.0 | Compatibilidade retroativa |

---

## 🔮 Roadmap — Próximos Passos

- [ ] 🔗 Integração real com **OpenAI API** para sugestões de IA
- [ ] 🗄️ Migrar de SharedPreferences para **Room Database**
- [ ] 🌐 Backend REST com **MongoDB Atlas** via Retrofit
- [ ] 🔔 Notificações push para prazos de tarefas
- [ ] 📅 Integração com **Google Calendar**
- [ ] 🌙 Modo claro (light theme)
- [ ] 📊 Gráficos de produtividade com **MPAndroidChart**
- [ ] 🔒 Autenticação real com **Firebase Auth**
- [ ] 📱 Widget de tarefas na home screen
- [ ] 🌍 Suporte multilíngue (PT-BR / EN)

---

## 👥 Equipe

| Papel | Responsabilidade |
|-------|-----------------|
| **Desenvolvedor Full-Stack** | Arquitetura, Java/Android, UI/UX |
| **Orientação Acadêmica** | Validação de requisitos e engenharia de software |

---

## 📄 Licença

Este projeto é desenvolvido para fins acadêmicos na **UNICID** — Universidade Cidade de São Paulo.

---

<p align="center">
  <img src="mascot.png" width="60" alt="Hey Ya!"/>
  <br/>
  <sub>Feito com 💜 pela equipe Hey Ya!</sub>
</p>
