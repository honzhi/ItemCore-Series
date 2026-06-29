package com.minemart.itemcore.util;

import java.util.Stack;

public class FormulaEvaluator {

    public static double evaluate(String formula, java.util.Map<String, Double> variables) throws IllegalArgumentException {
        if (formula == null || formula.trim().isEmpty()) {
            throw new IllegalArgumentException("公式不能为空");
        }

        // 替换变量占位符
        String processed = formula;
        for (java.util.Map.Entry<String, Double> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            processed = processed.replace(placeholder, String.valueOf(entry.getValue()));
        }

        return evaluateExpression(processed);
    }

    private static double evaluateExpression(String expression) throws IllegalArgumentException {
        try {
            Stack<Double> values = new Stack<>();
            Stack<Character> operators = new Stack<>();

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (Character.isWhitespace(c)) {
                    continue;
                }

                if (Character.isDigit(c) || c == '.') {
                    StringBuilder sb = new StringBuilder();
                    while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                        sb.append(expression.charAt(i++));
                    }
                    values.push(Double.parseDouble(sb.toString()));
                    i--;
                } else if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                    }
                    if (operators.isEmpty()) {
                        throw new IllegalArgumentException("括号不匹配");
                    }
                    operators.pop();
                } else if (isOperator(c)) {
                    while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                    }
                    operators.push(c);
                } else {
                    throw new IllegalArgumentException("无效字符: " + c);
                }
            }

            while (!operators.isEmpty()) {
                values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
            }

            if (values.size() != 1) {
                throw new IllegalArgumentException("无效的表达式");
            }

            return values.pop();
        } catch (Exception e) {
            throw new IllegalArgumentException("公式计算失败: " + e.getMessage(), e);
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("除零错误");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("无效运算符: " + operator);
        }
    }
}
