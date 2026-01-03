import { useState } from "react";
import {
    Paper,
    Typography,
    Button,
    Box,
    Chip,
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";

const PartsList = ({ workOrderId, parts, onRefresh }) => {
    const [confirmDelete, setConfirmDelete] = useState(null);
    const [orderingPart, setOrderingPart] = useState(null);

    const formatPrice = (price) => {
        return new Intl.NumberFormat("sv-SE", {
            style: "currency",
            currency: "SEK"
        }).format(price);
    };

    const getStatusColor = (status) => {
        const colors = {
            PLANNED: "default",
            ORDERED: "primary",
            RECEIVED: "success",
            INSTALLED: "success",
            CANCELLED: "error"
        };
        return colors[status] || "default";
    };

    const handleCancelPart = async (partLineId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(
                `/api/work-orders/${workOrderId}/parts/lines/${partLineId}`,
                {
                    method: "DELETE",
                    headers: { "Authorization": `Bearer ${token}` }
                }
            );

            if (response.ok) {
                console.log("✅ Part cancelled");
                onRefresh();
                setConfirmDelete(null);
            } else {
                console.error("❌ Failed to cancel part");
                alert("Failed to cancel part");
            }
        } catch (error) {
            console.error("❌ Error cancelling part:", error);
            alert("Error cancelling part");
        }
    };

    const handleOrderPart = async (partLineId) => {
        try {
            const token = localStorage.getItem("accessToken");
            const response = await fetch(
                `/api/work-orders/${workOrderId}/parts/lines/${partLineId}/order`,
                {
                    method: "POST",
                    headers: { "Authorization": `Bearer ${token}` }
                }
            );

            if (response.ok) {
                const result = await response.json();
                console.log("✅ Part ordered:", result);
                onRefresh();
                setOrderingPart(null);
                alert(`Order placed successfully! Reference: ${result.supplierOrderReference}`);
            } else {
                const error = await response.json();
                console.error("❌ Failed to order part:", error);
                alert(`Failed to place order: ${error.message || "Unknown error"}`);
            }
        } catch (error) {
            console.error("❌ Error ordering part:", error);
            alert("Error placing order");
        }
    };

    const calculateTotalPrice = (part) => {
        return part.finalUnitPriceExVat * part.quantity;
    };

    const calculateGrandTotal = () => {
        return parts.reduce((sum, part) => {
            if (part.status !== "CANCELLED") {
                return sum + calculateTotalPrice(part);
            }
            return sum;
        }, 0);
    };

    if (!parts || parts.length === 0) {
        return (
            <Typography color="text.secondary" align="center" sx={{ py: 2 }}>
                No parts added yet
            </Typography>
        );
    }

    return (
        <Box>
            <Table size="small">
                <TableHead>
                    <TableRow>
                        <TableCell><strong>Part</strong></TableCell>
                        <TableCell align="center"><strong>Qty</strong></TableCell>
                        <TableCell align="right"><strong>Unit Price</strong></TableCell>
                        <TableCell align="right"><strong>Total</strong></TableCell>
                        <TableCell align="center"><strong>Status</strong></TableCell>
                        <TableCell align="center"><strong>Actions</strong></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {parts.map((part) => (
                        <TableRow
                            key={part.id}
                            sx={{
                                backgroundColor: part.status === "CANCELLED" ? "#f5f5f5" : "white",
                                opacity: part.status === "CANCELLED" ? 0.6 : 1
                            }}
                        >
                            <TableCell>
                                <Typography variant="body2" fontWeight="bold">
                                    {part.partName}
                                </Typography>
                                <Typography variant="caption" color="text.secondary">
                                    {part.supplierCode} | {part.supplierPartId}
                                </Typography>
                                {part.supplierOrderReference && (
                                    <Typography variant="caption" color="primary" display="block">
                                        Order: {part.supplierOrderReference}
                                    </Typography>
                                )}
                            </TableCell>
                            <TableCell align="center">{part.quantity}</TableCell>
                            <TableCell align="right">
                                <Box>
                                    {part.finalUnitPriceExVat !== part.unitPriceExVat && (
                                        <Typography variant="caption" color="text.secondary" sx={{ textDecoration: "line-through" }}>
                                            {formatPrice(part.unitPriceExVat)}
                                        </Typography>
                                    )}
                                    <Typography variant="body2">
                                        {formatPrice(part.finalUnitPriceExVat)}
                                    </Typography>
                                </Box>
                            </TableCell>
                            <TableCell align="right">
                                <Typography variant="body2" fontWeight="bold">
                                    {formatPrice(calculateTotalPrice(part))}
                                </Typography>
                            </TableCell>
                            <TableCell align="center">
                                <Chip
                                    label={part.status}
                                    size="small"
                                    color={getStatusColor(part.status)}
                                />
                            </TableCell>
                            <TableCell align="center">
                                <Box sx={{ display: "flex", justifyContent: "center", gap: 0.5 }}>
                                    {part.status === "PLANNED" && (
                                        <>
                                            <IconButton
                                                size="small"
                                                color="primary"
                                                onClick={() => setOrderingPart(part)}
                                                title="Place order"
                                            >
                                                <ShoppingCartIcon fontSize="small" />
                                            </IconButton>
                                            <IconButton
                                                size="small"
                                                color="error"
                                                onClick={() => setConfirmDelete(part)}
                                                title="Cancel part"
                                            >
                                                <DeleteIcon fontSize="small" />
                                            </IconButton>
                                        </>
                                    )}
                                    {part.status === "ORDERED" && (
                                        <Typography variant="caption" color="text.secondary">
                                            Order placed
                                        </Typography>
                                    )}
                                </Box>
                            </TableCell>
                        </TableRow>
                    ))}

                    {/* Grand Total Row */}
                    <TableRow>
                        <TableCell colSpan={3} align="right">
                            <Typography variant="subtitle1" fontWeight="bold">
                                Grand Total (ex. VAT):
                            </Typography>
                        </TableCell>
                        <TableCell align="right">
                            <Typography variant="h6" color="primary" fontWeight="bold">
                                {formatPrice(calculateGrandTotal())}
                            </Typography>
                        </TableCell>
                        <TableCell colSpan={2} />
                    </TableRow>
                </TableBody>
            </Table>

            {/* Confirm Delete Dialog */}
            <Dialog open={!!confirmDelete} onClose={() => setConfirmDelete(null)}>
                <DialogTitle>Cancel Part?</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to cancel this part?
                    </Typography>
                    {confirmDelete && (
                        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                            {confirmDelete.partName} - {formatPrice(calculateTotalPrice(confirmDelete))}
                        </Typography>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setConfirmDelete(null)}>No, Keep It</Button>
                    <Button
                        color="error"
                        variant="contained"
                        onClick={() => handleCancelPart(confirmDelete.id)}
                    >
                        Yes, Cancel Part
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Confirm Order Dialog */}
            <Dialog open={!!orderingPart} onClose={() => setOrderingPart(null)}>
                <DialogTitle>Place Order?</DialogTitle>
                <DialogContent>
                    <Typography gutterBottom>
                        Are you sure you want to place an order for this part?
                    </Typography>
                    {orderingPart && (
                        <Box sx={{ mt: 2, p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
                            <Typography variant="body2">
                                <strong>Part:</strong> {orderingPart.partName}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Quantity:</strong> {orderingPart.quantity}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Supplier:</strong> {orderingPart.supplierCode}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Total:</strong> {formatPrice(calculateTotalPrice(orderingPart))}
                            </Typography>
                        </Box>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOrderingPart(null)}>Cancel</Button>
                    <Button
                        color="primary"
                        variant="contained"
                        onClick={() => handleOrderPart(orderingPart.id)}
                    >
                        Place Order
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default PartsList;
