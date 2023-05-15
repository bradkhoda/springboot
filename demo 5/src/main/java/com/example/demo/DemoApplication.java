package com.example.demo;

import java.io.Console;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;
import com.example.demo.Coins;

import com.example.demo.Coin;

import java.util.Set;

import javax.management.RuntimeErrorException;
import javax.websocket.Decoder.Binary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.json.*;

import java.util.ArrayList;

import java.sql.*;

enum MessageType {
	init, temperature, humidity, pressure
}


@SpringBootApplication
@RestController
public class DemoApplication {

	// Class Methods

	// Private methods
	private String parseHexBinary(String hex) {
		String digits = "0123456789ABCDEF";
  		hex = hex.toUpperCase();
		String binaryString = "";
		
		for(int i = 0; i < hex.length(); i++) {
			char c = hex.charAt(i);
			int d = digits.indexOf(c);
			if(d == 0)	binaryString += "0000"; 
			else  binaryString += Integer.toBinaryString(d);
		}
		return binaryString;
	}

	private String mysqlConnect(String password) {
		// This is COMPLETELY hardcoded atm.
		// What i want later is a modular function that
		// handles different connection types with enums

		String publicIP = "34.129.245.46";
		String privateIP = "172.27.0.5";
		String user = "root";
		
		String dbname = "bluetooth_rpi_database";

		
		// At the moment, only public IP connections are granted. Weirdly i cannot connect via internal networks, INTERNALLY/
		// please get the GKE clusters connected to internal netwrok.

		try {  
			Class.forName("com.mysql.cj.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://" + publicIP + ":3306/" + dbname ,user, password);  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("select * from entries;");  

			String allResults = "";
			while(rs.next()) { 
				String currentResult = rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3);
				allResults += currentResult + "\n";
				System.out.println(currentResult);

			}

			rs.close();
			con.close();

			return allResults;
			}
			catch(Exception e) {
				System.out.println(e);
				return e.getMessage();
			}

	}

	// Public methods
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/now")
	public String nowTime() {
		Date date=java.util.Calendar.getInstance().getTime();
		String dateString = date.toString();

		return dateString;
	}

	@GetMapping("/haha")
	public String haha() {
		return "haha";
	}

	@GetMapping("/trysql")
	public String trysql(@RequestParam(value = "pswd", defaultValue = "") String password) {
		return this.mysqlConnect(password);
	}

	@GetMapping("/insertsql")
	public String insertSQL(@RequestParam(value = "id", defaultValue = "") String deviceId, @RequestParam(value = "type", defaultValue = "") String messageType, @RequestParam(value = "val", defaultValue = "") String value, @RequestParam(value = "pswd", defaultValue = "") String password ) {
		/// Takes three inputs and makes heeby jeebies

		// Error handling
		if (deviceId == "" || messageType == "" || value == "" || password == "") {
			return "Wrong inputs";
		}
		else if(!(messageType.equalsIgnoreCase("temperature") || messageType.equalsIgnoreCase("pressure") || messageType.equalsIgnoreCase("humidity"))) {
			return "Cannot have that message type. Only acceptables are: Humidity, Temperature, and Pressure";
		}
		else {
			try {
				int temporaryValueInt = Integer.parseInt(value);
				int temporaryIdInt = Integer.parseInt(deviceId);
			}
			catch (Exception e) {
				return e.getLocalizedMessage();
			}

			// OK. We are good now.
			// Also, this integer casting protects against SQL injection.

			// Initiate the SQL sending

			// PLEASE for the love of god fix this copy paste cpode nonsenese

			String publicIP = "34.129.245.46";
			String privateIP = "172.27.0.5";
			String user = "root";
			String dbname = "bluetooth_rpi_database";
	
			
			// At the moment, only public IP connections are granted. Weirdly i cannot connect via internal networks, INTERNALLY/
			// please get the GKE clusters connected to internal netwrok.
	
			try {  
				Class.forName("com.mysql.cj.jdbc.Driver");  
				Connection con=DriverManager.getConnection(  
				"jdbc:mysql://" + publicIP + ":3306/" + dbname ,user ,password);  
				//here sonoo is database name, root is username and password  
				Statement stmt=con.createStatement();  
				Boolean rs = stmt.execute("insert into entries (deviceID, messageType, messageValue) values ('" + deviceId + "','" + messageType + "','" + value + "');");
				con.close();
	
				return String.valueOf(rs);
				}
				catch(Exception e) {
					System.out.println(e);
					return e.getMessage();
				}

		}

	}

	@GetMapping("/coinspot")
	public String coinspotCoins() {
		// Creating the URL

		try {
			URL url = new URL("https://www.coinspot.com.au/pubapi/v2/latest");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");

			conn.connect();

			// Get response code
			int response = conn.getResponseCode();

			// Handle request codes
			if (response != 200) {
				// Not OK
				System.out.println("What the hell is going on here;");
				return "Failed";
			
			}
			else {
				String inline = "";
				Scanner scanner = new Scanner(url.openStream());

				// Write JSON to inline ingest
				while (scanner.hasNext()) {
					inline += scanner.nextLine();
				}

			scanner.close();

			// Parse
			return inline;
		
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "REALLY TERRIBLY failed";
		
	}

	@GetMapping("/tmpsckt1")
	public String ingresString(@RequestParam(value = "binMsg", defaultValue = "0000000000000000") String binaryMsg) {

		// This method is supposed to take a msg and push it to an SQL server

		// This part is the decoding.
		
		// Error handling

		if (binaryMsg == "") {
			// move to an "Actually empty" function later
			return "Your message device sent nothing.";
		}
		if (binaryMsg.length() < 16) {
			// The message has to be 16 bits. Support for larger msgs coming soon
			return "Message needs to be 16 bits. Check with RPI portral.";
		}
		try {
			int temporaryInt = Integer.parseInt(binaryMsg, 2);
		}
		catch (Exception e) {
			// Do nothing
			return "You cannot input non-binary messages.";
		}

		// Good. Nothing to worry about.
		
		// Decode values from message
		String deviceIdString = binaryMsg.substring(0,4);
		String msgTypeString = binaryMsg.substring(4,8);
		String msgValueString = binaryMsg.substring(8,binaryMsg.length());

		// Map to Enums
		int msgTypeInt = Integer.parseInt(msgTypeString,2);
		MessageType messageType = MessageType.init;
		switch (msgTypeInt) {
			case 14: // 1110
				messageType = MessageType.temperature;
				break;
			case 12: //1100
				messageType = MessageType.humidity;
				break;
			case 8: //1000
				messageType = MessageType.pressure;
				break;
			default:
				break;
		}

		int decimalMsgValue = Integer.parseInt(msgValueString,2);


		// Just testing for now
		return ( "Your device id is: " + deviceIdString + "\nYour message type was: " + messageType + "\nYour message value was: " + decimalMsgValue);

		// Try to push the id into 

		// SQL PUSH SECTION //

		// TODO //

		// END //


	}

	@GetMapping("/tmpsckt2")
	public String hexIngress(@RequestParam(value = "hexMsg", defaultValue = "FFFFFF") String hexMsg) {
		// Takes a Hex input and runs the same shenanigans as the binary

		// Error Handling

		if (hexMsg == "") {
			// move to an "Actually empty" function later
			return "Your message device sent nothing.";
		}

		try {
			int temporaryInt = Integer.parseInt(hexMsg, 16);
		}
		catch (Exception e) {
			// Do nothing
			return "You cannot input non-hex messages.";
		}

		// Okay. No errors.

		// now, just like, print it bro

		String binaryMsg = this.parseHexBinary(hexMsg);

		String mama = this.ingresString(binaryMsg);

		return mama;
	}

	// Playing around with passing Objects, and seeing them go JSON

	@GetMapping("/testjson")
	public Coins testJson() {
		// Pass some fucking dumbass value and see if springboot automatically makes this shit into a json

		// OK let's go with Coinspot.
		Coin ergerg = new Coin("Example", "Example", 20);
		Coin[] hahaaaaaaaa = {ergerg};
		Coins fuckit = new Coins(-1, hahaaaaaaaa);

		try {
			URL url = new URL("https://www.coinspot.com.au/pubapi/v2/latest");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");

			conn.connect();

			// Get response code
			int response = conn.getResponseCode();

			// Handle request codes
			if (response != 200) {
				// Not OK
				System.out.println("What the hell is going on here;");
				//return "Failed";

				return fuckit;
			
			}
			else {
				ArrayList<Coin> tempCoins = new ArrayList<Coin>();
				Scanner scanner = new Scanner(url.openStream());

				// Write JSON to inline ingest
				while (scanner.hasNext()) {

					// This is where i create Coin objects in-line and pass it to my buddy.

					String nextLine = scanner.nextLine();
					
					// Usually, the entire thing is inside of one single nextyline.

					JSONObject yourmum = new JSONObject(nextLine);
					//System.out.println("Your JSON Object came out to be: \n" + yourmum);

					// Now separate them
					JSONObject prices = yourmum.getJSONObject("prices");
					
					Set<String> keys = prices.keySet();
					//System.out.println("Your JSON keys came out to be: \n" + keys);

					for (String key : keys) {
						// iterate my boy
						float lastValueCurrent = Float.parseFloat(prices.getJSONObject(key).getString("last"));
						System.out.println("Your Last value for " + key + " is: " + lastValueCurrent + "\n");

						Coin currentCoin = new Coin(key, key, lastValueCurrent);
						tempCoins.add(currentCoin);

					}

					// inline += scanner.nextLine();
				}

				scanner.close();
				Coin[] arrayed = new Coin[tempCoins.size()];
				tempCoins.toArray(arrayed);
				Coins fuckyou = new Coins(0, arrayed);
				return fuckyou;

				// Parse
				//return inline;
		
		}
		
		} catch (Exception e) {
			e.printStackTrace();
			return fuckit;
		}

	}

}
