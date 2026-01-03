import axios from "axios";

// Skapa en axios-instans
export const apiClient = axios.create({
    baseURL: "/api", // Relative URL - works in dev (via Vite proxy) and production (served by Spring Boot)
    headers: {
        "Content-Type": "application/json",
    },
});

// Interceptor för att lägga till token i varje förfrågan
apiClient.interceptors.request.use(async (config) => {
    let token = localStorage.getItem("accessToken");

    if (token) {
        const expiration = JSON.parse(atob(token.split(".")[1])).exp * 1000;
        if (Date.now() >= expiration) {
            token = await refreshToken();
        }
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Interceptor för att hantera 401-fel
apiClient.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401) {
            console.error("Token ogiltig eller utgången. Loggar ut användaren...");
            logout();
        }
        return Promise.reject(error);
    }
);

// Funktion för att förnya token
const refreshToken = async () => {
    try {
        const refreshToken = localStorage.getItem("refreshToken");

        if (!refreshToken) {
            console.error("Ingen refreshToken hittades, loggar ut...");
            logout();
            return null;
        }

        const response = await axios.post(`${apiClient.defaults.baseURL}/auth/refresh`, { refreshToken });

        localStorage.setItem("accessToken", response.data.accessToken);
        return response.data.accessToken;
    } catch (error) {
        console.error("Failed to refresh token:", error);
        logout();
        return null;
    }
};

// Funktion för att logga ut användaren
export const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    window.location.href = "/login";
};
