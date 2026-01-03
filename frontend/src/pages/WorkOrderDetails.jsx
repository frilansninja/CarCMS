import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Container, Paper, Typography, Button, Select, MenuItem, Box } from "@mui/material";
import PartsSearchDialog from "../components/parts/PartsSearchDialog";
import PartsList from "../components/parts/PartsList";

const WorkOrderDetails = () => {
    const { id } = useParams();
    const [workOrder, setWorkOrder] = useState(null);
    const [statuses, setStatuses] = useState([]);
    const [taskStatuses, setTaskStatuses] = useState([]);
    const [selectedStatus, setSelectedStatus] = useState("");
    const [isAdmin, setIsAdmin] = useState(false);
    const [mechanics, setMechanics] = useState([]);
    const [parts, setParts] = useState([]);
    const [partsDialogOpen, setPartsDialogOpen] = useState(false);

    useEffect(() => {


        const fetchStatuses = async () => {
            try {
                const token = localStorage.getItem("accessToken");
                const response = await fetch(`/api/workorders/statuses`, {
                    headers: { "Authorization": `Bearer ${token}` }
                });

                if (response.ok) {
                    const data = await response.json();
                    console.log("✅ Hämtade statusar:", data);
                    setStatuses(data);
                } else {
                    console.error("Misslyckades med att hämta statusar.");
                }
            } catch (error) {
                console.error("Fel vid hämtning av statusar:", error);
            }
        };

        fetchWorkOrder();
        fetchStatuses();
        fetchMechanics();
        fetchParts();

        const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
        setIsAdmin(roles.includes("ADMIN"));

    }, [id]); // Endast en useEffect behövs!

    useEffect(() => {
        if (workOrder?.status?.id) {
            setSelectedStatus(workOrder.status.id);
        }
    }, [workOrder]);

    useEffect(() => {
        fetchTaskStatuses();
    }, []);
    const fetchTaskStatuses = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/worktasks/statuses`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                setTaskStatuses(data);
            } else {
                console.error("🚫 Misslyckades med att hämta arbetsmoment-statusar.");
            }
        } catch (error) {
            console.error("❌ Fel vid hämtning av arbetsmoment-statusar:", error);
        }
    };

    const fetchWorkOrder = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/workorders/details/${id}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                console.log("✅ Hämtad arbetsorder:", data);
                setWorkOrder(data);
            } else {
                console.error("Misslyckades med att hämta arbetsorder.");
            }
        } catch (error) {
            console.error("Fel vid hämtning av arbetsorder:", error);
        }
    };
    const fetchMechanics = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/workorders/${id}/available-mechanics`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                setMechanics(data);
            } else {
                console.error("Misslyckades med att hämta mekaniker.");
            }
        } catch (error) {
            console.error("Fel vid hämtning av mekaniker:", error);
        }
    };

    const fetchParts = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/work-orders/${id}/parts/lines`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                console.log("✅ Fetched parts:", data);
                setParts(data);
            } else {
                console.error("Failed to fetch parts.");
            }
        } catch (error) {
            console.error("Error fetching parts:", error);
        }
    };

    const handlePartAdded = () => {
        fetchParts(); // Refresh parts list
    };


    const handleGenerateTasks = async () => {
        if (!workOrder || !workOrder.type) {
            console.error("Work order type is undefined");
            return;
        }
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/workorders/generate-tasks/${workOrder.id}`, {
                method: "GET",
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const updatedTasks = await response.json();
                setWorkOrder({ ...workOrder, tasks: updatedTasks });
            } else {
                console.error("Misslyckades med att generera arbetsmoment.");
            }
        } catch (error) {
            console.error("Fel vid generering av arbetsmoment:", error);
        }
    };


    const handleTaskStatusChange = async (taskId, newStatusId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/worktasks/${taskId}/status`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ statusId: newStatusId })
            });

            if (response.ok) {
                console.log("✅ Arbetsmomentstatus uppdaterad!");
                fetchWorkOrder(); // 🔄 Hämta arbetsordern igen för att visa den nya statusen
            } else {
                console.error("🚫 Misslyckades med att uppdatera arbetsmomentstatus.");
            }
        } catch (error) {
            console.error("❌ Fel vid uppdatering av arbetsmomentstatus:", error);
        }
    };

    const handleStatusChange = async (event) => {
        const newStatusId = event.target.value;
        setSelectedStatus(newStatusId); // 🔄 Uppdatera lokala state direkt för snabb feedback
        const token = localStorage.getItem("accessToken");

        try {
            const response = await fetch(`/api/workorders/${id}/status`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ statusId: newStatusId })
            });

            if (response.ok) {
                console.log("✅ Arbetsorderstatus uppdaterad!");
                fetchWorkOrder(); // 🔄 Hämta arbetsordern igen för att visa den nya statusen
            } else {
                console.error("🚫 Misslyckades med att uppdatera arbetsorderstatus.");
            }
        } catch (error) {
            console.error("❌ Fel vid uppdatering av arbetsorderstatus:", error);
        }
    };


    const handleAssignMechanic = async (mechanicId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/workorders/${id}/assign-mechanic`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(mechanicId)
            });

            if (response.ok) {
                const updatedWorkOrder = await response.json();
                setWorkOrder(updatedWorkOrder);
                console.log("✅ Mekaniker tilldelad:", updatedWorkOrder);
            } else {
                console.error("🚫 Misslyckades med att tilldela mekaniker.");
            }
        } catch (error) {
            console.error("❌ Fel vid tilldelning av mekaniker:", error);
        }
    };


    if (!workOrder) return <Typography>Laddar...</Typography>;

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h5">Arbetsorder #{workOrder.id}</Typography>
                <Typography variant="h6">Beskrivning</Typography>
                <Typography>{workOrder.description || "Ingen beskrivning angiven"}</Typography>

                <Typography variant="h6">Skapad</Typography>
                <Typography>{workOrder.createdDate || "Okänd"}</Typography>

                <Typography variant="h6">Fordon</Typography>
                <Typography> {workOrder.vehicle
                    ? `${workOrder.vehicle.brand} ${workOrder.vehicle.modelName} (${workOrder.vehicle.registrationNumber})`
                    : "Okänt fordon"}</Typography>

                <Typography variant="h6">Mekaniker</Typography>
                <Typography>{workOrder.mechanicName || "Ingen mekaniker tilldelad"}</Typography>
                <Typography variant="h6">Tilldela mekaniker</Typography>
                <Select
                    value={workOrder.mechanic?.id || ""}
                    onChange={(e) => handleAssignMechanic(e.target.value)}
                    fullWidth
                    sx={{ mb: 2 }}
                >
                    {mechanics.map((mechanic) => (
                        <MenuItem key={mechanic.id} value={mechanic.id}>
                            {mechanic.username}
                        </MenuItem>
                    ))}
                </Select>


                <Typography variant="h6" sx={{ mt: 3 }}>Arbetsmoment</Typography>
                {workOrder.workTasks?.length > 0 ? (
                    workOrder.workTasks
                        .sort((a, b) => a.statusId - b.statusId) // 🔥 Sortera efter status-ID
                        .map((task) => (
                            <Paper
                                key={task.id}
                                elevation={2}
                                sx={{
                                    p: 2,
                                    my: 1,
                                    display: "flex",
                                    justifyContent: "space-between",
                                    alignItems: "center",
                                    cursor: "pointer", // 🔥 Gör raden klickbar
                                    "&:hover": { backgroundColor: "#f5f5f5" }
                                }}
                                onClick={() => window.location.assign(`/worktasks/${task.id}`)} // 🔥 Navigera till ny sida
                            >
                                <Typography>🔧 {task.description}</Typography>
                                <Typography>Status: {task.statusName}</Typography>
                            </Paper>
                        ))
                ) : (
                    <Typography>Inga arbetsmoment registrerade</Typography>
                )}



                {/* Spare Parts Section */}
                <Box sx={{ mt: 3 }}>
                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
                        <Typography variant="h6">Spare Parts</Typography>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={() => setPartsDialogOpen(true)}
                        >
                            + Add Parts
                        </Button>
                    </Box>
                    <PartsList
                        workOrderId={id}
                        parts={parts}
                        onRefresh={fetchParts}
                    />
                </Box>

                <PartsSearchDialog
                    open={partsDialogOpen}
                    onClose={() => setPartsDialogOpen(false)}
                    workOrderId={id}
                    onPartAdded={handlePartAdded}
                />

                <Typography variant="h6">Status</Typography>
                {isAdmin ? (
                    <Select
                        value={selectedStatus}
                        //value={workOrder?.status?.id || ""}
                        onChange={handleStatusChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    >
                        {statuses.map((status) => (
                            <MenuItem key={status.id} value={status.id}>
                                {status.name}
                            </MenuItem>
                        ))}
                    </Select>
                ) : (
                    <Typography>{workOrder.status?.name || "Ingen status"}</Typography>
                )}

            </Paper>
        </Container>

    );
};

export default WorkOrderDetails;
