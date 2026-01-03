import React, { useState } from "react";
import {addEndCustomer} from "../api";

const AddEndCustomerForm = ({ customerId, onEndCustomerAdded }) => {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        await addEndCustomer(customerId, { name, email, phone });
        setName("");
        setEmail("");
        setPhone("");
        onEndCustomerAdded();
    };

    return (
        <form onSubmit={handleSubmit} className="mt-4">
            <input type="text" placeholder="Namn" value={name} onChange={(e) => setName(e.target.value)} required />
            <input type="email" placeholder="E-post" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <input type="text" placeholder="Telefon" value={phone} onChange={(e) => setPhone(e.target.value)} required />
            <button type="submit">Lägg till slutkund</button>
        </form>
    );
};

export default AddEndCustomerForm;
