package tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.CorreoPage;
import pages.DashboardPage;
import pages.DocumentosCobranzaPage;
import pages.LoginPage;
import utils.ConfigReader;

public class CorreoE2ETest extends BaseTest {

    private void realizarLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("TEST_USER"), ConfigReader.get("TEST_PASSWORD"));
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assertions.assertTrue(dashboardPage.isDashboardLoaded(), "Login fallido");
    }

    @Test
    public void testPE2E_12_EnvioDocumentoPorCorreo() {
        realizarLogin();
        
        // Asumiendo que el botón de correo está en Documentos de Cobranza
        logPaso("Navegando a Documentos de Cobranza");
        DocumentosCobranzaPage cobranzasPage = new DocumentosCobranzaPage(driver);
        cobranzasPage.irACobranzas();

        logPaso("Abriendo formulario de correo");
        CorreoPage correoPage = new CorreoPage(driver);
        correoPage.abrirModalCorreo();

        logPaso("Llenando formulario de correo");
        String destino = ConfigReader.get("TEST_EMAIL_DESTINATION");
        if (destino == null) destino = "test@example.com";
        correoPage.llenarYEnviar(destino, "Documento de Cobranza E2E", "Este es un correo de prueba automatizado.");

        boolean exito = correoPage.isMensajeExitoMostrado();
        if (exito) {
            logOk("Correo enviado exitosamente (Confirmación UI)");
            tomarCaptura("PE2E-12_correo_enviado", false);
        } else {
            tomarCaptura("PE2E-12_error_correo", true);
        }
        Assertions.assertTrue(exito, "No se mostró el mensaje de éxito al enviar el correo");
    }
}
