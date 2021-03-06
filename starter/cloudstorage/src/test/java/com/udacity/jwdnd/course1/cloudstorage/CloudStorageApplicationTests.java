package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.close();
			driver.quit();
		}
	}

	//test that verifies that an unauthorized user can only access login and signup pages.
	@Test
	public void unauthorizedUserAccessibleRoutes(){
		driver.get("http://localhost:" + this.port + "/home");
		Assertions.assertEquals("Login", driver.getTitle());

		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());

		driver.get("http://localhost:" + this.port + "/signup");
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}

	/*test that signs up a new user, logs in, verifies that the home page is accessible, logs out,
	and verifies that the home page is no longer accessible.
	 */
	@Test
	public void testUserSignupLoginLogout() throws InterruptedException {
		//signup
		doMockSignUp("firstName", "lastName", "test", "12345");
		//login
		doLogIn("test", "12345");
		//logout
		WebElement logoutButton = driver.findElement(By.id("logout-button"));
		logoutButton.click();

		Assertions.assertFalse(driver.getTitle().equals("Home"));
		Assertions.assertEquals("Login", driver.getTitle());

		Thread.sleep(3000);
	}

	//test that creates a note, and verifies it is displayed.
	public void redirectToNotesTab(){
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		driver.get("http://localhost:" + this.port + "/home");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
		driver.findElement(By.id("nav-notes-tab")).click();
	}
	@Test
	public void createNote() throws InterruptedException {
		// signup the user
		doMockSignUp("firstName", "lastName", "test", "12345");

		// login the user
		doLogIn("test", "12345");

		// go to note-tab
		WebElement notesTab= driver.findElement(By.id("nav-notes-tab"));
		notesTab.click();

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		// verify note-tab appears
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes")));
		Assertions.assertTrue(driver.findElement(By.id("nav-notes")).isDisplayed());

		// press on add note button
		WebElement addNoteButton= driver.findElement(By.id("add-note-button"));
		addNoteButton.click();

		// Fill out the note
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
		WebElement inputTitle = driver.findElement(By.id("note-title"));
		inputTitle.click();
		inputTitle.sendKeys("Test Note");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
		WebElement inputDescription = driver.findElement(By.id("note-description"));
		inputDescription.click();
		inputDescription.sendKeys("testing a note ...");

		// Attempt ot submit the note
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submit-note-button")));
		WebElement submitNote = driver.findElement(By.id("submit-note-button"));
		submitNote.click();

		// Redirect to home page & press on notes tab
		redirectToNotesTab();

		// Check if the note appears
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-note-title")).getText().contains("Test Note"));


		Thread.sleep(3000);

	}

	//test that edits an existing note and verifies that the changes are displayed
	@Test
	public void editNote() throws InterruptedException {
		createNote();

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		// open edit modal
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-note-button")));
		WebElement editNote = driver.findElement(By.id("edit-note-button"));
		editNote.click();

		// Edit the note description
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
		WebElement inputDescription = driver.findElement(By.id("note-description"));
		inputDescription.click();
		inputDescription.clear();
		inputDescription.sendKeys("edited description ...");

		// save changes
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submit-note-button")));
		WebElement submitNote = driver.findElement(By.id("submit-note-button"));
		submitNote.click();

		// Redirect to home page & press on notes tab
		redirectToNotesTab();

		// Check if the note description has changed
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-note-description")).getText().contains("edited description"));

	}

	//test that deletes a note and verifies that the note is no longer displayed.
	@Test
	public void deleteNote() throws InterruptedException {

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		createNote();

		// press on delete button
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delete-note-button")));
		WebElement deleteNote = driver.findElement(By.id("delete-note-button"));
		deleteNote.click();

		// Redirect to home page & press on notes tab
		redirectToNotesTab();

		// check note is deleted
		WebElement notesTable = driver.findElement(By.id("userTable"));
		List<WebElement> notesList = notesTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(0, notesList.size());
	}

	//test that creates a set of credentials, verifies that they are displayed, and verifies that the displayed password is encrypted.
	public void redirectToCredentialsTab(){
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		driver.get("http://localhost:" + this.port + "/home");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));
		driver.findElement(By.id("nav-credentials-tab")).click();
	}

	@Test
	public void createCredential() throws InterruptedException {
		// signup the user
		doMockSignUp("firstName", "lastName", "test", "12345");

		// login the user
		doLogIn("test", "12345");

		// go to credentials-tab
		WebElement credentialsTab= driver.findElement(By.id("nav-credentials-tab"));
		credentialsTab.click();

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String inputCredentialPassword = "12345";

		// press on add credentials button
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-credentials-button")));
		WebElement addCredentialsButton= driver.findElement(By.id("add-credentials-button"));
		addCredentialsButton.click();

		// Fill out the credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
		WebElement inputURL = driver.findElement(By.id("credential-url"));
		inputURL.click();
		inputURL.sendKeys("https://www.google.com/");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
		WebElement inputUsername = driver.findElement(By.id("credential-username"));
		inputUsername.click();
		inputUsername.sendKeys("firstName");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
		WebElement inputPassword = driver.findElement(By.id("credential-password"));
		inputPassword.click();
		inputPassword.sendKeys(inputCredentialPassword);

		// Attempt to submit the credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submit-credential-button")));
		WebElement submitNote = driver.findElement(By.id("submit-credential-button"));
		submitNote.click();

		// Redirect to home page & press on credentials tab
		redirectToCredentialsTab();

		// Check if the credentials appears
		WebElement credentialsTable = driver.findElement(By.id("credentialTable"));
		List<WebElement> credList = credentialsTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(1, credList.size());


		// Check if the password shown in table is encrypted or not equal to the original password
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
		Assertions.assertNotEquals(driver.findElement(By.id("table-cred-password")).getText(), inputCredentialPassword);

		Thread.sleep(3000);
	}

	/* test that views an existing set of credentials, verifies that the viewable password is unencrypted, edits the credentials
	*  and verifies that the changes are displayed */
	@Test
	public void editCredentials() throws InterruptedException {
		createCredential();

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		// press on edit
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-credential-button")));
		WebElement editCredentialsButton= driver.findElement(By.id("edit-credential-button"));
		editCredentialsButton.click();

		// make changes
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
		WebElement inputURL = driver.findElement(By.id("credential-url"));
		inputURL.click();
		inputURL.clear();
		inputURL.sendKeys("https://github.com/BabyDeOne");

		// get the unencrypted pwd
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
		String inputPassword = driver.findElement(By.id("credential-password")).getAttribute("value");


		// Attempt ot submit the changes in the credential
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submit-credential-button")));
		WebElement submitCredential = driver.findElement(By.id("submit-credential-button"));
		submitCredential.click();

		// Redirect to home page & press on credentials tab
		redirectToCredentialsTab();

		// check changes
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
		Assertions.assertTrue(driver.findElement(By.id("table-cred-url")).getText().contains("https://github.com/BabyDeOne"));

		// Verify that the viewable password is unencrypted
		Assertions.assertNotEquals(driver.findElement(By.id("table-cred-password")).getText(), inputPassword);

		Thread.sleep(3000);

	}

	//test that delets an existing set of credentials and verifies that the credentials are no longer displayed.
	@Test
	public void deleteCredentials() throws InterruptedException {
		createCredential();

		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		// press on edit
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("delete-credential-button")));
		WebElement deleteCredentialsButton= driver.findElement(By.id("delete-credential-button"));
		deleteCredentialsButton.click();

		// Redirect to home page & press on credentials tab
		redirectToCredentialsTab();

		// check the credential is deleted
		WebElement credentialTable = driver.findElement(By.id("credentialTable"));
		List<WebElement> credList = credentialTable.findElements(By.tagName("tbody"));

		Assertions.assertEquals(0, credList.size());

		Thread.sleep(3000);
	}
	@Test
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doMockSignUp(String firstName, String lastName, String userName, String password){
		// Create a dummy account for logging in later.

		// Visit the sign-up page.
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		driver.get("http://localhost:" + this.port + "/signup");
		webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));
		
		// Fill out credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.click();
		inputFirstName.sendKeys(firstName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.click();
		inputLastName.sendKeys(lastName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.click();
		inputUsername.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.click();
		inputPassword.sendKeys(password);

		// Attempt to sign up.
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
		WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
		buttonSignUp.click();

		/* Check that the sign up was successful. 
		// You may have to modify the element "success-msg" and the sign-up 
		// success message below depening on the rest of your code.
		*/
		//Assertions.assertTrue(driver.findElement(By.id("success-msg")).getText().contains("You successfully signed up!"));
	}

	
	
	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doLogIn(String userName, String password)
	{
		// Log in to our dummy account.
		driver.get("http://localhost:" + this.port + "/login");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement loginUserName = driver.findElement(By.id("inputUsername"));
		loginUserName.click();
		loginUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement loginPassword = driver.findElement(By.id("inputPassword"));
		loginPassword.click();
		loginPassword.sendKeys(password);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
		WebElement loginButton = driver.findElement(By.id("login-button"));
		loginButton.click();

		webDriverWait.until(ExpectedConditions.titleContains("Home"));

	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling redirecting users 
	 * back to the login page after a succesful sign up.
	 * Read more about the requirement in the rubric: 
	 * https://review.udacity.com/#!/rubrics/2724/view 
	 */
	@Test
	public void testRedirection() {
		// Create a test account

		doMockSignUp("firstName", "lastName", "test", "12345");

		
		// Check if we have been redirected to the log in page.
		Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling bad URLs 
	 * gracefully, for example with a custom error page.
	 * 
	 * Read more about custom error pages at: 
	 * https://attacomsian.com/blog/spring-boot-custom-error-page#displaying-custom-error-page
	 */
	@Test
	public void testBadUrl()  {
		// Create a test account

		doMockSignUp("firstName", "lastName", "test", "12345");
		doLogIn("test", "12345");
		
		// Try to access a random made-up URL.
		driver.get("http://localhost:" + this.port + "/some-random-page");
		Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));


	}


	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the 
	 * rest of your code. 
	 * This test is provided by Udacity to perform some basic sanity testing of 
	 * your code to ensure that it meets certain rubric criteria. 
	 * 
	 * If this test is failing, please ensure that you are handling uploading large files (>1MB),
	 * gracefully in your code. 
	 * 
	 * Read more about file size limits here: 
	 * https://spring.io/guides/gs/uploading-files/ under the "Tuning File Upload Limits" section.
	 */
	@Test
	public void testLargeUpload() {
		// Create a test account
		doMockSignUp("firstName", "lastName", "test", "12345");
		doLogIn("test", "12345");

		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String filename = "upload5m.zip";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
		WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
		fileSelectButton.sendKeys(new File(filename).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("uploadButton"));
		uploadButton.click();
		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Large File upload failed");
		}
		Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403 ??? Forbidden"));

	}



}
