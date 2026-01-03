import { useEffect, useState } from "react";
import { useSearchParams, useNavigate, useLocation } from "react-router-dom";
import { Container, Paper, Typography, Button } from "@mui/material";
import workOrder from "../components/WorkOrder.jsx";


const OrderParts = () => {
    const location = useLocation();
    const workOrderId = location.state?.workOrderId || null;
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const partNumber = searchParams.get("partNumber");
    const [availableSuppliers, setAvailableSuppliers] = useState([]);

    console.log("workOrderId? ", workOrderId);
    useEffect(() => {
        if (partNumber) {
            fetchAvailableSuppliers();
        }
    }, [partNumber]);

    const fetchAvailableSuppliers = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/suppliers/parts?partNumber=${partNumber}`, {
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                setAvailableSuppliers(data);
            } else {
                console.error("🚫 Misslyckades med att hämta leverantörer.");
            }
        } catch (error) {
            console.error("❌ Fel vid hämtning av leverantörer:", error);
        }
    };

    const handleOrderPart = async (supplierId, articleId) => {
        const orderData = {
            articleId,
            supplierId,
            workOrderId,
            quantity: 1
        }
        console.log("📦 Skickar beställning:", { orderData });
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/part-orders/order`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(orderData) // Standard 1 st
            });

            if (response.ok) {
                console.log("✅ Beställning skapad!");
                navigate(-1); // Gå tillbaka till föregående sida
            } else {
                console.error("🚫 Misslyckades med att beställa del.");
            }
        } catch (error) {
            console.error("❌ Fel vid beställning av del:", error);
        }
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h5">Beställ reservdel</Typography>
                <Typography variant="h6">Artikelnummer: {partNumber}</Typography>

                <Typography variant="h6">Tillgängliga leverantörer</Typography>
                {availableSuppliers.length > 0 ? (
                    availableSuppliers.map((supplier, index) => {
                        console.log("🔍 Supplier data:", supplier); // 🔄 Logga leverantören
                        return (
                            <Paper key={supplier.id || `supplier-${index}`} elevation={2} sx={{ p: 2, my: 1 }}>
                                <Typography>{supplier.name}</Typography>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={() => {
                                        if (supplier.articles && supplier.articles.length > 0) {
                                            console.log("✅ Skickar artikelId:", supplier.articles[0].id);
                                            handleOrderPart(supplier.id, supplier.articles[0].id);
                                        } else {
                                            console.warn("🚨 Ingen artikel kopplad till denna leverantör!");
                                        }
                                    }}
                                >
                                    🛒 Beställ från {supplier.name || "Okänd leverantör"}
                                </Button>

                            </Paper>
                        );
                    })
                ) : (
                    <Typography>Inga leverantörer hittades.</Typography>
                )}


                <Button
                    variant="contained"
                    color="secondary"
                    sx={{ mt: 2 }}
                    onClick={() => navigate(-1)}
                >
                    🔙 Tillbaka
                </Button>
            </Paper>
        </Container>
    );
};

export default OrderParts;
