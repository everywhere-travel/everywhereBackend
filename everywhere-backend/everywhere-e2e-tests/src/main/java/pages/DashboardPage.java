package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    @FindBy(css = ".dashboard-layout, .dashboard-main, app-dashboard-header")
    private WebElement navigationMenu;

    @FindBy(xpath = "//*[contains(text(), 'Cerrar sesión') or contains(text(), 'Logout') or contains(text(), 'Salir')]")
    private WebElement logoutButton;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDashboardLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("/dashboard"));
            return wait.until(ExpectedConditions.visibilityOf(navigationMenu)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
    }
}
