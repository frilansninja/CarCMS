import { useState, useEffect } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    List,
    ListItem,
    ListItemText,
    Typography,
    CircularProgress,
    Box,
    Chip,
    Divider
} from "@mui/material";

const PartsSearchDialog = ({ open, onClose, workOrderId, onPartAdded }) => {
    const [searchQuery, setSearchQuery] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [selectedPart, setSelectedPart] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [markup, setMarkup] = useState("");

    useEffect(() => {
        if (!open) {
            // Reset state when dialog closes
            setSearchQuery("");
            setSearchResults([]);
            setSelectedPart(null);
            setQuantity(1);
            setMarkup("");
        }
    }, [open]);

    const handleSearch = async () => {
        if (!searchQuery.trim()) return;

        setLoading(true);
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(
                `/api/work-orders/${workOrderId}/parts/search?q=${encodeURIComponent(searchQuery)}&limit=20`,
                {
                    headers: { "Authorization": `Bearer ${token}` }
                }
            );

            if (response.ok) {
                const data = await response.json();
                setSearchResults(data);
                console.log("✅ Parts search results:", data);
            } else {
                console.error("❌ Failed to search parts");
                setSearchResults([]);
            }
        } catch (error) {
            console.error("❌ Error searching parts:", error);
            setSearchResults([]);
        } finally {
            setLoading(false);
        }
    };

    const handleKeyPress = (event) => {
        if (event.key === "Enter") {
            handleSearch();
        }
    };

    const handleAddPart = async () => {
        if (!selectedPart) return;

        try {
            const token = localStorage.getItem("accessToken");
            const requestBody = {
                supplierCode: selectedPart.supplierCode,
                supplierPartId: selectedPart.supplierPartId,
                quantity: quantity
            };

            // Add markup if specified
            if (markup && parseFloat(markup) > 0) {
                requestBody.manualMarkupPercent = parseFloat(markup);
            }

            const response = await fetch(`/api/work-orders/${workOrderId}/parts/lines`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(requestBody)
            });

            if (response.ok) {
                const addedPart = await response.json();
                console.log("✅ Part added:", addedPart);
                onPartAdded(); // Notify parent to refresh
                onClose();
            } else {
                const error = await response.json();
                console.error("❌ Failed to add part:", error);
                alert(`Failed to add part: ${error.message || "Unknown error"}`);
            }
        } catch (error) {
            console.error("❌ Error adding part:", error);
            alert("Error adding part. Please try again.");
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("sv-SE", {
            style: "currency",
            currency: "SEK"
        }).format(price);
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>Search Spare Parts</DialogTitle>
            <DialogContent>
                {/* Search Section */}
                <Box sx={{ mb: 3, display: "flex", gap: 1 }}>
                    <TextField
                        fullWidth
                        label="Search parts (e.g., brake pads, oil filter)"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyPress={handleKeyPress}
                        autoFocus
                    />
                    <Button
                        variant="contained"
                        onClick={handleSearch}
                        disabled={loading || !searchQuery.trim()}
                    >
                        Search
                    </Button>
                </Box>

                {/* Loading Indicator */}
                {loading && (
                    <Box sx={{ display: "flex", justifyContent: "center", my: 3 }}>
                        <CircularProgress />
                    </Box>
                )}

                {/* Search Results */}
                {!loading && searchResults.length > 0 && !selectedPart && (
                    <>
                        <Typography variant="h6" gutterBottom>
                            Search Results ({searchResults.length})
                        </Typography>
                        <List>
                            {searchResults.map((part) => (
                                <ListItem
                                    key={`${part.supplierCode}-${part.supplierPartId}`}
                                    button
                                    onClick={() => setSelectedPart(part)}
                                    sx={{
                                        border: "1px solid #e0e0e0",
                                        borderRadius: 1,
                                        mb: 1,
                                        "&:hover": { backgroundColor: "#f5f5f5" }
                                    }}
                                >
                                    <ListItemText
                                        primary={
                                            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                                                <Typography variant="subtitle1" fontWeight="bold">
                                                    {part.partName}
                                                </Typography>
                                                <Chip
                                                    label={part.brand}
                                                    size="small"
                                                    color="primary"
                                                    variant="outlined"
                                                />
                                            </Box>
                                        }
                                        secondary={
                                            <Box>
                                                <Typography variant="body2" color="text.secondary">
                                                    Part #: {part.supplierPartId} | OEM: {part.oemNumber || "N/A"}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    Supplier: {part.supplierCode}
                                                </Typography>
                                                <Box sx={{ display: "flex", gap: 2, mt: 1 }}>
                                                    <Typography variant="body1" fontWeight="bold" color="primary">
                                                        {formatPrice(part.unitPriceExVat)}
                                                    </Typography>
                                                    <Chip
                                                        label={part.availabilityStatus}
                                                        size="small"
                                                        color={part.availabilityStatus === "IN_STOCK" ? "success" : "warning"}
                                                    />
                                                    <Typography variant="body2" color="text.secondary">
                                                        Delivery: {part.deliveryEstimateDays} days
                                                    </Typography>
                                                </Box>
                                            </Box>
                                        }
                                    />
                                </ListItem>
                            ))}
                        </List>
                    </>
                )}

                {/* No Results */}
                {!loading && searchResults.length === 0 && searchQuery && (
                    <Typography color="text.secondary" align="center" sx={{ my: 3 }}>
                        No parts found. Try a different search term.
                    </Typography>
                )}

                {/* Selected Part Details */}
                {selectedPart && (
                    <Box>
                        <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
                            <Typography variant="h6">Selected Part</Typography>
                            <Button onClick={() => setSelectedPart(null)}>
                                Change Selection
                            </Button>
                        </Box>

                        <Box sx={{ p: 2, border: "2px solid #1976d2", borderRadius: 1, mb: 3 }}>
                            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                                {selectedPart.partName}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Brand: {selectedPart.brand} | Part #: {selectedPart.supplierPartId}
                            </Typography>
                            <Typography variant="body2" color="text.secondary" gutterBottom>
                                OEM: {selectedPart.oemNumber || "N/A"}
                            </Typography>
                            <Divider sx={{ my: 1 }} />
                            <Typography variant="h6" color="primary">
                                {formatPrice(selectedPart.unitPriceExVat)} (ex. VAT)
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                + VAT {selectedPart.vatRate}%
                            </Typography>
                        </Box>

                        {/* Quantity and Markup */}
                        <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
                            <TextField
                                label="Quantity"
                                type="number"
                                value={quantity}
                                onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                                inputProps={{ min: 1 }}
                                sx={{ width: 150 }}
                            />
                            <TextField
                                label="Markup (%)"
                                type="number"
                                value={markup}
                                onChange={(e) => setMarkup(e.target.value)}
                                placeholder="Optional"
                                inputProps={{ min: 0, step: 0.1 }}
                                sx={{ width: 150 }}
                            />
                        </Box>

                        {/* Price Preview */}
                        {markup && parseFloat(markup) > 0 && (
                            <Box sx={{ p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
                                <Typography variant="body2">
                                    Base price: {formatPrice(selectedPart.unitPriceExVat)}
                                </Typography>
                                <Typography variant="body2">
                                    Markup ({markup}%): {formatPrice(selectedPart.unitPriceExVat * parseFloat(markup) / 100)}
                                </Typography>
                                <Divider sx={{ my: 1 }} />
                                <Typography variant="subtitle1" fontWeight="bold">
                                    Final price: {formatPrice(selectedPart.unitPriceExVat * (1 + parseFloat(markup) / 100))}
                                </Typography>
                            </Box>
                        )}
                    </Box>
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cancel</Button>
                {selectedPart && (
                    <Button
                        variant="contained"
                        onClick={handleAddPart}
                        disabled={quantity < 1}
                    >
                        Add to Work Order
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default PartsSearchDialog;
