import React, { useState } from "react";
import {addWorkplace} from "../api";

const AddWorkplaceForm = ({ onWorkplaceAdded }) => {
    const [name, setName] = useState("");
    const [location, setLocation] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        await addWorkplace({ name, location });
        setName("");
        setLocation("");
        onWorkplaceAdded(); // 🔹 Uppdaterar listan efter att en arbetsplats har lagts till
    };

    return (
        <form onSubmit={handleSubmit} className="mt-4">
            <input type="text" placeholder="Arbetsplatsnamn" value={name} onChange={(e) => setName(e.target.value)} required />
            <input type="text" placeholder="Plats" value={location} onChange={(e) => setLocation(e.target.value)} required />
            <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded">
                Lägg till arbetsplats
            </button>
        </form>
    );
};

export default AddWorkplaceForm;
