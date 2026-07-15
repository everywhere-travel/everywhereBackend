package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CorreoPage extends BasePage {

    // Asumiendo que hay un botón de enviar correo desde el listado de documentos
    @FindBy(css = "button.btn-mail")
    private WebElement btnAbrirCorreo;

    @FindBy(css = "input[formcontrolname='destinatario'], input[type='email']")
    private WebElement inputDestinatario;

    @FindBy(css = "input[formcontrolname='asunto'], input[name='asunto']")
    private WebElement inputAsunto;
    
    @FindBy(css = "textarea[formcontrolname='mensaje'], textarea[name='mensaje']")
    private WebElement inputMensaje;

    @FindBy(css = "button[type='submit']")
    private WebElement btnEnviar;

    @FindBy(css = ".toast-success, .snack-bar-container")
    private WebElement mensajeExito;

    public CorreoPage(WebDriver driver) {
        super(driver);
    }

    public void abrirModalCorreo() {
        wait.until(ExpectedConditions.elementToBeClickable(btnAbrirCorreo)).click();
    }

    public void llenarYEnviar(String destinatario, String asunto, String mensaje) {
        wait.until(ExpectedConditions.visibilityOf(inputDestinatario)).clear();
        inputDestinatario.sendKeys(destinatario);
        
        inputAsunto.clear();
        inputAsunto.sendKeys(asunto);
        
        inputMensaje.clear();
        inputMensaje.sendKeys(mensaje);
        
        wait.until(ExpectedConditions.elementToBeClickable(btnEnviar)).click();
    }

    public boolean isMensajeExitoMostrado() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(mensajeExito)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
