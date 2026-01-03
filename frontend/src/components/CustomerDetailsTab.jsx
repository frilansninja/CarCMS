// components/CustomerDetailsTab.jsx
import React from "react";
import { Box, Typography, TextField, Button } from "@mui/material";

const CustomerDetailsTab = ({ endCustomer, isEditing, handleChange, handleSave }) => {
    // Lägg till en guard om endCustomer inte är laddad än
    if (!endCustomer) {
        return <Typography>Laddar kunddata...</Typography>;
    }

    return (
        <Box>
            <Typography variant="h6">Kontaktuppgifter</Typography>
            {isEditing ? (
                <>
                    <TextField
                        label="Namn"
                        name="name"
                        value={endCustomer.name || ""}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Email"
                        name="email"
                        value={endCustomer.email || ""}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Telefon"
                        name="phone"
                        value={endCustomer.phone || ""}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <Button variant="contained" color="success" onClick={handleSave}>
                        Spara
                    </Button>
                </>
            ) : (
                <>
                    <Typography>Namn: {endCustomer.name}</Typography>
                    <Typography>Email: {endCustomer.email || "Saknas"}</Typography>
                    <Typography>Telefon: {endCustomer.phone || "Saknas"}</Typography>
                </>
            )}
            <Typography variant="h6" sx={{ mt: 2 }}>Faktureringsadress</Typography>
            {isEditing ? (
                <>
                    <TextField
                        label="Gata"
                        name="billingStreet"
                        value={endCustomer.billingStreet || ""}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Postnummer"
                        name="billingZip"
                        value={endCustomer.billingZip || ""}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Stad"
                        name="billingCity"
                        value={endCustomer.billingCity || ""}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        label="Land"
                        name="billingCountry"
                        value={endCustomer.billingCountry || ""}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mb: 2 }}
                    />
                </>
            ) : (
                <>
                    <Typography>{endCustomer.billingStreet || "Saknas"}</Typography>
                    <Typography>
                        {endCustomer.billingZip || "Saknas"} {endCustomer.billingCity || "Saknas"}
                    </Typography>
                    <Typography>{endCustomer.billingCountry || "Saknas"}</Typography>
                </>
            )}
        </Box>
    );
};

export default CustomerDetailsTab;
