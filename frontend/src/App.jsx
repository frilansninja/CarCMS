import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from "react-router-dom";
import { useState, useEffect } from "react";
import ProtectedRoute from "./components/ProtectedRoute";
import Navigation from "./components/Navigation";
import Workplaces from "./pages/Workplaces";
import WorkOrders from "./pages/WorkOrders";
import Vehicles from "./pages/Vehicles.jsx";
import VehicleDetails from "./pages/VehicleDetails.jsx";
import Orders from "./pages/Orders";
import Invoices from "./pages/Invoices.jsx";
import LoginPage from "./pages/LoginPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import Dashboard from "./pages/Dashboard";
import UserPage from "./pages/UserPage";
import UserCreatePage from "./pages/UserCreatePage.jsx";
import UserDetailsPage from "./pages/UserDetailsPage";
import EndCustomerPage from "./pages/EndCustomerPage";
import EndCustomerDetails from "./pages/EndCustomerDetails";
import ProfilePage from "./pages/ProfilePage";
import WorkOrderDetails from "./pages/WorkOrderDetails";
import WorkTaskDetails from "./pages/WorkTaskDetails";
import ServiceIntervalsPage from "./pages/ServiceIntervalsPage.jsx";
import TemplateAdminPage from "./pages/TemplateAdminPage";
import OrderPartsPage from "./pages/OrderParts";
import CalendarPage from "./pages/CalendarPage";
import Companies from "./pages/Companies";
import CompanySettingsPage from "./pages/CompanySettingsPage";
import CompanyDetailsPage from "./pages/CompanyDetailsPage";
import AuthLayout from "./layouts/AuthLayout";
import AppLayout from "./layouts/AppLayout";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Box, CssBaseline, ThemeProvider } from "@mui/material";
import theme from "./theme";

// ProtectedLayout använder ProtectedRoute för att skydda alla underliggande routes
function ProtectedLayout() {
    return (
        <ProtectedRoute>
            <Outlet />
        </ProtectedRoute>
    );
}

function App() {
    const [roles, setRoles] = useState([]);
    const [isSuperAdmin, setIsSuperAdmin] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        const loadRoles = () => {
            const storedRoles = JSON.parse(localStorage.getItem("userRoles") || "[]");
            setRoles(storedRoles);
            setIsSuperAdmin(storedRoles.includes("SUPER_ADMIN"));
            setIsAdmin(
                storedRoles.includes("CUSTOMER_ADMIN") ||
                storedRoles.includes("SUPER_ADMIN")
            );
        };

        // Load roles on mount
        loadRoles();

        // Listen for storage changes (when user logs in/out)
        window.addEventListener("storage", loadRoles);

        // Custom event for same-tab updates
        window.addEventListener("rolesUpdated", loadRoles);

        return () => {
            window.removeEventListener("storage", loadRoles);
            window.removeEventListener("rolesUpdated", loadRoles);
        };
    }, []);

    return (
        <ThemeProvider theme={theme}>
            <DndProvider backend={HTML5Backend}>
                <Router>
                    <CssBaseline />
                    <Routes>
                        {/* Auth routes (centrerade, utan Navigation) */}
                        <Route element={<AuthLayout />}>
                            <Route path="/login" element={<LoginPage />} />
                            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
                            <Route path="/reset-password/:token" element={<ResetPasswordPage />} />
                        </Route>

                        {/* App routes (med Navigation + maxWidth wrapper) */}
                        <Route element={<AppLayout />}>
                            {/* Skyddade routes */}
                            <Route
                                element={
                                    <ProtectedRoute>
                                        <Dashboard />
                                    </ProtectedRoute>
                                }
                                path="/"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <Dashboard />
                                    </ProtectedRoute>
                                }
                                path="/dashboard"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <ProfilePage />
                                    </ProtectedRoute>
                                }
                                path="/profile"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <WorkTaskDetails />
                                    </ProtectedRoute>
                                }
                                path="/worktasks/:id"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <Workplaces />
                                    </ProtectedRoute>
                                }
                                path="/workplaces"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <EndCustomerPage />
                                    </ProtectedRoute>
                                }
                                path="/endcustomers"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <WorkOrderDetails />
                                    </ProtectedRoute>
                                }
                                path="/workorders/:id"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <EndCustomerDetails />
                                    </ProtectedRoute>
                                }
                                path="/endcustomers/:id"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <ServiceIntervalsPage />
                                    </ProtectedRoute>
                                }
                                path="/service-intervals"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <WorkOrders />
                                    </ProtectedRoute>
                                }
                                path="/workorders"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <Vehicles />
                                    </ProtectedRoute>
                                }
                                path="/vehicles"
                            />

                            {isAdmin && (
                                <Route
                                    path="/vehicles/:id"
                                    element={
                                        <ProtectedRoute>
                                            <VehicleDetails />
                                        </ProtectedRoute>
                                    }
                                />
                            )}

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <Orders />
                                    </ProtectedRoute>
                                }
                                path="/orders"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <OrderPartsPage />
                                    </ProtectedRoute>
                                }
                                path="/order-parts"
                            />

                            <Route
                                element={
                                    <ProtectedRoute>
                                        <Invoices />
                                    </ProtectedRoute>
                                }
                                path="/invoices"
                            />

                            {isSuperAdmin && (
                                <>
                                    <Route
                                        path="/admin/companies"
                                        element={
                                            <ProtectedRoute>
                                                <Companies />
                                            </ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path="/admin/companies/:companyId"
                                        element={
                                            <ProtectedRoute>
                                                <CompanyDetailsPage />
                                            </ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path="/admin/companies/:companyId/settings"
                                        element={
                                            <ProtectedRoute>
                                                <CompanySettingsPage />
                                            </ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path="/admin/companies/:companyId/users"
                                        element={
                                            <ProtectedRoute>
                                                <UserPage />
                                            </ProtectedRoute>
                                        }
                                    />
                                </>
                            )}

                            {isAdmin && (
                                <>
                                    <Route
                                        path="/users"
                                        element={
                                            <ProtectedRoute>
                                                <UserPage />
                                            </ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path="/users/:id"
                                        element={
                                            <ProtectedRoute>
                                                <UserDetailsPage />
                                            </ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path="/user-create"
                                        element={
                                            <ProtectedRoute>
                                                <UserCreatePage />
                                            </ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path="/admin/templates"
                                        element={
                                            <ProtectedRoute>
                                                <TemplateAdminPage />
                                            </ProtectedRoute>
                                        }
                                    />
                                    <Route
                                        path="/calendar"
                                        element={
                                            <ProtectedRoute>
                                                <CalendarPage />
                                            </ProtectedRoute>
                                        }
                                    />
                                </>
                            )}
                        </Route>

                        {/* fallback */}
                        <Route path="*" element={<Navigate to="/" />} />
                    </Routes>
            </Router>
        </DndProvider>
        </ThemeProvider>
    );
}

export default App;
