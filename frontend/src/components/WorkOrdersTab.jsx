import React, { useState } from "react";
import { Box, Typography, Button, Dialog } from "@mui/material";
import WorkOrderForm from "./WorkOrderForm"; // Det stegvisa formuläret

const WorkOrdersTab = ({ workOrders, setWorkOrders, isAdmin, vehicles, categories }) => {
    const [isFormOpen, setIsFormOpen] = useState(false);

    // Funktion för att hantera när en arbetsorder skapas
    const handleWorkOrderCreated = (newWorkOrder) => {
        setWorkOrders((prev) => [...prev, newWorkOrder]); // Uppdatera listan
        setIsFormOpen(false); // Stäng formuläret
    };

    return (
        <Box>
            <Typography variant="h6">Arbetsordrar</Typography>
            {isAdmin && (
                <Button variant="contained" color="primary" onClick={() => {
                    console.log("Knappen klickad");
                    setIsFormOpen(true);
                 }}>
                    Skapa arbetsorder
                </Button>
            )}

            {workOrders.length > 0 ? (
                workOrders.map((order) => (
                    <Box
                        key={order.id}
                        sx={{ p: 2, borderBottom: "1px solid #ccc", cursor: "pointer", "&:hover": { backgroundColor: "#f5f5f5" } }}
                        onClick={() => window.location.assign(`/workorders/${order.id}`)}
                    >
                        <Typography variant="subtitle1">
                            <strong>{order.description}</strong>
                        </Typography>
                        <Typography variant="body2">Status: {order.status?.name}</Typography>
                    </Box>
                ))
            ) : (
                <Typography>Inga arbetsordrar registrerade</Typography>
            )}

            {/* Steg-för-steg-formulär för att skapa en arbetsorder */}
            <Dialog open={isFormOpen} onClose={() => setIsFormOpen(false)} fullWidth maxWidth="sm">
                <WorkOrderForm
                    open={isFormOpen}
                    handleClose={() => setIsFormOpen(false)}
                    vehicles={vehicles}
                    categories={categories} // ✅ Se till att detta skickas in
                    setWorkOrders={setWorkOrders}    // onWorkOrderCreated={handleWorkOrderCreated}
                />
            </Dialog>
        </Box>
    );
};

export default WorkOrdersTab;
