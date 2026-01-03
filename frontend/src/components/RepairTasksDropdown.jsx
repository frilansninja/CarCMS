import React, { useEffect, useState } from "react";
import { Box, TextField, MenuItem, Checkbox, Typography, Button } from "@mui/material";

const RepairTasksDropdown = ({ newWorkOrder, setNewWorkOrder, selectedTasks, setSelectedTasks }) => {
    const [repairCategories, setRepairCategories] = useState([]);
    const [templates, setTemplates] = useState([]);
    const [customTask, setCustomTask] = useState("");

    // Hämta alla reparationskategorier
    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        fetch("/api/repaircategories", {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => res.json())
            .then((data) => setRepairCategories(data))
            .catch((err) => console.error("Fel vid hämtning av reparationskategorier:", err));
    }, []);

    // Hämta mallar för vald kategori
    const handleRepairCategoryChange = (e) => {
        const repairCategoryId = e.target.value;
        setNewWorkOrder((prev) => ({ ...prev, repairCategoryId, workTaskTemplateId: "" }));

        const token = localStorage.getItem("accessToken");
        fetch(`/api/worktasktemplates/byRepairCategory?repairCategoryId=${repairCategoryId}`, {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => res.json())
            .then((data) => setTemplates(data))
            .catch((err) => {
                console.error("Misslyckades med att hämta mallar", err);
                setTemplates([]);
            });
    };

    // Hantera val av arbetsmoment
    const handleTaskSelection = (taskId) => {
        setSelectedTasks((prev) =>
            prev.includes(taskId)
                ? prev.filter((id) => id !== taskId) // Avmarkera om redan vald
                : [...prev, taskId] // Lägg till om ej vald
        );
    };

    // Lägg till eget moment
    const handleAddCustomTask = () => {
        if (customTask.trim() !== "") {
            const newTask = { id: `custom-${Date.now()}`, description: customTask };
            setSelectedTasks((prev) => [...prev, newTask]);
            setCustomTask("");
        }
    };

    return (
        <Box sx={{ mt: 2 }}>
            <TextField
                select
                label="Välj reparationskategori"
                value={newWorkOrder.repairCategoryId || ""}
                onChange={handleRepairCategoryChange}
                fullWidth
                sx={{ mb: 2 }}
            >
                <MenuItem value="" disabled>
                    Välj en reparationskategori
                </MenuItem>
                {repairCategories.map((rc) => (
                    <MenuItem key={rc.id} value={rc.id}>
                        {rc.name}
                    </MenuItem>
                ))}
            </TextField>

            {templates.length > 0 && (
                <Box sx={{ mb: 2 }}>
                    <Typography variant="subtitle1">Välj arbetsmoment:</Typography>
                    {templates.map((template) => (
                        <Box key={template.id} sx={{ display: "flex", alignItems: "center" }}>
                            <Checkbox
                                checked={selectedTasks.includes(template.id)}
                                onChange={() => handleTaskSelection(template.id)}
                            />
                            <Typography>{template.description}</Typography>
                        </Box>
                    ))}
                </Box>
            )}

            {/* Lägg till eget arbetsmoment */}
            <TextField
                label="Lägg till eget arbetsmoment"
                value={customTask}
                onChange={(e) => setCustomTask(e.target.value)}
                fullWidth
                sx={{ mb: 2 }}
            />
            <Button variant="contained" onClick={handleAddCustomTask}>
                Lägg till eget moment
            </Button>
        </Box>
    );
};

export default RepairTasksDropdown;
