// components/VehiclesTab.jsx
import React, { useEffect } from "react";
import { Box, Typography, TextField, Button, MenuItem } from "@mui/material";

const VehiclesTab = ({
                         vehicles,
                         newVehicle,
                         setNewVehicle,
                         showNewVehicleForm,
                         setShowNewVehicleForm,
                         isAdmin,
                         recommendedServices,
                         selectedServices,
                         setSelectedServices,
                         newWorkOrder,
                         setNewWorkOrder,
                         categories
                     }) => {
    // Exempel på en onChange-hanterare för kategori, om det behövs här
    const handleCategoryChange = (e) => {
        const categoryId = e.target.value;
        setNewWorkOrder({ ...newWorkOrder, categoryId });
    };

    const handleServiceCheckboxChange = (e, serviceId) => {
        if (e.target.checked) {
            setSelectedServices((prev) => [...prev, serviceId]);
        } else {
            setSelectedServices((prev) => prev.filter((id) => id !== serviceId));
        }
    };

    const handleAddVehicle = () => {
        // Extern funktion skickas eventuellt som prop eller implementeras här
    };

    return (
        <Box>
            <Typography variant="h6">Fordon</Typography>
            {isAdmin && !showNewVehicleForm && (
                <Button
                    variant="contained"
                    color="primary"
                    onClick={() => setShowNewVehicleForm(true)}
                    sx={{ mb: 2 }}
                >
                    Lägg till fordon
                </Button>
            )}
            {isAdmin && showNewVehicleForm && (
                <Box sx={{ mb: 2 }}>
                    <TextField
                        label="Märke"
                        name="brand"
                        value={newVehicle.brand}
                        onChange={(e) => setNewVehicle({ ...newVehicle, brand: e.target.value })}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Modell"
                        name="model"
                        value={newVehicle.model}
                        onChange={(e) => setNewVehicle({ ...newVehicle, model: e.target.value })}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Registreringsnummer"
                        name="registrationNumber"
                        value={newVehicle.registrationNumber}
                        onChange={(e) => setNewVehicle({ ...newVehicle, registrationNumber: e.target.value })}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Årsmodell"
                        name="year"
                        type="number"
                        value={newVehicle.year}
                        onChange={(e) => setNewVehicle({ ...newVehicle, year: e.target.value })}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <Button variant="contained" color="primary" onClick={handleAddVehicle} sx={{ mr: 2 }}>
                        Spara fordon
                    </Button>
                    <Button variant="outlined" color="secondary" onClick={() => setShowNewVehicleForm(false)}>
                        Avbryt
                    </Button>
                </Box>
            )}
            {/* Visa checkboxar för service om rekommenderade tjänster finns */}
            {recommendedServices.length > 0 && (
                <Box sx={{ mb: 2 }}>
                    <Typography variant="subtitle1">Välj serviceåtgärder:</Typography>
                    {recommendedServices.map((service) => (
                        <Box key={service.id} sx={{ display: "flex", alignItems: "center" }}>
                            <input
                                type="checkbox"
                                id={`service-${service.id}`}
                                checked={selectedServices.includes(service.id)}
                                onChange={(e) => handleServiceCheckboxChange(e, service.id)}
                            />
                            <label htmlFor={`service-${service.id}`} style={{ marginLeft: 8 }}>
                                {service.serviceName}
                            </label>
                        </Box>
                    ))}
                </Box>
            )}
            {/* Lista fordon */}
            {vehicles.length > 0 ? (
                vehicles.map((vehicle) => (
                    <Box
                        key={vehicle.id}
                        sx={{ p: 2, borderBottom: "1px solid #ccc", cursor: "pointer" }}
                        onClick={() => window.location.assign(`/vehicles/${vehicle.id}`)}
                    >
                        <Typography>
                            {vehicle.brand} {vehicle.model} - {vehicle.registrationNumber} ({vehicle.year})
                        </Typography>
                    </Box>
                ))
            ) : (
                <Typography>Inga fordon registrerade</Typography>
            )}
        </Box>
    );
};

export default VehiclesTab;
