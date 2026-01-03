import { apiClient } from "./apiConfig";



export const fetchVehiclesByCompany = async () => {
    try {
        const token = localStorage.getItem("accessToken");
        const companyId = localStorage.getItem("companyId");

        const response = await apiClient.get(`/vehicles/company/${companyId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        return response.data; // En lista av Vehicle
    } catch (error) {
        console.error("Error fetching vehicles:", error);
        return [];
    }
};

export const fetchVehicleById = async (id) => {
    try {
        const token = localStorage.getItem("accessToken");
        const response = await apiClient.get(`/vehicles/details/${id}`, {

            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching vehicles:", error);
        return [];
    }
};

export const fetchVehiclesByEndCustomer = async (endCustomerId) => {
    try {
        const token = localStorage.getItem("accessToken");
        const response = await apiClient.get(`/vehicles/endcustomer/${endCustomerId}`, {

            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching vehicles:", error);
        return [];
    }
};

export const addVehicle = async (endCustomerId, vehicle) => {
    try {
        const token = localStorage.getItem("accessToken");
        await apiClient.post(`/vehicles/endcustomer/${endCustomerId}`, vehicle, {
            headers: { "Content-Type": "application/json",
            Authorization: `Bearer ${token}`}
        });
    } catch (error) {
        console.error("Error adding vehicle:", error);
    }
};

export const updateVehicle = async (id, vehicle) => {
    try {
        await apiClient.put(`/vehicles/${id}`, vehicle, {
            headers: { "Content-Type": "application/json" }
        });
    } catch (error) {
        console.error("Error updating vehicle:", error);
    }
};

export const deleteVehicle = async (id) => {
    try {
        await apiClient.delete(`/vehicles/${id}`);
    } catch (error) {
        console.error("Error deleting vehicle:", error);
    }
};
