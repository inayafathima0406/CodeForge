// Include this script on any page that should show the shared navbar.
// Expects an element with id="navbar" already in the page.
(function renderNavbar() {
    const token = localStorage.getItem("cf_token");
    const navbarEl = document.getElementById("navbar");
    if (!navbarEl) return;

    const loggedOutLinks = `
        <a href="/pages/login.html">Log in</a>
        <a href="/pages/register.html" class="btn btn-primary">Sign up</a>
    `;

    const loggedInLinks = `
        <a href="/pages/dashboard.html">Dashboard</a>
        <a href="/pages/problems.html">Problems</a>
        <a href="#" id="logout-link">Log out</a>
    `;

    navbarEl.innerHTML = `
        <strong><a href="/index.html" style="color: inherit;">CodeForge</a></strong>
        <div class="navbar-links">
            ${token ? loggedInLinks : loggedOutLinks}
        </div>
    `;

    const logoutLink = document.getElementById("logout-link");
    if (logoutLink) {
        logoutLink.addEventListener("click", (e) => {
            e.preventDefault();
            clearToken();
            window.location.href = "/index.html";
        });
    }
})();