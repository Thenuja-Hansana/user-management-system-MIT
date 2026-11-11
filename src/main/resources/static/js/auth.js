// [NEW FILE - login] Small helpers shared by login.html, index.html and account.html.

// Spring Security puts a security token (CSRF token) in a cookie called XSRF-TOKEN.
// Every POST / PUT / PATCH / DELETE must send it back in the X-XSRF-TOKEN header.
// Other websites cannot read our cookies, so this proves the request really came from our own pages.
function csrfToken() {
    const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
    return match ? decodeURIComponent(match[1]) : "";
}

// fetch() for our API: adds the security token, and if the session has ended (401 = not logged in)
// it sends the user to the login page instead of showing an error.
async function apiFetch(url, options = {}) {
    const headers = { ...(options.headers || {}), "X-XSRF-TOKEN": csrfToken() };
    const response = await fetch(url, { ...options, headers });
    if (response.status === 401) {
        // [CHANGED - session fix] "?expired" makes the login page explain why you are there
        location.href = "/login.html?expired";
        return new Promise(() => {});   // never finishes: the page is changing anyway
    }
    return response;
}

// [ADDED - tabs fix] All tabs of one browser share ONE login (the session cookie).
// When someone logs in or out in one tab, we tell the other tabs, and they reload.
// So a tab never keeps showing the Admin screens after another tab logged in as someone else.
const AUTH_CHANGED_KEY = "cms-auth-changed";

function notifyAuthChanged() {
    try {
        localStorage.setItem(AUTH_CHANGED_KEY, String(Date.now()));
    } catch (e) {
        // storage is blocked in this browser: the 403 check in index.html still protects us
    }
}

// The "storage" event fires in the OTHER tabs of this site when one tab writes to localStorage
window.addEventListener("storage", (event) => {
    if (event.key === AUTH_CHANGED_KEY) {
        location.reload();
    }
});

// Log out: end the session on the server, then go to the login page.
async function logout() {
    try {
        await fetch("/logout", { method: "POST", headers: { "X-XSRF-TOKEN": csrfToken() } });
    } finally {
        notifyAuthChanged();   // [ADDED - tabs fix] other tabs reload (and land on the login page)
        location.href = "/login.html?loggedOut";
    }
}
