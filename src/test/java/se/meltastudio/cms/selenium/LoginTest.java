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

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("WebDriver initialized successfully");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("WebDriver closed");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should load login page successfully")
    public void testLoginPageLoads() {
        driver.get(BASE_URL);

        // Wait for page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h2")));

        // Verify we're on the login page
        String pageTitle = driver.getTitle();
        assertNotNull(pageTitle, "Page title should not be null");

        // Check for login form elements
        WebElement usernameInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Användarnamn']"))
        );
        WebElement passwordInput = driver.findElement(By.xpath("//input[@placeholder='Lösenord']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Logga in')]"));

        assertNotNull(usernameInput, "Username input should be present");
        assertNotNull(passwordInput, "Password input should be present");
        assertNotNull(loginButton, "Login button should be present");

        System.out.println("Login page loaded successfully with all required elements");
    }

    @Test
    @Order(2)
    @DisplayName("Should login successfully with valid credentials")
    public void testSuccessfulLogin() throws InterruptedException {
        driver.get(BASE_URL);

        // Wait for login form to be ready
        WebElement usernameInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Användarnamn']"))
        );
        WebElement passwordInput = driver.findElement(By.xpath("//input[@placeholder='Lösenord']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Logga in')]"));

        // Fill in login credentials
        usernameInput.clear();
        usernameInput.sendKeys("admin");

        passwordInput.clear();
        passwordInput.sendKeys("admin");

        System.out.println("Filled in login credentials: admin / admin");

        // Click login button
        loginButton.click();
        System.out.println("Clicked login button");

        // Wait for navigation to dashboard (URL should change)
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/dashboard"),
            "Should navigate to dashboard page. Current URL: " + currentUrl);

        System.out.println("Successfully navigated to dashboard: " + currentUrl);

        // Verify access token is stored in localStorage
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String accessToken = (String) js.executeScript("return localStorage.getItem('accessToken');");
        String companyId = (String) js.executeScript("return localStorage.getItem('companyId');");
        String userRoles = (String) js.executeScript("return localStorage.getItem('userRoles');");

        assertNotNull(accessToken, "Access token should be stored in localStorage");
        assertFalse(accessToken.isEmpty(), "Access token should not be empty");
        assertTrue(accessToken.startsWith("eyJ"), "Access token should be a valid JWT token");

        assertNotNull(companyId, "Company ID should be stored in localStorage");
        assertEquals("1", companyId, "Company ID should be 1");

        assertNotNull(userRoles, "User roles should be stored in localStorage");
        assertTrue(userRoles.contains("SUPER_ADMIN"), "User should have SUPER_ADMIN role");

        System.out.println("Login verification successful!");
        System.out.println("Access Token: " + accessToken.substring(0, 20) + "...");
        System.out.println("Company ID: " + companyId);
        System.out.println("User Roles: " + userRoles);
    }

    @Test
    @Order(3)
    @DisplayName("Should show inline error message with invalid credentials")
    public void testInvalidLogin() throws InterruptedException {
        driver.get(BASE_URL);

        // Clear localStorage to ensure we're logged out
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("localStorage.clear();");

        // Reload the page after clearing localStorage
        driver.get(BASE_URL);

        // Wait for login form
        WebElement usernameInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Användarnamn']"))
        );
        WebElement passwordInput = driver.findElement(By.xpath("//input[@placeholder='Lösenord']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Logga in')]"));

        // Try with invalid credentials
        usernameInput.clear();
        usernameInput.sendKeys("wronguser");

        passwordInput.clear();
        passwordInput.sendKeys("wrongpassword");

        loginButton.click();

        // Wait for error message to appear
        WebElement errorMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'bg-red-100')]"))
        );

        // Verify error message is displayed
        assertNotNull(errorMessage, "Error message should be displayed");
        assertTrue(errorMessage.isDisplayed(), "Error message should be visible");

        String errorText = errorMessage.getText();
        assertTrue(errorText.contains("Felaktigt användarnamn eller lösenord") ||
                   errorText.contains("Fel vid inloggning"),
            "Error message should contain appropriate error text. Got: " + errorText);

        // Verify we're still on the login page
        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.contains("/dashboard"),
            "Should NOT navigate to dashboard with invalid credentials. Current URL: " + currentUrl);

        System.out.println("Login correctly failed with invalid credentials");
        System.out.println("Error message displayed: " + errorText);
    }
}
