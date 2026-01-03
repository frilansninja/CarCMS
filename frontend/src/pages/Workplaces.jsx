import React, { useState, useEffect } from "react";
import { getWorkplaces, addWorkplace, deleteWorkplace, updateWorkplace } from "../api/workplaces.js";

const Workplaces = () => {
    const companyId = localStorage.getItem("companyId");
    const [workplaces, setWorkplaces] = useState([]);
    const [newWorkplaceName, setNewWorkplaceName] = useState("");
    const [editingWorkplace, setEditingWorkplace] = useState(null);
    const [editName, setEditName] = useState("");

    useEffect(() => {
        if (companyId) {
            fetchWorkplaces();
        } else {
            console.error("No companyId found in localStorage.");
        }
    }, [companyId]);

    const fetchWorkplaces = async () => {
        try {
            const data = await getWorkplaces(companyId);
            setWorkplaces(data);
        } catch (error) {
            console.error("Error fetching workplaces:", error);
        }
    };

    const handleAddWorkplace = async () => {
        if (!newWorkplaceName) return;
        const newWorkplace = { name: newWorkplaceName };
        await addWorkplace(companyId, newWorkplace);
        setNewWorkplaceName("");
        fetchWorkplaces();
    };

    const handleDeleteWorkplace = async (id) => {
        await deleteWorkplace(id);
        fetchWorkplaces();
    };

    const handleEditClick = (workplace) => {
        setEditingWorkplace(workplace);
        setEditName(workplace.name);
    };

    const handleUpdateWorkplace = async () => {
        if (!editName) return;
        await updateWorkplace(editingWorkplace.id, { name: editName });
        setEditingWorkplace(null);
        fetchWorkplaces();
    };

    return (
        <div>
            <h3>Workplaces</h3>
            <ul>
                {workplaces.map((workplace) => (
                    <li key={workplace.id}>
                        {editingWorkplace?.id === workplace.id ? (
                            <>
                                <input
                                    type="text"
                                    value={editName}
                                    onChange={(e) => setEditName(e.target.value)}
                                />
                                <button onClick={handleUpdateWorkplace}>Save</button>
                                <button onClick={() => setEditingWorkplace(null)}>Cancel</button>
                            </>
                        ) : (
                            <>
                                {workplace.name}
                                <button onClick={() => handleEditClick(workplace)}>Edit</button>
                                <button onClick={() => handleDeleteWorkplace(workplace.id)}>Delete</button>
                            </>
                        )}
                    </li>
                ))}
            </ul>
            <input
                type="text"
                value={newWorkplaceName}
                onChange={(e) => setNewWorkplaceName(e.target.value)}
                placeholder="New Workplace Name"
            />
            <button onClick={handleAddWorkplace}>Add Workplace</button>
        </div>
    );
};

export default Workplaces;