import { useState, useEffect } from "react";
import { Container, Paper, Typography, TextField, Button, Select, MenuItem, FormControl, InputLabel } from "@mui/material";
import {useNavigate} from "react-router-dom";

const UserCreatePage = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [roles, setRoles] = useState([]);
    const [selectedCompanyId, setSelectedCompanyId] = useState("");
    const [companies, setCompanies] = useState([]);
    const navigate = useNavigate();

    const userRoles = JSON.parse(localStorage.getItem("userRoles") || "[]");
    const isSuperAdmin = userRoles.includes("SUPER_ADMIN");

    useEffect(() => {
        if (isSuperAdmin) {
            loadCompanies();
        }
    }, [isSuperAdmin]);

    const loadCompanies = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch("/api/companies", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (response.ok) {
                const data = await response.json();
                setCompanies(data);
            }
        } catch (error) {
            console.error("Error loading companies:", error);
        }
    };

    const handleCreateUser = async () => {
        if (isSuperAdmin && !selectedCompanyId) {
            alert("Vänligen välj ett företag");
            return;
        }

        const response = await fetch("/api/users/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            },
            body: JSON.stringify({
                username,
                password,
                roles,
                companyId: isSuperAdmin ? selectedCompanyId : undefined
            }),
        });

        if (response.ok) {
            setUsername("");
            setPassword("");
            setRoles([]);
            setSelectedCompanyId("");
            navigate("/users");
        } else {
            alert("Fel vid skapande av användare.");
        }
    };

    return (
        <Container maxWidth="sm" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h6" gutterBottom>Skapa ny användare</Typography>

                {isSuperAdmin && (
                    <FormControl fullWidth margin="dense" required>
                        <InputLabel>Företag</InputLabel>
                        <Select
                            value={selectedCompanyId}
                            onChange={(e) => setSelectedCompanyId(e.target.value)}
                            label="Företag"
                        >
                            <MenuItem value="">
                                <em>-- Välj företag --</em>
                            </MenuItem>
                            {companies.map((company) => (
                                <MenuItem key={company.id} value={company.id}>
                                    {company.name}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                )}

                <TextField
                    label="Användarnamn"
                    fullWidth
                    margin="dense"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
                <TextField
                    label="Lösenord"
                    type="password"
                    fullWidth
                    margin="dense"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                <FormControl fullWidth margin="dense" required>
                    <InputLabel>Roller</InputLabel>
                    <Select
                        multiple
                        value={roles}
                        onChange={(e) => setRoles(e.target.value)}
                        label="Roller"
                    >
                        <MenuItem value="ADMIN">Admin</MenuItem>
                        <MenuItem value="MECHANIC">Mekaniker</MenuItem>
                        <MenuItem value="CUSTOMER">Kund</MenuItem>
                    </Select>
                </FormControl>

                <Button
                    onClick={handleCreateUser}
                    variant="contained"
                    color="primary"
                    sx={{ mt: 2 }}
                    disabled={!username || !password || roles.length === 0 || (isSuperAdmin && !selectedCompanyId)}
                >
                    Skapa användare
                </Button>
            </Paper>
        </Container>
    );
};

export default UserCreatePage;
