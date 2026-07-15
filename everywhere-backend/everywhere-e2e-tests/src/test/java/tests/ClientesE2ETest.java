package tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.ClientesPage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

public class ClientesE2ETest extends BaseTest {

    private void realizarLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.get("TEST_USER"), ConfigReader.get("TEST_PASSWORD"));
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assertions.assertTrue(dashboardPage.isDashboardLoaded(), "Login fallido");
    }

    @Test
    public void testPE2E_03_RegistroYConsultaDeCliente() {
        realizarLogin();
        
        logPaso("Navegando a la sección de Clientes");
        ClientesPage clientesPage = new ClientesPage(driver);
        clientesPage.irAClientes();

        logPaso("Iniciando creación de nuevo cliente");
        clientesPage.iniciarNuevoCliente();

        String idUnico = generarIdUnico();
        String nombres = "Cliente E2E " + idUnico;
        String documento = "Doc" + idUnico;
        String correo = "e2e." + idUnico + "@example.com";

        logPaso("Llenando formulario con datos: " + nombres);
        clientesPage.llenarFormulario(nombres, "Prueba", documento, correo);
        clientesPage.guardar();

        boolean exito = clientesPage.isMensajeExitoMostrado();
        if (exito) {
            logOk("Mensaje de éxito mostrado");
            tomarCaptura("PE2E-03_cliente_registrado", false);
        } else {
            tomarCaptura("PE2E-03_error_registro", true);
        }
        Assertions.assertTrue(exito, "No se mostró el mensaje de éxito al registrar cliente");

        logPaso("Buscando el cliente en el listado");
        clientesPage.buscarCliente(documento);
        boolean encontrado = clientesPage.isClienteEnListado(documento);
        
        if (encontrado) {
            logOk("Cliente encontrado en el listado");
        } else {
            tomarCaptura("PE2E-03_cliente_no_encontrado", true);
        }
        
        Assertions.assertTrue(encontrado, "El cliente registrado no aparece en el listado");
    }

    @Test
    public void testPE2E_04_ValidacionClienteDatosIncompletos() {
        realizarLogin();
        
        ClientesPage clientesPage = new ClientesPage(driver);
        clientesPage.irAClientes();
        clientesPage.iniciarNuevoCliente();

        logPaso("Intentando guardar formulario vacío");
        clientesPage.guardar();

        boolean guardadoFallido = !clientesPage.isMensajeExitoMostrado();
        
        if (guardadoFallido) {
            logOk("El sistema no permitió guardar un cliente sin datos");
            tomarCaptura("PE2E-04_validacion_incompleto", false);
        } else {
            tomarCaptura("PE2E-04_error_permitio_guardar", true);
        }
        
        Assertions.assertTrue(guardadoFallido, "El sistema permitió guardar un cliente sin campos obligatorios");
    }
}
