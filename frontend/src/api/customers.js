import { apiClient } from './apiConfig';

export const addCustomer = async (customer) => {
    try {
        await apiClient.post("/customers", customer);
    } catch (error) {
        console.error("Error adding customer:", error);
    }
};

export const updateCustomer = async (customer) => {
    try {
        await apiClient.put(`/customers/${customer.id}`, customer);
    } catch (error) {
        console.error("Error updating customer:", error);
    }
};

export const deleteCustomer = async (id) => {
    try {
        await apiClient.delete(`/customers/${id}`);
    } catch (error) {
        console.error("Error deleting customer:", error);
    }
};

export const fetchCustomers = async () => {
    try {
        const response = await apiClient.get("/customers");
        return response.data;
    } catch (error) {
        console.error("Error fetching customers:", error);
        return [];
    }
};