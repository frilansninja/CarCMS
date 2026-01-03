package se.meltastudio.cms.service;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import se.meltastudio.cms.dto.InvoiceDTO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

    @Service
    public class PDFGeneratorService {

        private static final String LOGO_DIRECTORY = "uploads/logos/";
        private static final String DEFAULT_LOGO = "uploads/logos/default.png"; // Standardlogotyp om ingen egen finns

        public byte[] generateInvoicePDF(InvoiceDTO invoice) {
        throw new NotImplementedException("inte än");

        }
            /*try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Lägg till företagslogotyp
                    addLogo(document, contentStream, invoice.getCompanyId());

                    // Faktura rubrik
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("Faktura: " + invoice.getInvoiceNumber());
                    contentStream.endText();

                    // Fakturadetaljer
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    addText(contentStream, "Datum: " + invoice.getIssueDate(), 100, 670);
                    addText(contentStream, "Förfallodatum: " + invoice.getDueDate(), 100, 650);
                    addText(contentStream, "Kund-ID: " + invoice.getEndCustomerId(), 100, 630);
                    addText(contentStream, "Belopp: " + invoice.getAmount() + " SEK", 100, 610);
                    addText(contentStream, "Betald: " + (invoice.isPaid() ? "Ja" : "Nej"), 100, 590);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                document.save(outputStream);
                return outputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Fel vid generering av PDF", e);
            }
        }

        private void addLogo(PDDocument document, PDPageContentStream contentStream, Long customerId) throws IOException {
            String logoPath = LOGO_DIRECTORY + customerId + ".png";
            File logoFile = new File(logoPath);

            if (!logoFile.exists()) {
                logoFile = new File(DEFAULT_LOGO); // Använd standardlogotyp om ingen kundspecifik finns
            }

            PDImageXObject logo = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), document);
            contentStream.drawImage(logo, 450, 700, 100, 50); // Placering och storlek
        }

        private void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
        }
    }


    private void addLogo(PDDocument document, PDPageContentStream contentStream) throws IOException {
        File file = new File(logo_path);
        if (file.exists()) {
            PDImageXObject logo = PDImageXObject.createFromFile(logo_path, document);
            contentStream.drawImage(logo, 450, 700, 100, 50); // Justera position och storlek
        }
    }

    private void addText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }*/
}
