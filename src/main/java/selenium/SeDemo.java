package selenium;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: luqiwei
 * @Date: 2018/9/13 18:13
 */
public class SeDemo {

    WebDriver driver;
    WebDriver.Navigation navigation;
    WebDriverWait wait;
    String loginUrl = "https://c2staging.hairongyi.com/member/login";
    String bidUrl = "https://c2staging.hairongyi.com/bid/2857";
    String username = "13912340001";
    String password = "qweasd123";
    String passwordPay = "123456";
    String superVerifyCode = "Ed%8r5";

    @BeforeClass
    public void init() {
        System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        navigation = driver.navigate();
        wait = new WebDriverWait(driver, 30);
    }

    @AfterTest
    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void login() {
        navigation.to(loginUrl);
        WebElement loginName = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginName")));
        loginName.clear();
        loginName.sendKeys(username);
        WebElement unsafePassword = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("unsafePassword")));
        unsafePassword.clear();
        unsafePassword.sendKeys(password);
        WebElement verifyCode = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("verifyCode")));
        verifyCode.clear();
        verifyCode.sendKeys(superVerifyCode);
        driver.findElement(By.xpath("//*[@id=\"myform\"]/div[5]/a")).click();

        //登录成功返回首页
        wait.until(ExpectedConditions.titleIs("首页 | 海融易"));
        int i = 0;
        while (i < 1000) {
            long start = System.currentTimeMillis();
            System.out.println("-----------------------------------------------");
            System.out.println("---开始第 " + i + " 次出借");
            bid();
            long end = System.currentTimeMillis();
            long sub = end - start;
            System.out.println("---本次测试耗时: " + sub + " ms");
            i++;
        }
    }


    public void bid() {
        //导航到标的出借页面
        navigation.to(bidUrl);
        WebElement loanBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("立即出借")));
        WebElement input = driver.findElement(By.xpath("/html/body/div/div[2]/div/div[2]/div[2]/div/div[2]/div/div[1]/input"));
        input.clear();
        //随机出借金额[1,6),即1-5块随机
        Integer i = RandomUtils.nextInt(1, 6);

        input.sendKeys(i + "");
        loanBtn.click();

        //出借确认
        WebElement confirm = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("确认支付")));
        confirm.click();
        System.out.println("---本次出借金额: " + i);

        //恒丰网关支付
        while (true) {
            Set<String> windowHandles = driver.getWindowHandles();
            if(windowHandles.size()<2){
                continue;
            }
            List<String> it = new ArrayList<String>(windowHandles);
            driver.switchTo().window(it.get(1));
            WebElement password = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
            password.sendKeys(passwordPay);
            WebElement nextButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nextButton")));
            nextButton.click();
            driver.close();
            driver.switchTo().window(it.get(0));
            break;
        }
    }
}
