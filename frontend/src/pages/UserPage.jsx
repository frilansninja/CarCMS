import { useEffect, useState } from "react";
import { Container, Paper, Typography, TextField, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TablePagination, MenuItem, Select, CircularProgress, Box, Button, TableSortLabel } from "@mui/material";
import { Link, useNavigate, useParams } from "react-router-dom";
import { fetchCompanies } from "../api/companies";

const UserPage = () => {
    const navigate = useNavigate();
    const { companyId: urlCompanyId } = useParams();
    const [users, setUsers] = useState([]);
    const [companies, setCompanies] = useState([]);
    const [workplaces, setWorkplaces] = useState([]);
    const [searchTerm, setSearchTerm] = useState(localStorage.getItem("searchTerm") || "");
    const [roleFilter, setRoleFilter] = useState(localStorage.getItem("roleFilter") || "");
    const [companyFilter, setCompanyFilter] = useState(urlCompanyId || localStorage.getItem("companyFilter") || "");
    const [workplaceFilter, setWorkplaceFilter] = useState(localStorage.getItem("workplaceFilter") || "");
    const [page, setPage] = useState(parseInt(localStorage.getItem("page"), 10) || 0);
    const [rowsPerPage, setRowsPerPage] = useState(parseInt(localStorage.getItem("rowsPerPage"), 10) || 10);
    const [totalUsers, setTotalUsers] = useState(0);
    const [loading, setLoading] = useState(false);
    const [sortOrder, setSortOrder] = useState("asc"); // 🔹 Hantera sortering av namn
    const userRoles = JSON.parse(localStorage.getItem("userRoles") || "[]");
    const isSuperAdmin = userRoles.includes("SUPER_ADMIN");

    // Om vi kommer från en företagssida, sätt filtret
    useEffect(() => {
        if (urlCompanyId) {
            setCompanyFilter(urlCompanyId);
        }
    }, [urlCompanyId]);

    useEffect(() => {
        if (isSuperAdmin) {
            loadCompanies();
        }
        fetchUsers();
    }, [page, rowsPerPage]); // 🔹 Endast sidförändringar laddar data

    // Ladda workplaces när företag ändras
    useEffect(() => {
        if (companyFilter) {
            loadWorkplaces(companyFilter);
        } else {
            setWorkplaces([]);
            setWorkplaceFilter("");
        }
    }, [companyFilter]);

    const loadCompanies = async () => {
        try {
            const companiesData = await fetchCompanies();
            setCompanies(companiesData);
        } catch (error) {
            console.error("Fel vid hämtning av företag:", error);
        }
    };

    const loadWorkplaces = async (companyId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(`/api/workplaces/${companyId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const data = await response.json();
            setWorkplaces(data);
        } catch (error) {
            console.error("Fel vid hämtning av arbetsplatser:", error);
            setWorkplaces([]);
        }
    };

    const fetchUsers = async () => {
        setLoading(true);
        const token = localStorage.getItem("accessToken");

        // Använd valt företag om det finns, annars använd användarens eget företag
        const selectedCompanyId = companyFilter || localStorage.getItem("companyId");

        console.log("Fetching users for company:", selectedCompanyId, "workplace:", workplaceFilter);
        try {
            let url = `/api/companies/${selectedCompanyId}/users?search=${searchTerm}&role=${roleFilter}&page=${page}&size=${rowsPerPage}&sort=username,${sortOrder}`;
            if (workplaceFilter) {
                url += `&workplaceId=${workplaceFilter}`;
            }

            const response = await fetch(url, {
                headers: { Authorization: `Bearer ${token}` },
            });

            const text = await response.text();
            console.log("API Response:", text);

            const data = JSON.parse(text);
            setUsers(data.content);
            setTotalUsers(data.totalElements);

            localStorage.setItem("searchTerm", searchTerm);
            localStorage.setItem("roleFilter", roleFilter);
            localStorage.setItem("companyFilter", companyFilter);
            localStorage.setItem("workplaceFilter", workplaceFilter);
            localStorage.setItem("page", page);
            localStorage.setItem("rowsPerPage", rowsPerPage);
        } catch (error) {
            console.error("Fel vid hämtning av användare:", error);
            setUsers([]);
        } finally {
            setLoading(false);
        }
    };


    const handleSearch = () => {
        setPage(0);
        fetchUsers();
    };

    const handleResetFilters = () => {
        setSearchTerm("");
        setRoleFilter("");
        setCompanyFilter("");
        setWorkplaceFilter("");
        setPage(0);
        setRowsPerPage(10);
        localStorage.removeItem("searchTerm");
        localStorage.removeItem("roleFilter");
        localStorage.removeItem("companyFilter");
        localStorage.removeItem("workplaceFilter");
        localStorage.removeItem("page");
        localStorage.removeItem("rowsPerPage");
        fetchUsers();
    };

    const handlePageChange = (event, newPage) => {
        setPage(newPage);
    };

    const handleRowsPerPageChange = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleSort = () => {
        setSortOrder(sortOrder === "asc" ? "desc" : "asc");
        fetchUsers();
    };

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h6" gutterBottom>Användarhantering</Typography>

                {/* Filtrering och sökning */}
                <TextField
                    label="Sök användare"
                    variant="outlined"
                    fullWidth
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    sx={{ mb: 2 }}
                />

                <Select
                    value={roleFilter}
                    onChange={(e) => setRoleFilter(e.target.value)}
                    displayEmpty
                    fullWidth
                    sx={{ mb: 2 }}
                >
                    <MenuItem value="">Alla roller</MenuItem>
                    <MenuItem value="SUPER_ADMIN">Super Admin</MenuItem>
                    <MenuItem value="CUSTOMER_ADMIN">Kundadministratör</MenuItem>
                    <MenuItem value="WORKPLACE_ADMIN">Verkstadsadministratör</MenuItem>
                    <MenuItem value="MECHANIC">Mekaniker</MenuItem>
                    <MenuItem value="OFFICE">Kontor</MenuItem>
                </Select>

                {isSuperAdmin && (
                    <>
                        <Select
                            value={companyFilter}
                            onChange={(e) => setCompanyFilter(e.target.value)}
                            displayEmpty
                            fullWidth
                            sx={{ mb: 2 }}
                        >
                            <MenuItem value="">Alla företag</MenuItem>
                            {companies.map((company) => (
                                <MenuItem key={company.id} value={company.id}>
                                    {company.name}
                                </MenuItem>
                            ))}
                        </Select>

                        {companyFilter && (
                            <Select
                                value={workplaceFilter}
                                onChange={(e) => setWorkplaceFilter(e.target.value)}
                                displayEmpty
                                fullWidth
                                sx={{ mb: 2 }}
                            >
                                <MenuItem value="">Alla arbetsplatser</MenuItem>
                                {workplaces.map((workplace) => (
                                    <MenuItem key={workplace.id} value={workplace.id}>
                                        {workplace.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        )}
                    </>
                )}

                {/* Sök- och återställningsknappar */}
                <Box display="flex" gap={2} sx={{ mb: 2 }}>
                    <Button variant="contained" color="primary" onClick={handleSearch}>
                        Sök
                    </Button>
                    <Button variant="outlined" color="secondary" onClick={handleResetFilters}>
                        Återställ filter
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/user-create")}
                    >
                       Skapa användare
                    </Button>

                </Box>

                        {/* Visar antal användare */}
                        <Typography variant="body2" sx={{ mb: 2 }}>
                            Visar {users?.length || 0} av {totalUsers || 0} användare
                        </Typography>

                        {/* Laddningsindikator vid sidbyte */}
                        {loading ? (
                            <Box display="flex" justifyContent="center" sx={{ mt: 3, mb: 3 }}>
                                <CircularProgress />
                            </Box>
                        ) : users.length === 0 ? (
                            <Typography align="center" sx={{ mt: 3, mb: 3 }}>
                                Inga användare hittades.
                            </Typography>
                        ) : (
                            <>
                                {/* Tabell */}
                                <TableContainer component={Paper}>
                                    <Table>
                                        <TableHead>
                                            <TableRow>
                                                <TableCell>
                                                    <TableSortLabel
                                                        active={true}
                                                        direction={sortOrder}
                                                        onClick={handleSort}
                                                    >
                                                        Namn
                                                    </TableSortLabel>
                                                </TableCell>
                                                {isSuperAdmin && <TableCell>Företag</TableCell>}
                                                {isSuperAdmin && <TableCell>Arbetsplats</TableCell>}
                                                <TableCell>Roller</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {users.map((user) => (
                                                <TableRow
                                                    key={user.id}
                                                    hover
                                                    style={{ cursor: "pointer" }}
                                                    onClick={() => navigate(`/users/${user.id}`)}
                                                >
                                                    <TableCell>
                                                        {user.username}
                                                    </TableCell>
                                                    {isSuperAdmin && (
                                                        <TableCell>
                                                            {user.company?.name || "Inget företag"}
                                                        </TableCell>
                                                    )}
                                                    {isSuperAdmin && (
                                                        <TableCell>
                                                            {user.workplace?.name || "-"}
                                                        </TableCell>
                                                    )}
                                                    <TableCell>
                                                        {user.roles?.map(role => role.name).join(", ") || "Ingen roll"}
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>

                        {/* Paginering */}
                        <TablePagination
                            component="div"
                            count={totalUsers}
                            page={page}
                            onPageChange={handlePageChange}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={handleRowsPerPageChange}
                        />
                    </>
                )}
            </Paper>
        </Container>
    );
};

export default UserPage;
