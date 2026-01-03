import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Customers from "./pages/Customers";  //
import ProtectedRoute from "./components/ProtectedRoute";

const AppRoutes = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/dashboard" element={
                    <ProtectedRoute>
                        <Dashboard />
                    </ProtectedRoute>
                } />
                <Route path="/customers" element={
                    <ProtectedRoute>
                        <Customers />
                    </ProtectedRoute>
                } />
            </Routes>
        </Router>
    );
};

export default AppRoutes;
