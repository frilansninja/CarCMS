import React from "react";
import { Link, useNavigate } from "react-router-dom";

const Navbar = () => {
    const navigate = useNavigate();
    const token = localStorage.getItem("accessToken"); // 🔹 Kolla om användaren är inloggad

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("customerId");
        navigate("/login");
    };

    return (
        <nav className="bg-gray-800 text-white p-4 flex justify-between">
            <div>
                <Link to="/" className="mr-4">Hem</Link> {/* 🏠 Hem finns alltid */}
                {token ? ( // 🔹 Om token finns → visa full meny
                    <>
                        <Link to="/workplaces" className="mr-4">Arbetsplatser</Link>
                        <Link to="/endcustomers" className="mr-4">Kunder</Link>
                        <Link to="/vehicles" className="mr-4">Fordon</Link>
                        <Link to="/workorders" className="mr-4">Arbetsordrar</Link>
                        <Link to="/orders" className="mr-4">Beställningar</Link>
                        <Link to="/invoices" className="mr-4">Fakturor</Link>
                    </>
                ) : null}
            </div>
            <div>
                {token ? (
                    <button onClick={handleLogout} className="bg-red-500 px-4 py-2 rounded">
                        Logga ut
                    </button>
                ) : (
                    <Link to="/login" className="bg-blue-500 px-4 py-2 rounded">
                        Logga in
                    </Link>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
