package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CotizacionesPage extends BasePage {

    @FindBy(xpath = "//h4[text()='Cotizaciones']/ancestor::div[contains(@class, 'module-card')] | //span[text()='Cotizaciones']/ancestor::button | //a[contains(@href, '/quotes')]")
    private WebElement menuCotizaciones;

    @FindBy(xpath = "//button[contains(., 'Nueva Cotización') or contains(., 'Nueva')]")
    private WebElement btnNuevaCotizacion;

    @FindBy(css = "ng-select[formcontrolname='clienteId'], select[name='cliente']")
    private WebElement selectCliente;

    @FindBy(css = ".add-service-btn")
    private WebElement btnAgregarServicio;

    @FindBy(css = "input[name='cantidad']")
    private WebElement inputCantidad;

    @FindBy(css = "input[name='precioUnitario']")
    private WebElement inputPrecioUnitario;

    @FindBy(css = "button[type='submit']")
    private WebElement btnGuardar;

    @FindBy(css = ".toast-success, .snack-bar-container")
    private WebElement mensajeExito;

    @FindBy(css = ".total-amount, .summary-total")
    private WebElement lblTotal;

    public CotizacionesPage(WebDriver driver) {
        super(driver);
    }

    public void irACotizaciones() {
        wait.until(ExpectedConditions.elementToBeClickable(menuCotizaciones)).click();
        wait.until(ExpectedConditions.urlContains("/quotes"));
    }

    public void iniciarNuevaCotizacion() {
        wait.until(ExpectedConditions.elementToBeClickable(btnNuevaCotizacion)).click();
    }

    public void seleccionarCliente(String nombreCliente) {
        // Implementación básica (depende del control real, si es mat-select, ng-select o select nativo)
        wait.until(ExpectedConditions.elementToBeClickable(selectCliente)).click();
        // Asumiendo que al escribir y presionar Enter se selecciona
        selectCliente.sendKeys(nombreCliente + "\n");
    }

    public void agregarServicio(String cantidad, String precio) {
        wait.until(ExpectedConditions.elementToBeClickable(btnAgregarServicio)).click();
        wait.until(ExpectedConditions.visibilityOf(inputCantidad)).clear();
        inputCantidad.sendKeys(cantidad);
        inputPrecioUnitario.clear();
        inputPrecioUnitario.sendKeys(precio);
    }

    public String obtenerTotal() {
        return wait.until(ExpectedConditions.visibilityOf(lblTotal)).getText();
    }

    public void guardar() {
        wait.until(ExpectedConditions.elementToBeClickable(btnGuardar)).click();
    }

    public boolean isMensajeExitoMostrado() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(mensajeExito)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
