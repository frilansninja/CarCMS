import { Outlet } from "react-router-dom";
import { Box } from "@mui/material";
import Navigation from "../components/Navigation";

const AppLayout = () => {
    return (
        <>
            <Navigation />

            {/* Yttercontainer: alltid fullbredd */}
            <Box
                sx={{
                    width: "100vw",
                    minHeight: "100vh",
                    pt: 8,          // plats för AppBar
                    px: 3,          // horisontell padding
                    boxSizing: "border-box",
                }}
            >
                {/* Innercontainer: centreras med auto-margins */}
                <Box
                    sx={{
                        width: "100%",
                        maxWidth: 1400,
                        mx: "auto",
                    }}
                >
                    <Outlet />
                </Box>
            </Box>
        </>
    );
};

export default AppLayout;
