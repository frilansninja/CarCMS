import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
    Alert,
    Box,
    Button,
    Link,
    Paper,
    TextField,
    Typography
} from "@mui/material";

const LoginPage = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await axios.post("http://localhost:8080/api/auth/login", {
                username,
                password,
            });

            if (response.data.accessToken) {
                localStorage.setItem("accessToken", response.data.accessToken);
                localStorage.setItem("refreshToken", response.data.refreshToken);
                localStorage.setItem("companyId", response.data.companyId);

                if (response.data.roles) {
                    localStorage.setItem("userRoles", JSON.stringify(response.data.roles));
                    window.dispatchEvent(new Event("rolesUpdated"));
                }

                navigate("/dashboard");
            } else {
                setError("Inloggning misslyckades, ingen token.");
            }
        } catch (err) {
            console.error("Inloggning misslyckades:", err);
            if (err.response && err.response.status === 401) {
                setError("Felaktigt användarnamn eller lösenord.");
            } else {
                setError("Fel vid inloggning. Försök igen.");
            }
        }
    };

    return (
        <Box
            sx={{
                width: "100%",
                minHeight: "calc(100vh - 64px)", // 64px är standardhöjd för MUI AppBar på desktop
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
            }}
        >
            <Paper
                elevation={3}
                sx={{
                    width: "100%",
                    maxWidth: 420,
                    p: 4,
                }}
            >
                <Typography variant="h5" fontWeight={700} textAlign="center" gutterBottom>
                    Logga in
                </Typography>

                <Box component="form" onSubmit={handleLogin} sx={{ mt: 2 }}>
                    <TextField
                        label="Användarnamn"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        fullWidth
                        required
                        margin="normal"
                    />

                    <TextField
                        label="Lösenord"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        fullWidth
                        required
                        margin="normal"
                    />

                    {error && (
                        <Alert severity="error" sx={{ mt: 2 }}>
                            {error}
                        </Alert>
                    )}

                    <Box
                        sx={{
                            mt: 3,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between",
                            gap: 2,
                        }}
                    >
                        <Button type="submit" variant="contained">
                            Logga in
                        </Button>

                        <Link href="/forgot-password" underline="hover" variant="body2">
                            Glömt lösenord?
                        </Link>
                    </Box>
                </Box>
            </Paper>
        </Box>
    );
};

export default LoginPage;
