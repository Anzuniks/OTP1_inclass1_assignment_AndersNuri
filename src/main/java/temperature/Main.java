package temperature;

import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TemperatureConverter converter = new TemperatureConverter();
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.println("=== Temperature Converter ===");
        System.out.println("1) Fahrenheit -> Celsius");
        System.out.println("2) Celsius -> Fahrenheit");
        System.out.println("3) Kelvin -> Celsius");
        System.out.print("Choose (1-3): ");

        if (!scanner.hasNextInt()) {
            System.out.println("\nNo valid input. In Docker, run with: docker run -it <image>");
            return;
        }
        int choice = scanner.nextInt();

        System.out.print("Enter temperature: ");
        if (!scanner.hasNextDouble()) {
            System.out.println("\nInvalid number.");
            return;
        }
        double value = scanner.nextDouble();

        double celsius;
        switch (choice) {
            case 1:
                celsius = converter.fahrenheitToCelsius(value);
                System.out.printf(Locale.US, "%.2f F = %.2f C%n", value, celsius);
                break;
            case 2:
                celsius = value;
                double fahrenheit = converter.celsiusToFahrenheit(value);
                System.out.printf(Locale.US, "%.2f C = %.2f F%n", value, fahrenheit);
                break;
            case 3:
                celsius = converter.kelvinToCelsius(value);
                System.out.printf(Locale.US, "%.2f K = %.2f C%n", value, celsius);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        if (converter.isExtremeTemperature(celsius)) {
            System.out.println("Warning: extreme temperature!");
        }
    }
}
