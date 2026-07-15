package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class ClientesPage extends BasePage {

    @FindBy(xpath = "//h4[text()='Clientes']/ancestor::div[contains(@class, 'module-card')] | //span[text()='Clientes']/ancestor::button | //a[contains(@href, '/people')]")
    private WebElement menuClientes;

    @FindBy(xpath = "//button[contains(., 'Nuevo Cliente') or contains(., 'Nuevo')]")
    private WebElement btnNuevoCliente;

    @FindBy(css = "input[formcontrolname='nombres'], input[name='nombres']")
    private WebElement inputNombres;
    
    @FindBy(css = "input[formcontrolname='apellidos'], input[name='apellidos']")
    private WebElement inputApellidos;

    @FindBy(css = "input[formcontrolname='documento'], input[name='documento']")
    private WebElement inputDocumento;

    @FindBy(css = "input[formcontrolname='correo'], input[name='correo']")
    private WebElement inputCorreo;

    @FindBy(css = "button[type='submit']")
    private WebElement btnGuardar;

    @FindBy(css = ".toast-success, .snack-bar-container, .alert-success")
    private WebElement mensajeExito;

    @FindBy(css = "table tbody tr, .card-list .item")
    private List<WebElement> filasClientes;

    @FindBy(css = "input[type='search'], input[placeholder='Buscar']")
    private WebElement inputBuscar;

    public ClientesPage(WebDriver driver) {
        super(driver);
    }

    public void irAClientes() {
        wait.until(ExpectedConditions.elementToBeClickable(menuClientes)).click();
        wait.until(ExpectedConditions.urlContains("/people"));
    }

    public void iniciarNuevoCliente() {
        wait.until(ExpectedConditions.elementToBeClickable(btnNuevoCliente)).click();
    }

    public void llenarFormulario(String nombres, String apellidos, String documento, String correo) {
        wait.until(ExpectedConditions.visibilityOf(inputNombres)).sendKeys(nombres);
        inputApellidos.sendKeys(apellidos);
        inputDocumento.sendKeys(documento);
        inputCorreo.sendKeys(correo);
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

    public void buscarCliente(String termino) {
        wait.until(ExpectedConditions.visibilityOf(inputBuscar)).clear();
        inputBuscar.sendKeys(termino);
        // Esperar a que la tabla se actualice
        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    public boolean isClienteEnListado(String documento) {
        for (WebElement fila : filasClientes) {
            if (fila.getText().contains(documento)) {
                return true;
            }
        }
        return false;
    }
}
