// Include this script on any page that requires the user to be logged in.
// It must load AFTER api.js.
(function requireAuth() {
    const token = localStorage.getItem("cf_token");
    if (!token) {
        window.location.href = "/pages/login.html";
    }
})();