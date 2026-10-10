import java.util.Scanner;

/**
 * A simple calculator for very large integers.
 */
public class Calculator {
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);

        System.out.println("Large Integer Calculator");
        System.out.println("========================");

        boolean running = true;

        while (running) {
            printMenu();
            String choice = console.nextLine();

            if (choice.equals("1")) {
                calculate(console, '+');
            } else if (choice.equals("2")) {
                calculate(console, '-');
            } else if (choice.equals("3")) {
                calculate(console, '*');
            } else if (choice.equals("4")) {
                calculate(console, '/');
            } else if (choice.equals("5")) {
                running = false;
                System.out.println("Goodbye!");
            } else {
                System.out.println("Invalid choice. Please enter 1-5.");
            }

            System.out.println();
        }

        console.close();
    }

    public static void printMenu() {
        System.out.println("1. Add");
        System.out.println("2. Subtract");
        System.out.println("3. Multiply");
        System.out.println("4. Divide");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    public static void calculate(Scanner console, char operation) {
        try {
            System.out.print("Enter the first integer: ");
            LargeInteger first = new LargeInteger(console.nextLine());

            System.out.print("Enter the second integer: ");
            LargeInteger second = new LargeInteger(console.nextLine());

            LargeInteger result;

            if (operation == '+') {
                result = first.add(second);
                System.out.println(first + " + " + second + " = " + result);
            } else if (operation == '-') {
                result = first.subtract(second);
                System.out.println(first + " - " + second + " = " + result);
            } else if (operation == '*') {
                result = first.multiply(second);
                System.out.println(first + " * " + second + " = " + result);
            } else {
                result = first.divide(second);
                LargeInteger remainder = first.remainder(second);

                System.out.println(first + " / " + second + " = " + result);

                if (!remainder.toString().equals("0")) {
                    System.out.println("Remainder = " + remainder);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid integer. Please use digits with an optional leading - sign.");
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }
}
