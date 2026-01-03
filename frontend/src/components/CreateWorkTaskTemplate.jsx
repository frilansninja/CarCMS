import React, { useEffect, useState } from "react";
import { Box, Button, MenuItem, TextField, Typography } from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";

const CreateWorkTaskTemplate = () => {
    const [repairCategories, setRepairCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState("");
    const [description, setDescription] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();
    const returnToWorkOrder = location.state?.returnToWorkOrder || false;
    const preselectedCategoryId = location.state?.repairCategoryId || "";

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        fetch("/api/repaircategories", {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(data => setRepairCategories(data || []))
            .catch(err => console.error("Fel vid hämtning av reparationskategorier:", err));
    }, []);

    const handleSubmit = async () => {
        if (!selectedCategory || !description.trim()) {
            alert("Vänligen välj en kategori och ange en beskrivning.");
            return;
        }

        setLoading(true);
        const token = localStorage.getItem("accessToken");
        const newTaskTemplate = { repairCategoryId: selectedCategory, description };

        try {
            const response = await fetch("/api/worktasktemplates", {
                method: "POST",
                headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
                body: JSON.stringify(newTaskTemplate)
            });

            if (response.ok) {
                const createdTemplate = await response.json();
                alert("Mall skapad!");

                // Om vi kom från WorkOrderForm, skicka tillbaka användaren med den nya mallen vald
                if (returnToWorkOrder) {
                    navigate(-1, { state: { selectedTemplateId: createdTemplate.id } });
                } else {
                    setSelectedCategory("");
                    setDescription("");
                }
            } else {
                console.error("Misslyckades att skapa mallen", await response.text());
            }
        } catch (error) {
            console.error("Fel vid skapande av arbetsuppgiftsmall", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ p: 3, maxWidth: "500px", mx: "auto", mt: 5, boxShadow: 3, borderRadius: 2 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>Skapa ny arbetsuppgiftsmall</Typography>

            <TextField
                select
                fullWidth
                label="Välj reparationskategori"
                value={selectedCategory || preselectedCategoryId}
                onChange={(e) => setSelectedCategory(e.target.value)}
                sx={{ mb: 2 }}
            >
                <MenuItem value="" disabled>Välj en reparationskategori</MenuItem>
                {repairCategories.map(rc => (
                    <MenuItem key={rc.id} value={rc.id}>{rc.name}</MenuItem>
                ))}
            </TextField>

            <TextField
                fullWidth
                label="Beskrivning av arbetsmoment"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                sx={{ mb: 2 }}
            />

            <Button
                variant="contained"
                color="primary"
                fullWidth
                onClick={handleSubmit}
                disabled={loading}
            >
                {loading ? "Sparar..." : "Skapa arbetsuppgiftsmall"}
            </Button>

            {returnToWorkOrder && (
                <Button
                    variant="outlined"
                    color="secondary"
                    fullWidth
                    sx={{ mt: 1 }}
                    onClick={() => navigate(-1)}
                >
                    Tillbaka till arbetsorder
                </Button>
            )}
        </Box>
    );
};

export default CreateWorkTaskTemplate;
