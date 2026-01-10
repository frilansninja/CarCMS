import { useNavigate } from "react-router-dom";
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Button,
    IconButton,
    Tooltip,
} from '@mui/material';
import { Settings, Edit, Trash2, Users } from 'react-feather';

const CompanyTable = ({ companies, onShowUsers, onDelete }) => {
    const navigate = useNavigate();

    return (
        <TableContainer component={Paper} elevation={2}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Namn</TableCell>
                        <TableCell>Org. Nummer</TableCell>
                        <TableCell>Telefon</TableCell>
                        <TableCell>E-post</TableCell>
                        <TableCell>Momsnummer</TableCell>
                        <TableCell align="right">Åtgärder</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {companies.map((company) => (
                        <TableRow key={company.id} hover>
                            <TableCell>{company.name}</TableCell>
                            <TableCell>{company.orgNumber}</TableCell>
                            <TableCell>{company.phone}</TableCell>
                            <TableCell>{company.email}</TableCell>
                            <TableCell>{company.vatNumber || '-'}</TableCell>
                            <TableCell align="right">
                                <Tooltip title="Inställningar">
                                    <IconButton
                                        color="success"
                                        size="small"
                                        onClick={() => navigate(`/admin/companies/${company.id}/settings`)}
                                    >
                                        <Settings size={18} />
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Redigera">
                                    <IconButton
                                        color="warning"
                                        size="small"
                                        onClick={() => navigate(`/admin/companies/${company.id}`)}
                                    >
                                        <Edit size={18} />
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Ta bort">
                                    <IconButton
                                        color="error"
                                        size="small"
                                        onClick={() => onDelete(company.id)}
                                    >
                                        <Trash2 size={18} />
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Visa användare">
                                    <IconButton
                                        color="primary"
                                        size="small"
                                        onClick={() => onShowUsers(company.id)}
                                    >
                                        <Users size={18} />
                                    </IconButton>
                                </Tooltip>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};

export default CompanyTable;
