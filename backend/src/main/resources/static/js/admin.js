let testCaseCount = 0;

function addTestCaseRow(data) {
    const id = testCaseCount++;
    const div = document.createElement("div");
    div.className = "card";
    div.style.cssText = "margin-bottom:8px; padding:12px;";
    div.id = "tc-" + id;
    div.innerHTML = `
        <div style="display:flex; gap:8px; margin-bottom:6px;">
            <input class="input tc-input" placeholder="Input" style="flex:1;" value="${data ? escapeHtml(data.inputData) : ""}">
            <input class="input tc-expected" placeholder="Expected Output" style="flex:1;" value="${data ? escapeHtml(data.expectedOutput) : ""}">
        </div>
        <div style="display:flex; gap:8px; align-items:center;">
            <label style="display:flex; align-items:center; gap:4px; margin:0;">
                <input type="checkbox" class="tc-sample" ${!data || data.sample ? "checked" : ""}> Sample (visible)
            </label>
            <input class="input tc-explanation" placeholder="Explanation (optional)" style="flex:1;" value="${data && data.explanation ? escapeHtml(data.explanation) : ""}">
            <button type="button" class="btn btn-outline" onclick="document.getElementById('tc-${id}').remove()">Remove</button>
        </div>
    `;
    document.getElementById("test-cases").appendChild(div);
}

function collectTestCases() {
    return [...document.querySelectorAll("#test-cases > div")].map(row => ({
        inputData: row.querySelector(".tc-input").value,
        expectedOutput: row.querySelector(".tc-expected").value,
        sample: row.querySelector(".tc-sample").checked,
        explanation: row.querySelector(".tc-explanation").value || null
    }));
}

function openForm(question) {
    document.getElementById("form-title").textContent = question ? "Edit Question" : "New Question";
    document.getElementById("q-id").value = question ? question.id : "";
    document.getElementById("q-title").value = question ? question.title : "";
    document.getElementById("q-statement").value = question ? question.statement : "";
    document.getElementById("q-input").value = question ? question.inputDescription : "";
    document.getElementById("q-output").value = question ? question.outputDescription : "";
    document.getElementById("q-constraints").value = question ? question.constraints : "";
    document.getElementById("q-difficulty").value = question ? question.difficulty : "EASY";
    document.getElementById("q-topic").value = question ? question.topic : "";
    document.getElementById("test-cases").innerHTML = "";
    testCaseCount = 0;
    document.getElementById("form-error").style.display = "none";
    if (question) {
        api.get("/questions/" + question.id).then(full => {
            full.samples.forEach(s => addTestCaseRow({ inputData: s.input, expectedOutput: s.expectedOutput, sample: true, explanation: s.explanation }));
        });
    } else {
        addTestCaseRow();
    }
    document.getElementById("form-overlay").style.display = "block";
}

function closeForm() {
    document.getElementById("form-overlay").style.display = "none";
}

document.getElementById("question-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const errEl = document.getElementById("form-error");
    errEl.style.display = "none";

    const id = document.getElementById("q-id").value;
    const payload = {
        title: document.getElementById("q-title").value,
        statement: document.getElementById("q-statement").value,
        inputDescription: document.getElementById("q-input").value,
        outputDescription: document.getElementById("q-output").value,
        constraints: document.getElementById("q-constraints").value,
        difficulty: document.getElementById("q-difficulty").value,
        topic: document.getElementById("q-topic").value,
        testCases: collectTestCases()
    };

    try {
        if (id) {
            await api.put("/admin/questions/" + id, payload);
        } else {
            await api.post("/admin/questions", payload);
        }
        closeForm();
        loadQuestions();
    } catch (err) {
        errEl.textContent = err.message;
        errEl.style.display = "block";
    }
});

async function deleteQuestion(id, title) {
    if (!confirm("Delete \"" + title + "\"? This cannot be undone.")) return;
    try {
        await api.del("/admin/questions/" + id);
        loadQuestions();
    } catch (err) {
        alert(err.message);
    }
}

async function loadQuestions() {
    const loadingEl = document.getElementById("loading-text");
    const errorEl = document.getElementById("error-message");
    const listEl = document.getElementById("questions-list");
    try {
        const questions = await api.get("/questions");
        loadingEl.style.display = "none";
        listEl.innerHTML = questions.map(q => `
            <div class="card" style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
                <div>
                    <div style="font-weight:600;">${escapeHtml(q.title)}</div>
                    <div class="text-muted" style="font-size:13px;">${escapeHtml(q.topic)} &middot; ${q.difficulty}</div>
                </div>
                <div style="display:flex; gap:8px;">
                    <button class="btn btn-outline" onclick='openForm(${JSON.stringify(q).replace(/'/g, "&apos;")})'>Edit</button>
                    <button class="btn btn-outline" style="border-color:var(--color-danger); color:var(--color-danger);" onclick="deleteQuestion(${q.id}, '${escapeHtml(q.title).replace(/'/g, "\\'")}')">Delete</button>
                </div>
            </div>
        `).join("");
    } catch (err) {
        loadingEl.style.display = "none";
        errorEl.textContent = err.message;
        errorEl.style.display = "block";
    }
}

loadQuestions();