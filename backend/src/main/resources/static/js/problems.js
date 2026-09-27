let allTopics = [];

async function loadTopics() {
    // No dedicated /api/topics endpoint yet, so derive the topic list from the questions themselves
    const questions = await api.get("/questions");
    const topicNames = [...new Set(questions.map(q => q.topic))].sort();
    const topicSelect = document.getElementById("topic-filter");
    topicNames.forEach(name => {
        const opt = document.createElement("option");
        opt.value = name;
        opt.textContent = name;
        topicSelect.appendChild(opt);
    });
}

function difficultyBadgeClass(difficulty) {
    if (difficulty === "EASY") return "badge-easy";
    if (difficulty === "MEDIUM") return "badge-medium";
    return "badge-hard";
}

function renderProblems(questions) {
    const listEl = document.getElementById("problems-list");

    if (questions.length === 0) {
        listEl.innerHTML = '<p class="text-muted">No problems match your filters.</p>';
        return;
    }

    listEl.innerHTML = questions.map(q => `
        <a href="/pages/problem-detail.html?id=${q.id}" class="card"
           style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; color: inherit;">
            <div>
                <div style="font-weight: 600; margin-bottom: 4px;">${q.title}</div>
                <div class="text-muted" style="font-size: 13px;">${q.topic}</div>
            </div>
            <span class="badge ${difficultyBadgeClass(q.difficulty)}">${q.difficulty}</span>
        </a>
    `).join("");
}

async function loadProblems() {
    const loadingEl = document.getElementById("loading-text");
    const errorEl = document.getElementById("error-message");
    loadingEl.style.display = "block";
    errorEl.style.display = "none";

    const topic = document.getElementById("topic-filter").value;
    const difficulty = document.getElementById("difficulty-filter").value;
    const search = document.getElementById("search-input").value;

    const params = new URLSearchParams();
    if (topic) params.set("topic", topic);
    if (difficulty) params.set("difficulty", difficulty);
    if (search) params.set("search", search);

    try {
        const questions = await api.get("/questions?" + params.toString());
        renderProblems(questions);
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    } finally {
        loadingEl.style.display = "none";
    }
}

let searchDebounceTimer;
document.getElementById("search-input").addEventListener("input", () => {
    clearTimeout(searchDebounceTimer);
    searchDebounceTimer = setTimeout(loadProblems, 300);
});
document.getElementById("topic-filter").addEventListener("change", loadProblems);
document.getElementById("difficulty-filter").addEventListener("change", loadProblems);

loadTopics().then(loadProblems);
