// components/InvoicesTab.jsx
import React from "react";
import { Box, Typography } from "@mui/material";

const InvoicesTab = ({ invoices }) => {
    return (
        <Box>
            <Typography variant="h6">Fakturor</Typography>
            {invoices.length > 0 ? (
                invoices.map((invoice) => (
                    <Typography key={invoice.id}>
                        Faktura #{invoice.id} - {invoice.amount} kr
                    </Typography>
                ))
            ) : (
                <Typography>Inga fakturor registrerade</Typography>
            )}
        </Box>
    );
};

export default InvoicesTab;
