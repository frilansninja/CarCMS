import { apiClient } from './apiConfig';

export const addCompany = async (company) => {
    try {
        await apiClient.post("/companies", company);
    } catch (error) {
        console.error("Error adding company:", error);
    }
};

export const updateCompany = async (company) => {
    try {
        console.log('Updating company with data:', company);
        const response = await apiClient.put(`/companies/${company.id}`, company);
        console.log('Update response:', response.data);
        return response.data;
    } catch (error) {
        console.error("Error updating company:", error);
        throw error; // Re-throw så att anropande kod kan hantera felet
    }
};

export const deleteCompany = async (id) => {
    try {
        await apiClient.delete(`/companies/${id}`);
    } catch (error) {
        console.error("Error deleting company:", error);
    }
};

export const fetchCompanies = async () => {
    try {
        const response = await apiClient.get("/companies");
        return response.data;
    } catch (error) {
        console.error("Error fetching companies:", error);
        return [];
    }
};
