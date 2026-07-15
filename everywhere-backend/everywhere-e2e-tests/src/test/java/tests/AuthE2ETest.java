package tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;

public class AuthE2ETest extends BaseTest {

    @Test
    public void testPE2E_01_InicioSesionValido() {
        logPaso("Navegando a la página de login");
        LoginPage loginPage = new LoginPage(driver);

        logPaso("Ingresando credenciales válidas");
        String user = ConfigReader.get("TEST_USER");
        String pass = ConfigReader.get("TEST_PASSWORD");
        loginPage.login(user, pass);

        DashboardPage dashboardPage = new DashboardPage(driver);
        boolean isDashboardLoaded = dashboardPage.isDashboardLoaded();
        
        if (isDashboardLoaded) {
            logOk("Sesión iniciada correctamente y redirigido al dashboard");
            tomarCaptura("PE2E-01_login_exitoso", false);
        } else {
            tomarCaptura("PE2E-01_login_fallido", true);
        }
        
        Assertions.assertTrue(isDashboardLoaded, "No se pudo iniciar sesión o no se cargó el dashboard");
    }

    @Test
    public void testPE2E_02_InicioSesionInvalido() {
        logPaso("Navegando a la página de login");
        LoginPage loginPage = new LoginPage(driver);

        logPaso("Ingresando credenciales inválidas");
        loginPage.login("usuario_invalido@gmail.com", "claveIncorrecta123");

        boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();
        
        if (isErrorDisplayed) {
            logOk("Se mostró el mensaje de error esperado");
            tomarCaptura("PE2E-02_login_error_esperado", false);
        } else {
            tomarCaptura("PE2E-02_login_sin_error", true);
        }

        Assertions.assertTrue(isErrorDisplayed, "No se mostró el mensaje de error para credenciales inválidas");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/auth/login"), "El sistema redirigió a otra página a pesar de credenciales inválidas");
    }

    @Test
    public void testPE2E_15_ProteccionDeRutas() {
        logPaso("Intentando acceder a una ruta protegida sin sesión");
        String baseUrl = ConfigReader.get("BASE_URL");
        driver.get(baseUrl + "/dashboard");

        logPaso("Verificando que redirija al login");
        wait.until(d -> d.getCurrentUrl().contains("/auth/login"));
        
        boolean redirigido = driver.getCurrentUrl().contains("/auth/login");
        
        if(redirigido) {
            logOk("El usuario fue redirigido al login correctamente");
        } else {
            tomarCaptura("PE2E-15_falla_proteccion", true);
        }
        
        Assertions.assertTrue(redirigido, "No se redirigió al login al intentar acceder a ruta protegida");
    }
}
