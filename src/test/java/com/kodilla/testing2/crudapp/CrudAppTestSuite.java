package com.kodilla.testing2.crudapp;

import com.kodilla.testing2.google.config.WebDriverConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class CrudAppTestSuite {
    private static final String BASE_URL = "http://paulina-bukowska.github.io/";
    private WebDriver driver;
    private Random generator;

    @Before
    public void initTests() {
        driver = WebDriverConfig.getDriver(WebDriverConfig.FIREFOX);
        driver.get(BASE_URL);
        generator = new Random();
    }

    @After
    public void cleanUpAfterTest() {
        driver.close();
    }

    private String createCrudAppTestTask() throws InterruptedException {
        final String XPATH_TASK_NAME = "//form[contains(@action, \"createTask\")]/fieldset[1]/input";
        final String XPATH_TASK_CONTENT = "//form[contains(@action, \"createTask\")]/fieldset[2]/textarea";
        final String XPATH_ADD_BUTTON = "//form[contains(@action, \"createTask\")]/fieldset[3]/button";
        String taskName = "Task number " + generator.nextInt(100000);
        String taskContent = taskName + " content";

        WebElement name = driver.findElement(By.xpath(XPATH_TASK_NAME));
        name.sendKeys(taskName);

        WebElement content = driver.findElement(By.xpath(XPATH_TASK_CONTENT));
        content.sendKeys(taskContent);

        WebElement addButton = driver.findElement(By.xpath(XPATH_ADD_BUTTON));
        addButton.click();
        Thread.sleep(2000);

        return taskName;
    }

    private void deleteCrudAppTestTask(String taskName) throws InterruptedException {
        // to click the "OK" button in an alert box
        driver.switchTo().alert().accept();

        driver.findElements(By.xpath("//form[@class=\"datatable__row\"]")).stream()
                .filter(anyForm -> anyForm.findElement(By.xpath(".//p[@class=\"datatable__field-value\"]")).getText().equals(taskName))
                .forEach(theForm -> {
                                    WebElement element = theForm.findElement(By.xpath(".//button[4]"));
                                    element.click();
                });
    }

    private void sendTestTaskToTrello(String taskName) throws InterruptedException {
        // odświeżamy stronę, aby pojawiło się na niej zadanie dodane w metodzie createCrudAppTestTask()
        driver.navigate().refresh();

        //  oczekiwanie aż załadują się dane "dociągane" przy pomocy technologii AJAX;
        // oczekujemy aż pojawi się na stronie jakiekolwiek pole typu select (ComboBox służący do wyboru tablicy Trello)
        while(!driver.findElement(By.xpath("//select[1]")).isDisplayed());

        driver.findElements(By.xpath("//form[@class=\"datatable__row\"]")).stream()
                // funkcja filtrująca, która odfiltrowuje jedynie te tagi form, w których znajduje się
                // tag p zawierający tekst odpowiadający przekazanej nazwie zadania.
                // Wykorzystujemy tu mechanizm wyszukiwania elementów w kontekście innego elementu -
                // nasze wyrażenie XPath rozpoczyna się od kropki przed slashami, co oznacza wyszukiwanie w kontekście
                // aktualnego elementu (.//), zamiast w całej stronie WWW
                .filter(anyForm -> anyForm.findElement(By.xpath(".//p[@class=\"datatable__field-value\"]")).getText().equals(taskName))
                .forEach(theForm -> {
                    WebElement selectElement = theForm.findElement(By.xpath(".//select[1]"));
                    Select select = new Select(selectElement);
                    select.selectByIndex(1);

                    WebElement buttonCreateCard = theForm.findElement(By.xpath(".//button[contains(@class, \"card-creation\")]"));
                    buttonCreateCard.click();
                });
        // Technologia AJAX! - strona nie jest przeładowywana!
        // rozwiązanie - zastosowanie "sztywnego" timeoutu wynoszącego 5 sek
        Thread.sleep(5000);
    }

    private boolean checkTaskExistsInTrello(String taskName) throws InterruptedException {
        final String TRELLO_URL = "https://trello.com/login";
        boolean result = false;
        WebDriver driverTrello = WebDriverConfig.getDriver(WebDriverConfig.FIREFOX);
        driverTrello.get(TRELLO_URL);

        driverTrello.findElement(By.id("user")).sendKeys("paulina31893963");
        driverTrello.findElement(By.id("password")).sendKeys("kodilla_trello");
        driverTrello.findElement(By.id("login")).submit();

        Thread.sleep(3000);

        driverTrello.findElements(By.xpath("//a[@class=\"board-tile\"]")).stream()
                .filter(aHref -> aHref.findElements(By.xpath(".//span[@title=\"Kodilla Application\"]")).size() > 0)
                .forEach(aHref -> aHref.click());

        Thread.sleep(3000);

        result = driverTrello.findElements(By.xpath("//span")).stream()
                .filter(theSpan -> theSpan.getText().contains(taskName))
                .collect(Collectors.toList())
                .size() > 0;

        driverTrello.close();

        return result;
    }

    @Test
    public void shouldCreateTrelloCard() throws InterruptedException {
        String taskName = createCrudAppTestTask();
        sendTestTaskToTrello(taskName);
        deleteCrudAppTestTask(taskName);
        assertTrue(checkTaskExistsInTrello(taskName));
    }
}
