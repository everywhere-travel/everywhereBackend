package tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.DashboardPage;
import pages.LiquidacionesPage;
import pages.LoginPage;
import utils.ConfigReader;

public class LiquidacionesE2ETest extends BaseTest {

    private void realizarLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("TEST_USER"), ConfigReader.get("TEST_PASSWORD"));
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assertions.assertTrue(dashboardPage.isDashboardLoaded(), "Login fallido");
    }

    @Test
    public void testPE2E_08_GeneracionConsultaLiquidacion() {
        realizarLogin();
        
        logPaso("Navegando a Liquidaciones");
        LiquidacionesPage liquidacionesPage = new LiquidacionesPage(driver);
        liquidacionesPage.irALiquidaciones();
        
        logPaso("Iniciando nueva liquidación");
        liquidacionesPage.iniciarNuevaLiquidacion();

        logPaso("Seleccionando cotización y proveedor");
        liquidacionesPage.seleccionarCotizacionYProveedor("Cotizacion E2E", "Proveedor E2E");

        logPaso("Agregando costo");
        liquidacionesPage.agregarCosto("400");
        
        logPaso("Guardando liquidación");
        liquidacionesPage.guardar();

        boolean exito = liquidacionesPage.isMensajeExitoMostrado();
        if (exito) {
            logOk("Liquidación guardada exitosamente");
            tomarCaptura("PE2E-08_liquidacion_registrada", false);
        } else {
            tomarCaptura("PE2E-08_error_liquidacion", true);
        }
        Assertions.assertTrue(exito, "No se mostró el mensaje de éxito al registrar la liquidación");
    }
}
