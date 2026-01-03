import { useState } from "react";
import {
    AppBar,
    Toolbar,
    IconButton,
    Typography,
    Drawer,
    List,
    ListItem,
    ListItemText,
    Menu,
    MenuItem,
    Avatar,
    Button
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import HomeIcon from "@mui/icons-material/Home";
import { Link, useNavigate } from "react-router-dom";

const Navigation = () => {
    const navigate = useNavigate();
    const [menuOpen, setMenuOpen] = useState(false);
    const [anchorEl, setAnchorEl] = useState(null);

    const token = localStorage.getItem("accessToken");
    const roles = JSON.parse(localStorage.getItem("userRoles") || "[]");
    const isSuperAdmin = roles.includes("SUPER_ADMIN");
    const isAdmin = roles.includes("ADMIN") || roles.includes("SUPER_ADMIN");
    const isMechanic = roles.includes("MECHANIC");
    const isAccountant = roles.includes("ACCOUNTANT");

    const toggleMenu = (open) => () => {
        setMenuOpen(open);
    };

    const handleProfileMenuOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleProfileMenuClose = () => {
        setAnchorEl(null);
    };

    const handleProfileClick = () => {
        handleProfileMenuClose();
        navigate("/profile");
    };

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("customerId");
        localStorage.removeItem("userRoles");
        navigate("/login");
    };

    return (
        <>
            {/* 🔹 Fixerad toppmeny */}
            <AppBar position="fixed">
                <Toolbar>
                    <IconButton edge="start" color="inherit" aria-label="menu" onClick={toggleMenu(true)}>
                        <MenuIcon />
                    </IconButton>
                    <Typography
                        variant="h6"
                        sx={{
                            flexGrow: 1,
                            cursor: 'pointer',
                            '&:hover': {
                                opacity: 0.8
                            }
                        }}
                        onClick={() => navigate('/')}
                    >
                        Carmoury
                    </Typography>

                    {token ? (
                        <>
                            <IconButton color="inherit" onClick={handleProfileMenuOpen}>
                                <Avatar sx={{ width: 32, height: 32 }}>
                                    <AccountCircleIcon />
                                </Avatar>
                            </IconButton>

                            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleProfileMenuClose}>
                                <MenuItem onClick={handleProfileClick}>Profil</MenuItem>
                                <MenuItem onClick={handleLogout}>Logga ut</MenuItem>
                            </Menu>
                        </>
                    ) : (
                        <Button color="inherit" component={Link} to="/login">
                            Logga in
                        </Button>
                    )}
                </Toolbar>
            </AppBar>

            {/* 🔹 Sidomeny */}
            <Drawer anchor="left" open={menuOpen} onClose={toggleMenu(false)}>
                <List sx={{ width: 250 }}>
                    <ListItem component={Link} to="/" onClick={toggleMenu(false)}>
                        <ListItemText primary="Dashboard" />
                    </ListItem>

                    {token && (
                        <>
                            {isSuperAdmin && (
                                <>
                                    <ListItem component={Link} to="/admin/companies" onClick={toggleMenu(false)}>
                                        <ListItemText primary="Företag" />
                                    </ListItem>
                                    <ListItem component={Link} to="/users" onClick={toggleMenu(false)}>
                                        <ListItemText primary="Användare" />
                                    </ListItem>
                                </>
                            )}
                            {isAdmin && !isSuperAdmin && (
                                <ListItem component={Link} to="/users" onClick={toggleMenu(false)}>
                                    <ListItemText primary="Användare" />
                                </ListItem>
                            )}
                            <ListItem component={Link} to="/endcustomers" onClick={toggleMenu(false)}>
                                <ListItemText primary="Kunder" />
                            </ListItem>
                            <ListItem component={Link} to="/workplaces" onClick={toggleMenu(false)}>
                                <ListItemText primary="Arbetsplatser" />
                            </ListItem>
                            <ListItem component={Link} to="/vehicles" onClick={toggleMenu(false)}>
                                <ListItemText primary="Fordon" />
                            </ListItem>
                            <ListItem component={Link} to="/workorders" onClick={toggleMenu(false)}>
                                <ListItemText primary="Arbetsorder" />
                            </ListItem>
                            <ListItem component={Link} to="/calendar" onClick={toggleMenu(false)}>
                                <ListItemText primary="Kalender" />
                            </ListItem>
                            {isAdmin && (
                                <ListItem component={Link} to="/admin/templates" onClick={toggleMenu(false)}>
                                    <ListItemText primary="Arbetsuppgiftsmallar" />
                                </ListItem>
                            )}
                        </>
                    )}
                </List>
            </Drawer>
        </>
    );
};

export default Navigation;
