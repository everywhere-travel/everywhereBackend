package tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.DashboardPage;
import pages.DocumentosCobranzaPage;
import pages.LoginPage;
import pages.RecibosPage;
import utils.ConfigReader;

public class FinanzasE2ETest extends BaseTest {

    private void realizarLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("TEST_USER"), ConfigReader.get("TEST_PASSWORD"));
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assertions.assertTrue(dashboardPage.isDashboardLoaded(), "Login fallido");
    }

    @Test
    public void testPE2E_09_GeneracionDocumentoCobranza() {
        realizarLogin();
        
        logPaso("Navegando a Documentos de Cobranza");
        DocumentosCobranzaPage cobranzasPage = new DocumentosCobranzaPage(driver);
        cobranzasPage.irACobranzas();
        
        logPaso("Iniciando nuevo documento");
        cobranzasPage.iniciarNuevoDocumento();

        logPaso("Generando documento para cotización");
        cobranzasPage.generarDocumento("Cotizacion E2E");

        boolean exito = cobranzasPage.isMensajeExitoMostrado();
        if (exito) {
            logOk("Documento de cobranza generado exitosamente");
            tomarCaptura("PE2E-09_cobranza_generada", false);
        } else {
            tomarCaptura("PE2E-09_error_cobranza", true);
        }
        Assertions.assertTrue(exito, "No se mostró el mensaje de éxito al generar cobranza");
    }

    @Test
    public void testPE2E_10_RegistroYConsultaRecibo() {
        realizarLogin();
        
        logPaso("Navegando a Recibos");
        RecibosPage recibosPage = new RecibosPage(driver);
        recibosPage.irARecibos();
        
        logPaso("Iniciando nuevo recibo");
        recibosPage.iniciarNuevoRecibo();

        logPaso("Registrando recibo");
        recibosPage.registrarRecibo("Cotizacion E2E", "100", "Efectivo");

        boolean exito = recibosPage.isMensajeExitoMostrado();
        if (exito) {
            logOk("Recibo registrado exitosamente");
            tomarCaptura("PE2E-10_recibo_registrado", false);
        } else {
            tomarCaptura("PE2E-10_error_recibo", true);
        }
        Assertions.assertTrue(exito, "No se mostró el mensaje de éxito al registrar recibo");
    }

    @Test
    public void testPE2E_11_ValidacionReciboInvalido() {
        realizarLogin();
        
        RecibosPage recibosPage = new RecibosPage(driver);
        recibosPage.irARecibos();
        recibosPage.iniciarNuevoRecibo();

        logPaso("Intentando registrar recibo incompleto (sin monto)");
        recibosPage.registrarRecibo("Cotizacion E2E", "", "Efectivo");

        boolean guardadoFallido = !recibosPage.isMensajeExitoMostrado();
        
        if (guardadoFallido) {
            logOk("El sistema previno el registro de un recibo inválido");
            tomarCaptura("PE2E-11_validacion_recibo", false);
        } else {
            tomarCaptura("PE2E-11_error_permitio_recibo_invalido", true);
        }
        
        Assertions.assertTrue(guardadoFallido, "Se permitió registrar un recibo sin monto");
    }
}
