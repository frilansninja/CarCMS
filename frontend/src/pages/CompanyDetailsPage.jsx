import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Box,
    Paper,
    Typography,
    TextField,
    Button,
    IconButton,
    List,
    ListItem,
    ListItemText,
    Divider,
    Alert,
    CircularProgress,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Grid,
} from '@mui/material';
import { ArrowLeft, Plus, Edit2, Trash2, Save, X } from 'react-feather';
import { getWorkplaces, addWorkplace, deleteWorkplace, updateWorkplace } from '../api/workplaces';
import { updateCompany } from '../api/companies';

const CompanyDetailsPage = () => {
    const { companyId } = useParams();
    const navigate = useNavigate();

    const [company, setCompany] = useState(null);
    const [workplaces, setWorkplaces] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // Företagsredigering
    const [editingCompany, setEditingCompany] = useState(false);
    const [companyForm, setCompanyForm] = useState({
        name: '',
        orgNumber: '',
        phone: '',
        email: '',
        address: '',
        bankgiro: '',
        plusgiro: '',
        vatNumber: '',
        paymentTerms: '',
        gln: '',
        billingStreet: '',
        billingCity: '',
        billingZip: '',
        billingCountry: 'Sverige',
    });

    // Arbetsplats formulär
    const [openAddDialog, setOpenAddDialog] = useState(false);
    const [workplaceForm, setWorkplaceForm] = useState({
        name: '',
        address: '',
        city: '',
        zipCode: '',
        country: 'Sverige',
        phone: '',
        email: ''
    });
    const [editingWorkplace, setEditingWorkplace] = useState(null);

    useEffect(() => {
        fetchCompanyAndWorkplaces();
    }, [companyId]);

    const fetchCompanyAndWorkplaces = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem('accessToken');

            // Hämta företagsinfo
            const companyResponse = await fetch(`/api/companies/${companyId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (!companyResponse.ok) {
                throw new Error('Kunde inte hämta företagsinformation');
            }

            const companyData = await companyResponse.json();
            setCompany(companyData);

            // Fyll i formuläret med företagsdata
            setCompanyForm({
                name: companyData.name || '',
                orgNumber: companyData.orgNumber || '',
                phone: companyData.phone || '',
                email: companyData.email || '',
                address: companyData.address || '',
                bankgiro: companyData.bankgiro || '',
                plusgiro: companyData.plusgiro || '',
                vatNumber: companyData.vatNumber || '',
                paymentTerms: companyData.paymentTerms || '',
                gln: companyData.gln || '',
                billingStreet: companyData.billingStreet || '',
                billingCity: companyData.billingCity || '',
                billingZip: companyData.billingZip || '',
                billingCountry: companyData.billingCountry || 'Sverige',
            });

            // Hämta arbetsplatser
            const workplacesData = await getWorkplaces(companyId);
            setWorkplaces(workplacesData);

            setLoading(false);
        } catch (err) {
            setError(err.message);
            setLoading(false);
        }
    };

    const handleAddWorkplace = async () => {
        if (!workplaceForm.name.trim()) {
            setError('Arbetsplatsnamn får inte vara tomt');
            return;
        }

        try {
            setError('');
            await addWorkplace(companyId, workplaceForm);
            setSuccess('Arbetsplats tillagd!');
            setWorkplaceForm({
                name: '',
                address: '',
                city: '',
                zipCode: '',
                country: 'Sverige',
                phone: '',
                email: ''
            });
            setOpenAddDialog(false);
            fetchCompanyAndWorkplaces();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Kunde inte lägga till arbetsplats');
        }
    };

    const handleDeleteWorkplace = async (id) => {
        if (!window.confirm('Är du säker på att du vill ta bort denna arbetsplats?')) {
            return;
        }

        try {
            setError('');
            await deleteWorkplace(id);
            setSuccess('Arbetsplats borttagen!');
            fetchCompanyAndWorkplaces();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Kunde inte ta bort arbetsplats');
        }
    };

    const handleEditClick = (workplace) => {
        setEditingWorkplace(workplace);
        setWorkplaceForm({
            name: workplace.name || '',
            address: workplace.address || '',
            city: workplace.city || '',
            zipCode: workplace.zipCode || '',
            country: workplace.country || 'Sverige',
            phone: workplace.phone || '',
            email: workplace.email || ''
        });
    };

    const handleUpdateWorkplace = async () => {
        if (!workplaceForm.name.trim()) {
            setError('Arbetsplatsnamn får inte vara tomt');
            return;
        }

        try {
            setError('');
            await updateWorkplace(editingWorkplace.id, workplaceForm);
            setSuccess('Arbetsplats uppdaterad!');
            setEditingWorkplace(null);
            setWorkplaceForm({
                name: '',
                address: '',
                city: '',
                zipCode: '',
                country: 'Sverige',
                phone: '',
                email: ''
            });
            fetchCompanyAndWorkplaces();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Kunde inte uppdatera arbetsplats');
        }
    };

    const handleEditCompany = () => {
        setEditingCompany(true);
    };

    const handleCancelEditCompany = () => {
        setEditingCompany(false);
        // Återställ formuläret till ursprungliga värden
        if (company) {
            setCompanyForm({
                name: company.name || '',
                orgNumber: company.orgNumber || '',
                phone: company.phone || '',
                email: company.email || '',
                address: company.address || '',
                bankgiro: company.bankgiro || '',
                plusgiro: company.plusgiro || '',
                vatNumber: company.vatNumber || '',
                paymentTerms: company.paymentTerms || '',
                gln: company.gln || '',
                billingStreet: company.billingStreet || '',
                billingCity: company.billingCity || '',
                billingZip: company.billingZip || '',
                billingCountry: company.billingCountry || 'Sverige',
            });
        }
    };

    const handleSaveCompany = async () => {
        if (!companyForm.name.trim() || !companyForm.orgNumber.trim()) {
            setError('Företagsnamn och organisationsnummer krävs');
            return;
        }

        try {
            setError('');

            // Konvertera data till rätt format för backend
            const companyData = {
                id: parseInt(companyId, 10), // Konvertera ID från sträng till nummer
                name: companyForm.name,
                orgNumber: companyForm.orgNumber,
                phone: companyForm.phone || null,
                email: companyForm.email || null,
                address: companyForm.address || null,
                bankgiro: companyForm.bankgiro || null,
                plusgiro: companyForm.plusgiro || null,
                vatNumber: companyForm.vatNumber || null,
                paymentTerms: companyForm.paymentTerms ? parseInt(companyForm.paymentTerms, 10) : null,
                gln: companyForm.gln || null,
                billingStreet: companyForm.billingStreet || null,
                billingCity: companyForm.billingCity || null,
                billingZip: companyForm.billingZip || null,
                billingCountry: companyForm.billingCountry || null,
            };

            await updateCompany(companyData);
            setSuccess('Företagsinformation uppdaterad!');
            setEditingCompany(false);
            fetchCompanyAndWorkplaces();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            console.error('Save error:', err);
            setError('Kunde inte uppdatera företagsinformation: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleCompanyFormChange = (field, value) => {
        setCompanyForm(prev => ({
            ...prev,
            [field]: value
        }));
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            <Button
                startIcon={<ArrowLeft size={18} />}
                onClick={() => navigate('/admin/companies')}
                sx={{ mb: 3 }}
            >
                Tillbaka till företag
            </Button>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
                    {error}
                </Alert>
            )}

            {success && (
                <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
                    {success}
                </Alert>
            )}

            <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
                {!editingCompany ? (
                    <>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                            <Box>
                                <Typography variant="h5" gutterBottom>
                                    {company?.name}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                    Org.nr: {company?.orgNumber}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                    E-post: {company?.email}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                    Telefon: {company?.phone}
                                </Typography>
                                {company?.address && (
                                    <Typography variant="body2" color="text.secondary">
                                        Adress: {company?.address}
                                    </Typography>
                                )}
                            </Box>
                            <Button
                                variant="outlined"
                                color="warning"
                                startIcon={<Edit2 size={18} />}
                                onClick={handleEditCompany}
                            >
                                Redigera företag
                            </Button>
                        </Box>
                    </>
                ) : (
                    <Box>
                        <Typography variant="h6" gutterBottom>
                            Redigera företagsinformation
                        </Typography>
                        <Divider sx={{ mb: 3 }} />

                        <Grid container spacing={2}>
                            {/* Företagsinformation */}
                            <Grid item xs={12}>
                                <Typography variant="subtitle1" fontWeight={600} color="primary" gutterBottom>
                                    Företagsinformation
                                </Typography>
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Företagsnamn"
                                    value={companyForm.name}
                                    onChange={(e) => handleCompanyFormChange('name', e.target.value)}
                                    required
                                />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Organisationsnummer"
                                    value={companyForm.orgNumber}
                                    onChange={(e) => handleCompanyFormChange('orgNumber', e.target.value)}
                                    required
                                />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Telefon"
                                    value={companyForm.phone}
                                    onChange={(e) => handleCompanyFormChange('phone', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    type="email"
                                    label="E-post"
                                    value={companyForm.email}
                                    onChange={(e) => handleCompanyFormChange('email', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    fullWidth
                                    label="Besöksadress"
                                    value={companyForm.address}
                                    onChange={(e) => handleCompanyFormChange('address', e.target.value)}
                                />
                            </Grid>

                            {/* Faktureringsinformation */}
                            <Grid item xs={12} sx={{ mt: 2 }}>
                                <Typography variant="subtitle1" fontWeight={600} color="primary" gutterBottom>
                                    Faktureringsinformation
                                </Typography>
                            </Grid>
                            <Grid item xs={12} sm={6} md={4}>
                                <TextField
                                    fullWidth
                                    label="Bankgiro"
                                    value={companyForm.bankgiro}
                                    onChange={(e) => handleCompanyFormChange('bankgiro', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={4}>
                                <TextField
                                    fullWidth
                                    label="Plusgiro"
                                    value={companyForm.plusgiro}
                                    onChange={(e) => handleCompanyFormChange('plusgiro', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={4}>
                                <TextField
                                    fullWidth
                                    label="Momsnummer"
                                    value={companyForm.vatNumber}
                                    onChange={(e) => handleCompanyFormChange('vatNumber', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={4}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    label="Betalningsvillkor (dagar)"
                                    value={companyForm.paymentTerms}
                                    onChange={(e) => handleCompanyFormChange('paymentTerms', e.target.value)}
                                    helperText="T.ex. 30 dagar netto"
                                    InputProps={{
                                        inputProps: { min: 0 }
                                    }}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6} md={8}>
                                <TextField
                                    fullWidth
                                    label="GLN (Global Location Number)"
                                    value={companyForm.gln}
                                    onChange={(e) => handleCompanyFormChange('gln', e.target.value)}
                                    helperText="13-siffrig GS1-kod"
                                />
                            </Grid>

                            {/* Fakturaadress */}
                            <Grid item xs={12} sx={{ mt: 2 }}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Fakturaadress (om den skiljer sig från besöksadress)
                                </Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    fullWidth
                                    label="Gatuadress"
                                    value={companyForm.billingStreet}
                                    onChange={(e) => handleCompanyFormChange('billingStreet', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Postnummer"
                                    value={companyForm.billingZip}
                                    onChange={(e) => handleCompanyFormChange('billingZip', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Postort"
                                    value={companyForm.billingCity}
                                    onChange={(e) => handleCompanyFormChange('billingCity', e.target.value)}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    fullWidth
                                    label="Land"
                                    value={companyForm.billingCountry}
                                    onChange={(e) => handleCompanyFormChange('billingCountry', e.target.value)}
                                />
                            </Grid>
                        </Grid>

                        <Box sx={{ display: 'flex', gap: 2, mt: 3 }}>
                            <Button
                                variant="contained"
                                color="primary"
                                startIcon={<Save size={18} />}
                                onClick={handleSaveCompany}
                            >
                                Spara ändringar
                            </Button>
                            <Button
                                variant="outlined"
                                color="inherit"
                                startIcon={<X size={18} />}
                                onClick={handleCancelEditCompany}
                            >
                                Avbryt
                            </Button>
                        </Box>
                    </Box>
                )}
            </Paper>

            <Paper elevation={3} sx={{ p: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6">
                        Arbetsplatser
                    </Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={<Plus size={18} />}
                        onClick={() => setOpenAddDialog(true)}
                    >
                        Lägg till arbetsplats
                    </Button>
                </Box>

                <Divider sx={{ mb: 2 }} />

                {workplaces.length === 0 ? (
                    <Typography color="text.secondary" sx={{ textAlign: 'center', py: 3 }}>
                        Inga arbetsplatser ännu. Lägg till en arbetsplats för att komma igång.
                    </Typography>
                ) : (
                    <List>
                        {workplaces.map((workplace, index) => (
                            <Box key={workplace.id}>
                                <ListItem
                                    secondaryAction={
                                        <Box>
                                            <IconButton
                                                color="warning"
                                                onClick={() => handleEditClick(workplace)}
                                                size="small"
                                                sx={{ mr: 1 }}
                                            >
                                                <Edit2 size={18} />
                                            </IconButton>
                                            <IconButton
                                                color="error"
                                                onClick={() => handleDeleteWorkplace(workplace.id)}
                                                size="small"
                                            >
                                                <Trash2 size={18} />
                                            </IconButton>
                                        </Box>
                                    }
                                >
                                    <ListItemText
                                        primary={workplace.name}
                                        secondary={
                                            <>
                                                {workplace.address && <div>{workplace.address}</div>}
                                                {(workplace.zipCode || workplace.city) && (
                                                    <div>{workplace.zipCode} {workplace.city}</div>
                                                )}
                                                {workplace.phone && <div>Tel: {workplace.phone}</div>}
                                                {workplace.email && <div>E-post: {workplace.email}</div>}
                                            </>
                                        }
                                    />
                                </ListItem>
                                {index < workplaces.length - 1 && <Divider />}
                            </Box>
                        ))}
                    </List>
                )}
            </Paper>

            {/* Dialog för att lägga till/redigera arbetsplats */}
            <Dialog open={openAddDialog || editingWorkplace !== null} onClose={() => {
                setOpenAddDialog(false);
                setEditingWorkplace(null);
                setWorkplaceForm({
                    name: '',
                    address: '',
                    city: '',
                    zipCode: '',
                    country: 'Sverige',
                    phone: '',
                    email: ''
                });
            }} maxWidth="md" fullWidth>
                <DialogTitle>{editingWorkplace ? 'Redigera arbetsplats' : 'Lägg till ny arbetsplats'}</DialogTitle>
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 1 }}>
                        <Grid item xs={12}>
                            <TextField
                                autoFocus
                                fullWidth
                                label="Arbetsplatsnamn"
                                value={workplaceForm.name}
                                onChange={(e) => setWorkplaceForm({ ...workplaceForm, name: e.target.value })}
                                required
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Gatuadress"
                                value={workplaceForm.address}
                                onChange={(e) => setWorkplaceForm({ ...workplaceForm, address: e.target.value })}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Postnummer"
                                value={workplaceForm.zipCode}
                                onChange={(e) => setWorkplaceForm({ ...workplaceForm, zipCode: e.target.value })}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Stad"
                                value={workplaceForm.city}
                                onChange={(e) => setWorkplaceForm({ ...workplaceForm, city: e.target.value })}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Land"
                                value={workplaceForm.country}
                                onChange={(e) => setWorkplaceForm({ ...workplaceForm, country: e.target.value })}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Telefon"
                                value={workplaceForm.phone}
                                onChange={(e) => setWorkplaceForm({ ...workplaceForm, phone: e.target.value })}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                type="email"
                                label="E-post"
                                value={workplaceForm.email}
                                onChange={(e) => setWorkplaceForm({ ...workplaceForm, email: e.target.value })}
                            />
                        </Grid>
                    </Grid>
                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={() => {
                            setOpenAddDialog(false);
                            setEditingWorkplace(null);
                            setWorkplaceForm({
                                name: '',
                                address: '',
                                city: '',
                                zipCode: '',
                                country: 'Sverige',
                                phone: '',
                                email: ''
                            });
                        }}
                        color="inherit"
                        startIcon={<X size={18} />}
                    >
                        Avbryt
                    </Button>
                    <Button
                        onClick={editingWorkplace ? handleUpdateWorkplace : handleAddWorkplace}
                        variant="contained"
                        color="primary"
                        startIcon={editingWorkplace ? <Save size={18} /> : <Plus size={18} />}
                    >
                        {editingWorkplace ? 'Spara' : 'Lägg till'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default CompanyDetailsPage;
