import { useState } from "react";
import { apiClient } from './apiConfig';

const AddCustomerForm = ({ onCustomerAdded }) => {
    const [newCustomer, setNewCustomer] = useState({
        name: "",
        orgNumber: "",
        phone: "",
        email: "",
        address: "",
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        await addCustomer(newCustomer);
        setNewCustomer({ name: "", orgNumber: "", phone: "", email: "", address: "" });
        onCustomerAdded();
    };

    return (
        <form className="mb-4 flex gap-2" onSubmit={handleSubmit}>
            <input className="border p-2" type="text" placeholder="Namn" value={newCustomer.name} onChange={(e) => setNewCustomer({ ...newCustomer, name: e.target.value })} required />
            <input className="border p-2" type="text" placeholder="Org. Nummer" value={newCustomer.orgNumber} onChange={(e) => setNewCustomer({ ...newCustomer, orgNumber: e.target.value })} required />
            <input className="border p-2" type="text" placeholder="Telefon" value={newCustomer.phone} onChange={(e) => setNewCustomer({ ...newCustomer, phone: e.target.value })} />
            <input className="border p-2" type="email" placeholder="E-post" value={newCustomer.email} onChange={(e) => setNewCustomer({ ...newCustomer, email: e.target.value })} />
            <input className="border p-2" type="text" placeholder="Adress" value={newCustomer.address} onChange={(e) => setNewCustomer({ ...newCustomer, address: e.target.value })} />
            <button type="submit" className="bg-blue-500 text-white px-4 py-2">Lägg till</button>
        </form>
    );
};

export default AddCustomerForm;
