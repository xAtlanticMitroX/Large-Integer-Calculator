/**
 * Stores and calculates with integers larger than Java's primitive types.
 * Uses arrays of individual digits instead of BigInteger.
 */
public class LargeInteger {
    private int[] digits;
    private boolean negative;

    // Creates a LargeInteger from a String.
    public LargeInteger(String number) {
        number = number.trim();

        if (number.length() == 0) {
            throw new NumberFormatException("Empty number");
        }

        int start = 0;
        negative = false;

        if (number.charAt(0) == '-') {
            negative = true;
            start = 1;
        } else if (number.charAt(0) == '+') {
            start = 1;
        }

        if (start == number.length()) {
            throw new NumberFormatException("Invalid number");
        }

        while (start < number.length() - 1 && number.charAt(start) == '0') {
            start++;
        }

        digits = new int[number.length() - start];

        for (int i = start; i < number.length(); i++) {
            char c = number.charAt(i);

            if (c < '0' || c > '9') {
                throw new NumberFormatException("Invalid number: " + number);
            }

            digits[i - start] = c - '0';
        }

        removeLeadingZeroes();

        if (isZero()) {
            negative = false;
        }
    }

    // Creates a LargeInteger from an array of digits and a sign.
    private LargeInteger(int[] digits, boolean negative) {
        this.digits = digits;
        this.negative = negative;
        removeLeadingZeroes();

        if (isZero()) {
            this.negative = false;
        }
    }

    public boolean isZero() {
        return digits.length == 1 && digits[0] == 0;
    }

    private void removeLeadingZeroes() {
        int first = 0;

        while (first < digits.length - 1 && digits[first] == 0) {
            first++;
        }

        if (first > 0) {
            int[] newDigits = new int[digits.length - first];

            for (int i = 0; i < newDigits.length; i++) {
                newDigits[i] = digits[first + i];
            }

            digits = newDigits;
        }
    }

    // Adds two large integers.
    public LargeInteger add(LargeInteger other) {
        if (negative == other.negative) {
            int[] result = addMagnitudes(digits, other.digits);
            return new LargeInteger(result, negative);
        }

        int comparison = compareMagnitudes(digits, other.digits);

        if (comparison == 0) {
            return new LargeInteger("0");
        }

        if (comparison > 0) {
            int[] result = subtractMagnitudes(digits, other.digits);
            return new LargeInteger(result, negative);
        }

        int[] result = subtractMagnitudes(other.digits, digits);
        return new LargeInteger(result, other.negative);
    }

    // Subtracts another large integer from this one.
    public LargeInteger subtract(LargeInteger other) {
        if (negative != other.negative) {
            int[] result = addMagnitudes(digits, other.digits);
            return new LargeInteger(result, negative);
        }

        int comparison = compareMagnitudes(digits, other.digits);

        if (comparison == 0) {
            return new LargeInteger("0");
        }

        if (comparison > 0) {
            int[] result = subtractMagnitudes(digits, other.digits);
            return new LargeInteger(result, negative);
        }

        int[] result = subtractMagnitudes(other.digits, digits);
        return new LargeInteger(result, !negative);
    }

    // Multiplies two large integers.
    public LargeInteger multiply(LargeInteger other) {
        int[] result = new int[digits.length + other.digits.length];

        for (int i = digits.length - 1; i >= 0; i--) {
            int carry = 0;

            for (int j = other.digits.length - 1; j >= 0; j--) {
                int position = i + j + 1;
                int product = digits[i] * other.digits[j] + result[position] + carry;

                result[position] = product % 10;
                carry = product / 10;
            }

            result[i] += carry;
        }

        return new LargeInteger(result, negative != other.negative);
    }

    // Returns the integer quotient from division.
    public LargeInteger divide(LargeInteger other) {
        return divideAndRemainder(other)[0];
    }

    // Returns the remainder from integer division.
    public LargeInteger remainder(LargeInteger other) {
        return divideAndRemainder(other)[1];
    }

    // Performs long division and returns quotient and remainder.
    private LargeInteger[] divideAndRemainder(LargeInteger other) {
        if (other.isZero()) {
            throw new ArithmeticException("Cannot divide by zero.");
        }

        int[] quotient = new int[digits.length];
        int[] remainder = new int[] {0};

        for (int i = 0; i < digits.length; i++) {
            remainder = appendDigit(remainder, digits[i]);

            int quotientDigit = 0;

            while (compareMagnitudes(remainder, other.digits) >= 0) {
                remainder = subtractMagnitudes(remainder, other.digits);
                quotientDigit++;
            }

            quotient[i] = quotientDigit;
        }

        LargeInteger quotientNumber =
                new LargeInteger(quotient, negative != other.negative);

        LargeInteger remainderNumber =
                new LargeInteger(remainder, negative);

        return new LargeInteger[] {quotientNumber, remainderNumber};
    }

    private static int[] appendDigit(int[] number, int digit) {
        if (number.length == 1 && number[0] == 0) {
            return new int[] {digit};
        }

        int[] result = new int[number.length + 1];

        for (int i = 0; i < number.length; i++) {
            result[i] = number[i];
        }

        result[result.length - 1] = digit;
        return result;
    }

    private static int[] addMagnitudes(int[] first, int[] second) {
        int length = Math.max(first.length, second.length);
        int[] result = new int[length + 1];

        int firstIndex = first.length - 1;
        int secondIndex = second.length - 1;
        int resultIndex = result.length - 1;
        int carry = 0;

        while (resultIndex >= 0) {
            int sum = carry;

            if (firstIndex >= 0) {
                sum += first[firstIndex];
                firstIndex--;
            }

            if (secondIndex >= 0) {
                sum += second[secondIndex];
                secondIndex--;
            }

            result[resultIndex] = sum % 10;
            carry = sum / 10;
            resultIndex--;
        }

        return result;
    }

    // Assumes first >= second.
    private static int[] subtractMagnitudes(int[] first, int[] second) {
        int[] result = new int[first.length];

        int firstIndex = first.length - 1;
        int secondIndex = second.length - 1;
        int resultIndex = result.length - 1;
        int borrow = 0;

        while (resultIndex >= 0) {
            int difference = first[firstIndex] - borrow;

            if (secondIndex >= 0) {
                difference -= second[secondIndex];
                secondIndex--;
            }

            if (difference < 0) {
                difference += 10;
                borrow = 1;
            } else {
                borrow = 0;
            }

            result[resultIndex] = difference;
            firstIndex--;
            resultIndex--;
        }

        int firstNonZero = 0;

        while (firstNonZero < result.length - 1 && result[firstNonZero] == 0) {
            firstNonZero++;
        }

        if (firstNonZero > 0) {
            int[] trimmed = new int[result.length - firstNonZero];

            for (int i = 0; i < trimmed.length; i++) {
                trimmed[i] = result[firstNonZero + i];
            }

            return trimmed;
        }

        return result;
    }

    private static int compareMagnitudes(int[] first, int[] second) {
        if (first.length < second.length) {
            return -1;
        }

        if (first.length > second.length) {
            return 1;
        }

        for (int i = 0; i < first.length; i++) {
            if (first[i] < second[i]) {
                return -1;
            }

            if (first[i] > second[i]) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        String result = "";

        if (negative) {
            result += "-";
        }

        for (int i = 0; i < digits.length; i++) {
            result += digits[i];
        }

        return result;
    }
}
