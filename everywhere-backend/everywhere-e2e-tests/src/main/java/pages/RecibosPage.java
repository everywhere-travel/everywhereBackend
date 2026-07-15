package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RecibosPage extends BasePage {

    @FindBy(xpath = "//h4[text()='Recibos']/ancestor::div[contains(@class, 'module-card')] | //span[text()='Recibos']/ancestor::button | //a[contains(@href, '/receipts')]")
    private WebElement menuRecibos;

    @FindBy(xpath = "//button[contains(., 'Nuevo Recibo') or contains(., 'Nuevo')]")
    private WebElement btnNuevoRecibo;

    @FindBy(css = "ng-select[formcontrolname='cotizacionId']")
    private WebElement selectCotizacion;

    @FindBy(css = "input[formcontrolname='monto'], input[name='monto']")
    private WebElement inputMonto;

    @FindBy(css = "ng-select[formcontrolname='medioPago']")
    private WebElement selectMedioPago;

    @FindBy(css = "button[type='submit']")
    private WebElement btnRegistrar;

    @FindBy(css = ".toast-success, .snack-bar-container")
    private WebElement mensajeExito;

    public RecibosPage(WebDriver driver) {
        super(driver);
    }

    public void irARecibos() {
        wait.until(ExpectedConditions.elementToBeClickable(menuRecibos)).click();
        wait.until(ExpectedConditions.urlContains("/receipts"));
    }

    public void iniciarNuevoRecibo() {
        wait.until(ExpectedConditions.elementToBeClickable(btnNuevoRecibo)).click();
    }

    public void registrarRecibo(String idCotizacion, String monto, String medioPago) {
        wait.until(ExpectedConditions.elementToBeClickable(selectCotizacion)).click();
        selectCotizacion.sendKeys(idCotizacion + "\n");
        
        wait.until(ExpectedConditions.visibilityOf(inputMonto)).clear();
        inputMonto.sendKeys(monto);
        
        wait.until(ExpectedConditions.elementToBeClickable(selectMedioPago)).click();
        selectMedioPago.sendKeys(medioPago + "\n");
        
        wait.until(ExpectedConditions.elementToBeClickable(btnRegistrar)).click();
    }

    public boolean isMensajeExitoMostrado() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(mensajeExito)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
