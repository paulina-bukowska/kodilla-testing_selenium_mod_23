package com.kodilla.testing2.facebook;

import com.kodilla.testing2.google.config.WebDriverConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class FacebookTestingApp {
    public static final String XPATH_WAIT_FOR = "//select[1]";
    public static final String XPATH_SELECT1 = "//div[contains(@class, \"_5k_5\")]/span/span/select[1]";
    public static final String XPATH_SELECT2 = "//div[contains(@class, \"_5k_5\")]/span/span/select[2]";
    public static final String XPATH_SELECT3 = "//div[contains(@class, \"_5k_5\")]/span/span/select[3]";


    public static void main(String[] args) {
        WebDriver driver = WebDriverConfig.getDriver(WebDriverConfig.FIREFOX);
        driver.get("https://www.facebook.com/");

        while (!driver.findElement(By.xpath(XPATH_WAIT_FOR)).isDisplayed());

        WebElement selectCombo1 = driver.findElement(By.xpath(XPATH_SELECT1));
        Select selectBoard1 = new Select(selectCombo1);
        selectBoard1.selectByIndex(14);

        WebElement selectCombo2 = driver.findElement(By.xpath(XPATH_SELECT2));
        Select selectBoard2 = new Select(selectCombo2);
        selectBoard2.selectByIndex(6);

        WebElement selectCombo3 = driver.findElement(By.xpath(XPATH_SELECT3));
        Select selectBoard3 = new Select(selectCombo3);
        selectBoard3.selectByIndex(29);
    }
}
