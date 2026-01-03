import { apiClient } from './apiConfig';

export const addWorkOrderForVehicle = async (vehicleId, workOrder) => {
    try {
        const token = localStorage.getItem("accessToken");
        await apiClient.post(`http://localhost:8080/api/vehicles/${vehicleId}/workorders`, workOrder, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        console.error("Error adding work order:", error);
    }
};

export const addWorkOrder = async (companyId, vehicleId, workOrder) => {
    try {
        const token = localStorage.getItem("accessToken");
        await apiClient.post(`http://localhost:8080/api/customers/${companyId}/vehicles/${vehicleId}/workorders`, workOrder, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    } catch (error) {
        console.error("Error adding work order:", error);
    }
};

export const fetchWorkOrders = async () => {
    try {
        const token = localStorage.getItem("accessToken");
        const response = await apiClient.get("/workorders", {

            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching work orders:", error);
        return [];
    }
};