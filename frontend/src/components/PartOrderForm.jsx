import React, { useState } from "react";

const PartOrderForm = ({ workOrderId, onOrderCreated }) => {
    const [products, setProducts] = useState([]);
    const [selectedProductId, setSelectedProductId] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [expectedArrivalDate, setExpectedArrivalDate] = useState("");
    const [purchasePrice, setPurchasePrice] = useState(0);
    const [sellingPrice, setSellingPrice] = useState(0);

    useEffect(() => {
        fetch("/api/products")
            .then(response => response.json())
            .then(data => setProducts(data))
            .catch(error => console.error("Error fetching products:", error));
    }, []);
    const handleSubmit = (e) => {
        e.preventDefault();

        const newOrder = {
            workOrderId,
            productId: selectedProductId,
            quantity,
            expectedArrivalDate,
            purchasePrice,
            sellingPrice
        };

        fetch("/api/part-orders", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(newOrder)
        })
            .then(response => response.json())
            .then(order => {
                onOrderCreated(order);
                setSelectedProductId(null);
                setQuantity(1);
                setExpectedArrivalDate("");
                setPurchasePrice(0);
                setSellingPrice(0);
            })
            .catch(error => console.error("Error creating part order:", error));
    };

    return (
        <form onSubmit={handleSubmit}>
            <h3>Beställ ny del</h3>

            <label>Välj artikel:</label>
            <select value={selectedProductId} onChange={e => setSelectedProductId(e.target.value)} required>
                <option value="">-- Välj en artikel --</option>
                {products.map(product => (
                    <option key={product.id} value={product.id}>
                        {product.name} ({product.partNumber}) - {product.sellingPrice} kr
                    </option>
                ))}
            </select>

            <input type="number" value={quantity} onChange={e => setQuantity(e.target.value)} min="1" required />
            <input type="date" value={expectedArrivalDate} onChange={e => setExpectedArrivalDate(e.target.value)} required />

            <button type="submit">Lägg till</button>
        </form>
    );
};

export default PartOrderForm;
