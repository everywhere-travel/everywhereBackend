package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LiquidacionesPage extends BasePage {

    @FindBy(xpath = "//h4[text()='Liquidaciones']/ancestor::div[contains(@class, 'module-card')] | //span[text()='Liquidaciones']/ancestor::button | //a[contains(@href, '/settlements')]")
    private WebElement menuLiquidaciones;

    @FindBy(xpath = "//button[contains(., 'Nueva Liquidación') or contains(., 'Nueva')]")
    private WebElement btnNuevaLiquidacion;

    @FindBy(css = "ng-select[formcontrolname='cotizacionId'], select[name='cotizacion']")
    private WebElement selectCotizacion;
    
    @FindBy(css = "ng-select[formcontrolname='proveedorId'], select[name='proveedor']")
    private WebElement selectProveedor;

    @FindBy(css = "input[name='costo']")
    private WebElement inputCosto;

    @FindBy(css = "button[type='submit']")
    private WebElement btnGuardar;

    @FindBy(css = ".toast-success, .snack-bar-container")
    private WebElement mensajeExito;

    public LiquidacionesPage(WebDriver driver) {
        super(driver);
    }

    public void irALiquidaciones() {
        wait.until(ExpectedConditions.elementToBeClickable(menuLiquidaciones)).click();
        wait.until(ExpectedConditions.urlContains("/settlements"));
    }

    public void iniciarNuevaLiquidacion() {
        wait.until(ExpectedConditions.elementToBeClickable(btnNuevaLiquidacion)).click();
    }

    public void seleccionarCotizacionYProveedor(String idCotizacion, String proveedor) {
        wait.until(ExpectedConditions.elementToBeClickable(selectCotizacion)).click();
        selectCotizacion.sendKeys(idCotizacion + "\n");
        
        wait.until(ExpectedConditions.elementToBeClickable(selectProveedor)).click();
        selectProveedor.sendKeys(proveedor + "\n");
    }

    public void agregarCosto(String costo) {
        wait.until(ExpectedConditions.visibilityOf(inputCosto)).clear();
        inputCosto.sendKeys(costo);
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
