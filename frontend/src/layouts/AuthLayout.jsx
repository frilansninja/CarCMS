import { Outlet } from "react-router-dom";
import { Box } from "@mui/material";

const AuthLayout = () => {
    return (
        <Box
            sx={{
                width: "100vw",
                minHeight: "100vh",
                display: "grid",
                placeItems: "center",
                p: 3,
            }}
        >
            <Outlet />
        </Box>
    );
};

export default AuthLayout;
