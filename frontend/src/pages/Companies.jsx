import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchCompanies } from "../api/companies";
import CompanyTable from "../components/CompanyTable";
import AddCompanyForm from "../components/AddCompanyForm";
import { Typography, Box, Button, Dialog, DialogTitle, DialogContent } from "@mui/material";
import { Plus } from 'react-feather';

const Companies = () => {
    const [companies, setCompanies] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const accessToken = localStorage.getItem("accessToken");
        if (accessToken) {
            loadCompanies();
        } else {
            console.log('wont load companies, no token');
            navigate("/login");
        }
    }, []);

    const loadCompanies = async () => {
        const data = await fetchCompanies();
        setCompanies(data);
    };

    const handleCompanyAdded = () => {
        loadCompanies();
        setOpenDialog(false);
    };

    return (
        <Box>
            <Box sx={{ textAlign: 'center', mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Företagshantering
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<Plus size={18} />}
                    onClick={() => setOpenDialog(true)}
                    sx={{ mt: 2 }}
                >
                    Lägg till företag
                </Button>
            </Box>

            <CompanyTable
                companies={companies}
            />

            <Dialog
                open={openDialog}
                onClose={() => setOpenDialog(false)}
                maxWidth="md"
                fullWidth
            >
                <DialogTitle>Lägg till nytt företag</DialogTitle>
                <DialogContent sx={{ pt: 2 }}>
                    <AddCompanyForm
                        onCompanyAdded={handleCompanyAdded}
                        onCancel={() => setOpenDialog(false)}
                    />
                </DialogContent>
            </Dialog>
        </Box>
    );
};

export default Companies;
