import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Container, Paper, Typography, TextField, Button, FormControl, InputLabel, Select, MenuItem, Box } from "@mui/material";

const VehicleDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isCreateMode = id === 'create';
    const [vehicle, setVehicle] = useState(null);
    const [editedVehicle, setEditedVehicle] = useState(null);
    const [isEditing, setIsEditing] = useState(isCreateMode);
    const [isAdmin, setIsAdmin] = useState(false);
    const [newWorkOrder, setNewWorkOrder] = useState({ description: "" });
    const [endCustomers, setEndCustomers] = useState([]);
    const [selectedEndCustomerId, setSelectedEndCustomerId] = useState("");

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            console.log("navigate");
            navigate("/login");
            return;
        }

        // Skip fetching vehicle if we're in create mode
        if (isCreateMode) {
            // Initialize empty vehicle for create mode
            const emptyVehicle = {
                vehicleModel: {
                    brand: "",
                    model: "",
                    year: new Date().getFullYear()
                },
                registrationNumber: "",
                transmission: "",
                mileage: 0
            };
            setVehicle(emptyVehicle);
            setEditedVehicle(emptyVehicle);

            // Fetch end customers for the dropdown
            const fetchEndCustomers = async () => {
                try {
                    const companyId = localStorage.getItem("companyId");
                    const response = await fetch(`/api/endcustomers/company/${companyId}`, {
                        headers: { "Authorization": `Bearer ${token}` }
                    });
                    if (response.ok) {
                        const data = await response.json();
                        setEndCustomers(data);
                    }
                } catch (error) {
                    console.error("Fel vid hämtning av slutkunder:", error);
                }
            };

            fetchEndCustomers();

            const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
            setIsAdmin(roles.includes("ADMIN"));
            return;
        }

        const fetchVehicle = async () => {
            console.log("fetch v");
            try {
                const response = await fetch(`/api/vehicles/details/${id}`, {
                    headers: { "Authorization": `Bearer ${token}` }
                });

                if (!response.ok) {
                    console.error("API-fel:", response.status, response.statusText);
                    return;
                }

                const data = await response.json();
                console.log("Hämtad fordonsdata:", data);
                setVehicle(data);
                setEditedVehicle(data);
            } catch (error) {
                console.error("Fel vid hämtning av fordonsdetaljer:", error);
            }
        };

        fetchVehicle();

        const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
        setIsAdmin(roles.includes("ADMIN"));
    }, [id, isCreateMode, navigate]);

    const handleWorkOrderAdd = async () => {
        // Hantering av arbetsorder (oförändrat)
    };

    // För att hantera ändringar i fordonets fält som ligger i den inre objektet vehicleModel
    const handleVehicleModelChange = (e) => {
        const { name, value } = e.target;
        setEditedVehicle({
            ...editedVehicle,
            vehicleModel: {
                ...editedVehicle.vehicleModel,
                [name]: value
            }
        });
    };

    // För fält som ligger direkt på fordonet, t.ex. registreringsnummer
    const handleVehicleChange = (e) => {
        const { name, value } = e.target;
        setEditedVehicle({ ...editedVehicle, [name]: value });
    };
    const handleBack = () => {
        navigate(-1);
    };

    const isFormValid = () => {
        // Check if end customer is selected (only in create mode)
        if (isCreateMode && !selectedEndCustomerId) {
            return false;
        }

        // Check required vehicle fields
        if (!editedVehicle.vehicleModel?.brand?.trim()) return false;
        if (!editedVehicle.vehicleModel?.model?.trim()) return false;
        if (!editedVehicle.vehicleModel?.year) return false;
        if (!editedVehicle.registrationNumber?.trim()) return false;

        return true;
    };

    const handleSave = async () => {
        const token = localStorage.getItem("accessToken");

        // Validate end customer selection for create mode
        if (isCreateMode && !selectedEndCustomerId) {
            alert("Vänligen välj en kund");
            return;
        }

        console.log(isCreateMode ? "Skapar nytt fordon:" : "Skickar uppdatering:", editedVehicle);

        try {
            const url = isCreateMode ? `/api/vehicles/${selectedEndCustomerId}` : `/api/vehicles/${id}`;
            const method = isCreateMode ? "POST" : "PUT";

            const response = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(editedVehicle)
            });

            if (response.ok) {
                const savedVehicle = await response.json();
                if (isCreateMode) {
                    console.log("Fordon skapat:", savedVehicle);
                    // Navigate to the vehicle list or the newly created vehicle details
                    navigate("/vehicles");
                } else {
                    setVehicle(savedVehicle);
                    setIsEditing(false);
                    console.log("Uppdaterat fordon:", savedVehicle);
                }
            } else {
                const errorText = await response.text();
                console.error(`Misslyckades med att ${isCreateMode ? 'skapa' : 'uppdatera'} fordon. Status:`, response.status, errorText);
                alert(`Fel vid ${isCreateMode ? 'skapande' : 'uppdatering'} av fordon: ${errorText}`);
            }
        } catch (error) {
            console.error(`Fel vid ${isCreateMode ? 'skapande' : 'uppdatering'} av fordon:`, error);
            alert(`Fel vid ${isCreateMode ? 'skapande' : 'uppdatering'} av fordon: ${error.message}`);
        }
    };

    if (!vehicle) return <Typography>Laddar...</Typography>;

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h5">
                    {isCreateMode
                        ? "Skapa nytt fordon"
                        : isEditing
                        ? "Redigera fordon"
                        : `${vehicle.vehicleModel?.brand} ${vehicle.vehicleModel?.model}`}
                </Typography>

                {!isCreateMode && isAdmin && !isEditing && (
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => setIsEditing(true)}
                        sx={{ mb: 2 }}
                    >
                        Redigera
                    </Button>
                )}

                <Button variant="outlined" onClick={handleBack} sx={{ mb: 2 }}>
                    Tillbaka
                </Button>

                <Typography variant="h6" sx={{ mt: 2 }}>Fordonets detaljer</Typography>
                {(isEditing || isCreateMode) ? (
                    <>
                        {/* End Customer selection - only in create mode */}
                        {isCreateMode && (
                            <FormControl fullWidth sx={{ mb: 2 }} required>
                                <InputLabel id="endcustomer-select-label">Kund</InputLabel>
                                <Select
                                    labelId="endcustomer-select-label"
                                    value={selectedEndCustomerId}
                                    onChange={(e) => setSelectedEndCustomerId(e.target.value)}
                                    label="Kund"
                                    required
                                >
                                    <MenuItem value="">
                                        <em>-- Välj kund --</em>
                                    </MenuItem>
                                    {endCustomers.map((customer) => (
                                        <MenuItem key={customer.id} value={customer.id}>
                                            {customer.name} {customer.surname && `${customer.surname}`}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        )}

                        {/* Fält som tillhör vehicleModel */}
                        <TextField
                            label="Märke"
                            name="brand"
                            value={editedVehicle.vehicleModel?.brand || ""}
                            onChange={handleVehicleModelChange}
                            fullWidth
                            sx={{ mb: 2 }}
                        />
                        <TextField
                            label="Modell"
                            name="model"
                            value={editedVehicle.vehicleModel?.model || ""}
                            onChange={handleVehicleModelChange}
                            fullWidth
                            sx={{ mb: 2 }}
                        />
                        <TextField
                            label="Årsmodell"
                            name="year"
                            type="number"
                            value={editedVehicle.vehicleModel?.year || ""}
                            onChange={handleVehicleModelChange}
                            fullWidth
                            sx={{ mb: 2 }}
                        />
                        {/* Fält direkt på vehicle */}
                        <TextField
                            label="Registreringsnummer"
                            name="registrationNumber"
                            value={editedVehicle.registrationNumber || ""}
                            onChange={handleVehicleChange}
                            fullWidth
                            sx={{ mb: 2 }}
                        />
                        <TextField
                            label="Transmission"
                            name="transmission"
                            value={editedVehicle.transmission || ""}
                            onChange={handleVehicleChange}
                            fullWidth
                            sx={{ mb: 2 }}
                        />
                        <TextField
                            label="Mätarställning"
                            name="mileage"
                            type="number"
                            value={editedVehicle.mileage || ""}
                            onChange={handleVehicleChange}
                            fullWidth
                            sx={{ mb: 3 }}
                        />

                        {/* Save button at the bottom */}
                        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                            <Button
                                variant="contained"
                                color="success"
                                onClick={handleSave}
                                disabled={!isFormValid()}
                            >
                                {isCreateMode ? "Skapa fordon" : "Spara ändringar"}
                            </Button>
                        </Box>
                    </>
                ) : (
                    <>
                        <Typography>Märke: {vehicle.vehicleModel?.brand}</Typography>
                        <Typography>Modell: {vehicle.vehicleModel?.model}</Typography>
                        <Typography>Årsmodell: {vehicle.vehicleModel?.year}</Typography>
                        <Typography>Registreringsnummer: {vehicle.registrationNumber}</Typography>
                        <Typography>Transmission: {vehicle.transmission}</Typography>
                        <Typography>Mätarställning: {vehicle.mileage}</Typography>
                    </>
                )}
            </Paper>
        </Container>
    );
};

export default VehicleDetails;
