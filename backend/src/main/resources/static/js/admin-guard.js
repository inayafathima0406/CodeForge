// Stricter than auth-guard: also requires ADMIN role, checked via a lightweight API call
(async function requireAdmin() {
    const token = localStorage.getItem("cf_token");
    if (!token) { window.location.href = "/pages/login.html"; return; }
    try {
        const me = await api.get("/users/me");
        if (me.role !== "ADMIN") {
            document.body.innerHTML = '<div class="container" style="padding-top:60px;"><p class="error-text">Admins only.</p></div>';
            throw new Error("not admin");
        }
    } catch (err) {
        if (err.message !== "not admin") window.location.href = "/pages/login.html";
    }
})();