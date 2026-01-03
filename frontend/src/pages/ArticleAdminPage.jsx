import React, { useEffect, useState } from "react";
import {
    Box, Button, TextField, Dialog, DialogActions, DialogContent, DialogTitle,
    Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper
} from "@mui/material";

const ArticleAdminPage = () => {
    const [articles, setArticles] = useState([]);
    const [open, setOpen] = useState(false);
    const [selectedArticle, setSelectedArticle] = useState(null);
    const [formData, setFormData] = useState({
        description: "",
        partNumber: "",
        sellingPrice: "",
        stockQuantity: ""
    });

    useEffect(() => {
        fetchArticles();
    }, []);

    const fetchArticles = async () => {
        const token = localStorage.getItem("accessToken");
        try {
            const response = await fetch("/api/articles", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                setArticles(data);
            }
        } catch (error) {
            console.error("Fel vid hämtning av artiklar:", error);
        }
    };

    const handleOpen = (article = null) => {
        setSelectedArticle(article);
        setFormData(article
            ? { ...article }
            : { description: "", partNumber: "", sellingPrice: "", stockQuantity: "" }
        );
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setSelectedArticle(null);
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSave = async () => {
        const token = localStorage.getItem("accessToken");
        const method = selectedArticle ? "PUT" : "POST";
        const url = selectedArticle ? `/api/articles/${selectedArticle.id}` : "/api/articles";

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                fetchArticles();
                handleClose();
            } else {
                console.error("Fel vid spara artikel");
            }
        } catch (error) {
            console.error("Fel vid spara artikel:", error);
        }
    };

    const handleDelete = async (id) => {
        const token = localStorage.getItem("accessToken");
        if (!window.confirm("Är du säker på att du vill ta bort denna artikel?")) return;

        try {
            const response = await fetch(`/api/articles/${id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.ok) {
                fetchArticles();
            } else {
                console.error("Fel vid borttagning av artikel");
            }
        } catch (error) {
            console.error("Fel vid borttagning av artikel:", error);
        }
    };

    return (
        <Box sx={{ p: 2 }}>
            <Typography variant="h5" sx={{ mb: 2 }}>Hantera Artiklar</Typography>
            <Button variant="contained" color="primary" onClick={() => handleOpen()}>Ny Artikel</Button>

            {/* Artikel-tabell */}
            <TableContainer component={Paper} sx={{ mt: 2 }}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Namn</TableCell>
                            <TableCell>Artikelnummer</TableCell>
                            <TableCell>Pris</TableCell>
                            <TableCell>Lagersaldo</TableCell>
                            <TableCell>Åtgärder</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {articles.map((article) => (
                            <TableRow key={article.id}>
                                <TableCell>{article.id}</TableCell>
                                <TableCell>{article.description}</TableCell>
                                <TableCell>{article.partNumber}</TableCell>
                                <TableCell>{article.sellingPrice} kr</TableCell>
                                <TableCell>{article.stockQuantity}</TableCell>
                                <TableCell>
                                    <Button onClick={() => handleOpen(article)} color="primary">Redigera</Button>
                                    <Button onClick={() => handleDelete(article.id)} color="error">Ta bort</Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Dialog för att skapa/redigera en artikel */}
            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>{selectedArticle ? "Redigera Artikel" : "Ny Artikel"}</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Beskrivning"
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mt: 2 }}
                    />
                    <TextField
                        label="Artikelnummer"
                        name="partNumber"
                        value={formData.partNumber}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mt: 2 }}
                    />
                    <TextField
                        label="Pris"
                        name="sellingPrice"
                        type="number"
                        value={formData.sellingPrice}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mt: 2 }}
                    />
                    <TextField
                        label="Lagersaldo"
                        name="stockQuantity"
                        type="number"
                        value={formData.stockQuantity}
                        onChange={handleChange}
                        fullWidth
                        sx={{ mt: 2 }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="secondary">Avbryt</Button>
                    <Button onClick={handleSave} color="primary" variant="contained">
                        {selectedArticle ? "Spara Ändringar" : "Skapa"}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default ArticleAdminPage;
