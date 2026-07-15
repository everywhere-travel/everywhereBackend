package tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.*;
import utils.ConfigReader;

public class ProcesoVentaE2ETest extends BaseTest {

    @Test
    public void testPE2E_13_ProcesoCompletoVenta() {
        logPaso("1. Iniciar sesión");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("TEST_USER"), ConfigReader.get("TEST_PASSWORD"));
        DashboardPage dashboardPage = new DashboardPage(driver);
        boolean isDashboard = dashboardPage.isDashboardLoaded();
        if (!isDashboard) {
            tomarCaptura("PE2E-13_login_fallido", true);
            if (loginPage.isErrorMessageDisplayed()) {
                logError("El frontend mostró un mensaje de error de credenciales.");
            }
        }
        Assertions.assertTrue(isDashboard, "Login fallido");

        String idUnico = generarIdUnico();
        String nombreCliente = "Cliente E2E " + idUnico;
        String documento = "Doc" + idUnico;
        String correo = "e2e." + idUnico + "@example.com";

        logPaso("2. Registrar un cliente único: " + nombreCliente);
        ClientesPage clientesPage = new ClientesPage(driver);
        clientesPage.irAClientes();
        clientesPage.iniciarNuevoCliente();
        clientesPage.llenarFormulario(nombreCliente, "Prueba", documento, correo);
        clientesPage.guardar();
        Assertions.assertTrue(clientesPage.isMensajeExitoMostrado(), "Fallo registro cliente");

        logPaso("3. Crear una cotización para ese cliente");
        CotizacionesPage cotizacionesPage = new CotizacionesPage(driver);
        cotizacionesPage.irACotizaciones();
        cotizacionesPage.iniciarNuevaCotizacion();
        cotizacionesPage.seleccionarCliente(nombreCliente);

        logPaso("4. Agregar servicios y verificar el total");
        cotizacionesPage.agregarServicio("1", "1500");
        String total = cotizacionesPage.obtenerTotal();
        Assertions.assertTrue(total.contains("1500") || total.contains("1,500"), "Total cotización incorrecto");
        cotizacionesPage.guardar();
        Assertions.assertTrue(cotizacionesPage.isMensajeExitoMostrado(), "Fallo registro cotización");

        logPaso("5. Crear una liquidación");
        LiquidacionesPage liquidacionesPage = new LiquidacionesPage(driver);
        liquidacionesPage.irALiquidaciones();
        liquidacionesPage.iniciarNuevaLiquidacion();
        liquidacionesPage.seleccionarCotizacionYProveedor(nombreCliente, "Proveedor Base");
        liquidacionesPage.agregarCosto("500");
        liquidacionesPage.guardar();
        Assertions.assertTrue(liquidacionesPage.isMensajeExitoMostrado(), "Fallo registro liquidación");

        logPaso("6. Generar un documento de cobranza");
        DocumentosCobranzaPage cobranzasPage = new DocumentosCobranzaPage(driver);
        cobranzasPage.irACobranzas();
        cobranzasPage.iniciarNuevoDocumento();
        cobranzasPage.generarDocumento(nombreCliente);
        Assertions.assertTrue(cobranzasPage.isMensajeExitoMostrado(), "Fallo documento de cobranza");

        logPaso("7. Registrar un recibo");
        RecibosPage recibosPage = new RecibosPage(driver);
        recibosPage.irARecibos();
        recibosPage.iniciarNuevoRecibo();
        recibosPage.registrarRecibo(nombreCliente, "1500", "Transferencia");
        Assertions.assertTrue(recibosPage.isMensajeExitoMostrado(), "Fallo registro recibo");

        logPaso("8. Enviar un documento por correo");
        cobranzasPage.irACobranzas();
        CorreoPage correoPage = new CorreoPage(driver);
        correoPage.abrirModalCorreo();
        correoPage.llenarYEnviar(ConfigReader.get("TEST_EMAIL_DESTINATION"), "Tu Cotización y Recibo", "Gracias por tu compra.");
        Assertions.assertTrue(correoPage.isMensajeExitoMostrado(), "Fallo envío correo");

        logOk("Proceso completo de venta ejecutado con éxito");
        tomarCaptura("PE2E-13_proceso_completo", false);
    }
}
