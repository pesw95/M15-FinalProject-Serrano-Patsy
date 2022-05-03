package com.company.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Scanner;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);

		String userInput;
		int userChoice = 0;
		Scanner myScanner = new Scanner(System.in);

		System.out.println(" Please choose one of the following menu options:");
		System.out.println("  Menu:");
		System.out.println("  1. Weather in a City ");
		System.out.println("  2. Location of the International Space Station");
		System.out.println("  3. Weather in the Location of the ISS");
		System.out.println("  4. Current Cryptocurrency Prices");
		System.out.println("  5. Exit");

		while (myScanner.hasNext()) {

			try {
				userInput = myScanner.nextLine();
				userChoice = Integer.parseInt(userInput);

				if (userInput.equals("1")) {
					System.out.println("Welcome to Weather in a City!");
					System.out.println("");
					System.out.println("Please enter a name of a city");
					String city = myScanner.nextLine();
					System.out.println("Loading...");
					System.out.println("");
					WeatherResponse weatherResponse = getWeatherWithCityName(city);
					System.out.println("The Weather in " + weatherResponse.name + " is " + weatherResponse.main.temp +" degrees.");
					System.out.println("The Weather in " + weatherResponse.name + " feels like " + weatherResponse.main.feels_like + " degrees.");
					System.out.println("The minimum temperature is " + weatherResponse.main.temp_min + " degrees.");
					System.out.println("The maximum temperature is " + weatherResponse.main.temp_max + " degrees.");
					System.out.println("The humidity is " + weatherResponse.main.humidity + "%.");
					System.out.println("Wind speeds of " + weatherResponse.wind.speed + " mph." );
					System.out.println("Clouds: " + weatherResponse.clouds.all + ".");
					System.out.println("");


				} else if (userInput.equals("2")) {
					SpaceResponse issResponse = getLocation();
					System.out.println("Welcome to the Location of the International Space Station!");
					System.out.println("");
					System.out.println("The latitude of the ISS is:" + issResponse.iss_position.latitude);
					System.out.println("The longitude of the ISS is " + issResponse.iss_position.longitude);
					System.out.println("");


				} else if (userInput.equals("3")) {
					SpaceResponse issResponse = getLocation();
					WeatherResponse weatherResponse = getWeatherWithISSCoordinates(issResponse.iss_position.latitude, issResponse.iss_position.longitude);
					System.out.println("Welcome to  Weather in the Location of the ISS!");
					System.out.println("");
					System.out.println("City: " + weatherResponse.name);
					System.out.println("Country:" + weatherResponse.sys.country);
					if (weatherResponse.sys.country == null) {
						System.out.println("The ISS is not hovering over a country at the moment.");
					}
					System.out.println("");
					System.out.println("The weather for the location of the International Space Station is:");
					System.out.println("Temperature: " + weatherResponse.main.temp);
					System.out.println("Feels like: " + weatherResponse.main.feels_like);
					System.out.println("Maximum temperature: " + weatherResponse.main.temp_max);
					System.out.println("Minimum temperature: " + weatherResponse.main.temp_min);
					System.out.println("Humidity: " + weatherResponse.main.humidity + "%.");
					System.out.println("Wind speeds: " + weatherResponse.wind.speed + " mph." );
					System.out.println("Clouds: " + weatherResponse.clouds.all + ".");
					System.out.println("");


				} else if (userInput.equals("4")) {
					System.out.println("Welcome to Current Cryptocurrency Prices");
					System.out.println("");
					System.out.println("Please enter the symbol of a cryptocurrency:");
					userInput = myScanner.nextLine();
					CryptoResponse[] cryptoResponse = getCryptoPrices(userInput);
					System.out.println("");
					System.out.println("Asset Id:" + cryptoResponse[0].asset_id);
					System.out.println("Asset Name:" + cryptoResponse[0].name);
					System.out.println(cryptoResponse[0].asset_id + cryptoResponse[0].name+" Price: " +cryptoResponse[0].price_usd + " . ");
					System.out.println("");



				} else if (userInput.equals("5")) {
					System.out.println("Thank you! Bye!");
					break;
				}
			} catch (Exception e) {
				System.out.println("Please enter a valid menu choice.");
			}
			if (userChoice < 1 || userChoice > 5) {
				System.out.println("Please choose one of the following menu numbers between 1 and 4 (or 5 to Exit):");
			}

			System.out.println("Enter a menu number between 1 and 4 (or 5 to Exit):");

		}


	}


	public static SpaceResponse getLocation() {

		WebClient client = WebClient.create("http://api.open-notify.org/iss-now.json");

		SpaceResponse issResponse = null;

		try {
			Mono<SpaceResponse> response2 = client
					.get()
					.retrieve()
					.bodyToMono(SpaceResponse.class);

			issResponse = response2.share().block();

		} catch (WebClientResponseException we) {
			int statusCode = we.getRawStatusCode();
			if (statusCode >= 400 && statusCode < 500) {
				System.out.println("Client Error");
			} else if (statusCode >= 500 && statusCode < 600) {
				System.out.println("Server Error");
			}
			System.out.println("Message: " + we.getMessage());
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}

		return issResponse;
	}


		public static WeatherResponse getWeatherWithISSCoordinates(String latitude, String longitude){

		WebClient client = WebClient.create("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude +"&appid=479b1f486d5f4f52a23c79531722d92e");

		WeatherResponse weatherResponse = null;

			try {
				Mono<WeatherResponse> response = client
						.get()
						.retrieve()
						.bodyToMono(WeatherResponse.class);

				weatherResponse = response.share().block();
			}
			catch (WebClientResponseException we) {
				int statusCode = we.getRawStatusCode();
				if (statusCode >= 400 && statusCode <500){
					System.out.println("Client Error");
				}
				else if (statusCode >= 500 && statusCode <600){
					System.out.println("Server Error");
				}
				System.out.println("Message: " + we.getMessage());
			}
			catch (Exception e) {
				System.out.println("An error occurred: " + e.getMessage());
			}

			return weatherResponse;
		}


	public static WeatherResponse getWeatherWithCityName(String cityName){


		WebClient client = WebClient.create("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=imperial&appid=479b1f486d5f4f52a23c79531722d92e");

		WeatherResponse weatherResponse = null;

		try {
			Mono<WeatherResponse> response = client
					.get()
					.retrieve()
					.bodyToMono(WeatherResponse.class);

			weatherResponse = response.share().block();
		}
		catch (WebClientResponseException we) {
			int statusCode = we.getRawStatusCode();
			if (statusCode >= 400 && statusCode <500){
				System.out.println("Client Error");
			}
			else if (statusCode >= 500 && statusCode <600){
				System.out.println("Server Error");
			}
			System.out.println("Message: " + we.getMessage());
		}
		catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}

		return weatherResponse;
	}


	public static CryptoResponse[] getCryptoPrices(String assetId){

		WebClient client = WebClient.create("https://rest.coinapi.io/v1/assets/" + assetId.toUpperCase() + "?apikey=2CDDFF27-034B-4EAB-8D1F-31182F5DBEB7");
		CryptoResponse[] cryptoResponse = null;

		try {
			Mono<CryptoResponse[]> response = client
					.get()
					.retrieve()
					.bodyToMono(CryptoResponse[].class);

			cryptoResponse = response.share().block();
		}
		catch (WebClientResponseException we) {
			int statusCode = we.getRawStatusCode();
			if (statusCode >= 400 && statusCode <500){
				System.out.println("Client Error");
			}
			else if (statusCode >= 500 && statusCode <600){
				System.out.println("Server Error");
			}
			System.out.println("Message: " + we.getMessage());
		}
		catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}

		return cryptoResponse;
	}


}








