import { apiClient } from './apiConfig';

export const fetchUsersByCompany = async (companyId) => {
    try {
        const response = await apiClient.get(`/companies/${companyId}/users`);
        return response.data;
    } catch (error) {
        console.error("Error fetching users:", error);
        return [];
    }
};

export const addUser = async (user) => {
    try {
        await apiClient.post(`/users/register/${user.companyId}`, user);
    } catch (error) {
        console.error("Error adding user:", error);
    }
};

export const updateUser = async (user) => {
    try {
        await apiClient.put(`/users/${user.id}`, user);
    } catch (error) {
        console.error("Error updating user:", error);
    }
};

export const deleteUser = async (userId) => {
    try {
        await apiClient.delete(`/users/${userId}`);
    } catch (error) {
        console.error("Error deleting user:", error);
    }
};