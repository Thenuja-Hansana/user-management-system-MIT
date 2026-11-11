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
        location.href = "/login.html";
        return new Promise(() => {});   // never finishes: the page is changing anyway
    }
    return response;
}

// Log out: end the session on the server, then go to the login page.
async function logout() {
    try {
        await fetch("/logout", { method: "POST", headers: { "X-XSRF-TOKEN": csrfToken() } });
    } finally {
        location.href = "/login.html?loggedOut";
    }
}
