import React, { useState } from "react";
import {addWorkOrder} from "../api";

const AddWorkOrderForm = ({ customerId, vehicleId, onWorkOrderAdded }) => {
    const [description, setDescription] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        await addWorkOrder(customerId, vehicleId, { description });
        setDescription("");
        onWorkOrderAdded();
    };

    return (
        <form onSubmit={handleSubmit} className="mt-4">
            <input type="text" placeholder="Beskrivning av arbetet" value={description} onChange={(e) => setDescription(e.target.value)} required />
            <button type="submit">Skapa arbetsorder</button>
        </form>
    );
};

export default AddWorkOrderForm;
