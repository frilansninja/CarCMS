import React, { useEffect, useState } from "react";
import {
    Box, Typography, Tab, Tabs, Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, MenuItem, Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Paper
} from "@mui/material";
import CreateTemplateForm from "../components/CreateTemplateForm";

const TemplateAdminPage = () => {
    const [categories, setCategories] = useState([]);
    const [templates, setTemplates] = useState([]);
    const [articles, setArticles] = useState([]);
    const [selectedTab, setSelectedTab] = useState(0);
    const [selectedTemplate, setSelectedTemplate] = useState(null);
    const [formData, setFormData] = useState({
        description: "",
        categoryId: "",
        estimatedTime: "",
    });
    const [selectedArticles, setSelectedArticles] = useState([]);
    const [newArticle, setNewArticle] = useState({ articleId: "", quantity: "" });

    useEffect(() => {
        fetchCategories();
        fetchTemplates();
        fetchArticles();
    }, []);

    const fetchCategories = async () => {
        const token = localStorage.getItem("accessToken");
        try {
            const response = await fetch("/api/workordercategories", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.ok) {
                setCategories(await response.json());
            }
        } catch (error) {
            console.error("Fel vid hämtning av kategorier:", error);
        }
    };

    const fetchTemplates = async () => {
        const token = localStorage.getItem("accessToken");
        try {
            const response = await fetch("/api/worktasktemplates", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.ok) {
                setTemplates(await response.json());
            }
        } catch (error) {
            console.error("Fel vid hämtning av arbetsuppgiftsmallar:", error);
        }
    };

    const fetchArticles = async () => {
        const token = localStorage.getItem("accessToken");
        try {
            const response = await fetch("/api/articles", {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.ok) {
                setArticles(await response.json());
            }
        } catch (error) {
            console.error("Fel vid hämtning av artiklar:", error);
        }
    };

    const fetchTemplateArticles = async (templateId) => {
        const token = localStorage.getItem("accessToken");
        try {
            const response = await fetch(`/api/worktasktemplates/${templateId}/requiredParts`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                console.log("Hämtade artiklar för mall:", data);
                setSelectedArticles(data);
            }
        } catch (error) {
            console.error("Fel vid hämtning av artiklar för mallen:", error);
        }
    };

    const handleTemplateCreated = () => {
        fetchTemplates();
    };

    const handleOpenEditDialog = async  (template) => {
        console.log("Öppnar redigering för mall:", template);
        setSelectedTemplate(template);
        fetchTemplateArticles(template.id);
        setFormData({
            description: template.description || "",
            categoryId: template.categoryId || "",
            estimatedTime: template.estimatedTime || "",
        });
        await fetchTemplateArticles(template.id);
    };

    const handleCloseEditDialog = () => {
        setSelectedTemplate(null);
        setFormData({
            description: "",
            categoryId: "",
            estimatedTime: "",
        });
        setSelectedArticles([]);
    };

    const handleDeleteTemplate = async () => {
        if (!selectedTemplate) return;
        const confirmDelete = window.confirm(`Är du säker på att du vill ta bort "${selectedTemplate.description}"?`);
        if (!confirmDelete) return;

        const token = localStorage.getItem("accessToken");

        try {
            const response = await fetch(`/api/worktasktemplates/${selectedTemplate.id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.ok) {
                fetchTemplates();
                handleCloseEditDialog();
            } else {
                console.error("Fel vid borttagning av arbetsuppgiftsmall");
            }
        } catch (error) {
            console.error("Fel vid borttagning av arbetsuppgiftsmall:", error);
        }
    };

    const handleAddArticle = async () => {
        if (!newArticle.articleId || !newArticle.quantity) return;
        const token = localStorage.getItem("accessToken");

        try {
            const response = await fetch(`/api/worktasktemplates/${selectedTemplate.id}/articles`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(newArticle)
            });

            if (response.ok) {
                fetchTemplateArticles(selectedTemplate.id);
                setNewArticle({ articleId: "", quantity: "" });
            } else {
                console.error("Fel vid tillägg av artikel");
            }
        } catch (error) {
            console.error("Fel vid tillägg av artikel:", error);
        }
    };

    const handleRemoveArticle = async (articleId) => {
        const token = localStorage.getItem("accessToken");

        try {
            const response = await fetch(`/api/worktasktemplates/${selectedTemplate.id}/articles/${articleId}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.ok) {
                fetchTemplateArticles(selectedTemplate.id);
            } else {
                console.error("Fel vid borttagning av artikel");
            }
        } catch (error) {
            console.error("Fel vid borttagning av artikel:", error);
        }
    };

    return (
        <Box sx={{ p: 2 }}>
            <Typography variant="h5" sx={{ mb: 2 }}>Administrera Arbetsuppgiftsmallar</Typography>

            <Tabs value={selectedTab} onChange={(e, newValue) => setSelectedTab(newValue)}>
                <Tab label="Skapa Ny Mall" />
                <Tab label="Befintliga Mallar" />
            </Tabs>

            <Box sx={{ mt: 2 }}>
                {selectedTab === 0 && <CreateTemplateForm categories={categories} onTemplateCreated={handleTemplateCreated} />}
                {selectedTab === 1 && (
                    <Box>
                        <Typography variant="h6" sx={{ mb: 2 }}>Befintliga Mallar</Typography>
                        {templates.map((tpl) => (
                            <Box key={tpl.id} sx={{ borderBottom: "1px solid #ccc", p: 1, cursor: "pointer", "&:hover": { backgroundColor: "#f5f5f5" }}} onClick={() => handleOpenEditDialog(tpl)}>
                                <Typography>#{tpl.id} - {tpl.description} (Kategori: {tpl.categoryName || "Okänd"})</Typography>
                            </Box>
                        ))}
                    </Box>
                )}
            </Box>

            {selectedTemplate && (
                <Dialog open={Boolean(selectedTemplate)} onClose={handleCloseEditDialog} fullWidth maxWidth="md">
                    <DialogTitle>Redigera arbetsuppgiftsmall</DialogTitle>
                    <DialogContent>
                        <Typography variant="h6" sx={{ mt: 3 }}>Kopplade artiklar</Typography>
                        <TableContainer component={Paper} sx={{ mt: 2 }}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Artikel</TableCell>
                                        <TableCell>Antal</TableCell>
                                        <TableCell>Åtgärd</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {selectedArticles.map((part) => (
                                        <TableRow key={part.id}>
                                            <TableCell>{part.description} ({part.partNumber})</TableCell>
                                            <TableCell>{part.quantity}</TableCell>
                                            <TableCell>
                                                <Button color="error" onClick={() => handleRemoveArticle(part.id)}>Ta bort</Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </DialogContent>
                </Dialog>
            )}
        </Box>
    );
};

export default TemplateAdminPage;
