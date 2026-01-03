import { useEffect, useState } from "react";
import {
    Container,
    Paper,
    Typography,
    TextField,
    Select,
    MenuItem,
    Button,
    Box,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TablePagination,
    Chip,
    FormControl,
    InputLabel,
    Grid,
    IconButton,
    Divider,
    FormControlLabel,
    Checkbox,
    CircularProgress
} from "@mui/material";
import DirectionsCarIcon from "@mui/icons-material/DirectionsCar";
import SearchIcon from "@mui/icons-material/Search";
import FilterListIcon from "@mui/icons-material/FilterList";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import BuildIcon from "@mui/icons-material/Build";
import TravelExploreIcon from "@mui/icons-material/TravelExplore";
import { useNavigate } from "react-router-dom";

const Vehicles = () => {
    const navigate = useNavigate();
    const [vehicles, setVehicles] = useState([]);
    const [filteredVehicles, setFilteredVehicles] = useState([]);
    const [companies, setCompanies] = useState([]);
    const [workplaces, setWorkplaces] = useState([]);
    const [loading, setLoading] = useState(true);

    // Filter states
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedBrand, setSelectedBrand] = useState("");
    const [selectedYear, setSelectedYear] = useState("");
    const [selectedWorkplace, setSelectedWorkplace] = useState("");
    const [selectedCompany, setSelectedCompany] = useState("");
    const [showOnlyWithActiveOrders, setShowOnlyWithActiveOrders] = useState(false);

    // Pagination
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);

    // Vehicle lookup state (for car.info link)
    const [lookupRegNumber, setLookupRegNumber] = useState("");

    // User info
    const userRoles = JSON.parse(localStorage.getItem("userRoles") || "[]");
    const isSuperAdmin = userRoles.includes("SUPER_ADMIN");
    const companyId = localStorage.getItem("companyId");

    useEffect(() => {
        if (isSuperAdmin) {
            loadCompanies();
        }
        loadVehicles();
    }, []);

    useEffect(() => {
        if (selectedCompany && isSuperAdmin) {
            loadWorkplaces(selectedCompany);
        } else if (!isSuperAdmin) {
            loadWorkplaces(companyId);
        }
    }, [selectedCompany]);

    useEffect(() => {
        applyFilters();
    }, [vehicles, searchTerm, selectedBrand, selectedYear, selectedWorkplace, selectedCompany, showOnlyWithActiveOrders]);

    const loadCompanies = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch("/api/companies", {
                headers: { Authorization: `Bearer ${token}` },
            });
            const data = await response.json();
            setCompanies(data);
        } catch (error) {
            console.error("Error loading companies:", error);
        }
    };

    const loadWorkplaces = async (compId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/workplaces/${compId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const data = await response.json();
            setWorkplaces(data);
        } catch (error) {
            console.error("Error loading workplaces:", error);
            setWorkplaces([]);
        }
    };

    const loadVehicles = async () => {
        setLoading(true);
        try {
            const token = localStorage.getItem("accessToken");
            const targetCompanyId = isSuperAdmin && selectedCompany ? selectedCompany : companyId;
            const response = await fetch(`/api/vehicles/company/${targetCompanyId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const data = await response.json();
            setVehicles(data);
        } catch (error) {
            console.error("Error loading vehicles:", error);
            setVehicles([]);
        } finally {
            setLoading(false);
        }
    };

    const applyFilters = () => {
        let filtered = [...vehicles];

        // Search by registration number
        if (searchTerm) {
            filtered = filtered.filter(v =>
                v.registrationNumber?.toLowerCase().includes(searchTerm.toLowerCase())
            );
        }

        // Filter by brand
        if (selectedBrand) {
            filtered = filtered.filter(v => v.brand === selectedBrand);
        }

        // Filter by year
        if (selectedYear) {
            filtered = filtered.filter(v => v.year === parseInt(selectedYear));
        }

        // Filter by workplace
        if (selectedWorkplace) {
            filtered = filtered.filter(v => v.workplaceId === parseInt(selectedWorkplace));
        }

        // Filter by company (SUPER_ADMIN only)
        if (selectedCompany && isSuperAdmin) {
            filtered = filtered.filter(v => v.companyId === parseInt(selectedCompany));
        }

        // Filter by active work orders
        if (showOnlyWithActiveOrders) {
            filtered = filtered.filter(v => v.hasActiveWorkOrders);
        }

        setFilteredVehicles(filtered);
        setPage(0); // Reset to first page when filters change
    };

    const handleResetFilters = () => {
        setSearchTerm("");
        setSelectedBrand("");
        setSelectedYear("");
        setSelectedWorkplace("");
        setSelectedCompany("");
        setShowOnlyWithActiveOrders(false);
        setPage(0);
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Är du säker på att du vill ta bort detta fordon?")) return;

        try {
            const token = localStorage.getItem("accessToken");
            await fetch(`/api/vehicles/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            });
            loadVehicles();
        } catch (error) {
            console.error("Error deleting vehicle:", error);
            alert("Fel vid borttagning av fordon");
        }
    };

    // Get unique brands and years for filter dropdowns
    const uniqueBrands = [...new Set(vehicles.map(v => v.brand))].sort();
    const uniqueYears = [...new Set(vehicles.map(v => v.year))].sort((a, b) => b - a);

    const paginatedVehicles = filteredVehicles.slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage
    );

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            {/* Header */}
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Box display="flex" alignItems="center" gap={1}>
                    <DirectionsCarIcon fontSize="large" color="primary" />
                    <Typography variant="h4">Fordonshantering</Typography>
                </Box>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => navigate("/vehicles/create")}
                >
                    Lägg till fordon
                </Button>
            </Box>

            {/* Vehicle Lookup Section */}
            <Paper elevation={3} sx={{ p: 3, mb: 3, background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)", color: "white" }}>
                <Box display="flex" alignItems="center" gap={1} mb={2}>
                    <TravelExploreIcon />
                    <Typography variant="h6">Sök fordon via registreringsnummer</Typography>
                </Box>
                <Typography variant="body2" sx={{ mb: 2, opacity: 0.9 }}>
                    Slå upp fordonsinformation från car.info i en ny flik
                </Typography>
                <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} sm={8} md={6}>
                        <TextField
                            fullWidth
                            placeholder="ABC123"
                            value={lookupRegNumber}
                            onChange={(e) => setLookupRegNumber(e.target.value.toUpperCase())}
                            onKeyPress={(e) => {
                                if (e.key === "Enter" && lookupRegNumber.trim()) {
                                    window.open(`https://www.car.info/sv-se/license-plate/S/${lookupRegNumber.trim()}`, '_blank');
                                }
                            }}
                            sx={{
                                backgroundColor: "rgba(255, 255, 255, 0.9)",
                                borderRadius: 1,
                                "& .MuiInputBase-input": {
                                    color: "black",
                                },
                            }}
                            InputProps={{
                                startAdornment: <SearchIcon sx={{ mr: 1, color: "text.secondary" }} />,
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} sm={4} md={3}>
                        <Button
                            fullWidth
                            variant="contained"
                            component="a"
                            href={lookupRegNumber.trim() ? `https://www.car.info/sv-se/license-plate/S/${lookupRegNumber.trim()}` : "#"}
                            target={lookupRegNumber.trim() ? "_blank" : undefined}
                            rel="noopener noreferrer"
                            disabled={!lookupRegNumber.trim()}
                            sx={{
                                backgroundColor: "white",
                                color: "primary.main",
                                "&:hover": {
                                    backgroundColor: "rgba(255, 255, 255, 0.9)",
                                },
                                textDecoration: "none",
                            }}
                            startIcon={<TravelExploreIcon />}
                        >
                            Sök på car.info
                        </Button>
                    </Grid>
                </Grid>
            </Paper>

            {/* Statistics Cards - Only show when there are vehicles */}
            {vehicles.length > 0 && (
                <Grid container spacing={2} sx={{ mb: 3 }}>
                    <Grid item xs={12} sm={4}>
                        <Card>
                            <CardContent>
                                <Typography color="text.secondary" gutterBottom>
                                    Totalt antal fordon
                                </Typography>
                                <Typography variant="h4">{vehicles.length}</Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={12} sm={4}>
                        <Card>
                            <CardContent>
                                <Typography color="text.secondary" gutterBottom>
                                    Med pågående ordrar
                                </Typography>
                                <Typography variant="h4" color="warning.main">
                                    {vehicles.filter(v => v.hasActiveWorkOrders).length}
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={12} sm={4}>
                        <Card>
                            <CardContent>
                                <Typography color="text.secondary" gutterBottom>
                                    Efter filtrering
                                </Typography>
                                <Typography variant="h4" color="primary.main">
                                    {filteredVehicles.length}
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>
            )}

            {/* Filters - Only show when there are vehicles */}
            {vehicles.length > 0 && (
                <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
                    <Box display="flex" alignItems="center" gap={1} mb={2}>
                        <FilterListIcon />
                        <Typography variant="h6">Filter</Typography>
                    </Box>
                    <Divider sx={{ mb: 2 }} />

                    <Grid container spacing={2}>
                        {/* Search */}
                        <Grid item xs={12} sm={6} md={4}>
                            <TextField
                                fullWidth
                                label="Sök registreringsnummer"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                InputProps={{
                                    startAdornment: <SearchIcon sx={{ mr: 1, color: "text.secondary" }} />,
                                }}
                            />
                        </Grid>

                        {/* Brand filter */}
                        <Grid item xs={12} sm={6} md={4}>
                            <FormControl fullWidth>
                                <InputLabel>Bilmärke</InputLabel>
                                <Select
                                    value={selectedBrand}
                                    label="Bilmärke"
                                    onChange={(e) => setSelectedBrand(e.target.value)}
                                >
                                    <MenuItem value="">Alla märken</MenuItem>
                                    {uniqueBrands.map((brand) => (
                                        <MenuItem key={brand} value={brand}>
                                            {brand}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Year filter */}
                        <Grid item xs={12} sm={6} md={4}>
                            <FormControl fullWidth>
                                <InputLabel>Årsmodell</InputLabel>
                                <Select
                                    value={selectedYear}
                                    label="Årsmodell"
                                    onChange={(e) => setSelectedYear(e.target.value)}
                                >
                                    <MenuItem value="">Alla år</MenuItem>
                                    {uniqueYears.map((year) => (
                                        <MenuItem key={year} value={year}>
                                            {year}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Company filter (SUPER_ADMIN only) */}
                        {isSuperAdmin && (
                            <Grid item xs={12} sm={6} md={4}>
                                <FormControl fullWidth>
                                    <InputLabel>Företag</InputLabel>
                                    <Select
                                        value={selectedCompany}
                                        label="Företag"
                                        onChange={(e) => setSelectedCompany(e.target.value)}
                                    >
                                        <MenuItem value="">Alla företag</MenuItem>
                                        {companies.map((company) => (
                                            <MenuItem key={company.id} value={company.id}>
                                                {company.name}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                        )}

                        {/* Workplace filter */}
                        <Grid item xs={12} sm={6} md={4}>
                            <FormControl fullWidth>
                                <InputLabel>Arbetsplats</InputLabel>
                                <Select
                                    value={selectedWorkplace}
                                    label="Arbetsplats"
                                    onChange={(e) => setSelectedWorkplace(e.target.value)}
                                >
                                    <MenuItem value="">Alla arbetsplatser</MenuItem>
                                    {workplaces.map((workplace) => (
                                        <MenuItem key={workplace.id} value={workplace.id}>
                                            {workplace.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        {/* Active orders checkbox */}
                        <Grid item xs={12} sm={6} md={4}>
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        checked={showOnlyWithActiveOrders}
                                        onChange={(e) => setShowOnlyWithActiveOrders(e.target.checked)}
                                        icon={<BuildIcon />}
                                        checkedIcon={<BuildIcon />}
                                    />
                                }
                                label="Endast med pågående ordrar"
                            />
                        </Grid>
                    </Grid>

                    <Box mt={2}>
                        <Button variant="outlined" onClick={handleResetFilters}>
                            Återställ filter
                        </Button>
                    </Box>
                </Paper>
            )}

            {/* Table */}
            <Paper elevation={3}>
                {loading ? (
                    <Box display="flex" justifyContent="center" p={4}>
                        <CircularProgress />
                    </Box>
                ) : vehicles.length === 0 ? (
                    <Box p={6} textAlign="center">
                        <DirectionsCarIcon sx={{ fontSize: 60, color: "text.secondary", mb: 2 }} />
                        <Typography variant="h6" color="text.secondary">
                            Inga fordon registrerade
                        </Typography>
                        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                            Klicka på "Lägg till fordon" för att registrera ditt första fordon.
                        </Typography>
                    </Box>
                ) : filteredVehicles.length === 0 ? (
                    <Box p={4} textAlign="center">
                        <DirectionsCarIcon sx={{ fontSize: 60, color: "text.secondary", mb: 2 }} />
                        <Typography variant="h6" color="text.secondary">
                            Inga fordon hittades
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Försök ändra dina filterinställningar
                        </Typography>
                    </Box>
                ) : (
                    <>
                        <TableContainer>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Registreringsnummer</TableCell>
                                        <TableCell>Märke</TableCell>
                                        <TableCell>Modell</TableCell>
                                        <TableCell>År</TableCell>
                                        {isSuperAdmin && <TableCell>Företag</TableCell>}
                                        <TableCell>Arbetsplats</TableCell>
                                        <TableCell>Ägare</TableCell>
                                        <TableCell>Status</TableCell>
                                        <TableCell align="right">Åtgärder</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {paginatedVehicles.map((vehicle) => (
                                        <TableRow
                                            key={vehicle.id}
                                            hover
                                            sx={{ cursor: "pointer" }}
                                            onClick={() => navigate(`/vehicles/${vehicle.id}`)}
                                        >
                                            <TableCell>
                                                <Box display="flex" alignItems="center" gap={1}>
                                                    <DirectionsCarIcon fontSize="small" color="action" />
                                                    <strong>{vehicle.registrationNumber}</strong>
                                                </Box>
                                            </TableCell>
                                            <TableCell>{vehicle.brand}</TableCell>
                                            <TableCell>{vehicle.modelName}</TableCell>
                                            <TableCell>{vehicle.year}</TableCell>
                                            {isSuperAdmin && <TableCell>{vehicle.companyName}</TableCell>}
                                            <TableCell>{vehicle.workplaceName || "-"}</TableCell>
                                            <TableCell>{vehicle.endCustomerName}</TableCell>
                                            <TableCell>
                                                {vehicle.hasActiveWorkOrders ? (
                                                    <Chip
                                                        icon={<BuildIcon />}
                                                        label="Pågående arbete"
                                                        color="warning"
                                                        size="small"
                                                    />
                                                ) : (
                                                    <Chip label="Inget arbete" color="success" size="small" />
                                                )}
                                            </TableCell>
                                            <TableCell align="right" onClick={(e) => e.stopPropagation()}>
                                                <IconButton
                                                    size="small"
                                                    color="primary"
                                                    onClick={() => navigate(`/vehicles/${vehicle.id}/edit`)}
                                                >
                                                    <EditIcon />
                                                </IconButton>
                                                <IconButton
                                                    size="small"
                                                    color="error"
                                                    onClick={() => handleDelete(vehicle.id)}
                                                >
                                                    <DeleteIcon />
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                        <TablePagination
                            component="div"
                            count={filteredVehicles.length}
                            page={page}
                            onPageChange={(e, newPage) => setPage(newPage)}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={(e) => {
                                setRowsPerPage(parseInt(e.target.value, 10));
                                setPage(0);
                            }}
                            labelRowsPerPage="Rader per sida"
                        />
                    </>
                )}
            </Paper>
        </Container>
    );
};

export default Vehicles;
