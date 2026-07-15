# EveryWhere Travel - Pruebas End-to-End (E2E)

Este proyecto contiene las pruebas automatizadas End-to-End para el sistema EveryWhere Travel, utilizando Java 21, Selenium WebDriver y JUnit 5, basadas en el patrón de diseño Page Object Model (POM).

## Requisitos Previos

- Java 21 instalado (`java -version`).
- Maven instalado (`mvn -version`).
- Google Chrome instalado.
- Backend y Frontend de EveryWhere Travel en ejecución.

## Configuración

Antes de ejecutar las pruebas, asegúrate de configurar las variables de entorno o crear un archivo `.env` en la raíz de este proyecto (`everywhere-e2e-tests`).

Puedes copiar el archivo de ejemplo:
```bash
cp .env.example .env
```

Variables disponibles en el `.env`:
- `BASE_URL`: URL del frontend (ej. http://localhost:4200)
- `BROWSER`: chrome
- `HEADLESS`: false (cambiar a true para ejecución sin interfaz gráfica)
- `TIMEOUT_SECONDS`: 15
- `TEST_USER`: Correo del usuario administrador
- `TEST_PASSWORD`: Contraseña del usuario administrador
- `TEST_EMAIL_DESTINATION`: Correo destino para las pruebas de envío de email

> **Nota:** El archivo `.env` está excluido del control de versiones (añadir a `.gitignore`) por seguridad.

## Ejecución de las Pruebas

Para ejecutar todas las pruebas desde la terminal:

```bash
mvn clean test
```

Para ejecutar una prueba específica (por ejemplo, el flujo completo de ventas):

```bash
mvn -Dtest=ProcesoVentaE2ETest test
```

Para ejecutar las pruebas en modo oculto (headless):

```bash
mvn clean test -Dheadless=true
```

## Reportes y Evidencias

- **Reporte HTML**: Al finalizar la ejecución, se generará un reporte detallado en `reportes/ExtentReport.html`.
- **Capturas de pantalla**: Las capturas de éxito se guardan en `evidencias/` y las de fallos en `evidencias/errores/`.

## Estructura del Proyecto

- `src/main/java/pages`: Contiene los Page Objects de cada vista del sistema.
- `src/test/java/base`: Configuración base (Driver, esperas, capturas).
- `src/test/java/utils`: Utilidades (Lectura de config, ExtentReports).
- `src/test/java/tests`: Clases de pruebas divididas por módulo.
