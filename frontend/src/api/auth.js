import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/auth";

/**
 * Request password reset
 * @param {string} username - User's email/username
 * @returns {Promise<{resetLink: string, message: string}>}
 */
export const requestPasswordReset = async (username) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/forgot-password`, {
            username
        });
        return response.data;
    } catch (error) {
        console.error("Error requesting password reset:", error);
        throw error;
    }
};

/**
 * Reset password with token
 * @param {string} token - Reset token from URL
 * @param {string} newPassword - New password
 * @returns {Promise<{message: string}>}
 */
export const resetPassword = async (token, newPassword) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/reset-password`, {
            token,
            newPassword
        });
        return response.data;
    } catch (error) {
        console.error("Error resetting password:", error);
        throw error;
    }
};
