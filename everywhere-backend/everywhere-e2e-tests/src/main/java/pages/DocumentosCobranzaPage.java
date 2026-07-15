package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DocumentosCobranzaPage extends BasePage {

    @FindBy(xpath = "//h4[contains(text(), 'Cobranza')]/ancestor::div[contains(@class, 'module-card')] | //span[contains(text(), 'Cobranza')]/ancestor::button | //a[contains(@href, '/collection-documents')]")
    private WebElement menuCobranzas;

    @FindBy(xpath = "//button[contains(., 'Nuevo Documento') or contains(., 'Nuevo')]")
    private WebElement btnNuevoDocumento;

    @FindBy(css = "ng-select[formcontrolname='cotizacionId']")
    private WebElement selectCotizacion;

    @FindBy(css = "button[type='submit']")
    private WebElement btnGenerar;

    @FindBy(css = ".toast-success, .snack-bar-container")
    private WebElement mensajeExito;

    public DocumentosCobranzaPage(WebDriver driver) {
        super(driver);
    }

    public void irACobranzas() {
        wait.until(ExpectedConditions.elementToBeClickable(menuCobranzas)).click();
        wait.until(ExpectedConditions.urlContains("/collection-documents"));
    }

    public void iniciarNuevoDocumento() {
        wait.until(ExpectedConditions.elementToBeClickable(btnNuevoDocumento)).click();
    }

    public void generarDocumento(String idCotizacion) {
        wait.until(ExpectedConditions.elementToBeClickable(selectCotizacion)).click();
        selectCotizacion.sendKeys(idCotizacion + "\n");
        wait.until(ExpectedConditions.elementToBeClickable(btnGenerar)).click();
    }

    public boolean isMensajeExitoMostrado() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(mensajeExito)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
