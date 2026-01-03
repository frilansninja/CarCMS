// pages/EndCustomerDetails.jsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Container, Paper, Typography, Tabs, Tab, Box, Button, Chip } from "@mui/material";
import ArchiveIcon from "@mui/icons-material/Archive";
import UnarchiveIcon from "@mui/icons-material/Unarchive";
import CustomerDetailsTab from "../components/CustomerDetailsTab";
import VehiclesTab from "../components/VehiclesTab";
import WorkOrdersTab from "../components/WorkOrdersTab";
import InvoicesTab from "../components/InvoicesTab";

const EndCustomerDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    // Gemensamt state
    const [endCustomer, setEndCustomer] = useState(null);
    const [vehicles, setVehicles] = useState([]);
    const [workOrders, setWorkOrders] = useState([]);
    const [invoices, setInvoices] = useState([]);
    const [categories, setCategories] = useState([]);
    const [isAdmin, setIsAdmin] = useState(false);
    const [tab, setTab] = useState(0);

    // State för formulär
    const [newWorkOrder, setNewWorkOrder] = useState({ description: "", vehicleId: "", categoryId: "" });
    // Vi antar att för service (t.ex. "Service") vi hämtar rekommenderade delmoment (work tasks)
    const [recommendedServices, setRecommendedServices] = useState([]);
    const [selectedServices, setSelectedServices] = useState([]);
    const [newVehicle, setNewVehicle] = useState({ brand: "", model: "", registrationNumber: "", year: "" });
    const [showNewVehicleForm, setShowNewVehicleForm] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const token = localStorage.getItem("accessToken");
                if (!token) {
                    navigate("/login");
                    return;
                }
                const [endCustomerRes, vehiclesRes, workOrdersRes, categoriesRes] = await Promise.all([
                    fetch(`/api/endcustomers/details/${id}`, { headers: { Authorization: `Bearer ${token}` } }),
                    fetch(`/api/endcustomers/${id}/vehicles`, { headers: { Authorization: `Bearer ${token}` } }),
                    fetch(`/api/endcustomers/${id}/workorders`, { headers: { Authorization: `Bearer ${token}` } }),
                    fetch(`/api/workordercategories`, { headers: { Authorization: `Bearer ${token}` } })
                ]);

                setEndCustomer(await endCustomerRes.json());
                const vehiclesData = await vehiclesRes.json();
                setVehicles(vehiclesData);
                setWorkOrders(await workOrdersRes.json());

                const catData = await categoriesRes.json();
                console.log("Hämtade kategorier:", catData); // Debug
                setCategories(catData);

                // Sätt ett default fordon om listan inte är tom
                if (vehiclesData.length > 0) {
                    setNewWorkOrder((prev) => ({ ...prev, vehicleId: prev.vehicleId || vehiclesData[0].id }));
                }
            } catch (error) {
                console.error("Fel vid hämtning av data:", error);
            }
        };

        fetchData();
        setIsAdmin(JSON.parse(localStorage.getItem("userRoles") || "[]").includes("ADMIN"));
    }, [id, navigate]);

    // Hantera kategoriändring – vi sätter bara värdet här; en useEffect lyssnar på ändringar
    const handleCategoryChange = (e) => {
        const categoryId = e.target.value; // behåll som sträng
        setNewWorkOrder((prev) => ({ ...prev, categoryId }));
    };

    // useEffect: Om ett fordon och en kategori är valt, och om kategorin är "Service",
    // hämta rekommenderade delmoment (work tasks) från servern.
    useEffect(() => {
        const fetchRecommendedServices = async () => {
            if (categories.length > 0 && newWorkOrder.categoryId && newWorkOrder.vehicleId) {
                const selectedCategory = categories.find(
                    (cat) => Number(cat.id) === Number(newWorkOrder.categoryId)
                );
                console.log("Selected category:", selectedCategory);
                console.log("Vehicle ID:", newWorkOrder.vehicleId);
                if (selectedCategory?.name?.toLowerCase().trim() === "service") {
                    const token = localStorage.getItem("accessToken");
                    try {
                        const response = await fetch(`/api/service/vehicle/${newWorkOrder.vehicleId}`, {
                            headers: { Authorization: `Bearer ${token}` }
                        });
                        if (response.ok) {
                            const data = await response.json();
                            console.log("Uppdaterade rekommenderade delmoment:", data);
                            setRecommendedServices(data);
                        } else {
                            console.error("Misslyckades med att hämta rekommenderade delmoment.");
                            setRecommendedServices([]);
                        }
                    } catch (error) {
                        console.error("Fel vid hämtning av rekommenderade delmoment:", error);
                        setRecommendedServices([]);
                    }
                } else {
                    setRecommendedServices([]);
                    setSelectedServices([]);
                }
            }
        };

        fetchRecommendedServices();
    }, [newWorkOrder.vehicleId, newWorkOrder.categoryId, categories]);

    // handleAddWorkOrder: Bygg payloaden för en arbetsorder. Vi skickar endast de grundläggande fälten,
    // samt en lista med work tasks om kategorin är "Service".
    const handleAddWorkOrder = async () => {
        const token = localStorage.getItem("accessToken");
        if (!newWorkOrder.vehicleId || !newWorkOrder.categoryId) {
            alert("Välj både fordon och kategori för arbetsordern!");
            return;
        }
        const vehicleId = Number(newWorkOrder.vehicleId);
        const categoryId = Number(newWorkOrder.categoryId);
        const selectedCategory = categories.find(cat => Number(cat.id) === categoryId);

        // Bygg en payload med grunddata för arbetsordern
        let workOrderPayload = {
            description: newWorkOrder.description,
            categoryId: categoryId,
            vehicleId: vehicleId
        };

        // Om kategorin är "Service" och användaren har valt några delmoment, lägg med dem
        if (
            selectedCategory &&
            selectedCategory.name.toLowerCase().trim() === "service" &&
            selectedServices.length > 0
        ) {
            // Filtrera ut de valda delmomenten (baserat på exempelvis task id)
            const chosenTasks = recommendedTasks.filter(task =>
                selectedServices.includes(task.id)
            );
            // Skapa en lista med work task DTO:er – här använder vi fältet "templateId" från det rekommenderade momentet
            const workTasksPayload = chosenTasks.map(task => ({
                templateId: task.id,
                description: task.serviceName // eller annan logik för att sätta beskrivning
            }));
            workOrderPayload.workTasks = workTasksPayload;
            // Du kan även lägga till en sammanfattande text i beskrivningen om så önskas:
            const taskDescriptions = workTasksPayload.map(t => t.description).join(", ");
            workOrderPayload.description += ` Delmoment: ${taskDescriptions}`;
        }

        console.log("Skickar arbetsorder med payload:", workOrderPayload);

        try {
            const response = await fetch(`/api/workorders/vehicle/${vehicleId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(workOrderPayload)
            });
            if (response.ok) {
                const addedWorkOrder = await response.json();
                setWorkOrders([...workOrders, addedWorkOrder]);
                setNewWorkOrder({ description: "", vehicleId: "", categoryId: "" });
                setSelectedServices([]);
                setRecommendedServices([]);
            } else {
                console.error("Misslyckades med att skapa arbetsorder. Status:", response.status);
                const errorText = await response.text();
                console.error("Felmeddelande:", errorText);
            }
        } catch (error) {
            console.error("Fel vid skapande av arbetsorder:", error);
        }
    };

    const handleArchive = async () => {
        const token = localStorage.getItem("accessToken");
        try {
            const response = await fetch(`/api/endcustomers/${id}/archive`, {
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (response.ok) {
                const updated = await response.json();
                setEndCustomer(updated);
                alert("Kund arkiverad");
            } else {
                alert("Fel vid arkivering av kund");
            }
        } catch (error) {
            console.error("Fel vid arkivering:", error);
            alert("Fel vid arkivering av kund");
        }
    };

    const handleUnarchive = async () => {
        const token = localStorage.getItem("accessToken");
        try {
            const response = await fetch(`/api/endcustomers/${id}/unarchive`, {
                method: "PATCH",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (response.ok) {
                const updated = await response.json();
                setEndCustomer(updated);
                alert("Kund återställd");
            } else {
                alert("Fel vid återställning av kund");
            }
        } catch (error) {
            console.error("Fel vid återställning:", error);
            alert("Fel vid återställning av kund");
        }
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
                    <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                        <Typography variant="h5">
                            {endCustomer ? endCustomer.name : "Laddar..."}
                        </Typography>
                        {endCustomer && !endCustomer.isActive && (
                            <Chip label="Arkiverad" color="warning" size="small" />
                        )}
                    </Box>
                    {endCustomer && (
                        <Box>
                            {endCustomer.isActive ? (
                                <Button
                                    variant="outlined"
                                    color="warning"
                                    startIcon={<ArchiveIcon />}
                                    onClick={handleArchive}
                                    size="small"
                                >
                                    Arkivera
                                </Button>
                            ) : (
                                <Button
                                    variant="outlined"
                                    color="primary"
                                    startIcon={<UnarchiveIcon />}
                                    onClick={handleUnarchive}
                                    size="small"
                                >
                                    Återställ
                                </Button>
                            )}
                        </Box>
                    )}
                </Box>
                <Tabs value={tab} onChange={(e, newValue) => setTab(newValue)} sx={{ mb: 2 }}>
                    <Tab label="Detaljer" />
                    <Tab label="Fordon" />
                    <Tab label="Arbetsordrar" />
                    <Tab label="Fakturor" />
                </Tabs>
                <Box>
                    {tab === 0 && (
                        <CustomerDetailsTab endCustomer={endCustomer} />
                    )}
                    {tab === 1 && (
                        <VehiclesTab
                            vehicles={vehicles}
                            newVehicle={newVehicle}
                            setNewVehicle={setNewVehicle}
                            showNewVehicleForm={showNewVehicleForm}
                            setShowNewVehicleForm={setShowNewVehicleForm}
                            isAdmin={isAdmin}
                            recommendedServices={recommendedServices} // Om du fortfarande använder samma endpoint
                            selectedServices={selectedServices}
                            setSelectedServices={setSelectedServices}
                            newWorkOrder={newWorkOrder}
                            setNewWorkOrder={setNewWorkOrder}
                            categories={categories}
                        />
                    )}
                    {tab === 2 && (
                        <WorkOrdersTab
                            workOrders={workOrders}
                            newWorkOrder={newWorkOrder}
                            setNewWorkOrder={setNewWorkOrder}
                            categories={categories}
                            isAdmin={isAdmin}
                            vehicles={vehicles}
                            recommendedServices={recommendedServices}
                            setRecommendedServices={setRecommendedServices} // <---
                            selectedServices={selectedServices}
                            setSelectedServices={setSelectedServices}
                            handleAddWorkOrder={handleAddWorkOrder}
                            handleCategoryChange={handleCategoryChange}
                        />

                    )}
                    {tab === 3 && (
                        <InvoicesTab invoices={invoices} />
                    )}
                </Box>
            </Paper>
        </Container>
    );
};

export default EndCustomerDetails;
