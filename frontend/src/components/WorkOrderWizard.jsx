import React, { useState, useEffect } from "react";
import { Box, Button, TextField, MenuItem, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

const WorkOrderWizard = ({ vehicles, categories, handleSubmit }) => {
    const [step, setStep] = useState(1);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [tasks, setTasks] = useState([]);
    const [selectedTasks, setSelectedTasks] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        if (selectedCategory) {
            fetch(`/api/worktasktemplates/byCategory?categoryId=${selectedCategory.id}`)
                .then((res) => res.json())
                .then((data) => setTasks(data || []))
                .catch((err) => console.error("Fel vid hämtning av arbetsmoment:", err));
        }
    }, [selectedCategory]);

    const handleNext = () => {
        setStep((prev) => prev + 1);
    };

    const handleBack = () => {
        setStep((prev) => prev - 1);
    };

    const handleCreateNewTemplate = () => {
        navigate("/create-template", { state: { returnTo: "/work-order-wizard" } });
    };

    return (
        <Box>
            {step === 1 && (
                <Box>
                    <Typography variant="h6">Steg 1: Välj fordon</Typography>
                    <TextField
                        select
                        label="Välj fordon"
                        value={selectedVehicle || ""}
                        onChange={(e) => setSelectedVehicle(e.target.value)}
                        fullWidth
                        sx={{ mb: 2 }}
                    >
                        {vehicles.map((vehicle) => (
                            <MenuItem key={vehicle.id} value={vehicle.id}>
                                {vehicle.brand} {vehicle.model} - {vehicle.registrationNumber}
                            </MenuItem>
                        ))}
                    </TextField>
                    <Button variant="contained" onClick={handleNext} disabled={!selectedVehicle}>
                        Nästa
                    </Button>
                </Box>
            )}

            {step === 2 && (
                <Box>
                    <Typography variant="h6">Steg 2: Välj kategori</Typography>
                    <TextField
                        select
                        label="Välj kategori"
                        value={selectedCategory || ""}
                        onChange={(e) => setSelectedCategory(categories.find(c => c.id == e.target.value))}
                        fullWidth
                        sx={{ mb: 2 }}
                    >
                        {categories.map((category) => (
                            <MenuItem key={category.id} value={category.id}>
                                {category.name}
                            </MenuItem>
                        ))}
                    </TextField>
                    <Button variant="outlined" onClick={handleBack}>Tillbaka</Button>
                    <Button variant="contained" onClick={handleNext} disabled={!selectedCategory} sx={{ ml: 2 }}>
                        Nästa
                    </Button>
                </Box>
            )}

            {step === 3 && (
                <Box>
                    <Typography variant="h6">Steg 3: Välj arbetsmoment</Typography>
                    {tasks.length > 0 ? (
                        <TextField
                            select
                            label="Välj moment"
                            value={selectedTasks}
                            onChange={(e) => setSelectedTasks([...selectedTasks, e.target.value])}
                            fullWidth
                            sx={{ mb: 2 }}
                        >
                            {tasks.map((task) => (
                                <MenuItem key={task.id} value={task.id}>
                                    {task.description}
                                </MenuItem>
                            ))}
                        </TextField>
                    ) : (
                        <Typography>Inga arbetsmoment hittades för denna kategori.</Typography>
                    )}
                    <Button variant="outlined" onClick={handleCreateNewTemplate} sx={{ mb: 2 }}>
                        Skapa ny mall
                    </Button>
                    <br />
                    <Button variant="outlined" onClick={handleBack}>Tillbaka</Button>
                    <Button variant="contained" onClick={handleNext} disabled={selectedTasks.length === 0} sx={{ ml: 2 }}>
                        Nästa
                    </Button>
                </Box>
            )}

            {step === 4 && (
                <Box>
                    <Typography variant="h6">Steg 4: Granska och skapa arbetsorder</Typography>
                    <Typography variant="body1">Fordon: {selectedVehicle}</Typography>
                    <Typography variant="body1">Kategori: {selectedCategory?.name}</Typography>
                    <Typography variant="body1">Valda moment: {selectedTasks.join(", ")}</Typography>
                    <Button variant="outlined" onClick={handleBack}>Tillbaka</Button>
                    <Button variant="contained" color="success" onClick={() => handleSubmit({ selectedVehicle, selectedCategory, selectedTasks })} sx={{ ml: 2 }}>
                        Skapa arbetsorder
                    </Button>
                </Box>
            )}
        </Box>
    );
};

export default WorkOrderWizard;
