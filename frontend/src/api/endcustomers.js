import { apiClient } from "./apiConfig";

export const fetchEndCustomers = async () => {
    try {
        const response = await apiClient.get(`/endcustomers`);
        return response.data;
    } catch (error) {
        console.error("Error fetching end customers:", error);
        return [];
    }
};


export const addEndCustomer = async (companyId, company) => {
    try {
        await apiClient.post(`/endcustomers/${companyId}`, company, {
            headers: { "Content-Type": "application/json" } // ✅ FIXAR 415-FELET
        });
    } catch (error) {
        console.error("Error adding end customer:", error);
    }
};


export const updateEndCustomer = async (id, customer) => {
    try {
        await apiClient.put(`/endcustomers/${id}`, customer);
    } catch (error) {
        console.error("Error updating end customer:", error);
    }
};

export const deleteEndCustomer = async (id) => {
    try {
        await apiClient.delete(`/endcustomers/${id}`);
    } catch (error) {
        console.error("Error deleting end customer:", error);
    }
};
