package karkanis.proyecto.amazon.prime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.gargoylesoftware.htmlunit.javascript.host.Console;
import com.google.common.io.Files;

public class AppAmazonPrime {
	
	private static String ubicacionGuardadoDeArchivo = "C:\\Users\\Public\\Documents\\nueva-cuenta-Amazon-Prime.txt";
	
    public static void main( String[] args ) throws InterruptedException, IOException {
    	// Arranca Chrome
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
	
		// generar identidad falsa
		driver.get("https://es.fakenamegenerator.com/");
		String nombre = driver.findElement(By.xpath("//*[@id=\'details\']/div[2]/div[2]/div/div[1]/h3")).getText();
		String telefono = driver.findElement(By.xpath("//*[@id=\'details\']/div[2]/div[2]/div/div[2]/dl[4]/dd")).getText();
		// función para generar contraseñas
		String password = generarPassword();
		
		// cuenta de mail falsa
		driver.get("https://anonbox.net/es/");
		String mail = driver.findElement(By.xpath("//*[@id='content']/dl/dd[2]/p")).getText();
		String accesoMail = driver.findElement(By.xpath("//*[@id='content']/dl/dd[3]/p/a")).getText();
		
		// tarjeta falsa
		driver.get("http://virusteamdlg.com/gen/");
		// !!! OJO, BUSCAR ESTE NUMERO DE BIN!!! @_@ !!!
		driver.findElement(By.xpath("//*[@id='ccpN']")).sendKeys("434769xxxxxxxxxx");
		driver.findElement(By.id("generar")).click();
		WebElement variosNumTarjeta = driver.findElement(By.id("output2"));
		String numTarjetaADividir = variosNumTarjeta.getAttribute("value");
		String[] dividorDeString = numTarjetaADividir.split("\n");
		String numTarjeta = dividorDeString[0]; // saco el primer string
		// genero fechas de la tarjeta
		Random random = new Random();
		// mes aleatorio
		String randomMes = Integer.toString(random.nextInt(12));
		// año aleatorio del próximo hasta 7 más 
		Calendar cal= Calendar.getInstance();
		int anioActual= cal.get(Calendar.YEAR);
		String anioTarjeta = Integer.toString(anioActual + random.nextInt(6)+1);		

		// Amazon sign up
		driver.get("https://amzn.to/1qk7s2Y");		
		driver.findElement(By.id("ap_customer_name")).sendKeys(nombre);
		driver.findElement(By.id("ap_email")).sendKeys(mail);
		driver.findElement(By.id("ap_password")).sendKeys(password);
		driver.findElement(By.id("ap_password_check")).sendKeys(password);
		driver.findElement(By.id("continue")).click();
    	
		// Update a cuenta Amazon Prime
		driver.get("https://www.amazon.com/gp/prime/pipeline/membersignup");
		driver.findElement(By.name("ppw-accountHolderName")).sendKeys(nombre);
		driver.findElement(By.name("addCreditCardNumber")).sendKeys(numTarjeta);
		new Select (driver.findElement(By.name("ppw-expirationDate_month"))).selectByValue(randomMes);
		new Select (driver.findElement(By.name("ppw-expirationDate_year"))).selectByValue(anioTarjeta);
		driver.findElement(By.name("ppw-widgetEvent:AddCreditCardEvent")).click();
		// si está correcto, aparecen más inputs
		// !!! OJO, BUSCAR ESTOS NUMEROS DONDE SE CONSIGUE EL BIN!!! @_@ !!!
		// <! DESDE ACÁ
		// Direccion linea 1
		TimeUnit.SECONDS.sleep(5);
		driver.findElement(By.name("ppw-line1")).sendKeys("6505 TH AVENUE");
		// Ciudad
		driver.findElement(By.name("ppw-city")).sendKeys("New York");
		// Estado
		driver.findElement(By.name("ppw-stateOrRegion")).sendKeys("New York");
		// Código postal
		driver.findElement(By.name("ppw-postalCode")).sendKeys("10080");
		// HASTA ACÁ -->
		// Telefono
		driver.findElement(By.name("ppw-phoneNumber")).sendKeys(telefono);
		// Botón Use this address		
		driver.findElement(By.name("ppw-widgetEvent:AddAddressEvent")).click();
		// Botón Aceptar
		TimeUnit.SECONDS.sleep(5);
		driver.findElement(By.xpath("//*[@id=\'a-autoid-0\']/span/input")).click();
		
		// Alert con los datos de acceso
		JOptionPane.showMessageDialog(null, "¡Tu cuenta ha sido creada con éxito! Estos son tu datos de ingreso:\n" + "E-mail: " + mail + "\n" + "Contraseña: " + password + "\n" + "Acceso webmail: " + accesoMail);
		
		// Creación de archivo txt con los datos de acceso
				// ruta del archivo del cual quiero saber sus propiedades
		// get directorio primero -bin, etc.- a eso se concatena la barra y se crea el archivo

		File file = new File(ubicacionGuardadoDeArchivo);
				
		BufferedWriter writer = null;
		
        if(file.exists()) {
        	writer = new BufferedWriter(new FileWriter(file));
        	writer.write("El fichero de texto ya estaba creado.");
        } else {
        	file.createNewFile();
        	writer = new BufferedWriter(new FileWriter(file));
        	writer.write("Acabo de crear el fichero de texto.");
        }
		
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("Datos de tu cuenta de Amazon Prime, ingresá a través de https://www.primevideo.com/\n" + "E-mail: " + mail + "\n" + "Contraseña: " + password + "\n" + "Acceso webmail: " + accesoMail + "\nLa cuenta dejará de funcionar en 30 dias, reutilizá este programa para crear una nueva.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
            	// cierra la escritura del archivo
                writer.close();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }    	
    }
    
    private static String generarPassword() {
    	String pass = "";
    	Random r = new Random();
    	String abc = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVGBNM1234567890@´.;:/*-+!º$*_:;^*%&/()=?¿Ç";
    	
    	for(int i = 0; i < 12; i++) {
    		pass += abc.charAt(r.nextInt(abc.length()));
    	}
    	
    	return pass;
    }
}
