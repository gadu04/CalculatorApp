package com.example.appcal.utils;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

public class EquationSolver {

    public static String solveEquation(double[] coefficients) {
        if (coefficients == null || coefficients.length != 3 && coefficients.length != 4 && coefficients.length != 5) {
            return "Chỉ hỗ trợ giải phương trình bậc 2, 3 hoặc 4.";
        }

        int degree = coefficients.length - 1;

        // Kiểm tra hệ số bậc cao nhất
        if (coefficients[degree] == 0) {
            return "Hệ số bậc cao nhất bằng 0. Không phải phương trình bậc " + degree + ".";
        }

        try {
            if (degree == 2) {
                return solveQuadratic(coefficients[2], coefficients[1], coefficients[0]);
            } else {
                return solvePolynomial(coefficients, degree);
            }
        } catch (Exception e) {
            return "Lỗi khi giải phương trình: " + e.getMessage();
        }
    }

    // ✅ Bậc 2: ax² + bx + c = 0
    public static String solveQuadratic(double a, double b, double c) {
        if (a == 0) return "Không phải phương trình bậc 2.";

        double delta = b * b - 4 * a * c;
        if (delta > 0) {
            double x1 = (-b + Math.sqrt(delta)) / (2 * a);
            double x2 = (-b - Math.sqrt(delta)) / (2 * a);
            return "2 nghiệm phân biệt:\nx1 = " + x1 + "\nx2 = " + x2;
        } else if (delta == 0) {
            double x = -b / (2 * a);
            return "1 nghiệm kép:\nx = " + x;
        } else {
            double real = -b / (2 * a);
            double imag = Math.sqrt(-delta) / (2 * a);
            return "2 nghiệm phức:\nx1 = " + real + " + " + imag + "i\nx2 = " + real + " - " + imag + "i";
        }
    }

    // ✅ Bậc 3 và 4 dùng solver
    private static String solvePolynomial(double[] coefficients, int degree) {
        PolynomialFunction poly = new PolynomialFunction(coefficients);
        LaguerreSolver solver = new LaguerreSolver();
        Complex[] roots = solver.solveAllComplex(coefficients, 0);

        StringBuilder result = new StringBuilder("Nghiệm phương trình bậc " + degree + ":\n");
        int count = 1;
        for (Complex root : roots) {
            if (root.getImaginary() == 0) {
                result.append("x").append(count).append(" = ").append(root.getReal()).append("\n");
            } else {
                result.append("x").append(count).append(" = ")
                        .append(root.getReal()).append(" + ").append(root.getImaginary()).append("i\n");
            }
            count++;
        }
        return result.toString();
    }
}
