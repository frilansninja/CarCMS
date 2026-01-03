import { useState } from "react";
import { requestPasswordReset } from "../api/auth";
import { Box, TextField, Button, Typography, Alert, Paper } from "@mui/material";

const ForgotPasswordPage = () => {
    const [username, setUsername] = useState("");
    const [resetLink, setResetLink] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setMessage("");
        setResetLink("");
        setLoading(true);

        try {
            const response = await requestPasswordReset(username);
            setResetLink(response.resetLink);
            setMessage(response.message);
        } catch (err) {
            setError("An error occurred. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const copyToClipboard = () => {
        navigator.clipboard.writeText(resetLink);
        alert("Reset link copied to clipboard!");
    };

    return (
        <Box sx={{ maxWidth: 500, mx: "auto", mt: 8, p: 3 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Typography variant="h4" gutterBottom>
                    Glömt lösenord
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                    Ange din e-postadress så genererar vi en återställningslänk.
                </Typography>

                <form onSubmit={handleSubmit}>
                    <TextField
                        fullWidth
                        label="E-postadress"
                        type="email"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        sx={{ mb: 2 }}
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        disabled={loading}
                        sx={{ mb: 2 }}
                    >
                        {loading ? "Genererar..." : "Generera återställningslänk"}
                    </Button>
                </form>

                {message && (
                    <Alert severity="info" sx={{ mb: 2 }}>
                        {message}
                    </Alert>
                )}

                {resetLink && (
                    <Box sx={{ mt: 3 }}>
                        <Alert severity="success" sx={{ mb: 2 }}>
                            Återställningslänk genererad!
                        </Alert>
                        <TextField
                            fullWidth
                            label="Återställningslänk"
                            value={resetLink}
                            InputProps={{ readOnly: true }}
                            sx={{ mb: 2 }}
                        />
                        <Button
                            variant="outlined"
                            fullWidth
                            onClick={copyToClipboard}
                        >
                            Kopiera länk
                        </Button>
                        <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: "block" }}>
                            Länken är giltig i 1 timme. Kopiera och skicka den manuellt till användaren.
                        </Typography>
                    </Box>
                )}

                {error && (
                    <Alert severity="error" sx={{ mt: 2 }}>
                        {error}
                    </Alert>
                )}

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

export default ForgotPasswordPage;
