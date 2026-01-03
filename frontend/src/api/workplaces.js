import { apiClient } from "./apiConfig";

export const getWorkplaces = async (companyId) => {
    try {
        const response = await apiClient.get(`/workplaces/${companyId}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching workplaces:", error);
        return [];
    }
};

export const addWorkplace = async (companyId, workplace) => {
    try {
        const response = await apiClient.post(`/workplaces/${companyId}`, workplace);
        return response.data;
    } catch (error) {
        console.error("Error adding workplace:", error);
    }
};

export const deleteWorkplace = async (id) => {
    try {
        await apiClient.delete(`/workplaces/${id}`);
    } catch (error) {
        console.error("Error deleting workplace:", error);
    }
};

export const updateWorkplace = async (id, workplace) => {
    try {
        const response = await apiClient.put(`/workplaces/${id}`, workplace);
        return response.data;
    } catch (error) {
        console.error("Error updating workplace:", error);
    }
};

export const fetchWorkplaces = async (companyId) => {
    try {
        const response = await apiClient.get(`/workplaces/?companyId=${companyId}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching workplaces:", error);
        return [];
    }
};
