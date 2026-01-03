import React, { useEffect, useState } from "react";
import {
    Container,
    Paper,
    Typography,
    Box,
    TextField,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Alert,
    AlertTitle
} from "@mui/material";
import WarningIcon from "@mui/icons-material/Warning";
import AssignmentIcon from "@mui/icons-material/Assignment";
import {fetchWorkOrders, addWorkOrderForVehicle, fetchVehiclesByEndCustomer, fetchVehiclesByCompany} from "../api";

const WorkOrders = () => {
    const [vehicles, setVehicles] = useState([]);
    const [workOrders, setWorkOrders] = useState([]);
    const [selectedVehicle, setSelectedVehicle] = useState("");
    const [description, setDescription] = useState("");

    // Hämta fordon och arbetsordrar när komponenten mountas
    useEffect(() => {
        loadVehicles();
        loadWorkOrders();
    }, []);

    const loadVehicles = async () => {
        try {
            // 1. Hämta customerId från localStorage
            const storedCompanyId = localStorage.getItem("companyId");
            if (!storedCompanyId) {
                console.error("Ingen 'customerId' hittad i localStorage.");
                return;
            }

            // 2. Om din backend förväntar sig ett numeriskt värde
            //    kan du behöva parsea det:
            const companyId = parseInt(storedCompanyId, 10);

            // 3. Anropa fetchVehicles med customerId
            const data = await fetchVehiclesByCompany(companyId);
            setVehicles(data);
        } catch (error) {
            console.error("Fel vid hämtning av fordon:", error);
        }
    };

    const loadWorkOrders = async () => {
        try {
            const data = await fetchWorkOrders();
            setWorkOrders(data);
        } catch (error) {
            console.error("Fel vid hämtning av arbetsordrar:", error);
        }
    };

    const handleAddWorkOrder = async (e) => {
        e.preventDefault();
        if (!selectedVehicle) {
            alert("Vänligen välj ett fordon innan du skapar arbetsordern.");
            return;
        }
        try {
            await addWorkOrderForVehicle(selectedVehicle, { description });
            setDescription("");
            // Efter att arbetsordern skapats, ladda om listan
            loadWorkOrders();
        } catch (error) {
            console.error("Fel vid skapande av arbetsorder:", error);
        }
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Typography variant="h4" sx={{ mb: 3 }}>
                Arbetsordrar
            </Typography>

            {/* Formulär för att skapa ny arbetsorder */}
            <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>
                    Skapa ny arbetsorder
                </Typography>
                <Box component="form" onSubmit={handleAddWorkOrder}>
                    {vehicles.length === 0 ? (
                        <Alert severity="warning" icon={<WarningIcon />} sx={{ mb: 2 }}>
                            <AlertTitle>Inga fordon tillgängliga</AlertTitle>
                            Vänligen lägg till fordon innan du skapar arbetsordrar. Gå till Fordon-sidan för att lägga till ett fordon.
                        </Alert>
                    ) : (
                        <FormControl fullWidth sx={{ mb: 2 }}>
                            <InputLabel id="vehicle-select-label">Välj fordon</InputLabel>
                            <Select
                                labelId="vehicle-select-label"
                                value={selectedVehicle}
                                onChange={(e) => setSelectedVehicle(e.target.value)}
                                label="Välj fordon"
                                required
                            >
                                <MenuItem value="">
                                    <em>-- Välj fordon --</em>
                                </MenuItem>
                                {vehicles.map((vehicle) => (
                                    <MenuItem key={vehicle.id} value={vehicle.id}>
                                        {vehicle.brand} {vehicle.model} (ID: {vehicle.id})
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    )}

                    <TextField
                        fullWidth
                        label="Beskrivning av arbetet"
                        placeholder="Ex. 'Byta bromsar'"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                        sx={{ mb: 2 }}
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        disabled={vehicles.length === 0}
                        sx={{ mt: 1 }}
                    >
                        Skapa arbetsorder
                    </Button>
                </Box>
            </Paper>

            {/* Tabell över befintliga arbetsordrar */}
            {workOrders.length === 0 ? (
                <Box sx={{ p: 6, textAlign: "center" }}>
                    <AssignmentIcon sx={{ fontSize: 60, color: "text.secondary", mb: 2 }} />
                    <Typography variant="h6" color="text.secondary">
                        Inga aktiva arbetsordrar
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                        Det finns för närvarande inga arbetsordrar. Skapa en med formuläret ovan.
                    </Typography>
                </Box>
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: "grey.200" }}>
                                <TableCell><strong>ID</strong></TableCell>
                                <TableCell><strong>Fordon ID</strong></TableCell>
                                <TableCell><strong>Status</strong></TableCell>
                                <TableCell><strong>Beskrivning</strong></TableCell>
                                <TableCell><strong>Skapad</strong></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {workOrders.map((order) => (
                                <TableRow key={order.id} hover>
                                    <TableCell>{order.id}</TableCell>
                                    <TableCell>{order.vehicleId}</TableCell>
                                    <TableCell>{order.status?.name}</TableCell>
                                    <TableCell>{order.description}</TableCell>
                                    <TableCell>
                                        {new Date(order.createdAt).toLocaleDateString()}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </Container>
    );
};

export default WorkOrders;
