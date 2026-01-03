package se.meltastudio.cms.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium tests for Spare Parts functionality.
 * Tests the complete flow: search, select, add, and order parts.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SparePartsTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static Long testWorkOrderId;

    @BeforeAll
    public static void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        System.out.println("✅ WebDriver initialized for Spare Parts tests");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ WebDriver closed");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should login successfully")
    public void testLogin() {
        driver.get(BASE_URL);

        // Login
        WebElement usernameInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Användarnamn']"))
        );
        WebElement passwordInput = driver.findElement(By.xpath("//input[@placeholder='Lösenord']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Logga in')]"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();

        // Wait for dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        System.out.println("✅ Login successful");
    }

    @Test
    @Order(2)
    @DisplayName("Should navigate to work order with parts section")
    public void testNavigateToWorkOrder() throws InterruptedException {
        // Navigate to work orders page
        driver.get(BASE_URL + "/workorders");

        // Wait for work orders list to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h4[contains(text(), 'Arbetsordrar')]")));

        // Find first work order and click it
        WebElement firstWorkOrder = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'MuiPaper-root')]//p[starts-with(text(), 'Arbetsorder')]"))
        );

        // Extract work order ID from text (e.g., "Arbetsorder #123")
        String workOrderText = firstWorkOrder.getText();
        testWorkOrderId = Long.parseLong(workOrderText.replaceAll("[^0-9]", ""));

        firstWorkOrder.click();

        // Wait for work order details page to load
        wait.until(ExpectedConditions.urlMatches(".*/workorder-details/\\d+"));

        // Verify "Spare Parts" section exists
        WebElement partsSection = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h6[contains(text(), 'Spare Parts')]"))
        );

        assertNotNull(partsSection, "Spare Parts section should be visible");
        System.out.println("✅ Navigated to Work Order #" + testWorkOrderId);
    }

    @Test
    @Order(3)
    @DisplayName("Should open parts search dialog")
    public void testOpenPartsSearchDialog() {
        // Find and click "Add Parts" button
        WebElement addPartsButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), '+ Add Parts') or contains(text(), 'Add Parts')]"))
        );

        addPartsButton.click();

        // Wait for dialog to open
        WebElement dialogTitle = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[contains(text(), 'Search Spare Parts')]"))
        );

        assertNotNull(dialogTitle, "Search dialog should open");

        // Verify search input exists
        WebElement searchInput = driver.findElement(By.xpath("//input[@placeholder='Search parts (e.g., brake pads, oil filter)' or contains(@label, 'Search')]"));
        assertNotNull(searchInput, "Search input should be present");

        System.out.println("✅ Parts search dialog opened");
    }

    @Test
    @Order(4)
    @DisplayName("Should search for brake pads and display results")
    public void testSearchParts() throws InterruptedException {
        // Enter search query
        WebElement searchInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@label, 'Search') or @placeholder='Search parts (e.g., brake pads, oil filter)']"))
        );

        searchInput.clear();
        searchInput.sendKeys("brake");

        // Click search button
        WebElement searchButton = driver.findElement(By.xpath("//button[contains(text(), 'Search')]"));
        searchButton.click();

        // Wait for search results to appear
        Thread.sleep(2000); // Wait for API call

        WebElement searchResults = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h6[contains(text(), 'Search Results')]"))
        );

        assertNotNull(searchResults, "Search results should be displayed");

        // Verify at least one result
        List<WebElement> resultItems = driver.findElements(By.xpath("//div[@role='button']//div[contains(@class, 'MuiListItemText-root')]"));
        assertTrue(resultItems.size() > 0, "Should have at least one search result");

        System.out.println("✅ Found " + resultItems.size() + " brake parts");
    }

    @Test
    @Order(5)
    @DisplayName("Should select a part from search results")
    public void testSelectPart() throws InterruptedException {
        // Click on first search result
        WebElement firstResult = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("(//div[@role='button'])[1]"))
        );

        firstResult.click();

        // Wait for "Selected Part" section to appear
        Thread.sleep(1000);

        WebElement selectedPartSection = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h6[contains(text(), 'Selected Part')]"))
        );

        assertNotNull(selectedPartSection, "Selected part section should be visible");

        // Verify quantity and markup fields are present
        WebElement quantityField = driver.findElement(By.xpath("//input[@type='number' and contains(@label, 'Quantity')]"));
        WebElement markupField = driver.findElement(By.xpath("//input[@type='number' and contains(@label, 'Markup')]"));

        assertNotNull(quantityField, "Quantity field should be present");
        assertNotNull(markupField, "Markup field should be present");

        System.out.println("✅ Part selected successfully");
    }

    @Test
    @Order(6)
    @DisplayName("Should add part to work order with markup")
    public void testAddPartWithMarkup() throws InterruptedException {
        // Set quantity to 2
        WebElement quantityField = driver.findElement(By.xpath("//input[contains(@label, 'Quantity')]"));
        quantityField.clear();
        quantityField.sendKeys("2");

        // Set markup to 20%
        WebElement markupField = driver.findElement(By.xpath("//input[contains(@label, 'Markup')]"));
        markupField.clear();
        markupField.sendKeys("20");

        Thread.sleep(500); // Wait for price preview to update

        // Click "Add to Work Order" button
        WebElement addButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Add to Work Order')]"))
        );

        addButton.click();

        // Wait for dialog to close and parts list to refresh
        Thread.sleep(2000);

        // Verify dialog is closed
        List<WebElement> dialogs = driver.findElements(By.xpath("//h2[contains(text(), 'Search Spare Parts')]"));
        assertEquals(0, dialogs.size(), "Dialog should be closed");

        System.out.println("✅ Part added to work order with 20% markup");
    }

    @Test
    @Order(7)
    @DisplayName("Should display added part in parts list")
    public void testVerifyPartInList() throws InterruptedException {
        Thread.sleep(1000); // Wait for list to refresh

        // Verify part appears in the table
        WebElement partsTable = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//table"))
        );

        assertNotNull(partsTable, "Parts table should be visible");

        // Verify table has at least one row (excluding header)
        List<WebElement> tableRows = driver.findElements(By.xpath("//table//tbody//tr"));
        assertTrue(tableRows.size() > 0, "Should have at least one part in the list");

        // Verify quantity is 2
        WebElement quantityCell = driver.findElement(By.xpath("//table//tbody//tr[1]//td[2]"));
        assertEquals("2", quantityCell.getText(), "Quantity should be 2");

        // Verify status is PLANNED
        WebElement statusChip = driver.findElement(By.xpath("//table//tbody//tr[1]//span[contains(text(), 'PLANNED')]"));
        assertNotNull(statusChip, "Status should be PLANNED");

        System.out.println("✅ Part verified in parts list");
    }

    @Test
    @Order(8)
    @DisplayName("Should place order for the part")
    public void testPlaceOrder() throws InterruptedException {
        // Find and click the shopping cart icon (order button)
        WebElement orderButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Place order' or .//svg[contains(@data-testid, 'ShoppingCart')]]"))
        );

        orderButton.click();

        // Wait for confirmation dialog
        Thread.sleep(500);

        WebElement confirmDialog = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[contains(text(), 'Place Order')]"))
        );

        assertNotNull(confirmDialog, "Confirm order dialog should appear");

        // Click "Place Order" button in dialog
        WebElement confirmButton = driver.findElement(By.xpath("//button[contains(text(), 'Place Order')]"));
        confirmButton.click();

        // Wait for success alert and dialog to close
        Thread.sleep(2000);

        System.out.println("✅ Order placement initiated");
    }

    @Test
    @Order(9)
    @DisplayName("Should verify part status changed to ORDERED")
    public void testVerifyOrderedStatus() throws InterruptedException {
        Thread.sleep(1000); // Wait for status update

        // Refresh the page to ensure we have latest data
        driver.navigate().refresh();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h6[contains(text(), 'Spare Parts')]")));
        Thread.sleep(1000);

        // Verify status changed to ORDERED
        WebElement statusChip = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'ORDERED')]"))
        );

        assertNotNull(statusChip, "Status should be ORDERED");

        // Verify supplier order reference is displayed
        List<WebElement> orderReferences = driver.findElements(By.xpath("//td//span[contains(text(), 'Order:')]"));
        assertTrue(orderReferences.size() > 0, "Supplier order reference should be displayed");

        System.out.println("✅ Part status verified as ORDERED");
    }

    @Test
    @Order(10)
    @DisplayName("Should verify grand total is calculated correctly")
    public void testVerifyGrandTotal() {
        // Find the grand total row
        WebElement grandTotalLabel = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(text(), 'Grand Total')]"))
        );

        assertNotNull(grandTotalLabel, "Grand total label should be present");

        // Verify grand total value is displayed (should be > 0)
        WebElement grandTotalValue = driver.findElement(By.xpath("//h6[contains(@class, 'MuiTypography-h6')]"));
        String totalText = grandTotalValue.getText();

        assertNotNull(totalText, "Grand total value should be displayed");
        assertTrue(totalText.contains("kr") || totalText.contains("SEK"), "Total should show currency");

        System.out.println("✅ Grand total displayed: " + totalText);
    }

    @Test
    @Order(11)
    @DisplayName("Should search for oil filter and add another part")
    public void testAddSecondPart() throws InterruptedException {
        // Click Add Parts button again
        WebElement addPartsButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), '+ Add Parts') or contains(text(), 'Add Parts')]"))
        );

        addPartsButton.click();

        // Search for oil filter
        WebElement searchInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@label, 'Search')]"))
        );

        searchInput.clear();
        searchInput.sendKeys("oil");

        WebElement searchButton = driver.findElement(By.xpath("//button[contains(text(), 'Search')]"));
        searchButton.click();

        Thread.sleep(2000);

        // Select first result
        WebElement firstResult = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("(//div[@role='button'])[1]"))
        );
        firstResult.click();

        Thread.sleep(500);

        // Add without markup (quantity 1)
        WebElement addButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Add to Work Order')]"))
        );
        addButton.click();

        Thread.sleep(2000);

        // Verify we now have 2 parts in the list
        List<WebElement> tableRows = driver.findElements(By.xpath("//table//tbody//tr[not(contains(., 'Grand Total'))]"));
        assertEquals(2, tableRows.size(), "Should have 2 parts in the list");

        System.out.println("✅ Second part (oil filter) added successfully");
    }

    @Test
    @Order(12)
    @DisplayName("Should cancel a planned part")
    public void testCancelPart() throws InterruptedException {
        // Find the second part (oil filter, should be PLANNED)
        List<WebElement> deleteButtons = driver.findElements(By.xpath("//button[@title='Cancel part' or .//svg[contains(@data-testid, 'Delete')]]"));

        assertTrue(deleteButtons.size() > 0, "Should have at least one delete button for PLANNED parts");

        // Click delete button
        deleteButtons.get(0).click();

        // Wait for confirmation dialog
        Thread.sleep(500);

        WebElement confirmDialog = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[contains(text(), 'Cancel Part')]"))
        );

        assertNotNull(confirmDialog, "Cancel confirmation dialog should appear");

        // Click "Yes, Cancel Part" button
        WebElement confirmButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes, Cancel Part')]"));
        confirmButton.click();

        Thread.sleep(1500);

        // Verify part status changed to CANCELLED
        WebElement cancelledStatus = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'CANCELLED')]"))
        );

        assertNotNull(cancelledStatus, "Part should be marked as CANCELLED");

        System.out.println("✅ Part cancelled successfully");
    }

    @Test
    @Order(13)
    @DisplayName("Should verify metrics endpoint is accessible")
    public void testMetricsEndpoint() {
        // Navigate to metrics (this would typically be admin-only)
        driver.get(BASE_URL + "/api/parts/metrics");

        // Wait for response
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("pre")));

        // Verify we got a response (JSON or text)
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("cache") || pageSource.contains("Metrics"),
            "Metrics endpoint should return data");

        System.out.println("✅ Metrics endpoint accessible");
    }
}
