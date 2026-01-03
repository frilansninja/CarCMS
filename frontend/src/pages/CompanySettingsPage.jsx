import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Container,
    Paper,
    Typography,
    TextField,
    Button,
    Box,
    Alert,
    CircularProgress
} from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';

const CompanySettingsPage = () => {
    const { companyId } = useParams();
    const navigate = useNavigate();
    const [company, setCompany] = useState(null);
    const [customerInactiveDays, setCustomerInactiveDays] = useState('');
    const [customerGdprDeletionDays, setCustomerGdprDeletionDays] = useState('');
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchCompanySettings();
    }, [companyId]);

    const fetchCompanySettings = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/companies/${companyId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!response.ok) {
                throw new Error('Kunde inte hämta företagsinställningar');
            }

            const data = await response.json();
            setCompany(data);
            setCustomerInactiveDays(data.customerInactiveDays || '');
            setCustomerGdprDeletionDays(data.customerGdprDeletionDays || '');
            setLoading(false);
        } catch (err) {
            setError(err.message);
            setLoading(false);
        }
    };

    const handleSave = async () => {
        setSaving(true);
        setError('');
        setSuccess('');

        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/companies/${companyId}/settings`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    customerInactiveDays: customerInactiveDays === '' ? null : parseInt(customerInactiveDays, 10),
                    customerGdprDeletionDays: customerGdprDeletionDays === '' ? null : parseInt(customerGdprDeletionDays, 10)
                })
            });

            if (!response.ok) {
                throw new Error('Kunde inte spara inställningar');
            }

            const updated = await response.json();
            setCompany(updated);
            setSuccess('Inställningar sparade!');
        } catch (err) {
            setError(err.message);
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <Container maxWidth="md" sx={{ mt: 4, display: 'flex', justifyContent: 'center' }}>
                <CircularProgress />
            </Container>
        );
    }

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h5" gutterBottom>
                    Företagsinställningar
                </Typography>
                <Typography variant="subtitle1" color="textSecondary" gutterBottom>
                    {company?.name}
                </Typography>

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}

                {success && (
                    <Alert severity="success" sx={{ mb: 2 }}>
                        {success}
                    </Alert>
                )}

                <Box sx={{ mt: 3 }}>
                    <Typography variant="h6" gutterBottom>
                        Kundhantering
                    </Typography>

                    <TextField
                        label="Antal dagar innan kund anses inaktiv"
                        type="number"
                        fullWidth
                        value={customerInactiveDays}
                        onChange={(e) => setCustomerInactiveDays(e.target.value)}
                        helperText="Lämna tomt för att aldrig markera kunder som inaktiva automatiskt"
                        sx={{ mb: 3 }}
                        InputProps={{
                            inputProps: { min: 1 }
                        }}
                    />

                    <Typography variant="h6" gutterBottom>
                        GDPR-inställningar
                    </Typography>

                    <TextField
                        label="Antal dagar innan inaktiv kund anonymiseras"
                        type="number"
                        fullWidth
                        value={customerGdprDeletionDays}
                        onChange={(e) => setCustomerGdprDeletionDays(e.target.value)}
                        helperText="Lämna tomt för att aldrig anonymisera kunder automatiskt. Kunder anonymiseras efter att ha varit inaktiva i detta antal dagar."
                        sx={{ mb: 3 }}
                        InputProps={{
                            inputProps: { min: 1 }
                        }}
                    />

                    <Box sx={{ display: 'flex', gap: 2 }}>
                        <Button
                            variant="contained"
                            color="primary"
                            startIcon={<SaveIcon />}
                            onClick={handleSave}
                            disabled={saving}
                        >
                            {saving ? 'Sparar...' : 'Spara inställningar'}
                        </Button>
                        <Button
                            variant="outlined"
                            onClick={() => navigate('/admin/companies')}
                        >
                            Tillbaka
                        </Button>
                    </Box>
                </Box>
            </Paper>
        </Container>
    );
};

export default CompanySettingsPage;
