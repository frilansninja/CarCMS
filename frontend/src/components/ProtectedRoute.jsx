import React from "react";
import { Navigate } from "react-router-dom";

// Funktion för att validera JWT token
const isTokenValid = (token) => {
    if (!token) {
        return false;
    }

    try {
        // Dekoda JWT token (format: header.payload.signature)
        const payloadBase64 = token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payloadBase64));

        // Kontrollera om token har gått ut
        const expirationTime = decodedPayload.exp * 1000; // Konvertera till millisekunder
        const currentTime = Date.now();

        if (currentTime >= expirationTime) {
            console.log("🔑 Token har gått ut");
            return false;
        }

        return true;
    } catch (error) {
        console.error("🔑 Fel vid validering av token:", error);
        return false;
    }
};

const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem("accessToken");

    console.log("🔑 ProtectedRoute kontrollerar token:", token);

    // Validera token
    if (!isTokenValid(token)) {
        // Rensa localStorage om token är ogiltig eller utgången
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        console.log("🔑 Ogiltig token - omdirigerar till login");
        return <Navigate to="/login" replace />;
    }

    return children;
};


export default ProtectedRoute;
