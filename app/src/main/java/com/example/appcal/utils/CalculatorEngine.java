package com.example.appcal.utils;

import java.util.*;

public class CalculatorEngine {

    public static String evaluate(String expression, boolean isDegreeMode) {
        try {
            // Replace symbols and function names first
            expression = new CalculatorEngine().convertUserInput(expression);

            expression = expression.replace("×", "*")
                    .replace("÷", "/")
                    .replace("−", "-")
                    .replace("x", "*");

            List<String> tokens = tokenize(expression);
            List<String> postfix = toPostfix(tokens);
            double result = evalPostfix(postfix, isDegreeMode);

            if (Math.abs(result) < 1e-10) result = 0;
            else result = Math.round(result * 1e10) / 1e10;

            if (result == (long) result) {
                return String.format(Locale.US, "%d", (long) result);
            } else {
                return String.format(Locale.US, "%s", result);
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    private String convertUserInput(String input) {
        return input
                .replace("sin⁻¹", "asin")
                .replace("cos⁻¹", "acos")
                .replace("tan⁻¹", "atan")
                .replace("π", String.valueOf(Math.PI))
                .replace("e", String.valueOf(Math.E))
                .replace("ϕ", "1.6180339887")
                .replace("x²", "^2")
                .replace("xⁿ", "^")
                .replace("√", "√")
                .replace("∛", "∛")
                .replace("mod", "mod")
                .replace("abs", "abs");
    }

    private static List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isLetter(c)) {
                token.append(c);
                continue;
            }

            if (token.length() > 0) {
                tokens.add(token.toString());
                token.setLength(0);
            }

            // Negative numbers
            if ((c == '-' && (i == 0 || "+-*/^(,".contains(String.valueOf(expr.charAt(i - 1))))) ||
                    Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                number.append(c);
                while (i + 1 < expr.length() && (Character.isDigit(expr.charAt(i + 1)) || expr.charAt(i + 1) == '.')) {
                    number.append(expr.charAt(++i));
                }
                tokens.add(number.toString());
            } else if (!Character.isWhitespace(c)) {
                tokens.add(String.valueOf(c));
            }
        }

        if (token.length() > 0) tokens.add(token.toString());

        return tokens;
    }

    private static int precedence(String op) {
        switch (op) {
            case "sin": case "cos": case "tan": case "asin": case "acos": case "atan":
            case "log": case "ln": case "√": case "∛": case "abs": case "^": return 4;
            case "%": return 3;
            case "*": case "/": return 2;
            case "+": case "-": return 1;
            default: return 0;
        }
    }

    private static boolean isFunction(String token) {
        return Arrays.asList("sin", "cos", "tan", "asin", "acos", "atan",
                "log", "ln", "√", "∛", "abs").contains(token);
    }

    private static List<String> toPostfix(List<String> tokens) {
        Stack<String> stack = new Stack<>();
        List<String> output = new ArrayList<>();
        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if (isFunction(token)) {
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty()) stack.pop();
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    output.add(stack.pop());
                }
            } else {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token)
                        && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }

    private static double evalPostfix(List<String> postfix, boolean isDegreeMode) {
        Stack<Double> stack = new Stack<>();
        for (String token : postfix) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else if (token.equals("+")) {
                stack.push(stack.pop() + stack.pop());
            } else if (token.equals("-")) {
                double b = stack.pop(), a = stack.pop();
                stack.push(a - b);
            } else if (token.equals("*")) {
                stack.push(stack.pop() * stack.pop());
            } else if (token.equals("/")) {
                double b = stack.pop(), a = stack.pop();
                if (b == 0) throw new ArithmeticException("Divide by 0");
                stack.push(a / b);
            } else if (token.equals("^")) {
                double b = stack.pop(), a = stack.pop();
                stack.push(Math.pow(a, b));
            } else if (token.equals("%")) {
                double b = stack.pop(); // sau %
                double a = stack.pop(); // truoc %
                stack.push((a/100.0)*b);
            } else if (token.equals("√")) {
                stack.push(Math.sqrt(stack.pop()));
            } else if (token.equals("∛")) {
                stack.push(Math.cbrt(stack.pop()));
            } else if (token.equals("abs")) {
                stack.push(Math.abs(stack.pop()));
            } else if (token.equals("sin")) {
                double x = stack.pop();
                if (isDegreeMode) x = Math.toRadians(x);
                stack.push(Math.sin(x));
            } else if (token.equals("cos")) {
                double x = stack.pop();
                if (isDegreeMode) x = Math.toRadians(x);
                stack.push(Math.cos(x));
            } else if (token.equals("tan")) {
                double x = stack.pop();
                if (isDegreeMode) x = Math.toRadians(x);
                stack.push(Math.tan(x));
            } else if (token.equals("asin")) {
                double x = stack.pop();
                if (x < -1 || x > 1) throw new ArithmeticException("asin domain");
                double r = Math.asin(x);
                stack.push(isDegreeMode ? Math.toDegrees(r) : r);
            } else if (token.equals("acos")) {
                double x = stack.pop();
                if (x < -1 || x > 1) throw new ArithmeticException("acos domain");
                double r = Math.acos(x);
                stack.push(isDegreeMode ? Math.toDegrees(r) : r);
            } else if (token.equals("atan")) {
                double x = stack.pop();
                double r = Math.atan(x);
                stack.push(isDegreeMode ? Math.toDegrees(r) : r);
            } else if (token.equals("log")) {
                double x = stack.pop();
                if (x <= 0) throw new ArithmeticException("log domain");
                stack.push(Math.log10(x));
            } else if (token.equals("ln")) {
                double x = stack.pop();
                if (x <= 0) throw new ArithmeticException("ln domain");
                stack.push(Math.log(x));
            }
        }

        if (stack.isEmpty()) throw new IllegalArgumentException("Empty stack");
        return stack.pop();
    }
}
