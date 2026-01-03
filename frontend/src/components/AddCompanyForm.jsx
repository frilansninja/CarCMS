import { useState } from "react";
import { addCompany } from '../api/companies';
import {
    TextField,
    Button,
    Box,
    Grid,
    DialogActions,
    Typography,
    Divider,
} from '@mui/material';
import { Plus, X } from 'react-feather';

const AddCompanyForm = ({ onCompanyAdded, onCancel }) => {
    const [newCompany, setNewCompany] = useState({
        name: "",
        orgNumber: "",
        phone: "",
        email: "",
        address: "",
        // Billing information
        bankgiro: "",
        plusgiro: "",
        vatNumber: "",
        paymentTerms: "",
        gln: "",
        billingStreet: "",
        billingCity: "",
        billingZip: "",
        billingCountry: "Sverige",
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        await addCompany(newCompany);
        setNewCompany({
            name: "",
            orgNumber: "",
            phone: "",
            email: "",
            address: "",
            bankgiro: "",
            plusgiro: "",
            vatNumber: "",
            paymentTerms: "",
            gln: "",
            billingStreet: "",
            billingCity: "",
            billingZip: "",
            billingCountry: "Sverige",
        });
        onCompanyAdded();
    };

    return (
        <Box component="form" onSubmit={handleSubmit}>
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
                        value={newCompany.name}
                        onChange={(e) => setNewCompany({ ...newCompany, name: e.target.value })}
                        required
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        label="Organisationsnummer"
                        value={newCompany.orgNumber}
                        onChange={(e) => setNewCompany({ ...newCompany, orgNumber: e.target.value })}
                        required
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        label="Telefon"
                        value={newCompany.phone}
                        onChange={(e) => setNewCompany({ ...newCompany, phone: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        type="email"
                        label="E-post"
                        value={newCompany.email}
                        onChange={(e) => setNewCompany({ ...newCompany, email: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        label="Besöksadress"
                        value={newCompany.address}
                        onChange={(e) => setNewCompany({ ...newCompany, address: e.target.value })}
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
                        value={newCompany.bankgiro}
                        onChange={(e) => setNewCompany({ ...newCompany, bankgiro: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <TextField
                        fullWidth
                        label="Plusgiro"
                        value={newCompany.plusgiro}
                        onChange={(e) => setNewCompany({ ...newCompany, plusgiro: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <TextField
                        fullWidth
                        label="Momsnummer"
                        value={newCompany.vatNumber}
                        onChange={(e) => setNewCompany({ ...newCompany, vatNumber: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <TextField
                        fullWidth
                        type="number"
                        label="Betalningsvillkor (dagar)"
                        value={newCompany.paymentTerms}
                        onChange={(e) => setNewCompany({ ...newCompany, paymentTerms: e.target.value })}
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
                        value={newCompany.gln}
                        onChange={(e) => setNewCompany({ ...newCompany, gln: e.target.value })}
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
                        value={newCompany.billingStreet}
                        onChange={(e) => setNewCompany({ ...newCompany, billingStreet: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        label="Postnummer"
                        value={newCompany.billingZip}
                        onChange={(e) => setNewCompany({ ...newCompany, billingZip: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        label="Postort"
                        value={newCompany.billingCity}
                        onChange={(e) => setNewCompany({ ...newCompany, billingCity: e.target.value })}
                    />
                </Grid>
                <Grid item xs={12}>
                    <TextField
                        fullWidth
                        label="Land"
                        value={newCompany.billingCountry}
                        onChange={(e) => setNewCompany({ ...newCompany, billingCountry: e.target.value })}
                    />
                </Grid>
            </Grid>

            <DialogActions sx={{ px: 0, pt: 3 }}>
                <Button
                    onClick={onCancel}
                    color="inherit"
                    startIcon={<X size={18} />}
                >
                    Avbryt
                </Button>
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    startIcon={<Plus size={18} />}
                >
                    Lägg till företag
                </Button>
            </DialogActions>
        </Box>
    );
};

export default AddCompanyForm;
