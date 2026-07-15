package tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.CotizacionesPage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

public class CotizacionesE2ETest extends BaseTest {

    private void realizarLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("TEST_USER"), ConfigReader.get("TEST_PASSWORD"));
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assertions.assertTrue(dashboardPage.isDashboardLoaded(), "Login fallido");
    }

    @Test
    public void testPE2E_05_ElaboracionYConsultaCotizacion() {
        realizarLogin();
        
        logPaso("Navegando a Cotizaciones");
        CotizacionesPage cotizacionesPage = new CotizacionesPage(driver);
        cotizacionesPage.irACotizaciones();
        
        logPaso("Iniciando nueva cotización");
        cotizacionesPage.iniciarNuevaCotizacion();

        // Nota: En una prueba E2E completa, idealmente creamos el cliente antes en la misma prueba
        // o asumimos que ya existe. Para este test, usaremos un cliente base.
        cotizacionesPage.seleccionarCliente("Cliente E2E");

        logPaso("Agregando servicio: Cantidad 2, Precio 500");
        cotizacionesPage.agregarServicio("2", "500");

        // El total esperado debería ser 2 * 500 = 1000
        String totalMostrado = cotizacionesPage.obtenerTotal();
        Assertions.assertTrue(totalMostrado.contains("1000") || totalMostrado.contains("1,000"), "El total calculado no es correcto. Mostrado: " + totalMostrado);

        logPaso("Guardando cotización");
        cotizacionesPage.guardar();

        boolean exito = cotizacionesPage.isMensajeExitoMostrado();
        if (exito) {
            logOk("Cotización guardada exitosamente");
            tomarCaptura("PE2E-05_cotizacion_registrada", false);
        } else {
            tomarCaptura("PE2E-05_error_cotizacion", true);
        }
        Assertions.assertTrue(exito, "No se mostró el mensaje de éxito");
    }

    @Test
    public void testPE2E_06_ValidacionCotizacionIncompleta() {
        realizarLogin();
        
        CotizacionesPage cotizacionesPage = new CotizacionesPage(driver);
        cotizacionesPage.irACotizaciones();
        cotizacionesPage.iniciarNuevaCotizacion();

        logPaso("Intentando guardar sin cliente ni servicios");
        cotizacionesPage.guardar();

        boolean guardadoFallido = !cotizacionesPage.isMensajeExitoMostrado();
        
        if (guardadoFallido) {
            logOk("Sistema previno guardar cotización incompleta");
            tomarCaptura("PE2E-06_validacion_cotizacion", false);
        } else {
            tomarCaptura("PE2E-06_error_permitio_guardar_cot", true);
        }
        
        Assertions.assertTrue(guardadoFallido, "Se permitió guardar una cotización vacía");
    }
}
