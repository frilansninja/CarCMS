import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiClient } from './apiConfig';
import CustomerTable from "../components/CustomerTable";
import AddCustomerForm from "../components/AddCustomerForm";

const Customers = () => {
    const [customers, setCustomers] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const accessToken = localStorage.getItem("accessToken") ;
        if (accessToken) {
            loadCustomers();
        } else {
            console.log('wont load customers, no token');
            navigate("/login");
        }
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("accessToken"); // Ta bort token
        navigate("/login"); // Skicka användaren till login-sidan
    };
    const loadCustomers = async () => {
        const data = await fetchCustomers();
        setCustomers(data);
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-bold">aKundhantering</h1>
                <button onClick={handleLogout} className="bg-red-500 text-white px-4 py-2">
                    Logga ut
                </button>
            </div>
            <AddCustomerForm onCustomerAdded={loadCustomers} />
            <CustomerTable customers={customers} onEdit={() => {}} onDelete={() => {}} onShowUsers={() => {}} />
        </div>
    );
};

export default Customers;
