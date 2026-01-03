import { useState, useEffect } from 'react';
import { Container, Paper, Typography, Box, Avatar, Chip, Divider, List, ListItem, ListItemText } from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import BadgeIcon from '@mui/icons-material/Badge';
import BusinessIcon from '@mui/icons-material/Business';

const ProfilePage = () => {
    const [userInfo, setUserInfo] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchUserInfo();
    }, []);

    const fetchUserInfo = async () => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch('/api/users/me', {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                setUserInfo(data);
            } else {
                console.error("Misslyckades med att hämta användarinformation");
            }
        } catch (error) {
            console.error("Fel vid hämtning av användarinformation:", error);
        } finally {
            setLoading(false);
        }
    };

    const getRoleDisplayName = (roleName) => {
        const roleMap = {
            'SUPER_ADMIN': 'Systemadministratör',
            'CUSTOMER_ADMIN': 'Företagsadministratör',
            'ADMIN': 'Administratör',
            'WORKPLACE_ADMIN': 'Verkstadsansvarig',
            'MECHANIC': 'Mekaniker',
            'OFFICE': 'Kontorspersonal',
            'ACCOUNTANT': 'Redovisning'
        };
        return roleMap[roleName] || roleName;
    };

    const getRoleColor = (roleName) => {
        const colorMap = {
            'SUPER_ADMIN': 'error',
            'CUSTOMER_ADMIN': 'primary',
            'ADMIN': 'primary',
            'WORKPLACE_ADMIN': 'secondary',
            'MECHANIC': 'success',
            'OFFICE': 'info',
            'ACCOUNTANT': 'warning'
        };
        return colorMap[roleName] || 'default';
    };

    if (loading) {
        return (
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Typography>Laddar...</Typography>
            </Container>
        );
    }

    if (!userInfo) {
        return (
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Typography>Kunde inte hämta användarinformation</Typography>
            </Container>
        );
    }

    return (
        <Container maxWidth="md" sx={{ mt: 4 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                {/* Header with avatar */}
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
                    <Avatar sx={{ width: 80, height: 80, bgcolor: 'primary.main', mr: 3 }}>
                        <PersonIcon sx={{ fontSize: 50 }} />
                    </Avatar>
                    <Box>
                        <Typography variant="h4" gutterBottom>
                            Min profil
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            Hantera din kontoinformation
                        </Typography>
                    </Box>
                </Box>

                <Divider sx={{ mb: 3 }} />

                {/* User Information */}
                <List>
                    <ListItem>
                        <ListItemText
                            primary={
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <BadgeIcon color="action" />
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Användarnamn
                                    </Typography>
                                </Box>
                            }
                            secondary={
                                <Typography variant="h6" sx={{ mt: 1 }}>
                                    {userInfo.username}
                                </Typography>
                            }
                        />
                    </ListItem>

                    <Divider component="li" />

                    <ListItem>
                        <ListItemText
                            primary={
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <BusinessIcon color="action" />
                                    <Typography variant="subtitle2" color="text.secondary">
                                        Företag
                                    </Typography>
                                </Box>
                            }
                            secondary={
                                <Typography variant="h6" sx={{ mt: 1 }}>
                                    {userInfo.companyName || 'Okänt'}
                                </Typography>
                            }
                        />
                    </ListItem>

                    <Divider component="li" />

                    <ListItem>
                        <ListItemText
                            primary={
                                <Typography variant="subtitle2" color="text.secondary">
                                    Roller
                                </Typography>
                            }
                            secondary={
                                <Box sx={{ mt: 1, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                                    {userInfo.roles && userInfo.roles.length > 0 ? (
                                        userInfo.roles.map((role, index) => (
                                            <Chip
                                                key={index}
                                                label={getRoleDisplayName(role.name)}
                                                color={getRoleColor(role.name)}
                                                size="medium"
                                            />
                                        ))
                                    ) : (
                                        <Typography variant="body2" color="text.secondary">
                                            Inga roller tilldelade
                                        </Typography>
                                    )}
                                </Box>
                            }
                        />
                    </ListItem>
                </List>
            </Paper>
        </Container>
    );
};

export default ProfilePage;
