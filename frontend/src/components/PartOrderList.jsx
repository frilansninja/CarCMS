import React, { useState, useEffect } from "react";

const PartOrderList = ({ workOrderId }) => {
    const [partOrders, setPartOrders] = useState([]);
    const [filterReceived, setFilterReceived] = useState("all"); // "all", "received", "not_received"
    const [filterSupplier, setFilterSupplier] = useState("");
    const [sortBy, setSortBy] = useState("orderDate"); // "orderDate", "expectedArrivalDate"
    const [sortOrder, setSortOrder] = useState("desc"); // "asc", "desc"

    useEffect(() => {
        fetch(`/api/part-orders/work-order/${workOrderId}`)
            .then(response => response.json())
            .then(data => setPartOrders(data))
            .catch(error => console.error("Error fetching part orders:", error));
    }, [workOrderId]);

    const markAsReceived = (partOrderId) => {
        fetch(`/api/part-orders/${partOrderId}/receive`, { method: "PUT" })
            .then(response => response.json())
            .then(updatedOrder => {
                setPartOrders(prevOrders =>
                    prevOrders.map(order =>
                        order.id === updatedOrder.id ? updatedOrder : order
                    )
                );
            })
            .catch(error => console.error("Error updating part order:", error));
    };

    // **Filtrera beställningar**
    let filteredOrders = partOrders.filter(order => {
        if (filterReceived === "received" && !order.received) return false;
        if (filterReceived === "not_received" && order.received) return false;
        if (filterSupplier && !order.supplier.toLowerCase().includes(filterSupplier.toLowerCase())) return false;
        return true;
    });

    // **Sortera beställningar**
    filteredOrders.sort((a, b) => {
        let valueA = a[sortBy];
        let valueB = b[sortBy];

        if (sortBy === "orderDate" || sortBy === "expectedArrivalDate") {
            valueA = new Date(valueA);
            valueB = new Date(valueB);
        }

        return sortOrder === "asc" ? valueA - valueB : valueB - valueA;
    });

    return (
        <div>
            <h3>Delbeställningar</h3>

            {/* Filtrering */}
            <label>Filtrera på status:</label>
            <select value={filterReceived} onChange={e => setFilterReceived(e.target.value)}>
                <option value="all">Alla</option>
                <option value="received">Endast mottagna</option>
                <option value="not_received">Endast ej mottagna</option>
            </select>

            <label>Filtrera på leverantör:</label>
            <input
                type="text"
                value={filterSupplier}
                onChange={e => setFilterSupplier(e.target.value)}
                placeholder="Skriv leverantörsnamn"
            />

            {/* Sortering */}
            <label>Sortera efter:</label>
            <select value={sortBy} onChange={e => setSortBy(e.target.value)}>
                <option value="orderDate">Beställningsdatum</option>
                <option value="expectedArrivalDate">Förväntat ankomstdatum</option>
            </select>

            <label>Ordning:</label>
            <select value={sortOrder} onChange={e => setSortOrder(e.target.value)}>
                <option value="asc">Stigande</option>
                <option value="desc">Fallande</option>
            </select>

            {/* Lista av delbeställningar */}
            <ul>
                {filteredOrders.map(order => (
                    <li key={order.id}>
                        {order.product.name} ({order.quantity} st) - {order.product.supplier}
                        - Inköpspris: {order.product.purchasePrice} kr
                        - Försäljningspris: {order.product.sellingPrice} kr
                        - **Totalt inköpspris: {order.totalPurchaseCost} kr**
                        - **Totalt försäljningspris: {order.totalSellingPrice} kr**
                        {order.received ? " ✅ Mottagen" : (
                            <button>Markera som mottagen</button>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default PartOrderList;
