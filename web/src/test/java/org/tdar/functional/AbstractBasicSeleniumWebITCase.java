package org.tdar.functional;

import java.io.IOException;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;
import org.tdar.functional.util.WebElementSelection;

public class AbstractBasicSeleniumWebITCase extends AbstractSeleniumWebITCase {

    public void applySelectizeOrNormal(String key, String value) {
        try {
            WebElementSelection find = find(By.name(key));
            find.val(value);
        } catch (InvalidElementStateException e) {
            try {
            String format = String.format("input[selectizeFor='%s']", key);
            logger.debug("\tFIND TO SET: {}", format);
            WebElementSelection find = find(By.cssSelector(format));
            find.val(value);
            find.sendKeys(Keys.RETURN);
            } catch (InvalidElementStateException ee) {

            }
        }
    }

    
    @Before
    public void runBefore() throws IOException {
        super.beforeTest();
        login();
    }

    @Override
    public void login() {
        setScreenshotsAllowed(false);
        if (testRequiresLucene()) {
            super.reindexOnce();
        }
        super.login();
        setIgnoreModals(false);
        setScreenshotsAllowed(true);
    }

    @Override
    public void logout() {
        // if we're shutting things down after aborted/failed test, don't bug me with formnavigate popups
        setIgnoreModals(true);
        setScreenshotsAllowed(false);
        // the test is over, so screenshots at this point aren't helpful
        super.logout();
    }

}
