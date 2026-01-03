import React, { useState, useEffect } from "react";
import {
    Box, Button, MenuItem, TextField, Typography, Dialog, Stepper, Step, StepLabel, Snackbar,
    Alert, Checkbox
} from "@mui/material";
import { useNavigate } from "react-router-dom";

const WorkOrderForm = ({ open, handleClose, vehicles, categories, onWorkOrderCreated,setWorkOrders }) => {
    const [step, setStep] = useState(0);
    const [newWorkOrder, setNewWorkOrder] = useState({ vehicleId: "", categoryId: "", repairCategoryId: "", selectedTasks: [] });
    const [repairCategories, setRepairCategories] = useState([]);
    const [taskTemplates, setTaskTemplates] = useState([]);
    const [successMessage, setSuccessMessage] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        console.log("📌 Vald fordon:", newWorkOrder.vehicleId);
    }, [newWorkOrder.vehicleId]);

    useEffect(() => {
        console.log("📌 Vald kategori:", newWorkOrder.categoryId);
        if (!newWorkOrder.categoryId) return;

        const selectedCategory = categories.find(cat => Number(cat.id) === Number(newWorkOrder.categoryId));

        if (selectedCategory?.name.toLowerCase() === "reparation") {
            console.log("Reparation, hämta repaircategories.");
            fetchRepairCategories();
            setTaskTemplates([]); // 🔄 Rensa servicemoment om vi byter från service
        } else {
            setRepairCategories([]);
        }

        if (selectedCategory?.name.toLowerCase() === "service") {
            console.log("Service, hämta service tasks och rensa repaircategories.");
            fetchServiceTasks(newWorkOrder.vehicleId);
            setRepairCategories([]); // 🔄 Rensa reparationskategorier om vi byter från reparation
        }

        // 🔄 Rensa valda arbetsmoment om vi byter kategori
        setNewWorkOrder(prev => ({ ...prev, repairCategoryId: "", selectedTasks: [] }));
    }, [newWorkOrder.categoryId]);

    useEffect(() => {
        if (newWorkOrder.repairCategoryId) {
            console.log("valt repair, hämta task-templates");
            fetchTaskTemplates(newWorkOrder.repairCategoryId);
        }
    }, [newWorkOrder.repairCategoryId]);

    const fetchRepairCategories = () => {
        const token = localStorage.getItem("accessToken");
        fetch("/api/repaircategories", { headers: { Authorization: `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => setRepairCategories(data || []))
            .catch(err => console.error("Kunde inte hämta reparationskategorier", err));
    };

    const fetchTaskTemplates = (repairCategoryId) => {
        const token = localStorage.getItem("accessToken");
        fetch(`/api/worktasktemplates/byRepairCategory?repairCategoryId=${repairCategoryId}`, { headers: { Authorization: `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => setTaskTemplates(data || []))
            .catch(err => console.error("Kunde inte hämta arbetsmoment", err));
    };

    const fetchServiceTasks = (vehicleId) => {
        if (!vehicleId) return;
        const token = localStorage.getItem("accessToken");
        fetch(`/api/service/vehicle/${vehicleId}`, { headers: { Authorization: `Bearer ${token}` } })
            .then(res => res.json())
            .then(data => {
                const formattedTasks = data.map((task, index) => ({
                    id: `service-${index}`,
                    description: task.serviceName || "Saknar beskrivning"
                }));
                setTaskTemplates(formattedTasks);
            })
            .catch(err => console.error("Kunde inte hämta servicemoment", err));
    };
    const handleNext = () => {
        if (step === 2 && taskTemplates.length === 0) {
            navigate("/create-worktask-template", { state: { returnToWorkOrder: true, repairCategoryId: newWorkOrder.repairCategoryId } });
        } else {
            setStep(prev => prev + 1);
        }
    };
    const handleSubmit = async () => {
        console.log("📤 Skickar arbetsorder:", newWorkOrder);

        const token = localStorage.getItem("accessToken");
        const workOrderPayload = {
            vehicleId: newWorkOrder.vehicleId,
            categoryId: newWorkOrder.categoryId,
            description: `Arbetsmoment: ${newWorkOrder.selectedTasks.map(t => t.description).join(", ")}`,
            workTasks: newWorkOrder.selectedTasks.map(task => ({
                templateId: task.id.includes("service") ? null : task.id,
                description: task.description
            }))
        };

        try {
            const response = await fetch(`/api/workorders/vehicle/${newWorkOrder.vehicleId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
                body: JSON.stringify(workOrderPayload)
            });

            if (!response.ok) {
                throw new Error(`Fel vid skapande av arbetsorder: ${await response.text()}`);
            }

            const createdWorkOrder = await response.json();
            console.log("✅ Arbetsorder skapad:", createdWorkOrder);

            if (typeof onWorkOrderCreated === "function") {
                onWorkOrderCreated(createdWorkOrder);
            }

            setSuccessMessage("✅ Arbetsorder skapad!");
            setTimeout(() => {
                setSuccessMessage("");
                handleClose();
                setNewWorkOrder({ vehicleId: "", categoryId: "", repairCategoryId: "", selectedTasks: [] });
            }, 3000);
        } catch (error) {
            console.error("⛔ Fel vid skapande av arbetsorder:", error);
        }
    };

    return (
        <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
            <Box sx={{ p: 3 }}>
                <Typography variant="h6">Skapa arbetsorder</Typography>
                <Stepper activeStep={step} sx={{ mb: 3 }}>
                    <Step><StepLabel>Välj fordon</StepLabel></Step>
                    <Step><StepLabel>Välj kategori</StepLabel></Step>
                    <Step><StepLabel>Välj arbetsmoment</StepLabel></Step>
                    <Step><StepLabel>Bekräfta</StepLabel></Step>
                </Stepper>

                <Snackbar open={Boolean(successMessage)} autoHideDuration={3000} onClose={() => setSuccessMessage("")}>
                    <Alert severity="success" onClose={() => setSuccessMessage("")}>
                        {successMessage}
                    </Alert>
                </Snackbar>

                {step === 0 && (
                    <TextField
                        select fullWidth label="Välj fordon" value={newWorkOrder.vehicleId || ""}
                        onChange={(e) => setNewWorkOrder({ ...newWorkOrder, vehicleId: e.target.value })}
                        sx={{ mb: 2 }}
                    >
                        {vehicles.map(vehicle => (
                            <MenuItem key={vehicle.id} value={vehicle.id}>
                                {vehicle.brand} {vehicle.model} - {vehicle.registrationNumber}
                            </MenuItem>
                        ))}
                    </TextField>
                )}

                {step === 1 && (
                    <TextField
                        select fullWidth label="Välj kategori" value={newWorkOrder.categoryId || ""}
                        onChange={(e) => setNewWorkOrder({ ...newWorkOrder, categoryId: e.target.value })}
                        sx={{ mb: 2 }}
                    >
                        {categories.map(category => (
                            <MenuItem key={category.id} value={category.id}>
                                {category.name}
                            </MenuItem>
                        ))}
                    </TextField>
                )}
                {step === 2 && newWorkOrder.categoryId && (
                    <>

                        <TextField
                            select fullWidth label="Välj reparationskategori"
                            value={newWorkOrder.repairCategoryId || ""}
                            onChange={(e) => setNewWorkOrder({ ...newWorkOrder, repairCategoryId: e.target.value })}
                            sx={{ mb: 2 }}
                        >
                            {repairCategories.map(rc => (
                                <MenuItem key={rc.id} value={rc.id}>{rc.name}</MenuItem>
                            ))}
                        </TextField>

                        {taskTemplates.length > 0 ? (
                            taskTemplates.map(template => (
                                <Box key={template.id} sx={{ display: "flex", alignItems: "center" }}>
                                    <Checkbox
                                        checked={newWorkOrder.selectedTasks.includes(template)}
                                        onChange={() =>
                                            setNewWorkOrder(prev => ({
                                                ...prev,
                                                selectedTasks: prev.selectedTasks.includes(template)
                                                    ? prev.selectedTasks.filter(t => t !== template)
                                                    : [...prev.selectedTasks, template]
                                            }))
                                        }
                                    />
                                    <Typography>{template.description}</Typography>
                                </Box>
                            ))
                        ) : (
                            <Button variant="contained" color="primary" onClick={handleNext}>
                                Skapa ny mall
                            </Button>
                        )}
                    </>
                )}

                {step === 3 && (
                    <Box>
                        <Typography variant="body1">Bekräfta arbetsorder</Typography>
                        <Typography variant="body2">Fordon: {vehicles.find(v => v.id === newWorkOrder.vehicleId)?.brand}</Typography>
                        <Typography variant="body2">Kategori: {categories.find(c => c.id === newWorkOrder.categoryId)?.name}</Typography>
                        <Typography variant="body2">Moment: {newWorkOrder.selectedTasks.map(t => t.description).join(", ")}</Typography>
                    </Box>
                )}


                <Box sx={{ mt: 2, display: "flex", justifyContent: "space-between" }}>
                    <Button disabled={step === 0} onClick={() => setStep(prev => prev - 1)}>Tillbaka</Button>
                    <Button
                        disabled={(step === 0 && !newWorkOrder.vehicleId) ||
                            (step === 1 && !newWorkOrder.categoryId) ||
                            (step === 2 && newWorkOrder.selectedTasks.length === 0)}
                        onClick={() => setStep(prev => prev + 1)}
                    >
                        Nästa
                    </Button>
                    {step === 3 && <Button onClick={handleSubmit}>Skapa arbetsorder</Button>}
                </Box>
            </Box>
        </Dialog>
    );
};

export default WorkOrderForm;
