import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
    Container,
    Paper,
    Typography,
    TextField,
    Select,
    MenuItem,
    Button,
    Box,
    Divider,
    Chip,
    Grid,
    Card,
    CardContent,
    IconButton,
    CircularProgress
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

const UserDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [roles, setRoles] = useState([]);
    const [workOrders, setWorkOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [availableRoles] = useState([
        { value: "SUPER_ADMIN", label: "Super Admin" },
        { value: "CUSTOMER_ADMIN", label: "Kundadministratör" },
        { value: "WORKPLACE_ADMIN", label: "Verkstadsadministratör" },
        { value: "MECHANIC", label: "Mekaniker" },
        { value: "OFFICE", label: "Kontor" },
    ]);

    useEffect(() => {
        fetchUser();
        fetchActiveWorkOrders();
    }, []);

    const fetchUser = async () => {
        try {
            const response = await fetch(`/api/users/${id}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` },
            });
            const data = await response.json();
            setUser(data);
            setRoles(data.roles?.map(r => r.name) || []);
        } catch (error) {
            console.error("Error fetching user:", error);
        } finally {
            setLoading(false);
        }
    };

    const fetchActiveWorkOrders = async () => {
        try {
            const response = await fetch(`/api/workorders/mechanic/${id}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` },
            });
            const data = await response.json();
            // Filter to only show active work orders (not completed)
            const activeOrders = data.filter(wo => wo.status?.name !== "COMPLETED" && wo.status?.name !== "CANCELLED");
            setWorkOrders(activeOrders);
        } catch (error) {
            console.error("Error fetching work orders:", error);
        }
    };

    const handleUpdate = async () => {
        try {
            // Convert role names to Role objects for the backend
            const roleObjects = roles.map(roleName => ({ name: roleName }));

            await fetch(`/api/users/${id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
                },
                body: JSON.stringify({
                    username: user.username,
                    roles: roleObjects
                }),
            });

            alert("Användare uppdaterad!");
            fetchUser(); // Refresh data
        } catch (error) {
            console.error("Error updating user:", error);
            alert("Fel vid uppdatering av användare");
        }
    };

    const handleDelete = async () => {
        if (!window.confirm("Är du säker på att du vill ta bort denna användare?")) return;

        try {
            await fetch(`/api/users/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` },
            });

            alert("Användare borttagen!");
            navigate("/users");
        } catch (error) {
            console.error("Error deleting user:", error);
            alert("Fel vid borttagning av användare");
        }
    };

    const handleRoleChange = (event) => {
        const value = event.target.value;
        setRoles(typeof value === 'string' ? value.split(',') : value);
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
                <CircularProgress />
            </Box>
        );
    }

    if (!user) {
        return (
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Typography>Användare hittades inte</Typography>
            </Container>
        );
    }

    return (
        <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
            {/* Back Button */}
            <Box sx={{ mb: 2 }}>
                <IconButton onClick={() => navigate("/users")} color="primary">
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="caption" sx={{ ml: 1 }}>Tillbaka till användarlistan</Typography>
            </Box>

            {/* User Information Section */}
            <Paper elevation={3} sx={{ padding: 3, mb: 3 }}>
                <Typography variant="h5" gutterBottom>
                    Användarinformation
                </Typography>
                <Divider sx={{ mb: 2 }} />

                <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                        <Typography variant="body2" color="text.secondary">
                            Användarnamn
                        </Typography>
                        <Typography variant="body1" sx={{ mb: 2 }}>
                            {user.username}
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <Typography variant="body2" color="text.secondary">
                            Företag
                        </Typography>
                        <Typography variant="body1" sx={{ mb: 2 }}>
                            {user.company?.name || "Inget företag"}
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <Typography variant="body2" color="text.secondary">
                            Arbetsplats
                        </Typography>
                        <Typography variant="body1" sx={{ mb: 2 }}>
                            {user.workplace?.name || "Ingen arbetsplats"}
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <Typography variant="body2" color="text.secondary">
                            Roller
                        </Typography>
                        <Box sx={{ display: "flex", gap: 1, flexWrap: "wrap", mt: 0.5 }}>
                            {user.roles?.map((role) => (
                                <Chip
                                    key={role.id}
                                    label={availableRoles.find(r => r.value === role.name)?.label || role.name}
                                    color="primary"
                                    size="small"
                                />
                            ))}
                            {(!user.roles || user.roles.length === 0) && (
                                <Typography variant="body2" color="text.secondary">
                                    Inga roller
                                </Typography>
                            )}
                        </Box>
                    </Grid>
                </Grid>
            </Paper>

            {/* Active Work Orders Section */}
            <Paper elevation={3} sx={{ padding: 3, mb: 3 }}>
                <Typography variant="h5" gutterBottom>
                    Pågående arbetsordrar
                </Typography>
                <Divider sx={{ mb: 2 }} />

                {workOrders.length === 0 ? (
                    <Typography variant="body2" color="text.secondary">
                        Användaren arbetar inte på några arbetsordrar just nu.
                    </Typography>
                ) : (
                    <Grid container spacing={2}>
                        {workOrders.map((workOrder) => (
                            <Grid item xs={12} key={workOrder.id}>
                                <Card
                                    variant="outlined"
                                    sx={{
                                        cursor: "pointer",
                                        "&:hover": { bgcolor: "action.hover" }
                                    }}
                                    onClick={() => navigate(`/workorders/${workOrder.id}`)}
                                >
                                    <CardContent>
                                        <Box display="flex" justifyContent="space-between" alignItems="start">
                                            <Box>
                                                <Typography variant="h6">
                                                    #{workOrder.id} - {workOrder.vehicle?.registrationNumber || "Okänt fordon"}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    {workOrder.vehicle?.brand} {workOrder.vehicle?.model}
                                                </Typography>
                                                <Typography variant="body2" sx={{ mt: 1 }}>
                                                    {workOrder.description || "Ingen beskrivning"}
                                                </Typography>
                                            </Box>
                                            <Chip
                                                label={workOrder.status?.displayName || workOrder.status?.name || "Okänd"}
                                                color="warning"
                                                size="small"
                                            />
                                        </Box>
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}
            </Paper>

            {/* Edit User Section */}
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h5" gutterBottom>
                    Redigera användare
                </Typography>
                <Divider sx={{ mb: 2 }} />

                <TextField
                    label="Användarnamn"
                    fullWidth
                    margin="normal"
                    value={user.username}
                    onChange={(e) => setUser({ ...user, username: e.target.value })}
                />

                <Typography variant="body2" color="text.secondary" sx={{ mt: 2, mb: 1 }}>
                    Roller
                </Typography>
                <Select
                    multiple
                    fullWidth
                    value={roles}
                    onChange={handleRoleChange}
                    renderValue={(selected) => (
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                            {selected.map((value) => (
                                <Chip
                                    key={value}
                                    label={availableRoles.find(r => r.value === value)?.label || value}
                                    size="small"
                                />
                            ))}
                        </Box>
                    )}
                >
                    {availableRoles.map((role) => (
                        <MenuItem key={role.value} value={role.value}>
                            {role.label}
                        </MenuItem>
                    ))}
                </Select>

                <Box sx={{ mt: 3, display: "flex", gap: 2 }}>
                    <Button
                        onClick={handleUpdate}
                        variant="contained"
                        color="primary"
                    >
                        Spara ändringar
                    </Button>
                    <Button
                        onClick={handleDelete}
                        variant="outlined"
                        color="error"
                    >
                        Ta bort användare
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
};

export default UserDetailsPage;
