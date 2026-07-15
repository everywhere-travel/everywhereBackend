package base;

import com.aventstack.extentreports.Status;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import utils.ReportManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        File reportDir = new File("reportes");
        if (!reportDir.exists()) reportDir.mkdirs();
        File evDir = new File("evidencias");
        if (!evDir.exists()) evDir.mkdirs();
        File evErrDir = new File("evidencias/errores");
        if (!evErrDir.exists()) evErrDir.mkdirs();
    }

    @BeforeEach
    public void setupTest(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        System.out.println("[INICIO] " + testName);
        ReportManager.startTest(testName, testInfo.getTags().toString());

        ChromeOptions options = new ChromeOptions();
        String headless = ConfigReader.get("HEADLESS");
        if (headless != null && headless.equalsIgnoreCase("true")) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        
        int timeout = 15;
        try {
            timeout = Integer.parseInt(ConfigReader.get("TIMEOUT_SECONDS"));
        } catch(Exception e) {}
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        String baseUrl = ConfigReader.get("BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:4200";
        }
        driver.get(baseUrl);
    }

    @AfterEach
    public void teardownTest(TestInfo testInfo) {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void teardownClass() {
        ReportManager.flushReports();
    }

    protected void logPaso(String mensaje) {
        System.out.println("[PASO] " + mensaje);
        ReportManager.getTest().log(Status.INFO, mensaje);
    }

    protected void logOk(String mensaje) {
        System.out.println("[OK] " + mensaje);
        ReportManager.getTest().log(Status.PASS, mensaje);
    }

    protected void logError(String mensaje) {
        System.out.println("[FALLIDO] " + mensaje);
        ReportManager.getTest().log(Status.FAIL, mensaje);
    }
    
    protected String generarIdUnico() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    protected void tomarCaptura(String nombre, boolean esError) {
        if (driver instanceof TakesScreenshot) {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = nombre + "_" + timestamp + ".png";
            String directory = esError ? "evidencias/errores/" : "evidencias/";
            File destination = new File(directory + fileName);
            try {
                Files.copy(screenshot.toPath(), destination.toPath());
                if (esError) {
                    System.out.println("[EVIDENCIA] " + destination.getPath());
                    ReportManager.getTest().addScreenCaptureFromPath(destination.getAbsolutePath());
                } else {
                    ReportManager.getTest().addScreenCaptureFromPath(destination.getAbsolutePath(), "Evidencia: " + nombre);
                }
            } catch (IOException e) {
                System.err.println("No se pudo guardar la captura de pantalla: " + e.getMessage());
            }
        }
    }
}
