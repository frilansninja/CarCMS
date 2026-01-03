import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Container, Paper, Typography, Button, Select, MenuItem, TextField } from "@mui/material";

const WorkTaskDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [task, setTask] = useState(null);
    const [taskStatuses, setTaskStatuses] = useState([]);
    const [availableParts, setAvailableParts] = useState([]);
    const [selectedParts, setSelectedParts] = useState([]);
    const [selectedStatus, setSelectedStatus] = useState("");

    useEffect(() => {
        fetchTaskDetails();
        fetchTaskStatuses();
        fetchAvailableParts();
    }, [id]);

    const fetchTaskDetails = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/worktasks/${id}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                console.log("✅ Hämtat arbetsmoment:", data);
                setTask(data);
                setSelectedStatus(data.statusId);
            } else {
                console.error("🚫 Misslyckades med att hämta arbetsmoment.");
            }
        } catch (error) {
            console.error("❌ Fel vid hämtning av arbetsmoment:", error);
        }
    };

    const fetchTaskStatuses = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/worktasks/statuses`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                setTaskStatuses(data.sort((a, b) => a.id - b.id)); // 🔄 Sortera efter ID
            } else {
                console.error("🚫 Misslyckades med att hämta statusar.");
            }
        } catch (error) {
            console.error("❌ Fel vid hämtning av statusar:", error);
        }
    };
    const fetchAvailableParts = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/articles`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                setAvailableParts(data);
            } else {
                console.error("🚫 Misslyckades med att hämta reservdelar.");
            }
        } catch (error) {
            console.error("❌ Fel vid hämtning av reservdelar:", error);
        }
    };

    const handleAddPart = async (partId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/worktasks/${id}/add-part`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ partId })
            });

            if (response.ok) {
                console.log("✅ Del tillagd!");
                fetchTaskDetails(); // 🔄 Hämta uppdaterat arbetsmoment
            } else {
                console.error("🚫 Misslyckades med att lägga till del.");
            }
        } catch (error) {
            console.error("❌ Fel vid tillägg av del:", error);
        }
    };

    const handleStatusChange = async (event) => {
        const newStatusId = event.target.value;
        setSelectedStatus(newStatusId); // 🔄 Uppdatera UI direkt
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/worktasks/${id}/status`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ statusId: newStatusId })
            });

            if (response.ok) {
                console.log("✅ Arbetsmomentstatus uppdaterad!");
                fetchTaskDetails(); // 🔄 Hämta om arbetsmomentet
            } else {
                console.error("🚫 Misslyckades med att uppdatera status.");
            }
        } catch (error) {
            console.error("❌ Fel vid uppdatering av status:", error);
        }
    };


    if (!task) return <Typography>Laddar...</Typography>;

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h5">Arbetsmoment #{task.id}</Typography>
                <Typography variant="h6">Beskrivning</Typography>
                <Typography>{task.description || "Ingen beskrivning angiven"}</Typography>

                <Typography variant="h6">Status</Typography>
                <Select
                    value={selectedStatus}
                    onChange={handleStatusChange}
                    fullWidth
                    sx={{ mb: 2 }}
                >
                    {taskStatuses.map((status) => (
                        <MenuItem key={status.id} value={status.id}>
                            {status.name}
                        </MenuItem>
                    ))}
                </Select>

                <Typography variant="h6">Delar som krävs</Typography>
                {task.articles?.length > 0 ? (
                    task.articles.map((part) => (
                        <Paper
                            key={part.id}
                            elevation={2}
                            sx={{ p: 2, my: 1, display: "flex", justifyContent: "space-between", alignItems: "center" }}
                        >
                            <Typography>🔩 {part.description} (Artikelnummer: {part.partNumber})</Typography>
                            {part.stockQuantity > 0 ? (
                                <Typography sx={{ color: "green" }}>✅ Finns i lager: {part.stockQuantity} st</Typography>
                            ) : (
                                <Button
                                    variant="contained"
                                    color="warning"
                                    onClick={() => {
                                        console.log("📢 Navigerar till OrderParts med workOrderId:", task.workOrderId);
                                        navigate("/order-parts?partNumber=" + part.partNumber, {
                                            state: { workOrderId: task.workOrderId }
                                        });
                                    }}
                                >
                                    🛒 Beställ
                                </Button>

                            )}
                        </Paper>
                    ))
                ) : (
                    <Typography>Inga delar krävs</Typography>
                )}

                <Button
                    variant="contained"
                    color="secondary"
                    sx={{ mt: 2 }}
                    onClick={() => window.history.back()} // 🔄 Gå tillbaka till arbetsorder
                >
                    🔙 Tillbaka
                </Button>
            </Paper>
        </Container>
    );
};

export default WorkTaskDetails;
