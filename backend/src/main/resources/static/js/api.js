const API_BASE = "/api";

function getToken() {
    return localStorage.getItem("cf_token");
}

function setToken(token) {
    localStorage.setItem("cf_token", token);
}

function clearToken() {
    localStorage.removeItem("cf_token");
}

async function apiRequest(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    const token = getToken();
    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }

    const response = await fetch(API_BASE + path, { ...options, headers });

    if (response.status === 401) {
        clearToken();
        if (!path.startsWith("/auth/")) {
            window.location.href = "/pages/login.html";
            return;
        }
    }

    const isJson = response.headers.get("content-type")?.includes("application/json");
    const body = isJson ? await response.json() : null;

    if (!response.ok) {
        const message = body?.message || "Something went wrong. Please try again.";
        throw new Error(message);
    }

    return body;
}

const api = {
    get: (path) => apiRequest(path, { method: "GET" }),
    post: (path, data) => apiRequest(path, { method: "POST", body: JSON.stringify(data) }),
    put: (path, data) => apiRequest(path, { method: "PUT", body: JSON.stringify(data) }),
    del: (path) => apiRequest(path, { method: "DELETE" }),
};