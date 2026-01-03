import { useEffect, useState } from "react";
import { Container, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Select, MenuItem, Button } from "@mui/material";

const UserManagementPage = () => {
    const [users, setUsers] = useState([]);
    const [userRoles, setUserRoles] = useState({});

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        const response = await fetch("/api/users"); // 🔹 Hämta alla användare inom admins Customer
        const data = await response.json();
        setUsers(data);

        // 🔹 Spara roller i state
        const rolesMap = {};
        data.forEach(user => {
            rolesMap[user.id] = user.roles || [];
        });
        setUserRoles(rolesMap);
    };

    const handleRoleChange = (userId, newRoles) => {
        setUserRoles(prev => ({ ...prev, [userId]: newRoles }));
    };

    const handleSaveRoles = async (userId) => {
        await fetch(`/api/users/${userId}/roles`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
            },
            body: JSON.stringify({ roles: userRoles[userId] }),
        });

        alert("Roller uppdaterade!");
    };


    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ padding: 3 }}>
                <Typography variant="h6" gutterBottom>Användarhantering</Typography>
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Namn</TableCell>
                                <TableCell>Roller</TableCell>
                                <TableCell>Åtgärder</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users.map(user => (
                                <TableRow key={user.id}>
                                    <TableCell>{user.username}</TableCell>
                                    <TableCell>
                                        <Select
                                            multiple
                                            value={userRoles[user.id] || []}
                                            onChange={(e) => handleRoleChange(user.id, e.target.value)}
                                            fullWidth
                                        >
                                            <MenuItem value="ADMIN">Admin</MenuItem>
                                            <MenuItem value="MECHANIC">Mekaniker</MenuItem>
                                            <MenuItem value="CUSTOMER">Kund</MenuItem>
                                        </Select>
                                    </TableCell>
                                    <TableCell>
                                        <Button onClick={() => handleSaveRoles(user.id)} variant="contained" color="primary">
                                            Spara
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Container>
    );
};

export default UserManagementPage;
