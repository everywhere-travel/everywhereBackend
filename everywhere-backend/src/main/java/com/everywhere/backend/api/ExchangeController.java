package com.everywhere.backend.api; 

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

    private static final Logger LOGGER = Logger.getLogger(ExchangeController.class.getName());

    @GetMapping("/tipo-de-cambio")
    public ResponseEntity<Object> getTipoDeCambio() {
        // --- Configuración de Selenium ---
        // Esto evita que se abran ventanas de consola de chromedriver
        System.setProperty("webdriver.chrome.silentOutput", "true"); 
        
        // Opcional pero recomendado: Especifica la ruta al driver si no está en el PATH del sistema.
        // System.setProperty("webdriver.chrome.driver", "C:/path/to/your/chromedriver.exe");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Ejecuta el navegador en modo headless (sin interfaz gráfica)
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = null;
        
        try {
            driver = new ChromeDriver(options);
            LOGGER.info("Robot iniciando, navegando a la página...");

            driver.get("https://www.cambiomundial.com/");

            // --- LA SOLUCIÓN: ESPERA EXPLÍCITA ---
            // Se crea un objeto de espera que esperará un máximo de 10 segundos.
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            LOGGER.info("El robot está en la página, esperando que los valores aparezcan...");

            // El robot ahora esperará hasta que el elemento con ID 'lblvalorcompra' TENGA TEXTO.
            WebElement buyElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lblvalorcompra")));
            WebElement sellElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lblvalorventa")));

            // Una pequeña espera adicional para asegurar que el texto se renderice completamente si es necesario
            wait.until(d -> !d.findElement(By.id("lblvalorcompra")).getText().isEmpty());

            String buyPrice = buyElement.getText();
            String sellPrice = sellElement.getText();
            
            LOGGER.info("Valores encontrados: Compra=" + buyPrice + ", Venta=" + sellPrice);

            Map<String, String> rates = new HashMap<>();
            rates.put("buy", buyPrice);
            rates.put("sell", sellPrice);
            
            return new ResponseEntity<>(rates, HttpStatus.OK);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El robot falló: " + e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "El robot no pudo leer los datos de la página.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            
        } finally {
            if (driver != null) {
                driver.quit(); // Cierra el navegador y el proceso del driver
                LOGGER.info("El robot ha terminado y el navegador se ha cerrado.");
            }
        }
    }
}