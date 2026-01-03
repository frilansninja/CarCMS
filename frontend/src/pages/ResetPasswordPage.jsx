import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { resetPassword } from "../api/auth";
import { Box, TextField, Button, Typography, Alert, Paper } from "@mui/material";

const ResetPasswordPage = () => {
    const { token } = useParams(); // Get token from URL
    const navigate = useNavigate();
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        // Validate passwords match
        if (newPassword !== confirmPassword) {
            setError("Lösenorden matchar inte");
            return;
        }

        // Validate password strength
        if (newPassword.length < 8) {
            setError("Lösenordet måste vara minst 8 tecken");
            return;
        }

        setLoading(true);

        try {
            await resetPassword(token, newPassword);
            setSuccess(true);

            // Redirect to login after 3 seconds
            setTimeout(() => {
                navigate("/login");
            }, 3000);
        } catch (err) {
            const errorMessage = err.response?.data?.error || "Ogiltig eller utgången återställningslänk";
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <Box sx={{ maxWidth: 500, mx: "auto", mt: 8, p: 3 }}>
                <Paper elevation={3} sx={{ p: 4 }}>
                    <Alert severity="success">
                        Lösenordet har återställts! Du omdirigeras till inloggningssidan...
                    </Alert>
                </Paper>
            </Box>
        );
    }

    return (
        <Box sx={{ maxWidth: 500, mx: "auto", mt: 8, p: 3 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Typography variant="h4" gutterBottom>
                    Återställ lösenord
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                    Ange ditt nya lösenord nedan.
                </Typography>

                <form onSubmit={handleSubmit}>
                    <TextField
                        fullWidth
                        label="Nytt lösenord"
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        sx={{ mb: 2 }}
                        helperText="Minst 8 tecken"
                    />

                    <TextField
                        fullWidth
                        label="Bekräfta lösenord"
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                        sx={{ mb: 2 }}
                    />

                    {error && (
                        <Alert severity="error" sx={{ mb: 2 }}>
                            {error}
                        </Alert>
                    )}

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        disabled={loading}
                    >
                        {loading ? "Återställer..." : "Återställ lösenord"}
                    </Button>
                </form>

                <Button
                    href="/login"
                    variant="text"
                    fullWidth
                    sx={{ mt: 3 }}
                >
                    Tillbaka till inloggning
                </Button>
            </Paper>
        </Box>
    );
};

export default ResetPasswordPage;
