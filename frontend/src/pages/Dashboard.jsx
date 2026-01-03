import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Typography, Grid, Card, CardContent, CardActionArea } from "@mui/material";
import BusinessIcon from "@mui/icons-material/Business";
import PeopleIcon from "@mui/icons-material/People";
import PersonIcon from "@mui/icons-material/Person";
import DirectionsCarIcon from "@mui/icons-material/DirectionsCar";
import BuildIcon from "@mui/icons-material/Build";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";

const Dashboard = () => {
    const navigate = useNavigate();
    const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
    const isSuperAdmin = roles.includes("SUPER_ADMIN");
    const isAdmin = roles.includes("ADMIN") || roles.includes("SUPER_ADMIN");

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            navigate("/login");
        }
    }, [navigate]);

    const quickLinks = [
        ...(isSuperAdmin ? [
            { title: "Företag", icon: <BusinessIcon fontSize="large" />, path: "/admin/companies", color: "#1976d2" },
        ] : []),
        ...(isAdmin ? [
            { title: "Användare", icon: <PeopleIcon fontSize="large" />, path: "/users", color: "#2e7d32" },
        ] : []),
        { title: "Kunder", icon: <PersonIcon fontSize="large" />, path: "/endcustomers", color: "#ed6c02" },
        { title: "Fordon", icon: <DirectionsCarIcon fontSize="large" />, path: "/vehicles", color: "#9c27b0" },
        { title: "Arbetsorder", icon: <BuildIcon fontSize="large" />, path: "/workorders", color: "#d32f2f" },
        ...(isAdmin ? [
            { title: "Kalender", icon: <CalendarMonthIcon fontSize="large" />, path: "/calendar", color: "#0288d1" },
        ] : []),
    ];

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                Välkommen till Carmoury
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
                {isSuperAdmin && "Du är inloggad som systemadministratör. "}
                Välj en sektion nedan eller använd menyn för att komma igång.
            </Typography>

            <Grid container spacing={3}>
                {quickLinks.map((link, index) => (
                    <Grid item xs={12} sm={6} md={4} key={index}>
                        <Card>
                            <CardActionArea onClick={() => navigate(link.path)}>
                                <CardContent sx={{ textAlign: "center", py: 4 }}>
                                    <Box sx={{ color: link.color, mb: 2 }}>
                                        {link.icon}
                                    </Box>
                                    <Typography variant="h6">
                                        {link.title}
                                    </Typography>
                                </CardContent>
                            </CardActionArea>
                        </Card>
                    </Grid>
                ))}
            </Grid>
        </Box>
    );
};

export default Dashboard;
