import { useState, useEffect } from 'react';
import { Container, Paper, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, TablePagination, Checkbox, Button, Chip, Box, Dialog, DialogTitle, DialogContent, DialogActions, IconButton } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';
import PersonIcon from '@mui/icons-material/Person';


const EndCustomerPage = () => {
    const navigate = useNavigate();
    const [endcustomers, setEndCustomers] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [hasActiveWorkOrders, setHasActiveWorkOrders] = useState(false);
    const [includeArchived, setIncludeArchived] = useState(false);
    const [exportFormat, setExportFormat] = useState('');
    const [openDialog, setOpenDialog] = useState(false);
    const [newCustomer, setNewCustomer] = useState({ name: '', email: '', phone: '', address: '' });

    useEffect(() => {
        fetchEndCustomers();
    }, [page, rowsPerPage, searchTerm, hasActiveWorkOrders, includeArchived]);

    const fetchEndCustomers = async () => {
        try {
            const token = localStorage.getItem("accessToken");

            if (!token) {
                console.error("Ingen accessToken hittades! Användaren kanske är utloggad.");
                return;
            }

            const url = `/api/endcustomers/all?includeArchived=${includeArchived}`;
            console.log("Anropar API:", url);

            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.status === 403) {
                console.error("Åtkomst nekad! Kontrollera API-behörigheter.");
                return;
            }

            const data = await response.json();

            if (!Array.isArray(data)) {
                console.error("Fel format på API-svar: Förväntade en array men fick:", data);
                setEndCustomers([]);
                return;
            }

            setEndCustomers(data);
        } catch (error) {
            console.error("Fel vid hämtning av slutkunder:", error);
            setEndCustomers([]);
        }
    };






    const handleSearch = (event) => {
        setSearchTerm(event.target.value);
        setPage(0);
    };

    const handlePageChange = (event, newPage) => {
        setPage(newPage);
    };

    const handleRowsPerPageChange = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleExport = (format) => {
        setExportFormat(format);
        window.location.href = `/api/endcustomers/export?format=${format}`;
    };

    const handleOpenDialog = () => {
        setNewCustomer({ name: '', email: '', phone: '', address: '' });
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setNewCustomer({ name: '', email: '', phone: '', address: '' });
    };

    const handleCreateCustomer = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const companyId = localStorage.getItem("companyId");

            const response = await fetch(`/api/endcustomers/${companyId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(newCustomer)
            });

            if (response.ok) {
                handleCloseDialog();
                fetchEndCustomers();
            } else {
                console.error("Misslyckades med att skapa slutkund");
            }
        } catch (error) {
            console.error("Fel vid skapande av slutkund:", error);
        }
    };

    const isFormValid = () => {
        return newCustomer.name?.trim() && newCustomer.email?.trim() && newCustomer.phone?.trim();
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6">Kunder</Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={<AddIcon />}
                        onClick={handleOpenDialog}
                    >
                        Lägg till kund
                    </Button>
                </Box>

                {endcustomers.length === 0 ? (
                    <Box sx={{ p: 6, textAlign: "center" }}>
                        <PersonIcon sx={{ fontSize: 60, color: "text.secondary", mb: 2 }} />
                        <Typography variant="h6" color="text.secondary">
                            Inga kunder registrerade
                        </Typography>
                        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                            Klicka på "Lägg till kund" för att registrera din första kund.
                        </Typography>
                    </Box>
                ) : (
                    <>
                        <TextField
                            label="Sök kund"
                            variant="outlined"
                            fullWidth
                            value={searchTerm}
                            onChange={handleSearch}
                            sx={{ mb: 2 }}
                        />

                        <Box sx={{ mb: 2 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                <Checkbox
                                    checked={hasActiveWorkOrders}
                                    onChange={(e) => setHasActiveWorkOrders(e.target.checked)}
                                />
                                <Typography variant="body2">Visa endast kunder med aktiva arbetsordrar</Typography>
                            </Box>
                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <Checkbox
                                    checked={includeArchived}
                                    onChange={(e) => setIncludeArchived(e.target.checked)}
                                />
                                <Typography variant="body2">Visa arkiverade kunder</Typography>
                            </Box>
                        </Box>

                        <TableContainer component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Namn</TableCell>
                                        <TableCell>Adress</TableCell>
                                        <TableCell>Status</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {endcustomers.map((endcustomer) => (
                                        <TableRow
                                            key={endcustomer.id}
                                            hover
                                            style={{
                                                cursor: 'pointer',
                                                opacity: endcustomer.isActive === false ? 0.6 : 1
                                            }}
                                            onClick={() => {
                                                console.log("Navigerar till:", `/endcustomers/${endcustomer.id}`);
                                                navigate(`/endcustomers/${endcustomer.id}`);
                                            }}
                                        >
                                            <TableCell>{endcustomer.name}</TableCell>
                                            <TableCell>{endcustomer.address}</TableCell>
                                            <TableCell>
                                                {endcustomer.isActive === false ? (
                                                    <Chip label="Arkiverad" color="warning" size="small" />
                                                ) : (
                                                    <Chip label="Aktiv" color="success" size="small" />
                                                )}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>

                            </Table>
                        </TableContainer>

                        <TablePagination
                            component="div"
                            count={endcustomers.length}
                            page={page}
                            onPageChange={handlePageChange}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={handleRowsPerPageChange}
                        />

                        <Button onClick={() => handleExport('csv')}>Exportera CSV</Button>
                        <Button onClick={() => handleExport('pdf')}>Exportera PDF</Button>
                    </>
                )}
            </Paper>

            {/* Add Customer Dialog */}
            <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
                <DialogTitle>Lägg till ny kund</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Namn"
                        type="text"
                        fullWidth
                        value={newCustomer.name}
                        onChange={(e) => setNewCustomer({ ...newCustomer, name: e.target.value })}
                        required
                    />
                    <TextField
                        margin="dense"
                        label="E-post"
                        type="email"
                        fullWidth
                        value={newCustomer.email}
                        onChange={(e) => setNewCustomer({ ...newCustomer, email: e.target.value })}
                        required
                    />
                    <TextField
                        margin="dense"
                        label="Telefon"
                        type="text"
                        fullWidth
                        value={newCustomer.phone}
                        onChange={(e) => setNewCustomer({ ...newCustomer, phone: e.target.value })}
                        required
                    />
                    <TextField
                        margin="dense"
                        label="Adress"
                        type="text"
                        fullWidth
                        value={newCustomer.address}
                        onChange={(e) => setNewCustomer({ ...newCustomer, address: e.target.value })}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog}>Avbryt</Button>
                    <Button
                        onClick={handleCreateCustomer}
                        variant="contained"
                        color="primary"
                        disabled={!isFormValid()}
                    >
                        Lägg till
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default EndCustomerPage;
