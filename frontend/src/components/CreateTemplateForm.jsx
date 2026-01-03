// components/CreateTemplateForm.jsx
import React, { useState } from "react";
import { TextField, Button, MenuItem, Box, Typography } from "@mui/material";

const CreateTemplateForm = ({ categories, onTemplateCreated }) => {
    const [description, setDescription] = useState("");
    const [categoryId, setCategoryId] = useState("");
    const [estimatedTime, setEstimatedTime] = useState("");

    const handleSubmit = async () => {
        if (!description || !categoryId || !estimatedTime) {
            alert("Fyll i alla fält!");
            return;
        }
        const token = localStorage.getItem("accessToken");
        const payload = {
            description,
            categoryId: Number(categoryId),
            estimatedTime: estimatedTime ? Number(estimatedTime) : 0
        };

        console.log("Skickar payload:", payload); // 🔥 Debug-logg
        try {
            const response = await fetch("/api/worktasktemplates", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });
            if (response.ok) {
                const createdTemplate = await response.json();
                console.log("Mall skapad:", createdTemplate);
                // Återställ formuläret
                setDescription("");
                setCategoryId("");
                setEstimatedTime("");

                // Anropa callback så att listan uppdateras
                if (onTemplateCreated) {
                    onTemplateCreated();
                }
            } else {
                console.error("Misslyckades med att skapa mall. Status:", response.status);
            }
        } catch (error) {
            console.error("Fel vid skapande av mall:", error);
        }
    };


    return (
        <Box sx={{ p: 2, border: "1px solid #ccc", mb: 2 }}>
            <Typography variant="h6">Skapa ny arbetsuppgiftsmall</Typography>
            <TextField
                label="Beskrivning"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                fullWidth
                sx={{ mb: 2 }}
            />
            <TextField
                select
                label="Välj kategori"
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                fullWidth
                sx={{ mb: 2 }}
            >
                <MenuItem value="" disabled>Välj en kategori</MenuItem>
                {categories.map((cat) => (
                    <MenuItem key={cat.id} value={cat.id}>
                        {cat.name}
                    </MenuItem>
                ))}
            </TextField>
            <TextField
                label="Beräknad tid (minuter)"
                type="number"
                value={estimatedTime}
                onChange={(e) => setEstimatedTime(e.target.value)}
                fullWidth
                sx={{ mb: 2 }}
            />
            <Button variant="contained" color="primary" onClick={handleSubmit}>
                Skapa mall
            </Button>
        </Box>
    );
};

export default CreateTemplateForm;
