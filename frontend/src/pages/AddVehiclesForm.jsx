import React, { useState } from "react";
import { addVehicle } from "../components";

const AddVehicleForm = ({ companyId, onVehicleAdded }) => {
    const [brand, setBrand] = useState("");
    const [model, setModel] = useState("");
    const [year, setYear] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        await addVehicle(companyId, { brand, model, year });
        setBrand("");
        setModel("");
        setYear("");
        onVehicleAdded();
    };

    return (
        <form onSubmit={handleSubmit} className="mt-4">
            <input type="text" placeholder="Märke" value={brand} onChange={(e) => setBrand(e.target.value)} required />
            <input type="text" placeholder="Modell" value={model} onChange={(e) => setModel(e.target.value)} required />
            <input type="number" placeholder="Årsmodell" value={year} onChange={(e) => setYear(e.target.value)} required />
            <button type="submit">Lägg till fordon</button>
        </form>
    );
};

export default AddVehicleForm;
