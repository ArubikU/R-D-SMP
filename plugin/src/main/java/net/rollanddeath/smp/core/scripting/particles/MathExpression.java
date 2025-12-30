package net.rollanddeath.smp.core.scripting.particles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Parser/evaluador simple y seguro para expresiones matemticas usadas por ScriptedParticleSystem.
 *
 * Soporta:
 * - Operadores: +, -, *, /, %, ^
 * - Parntesis
 * - Variables: provistas por el caller (ej: t, i, tick, age, time, pi, e, r, angle)
 * - Funciones: sin, cos, tan, asin, acos, atan, atan2,
 *              sqrt, abs, floor, ceil, round,
 *              min, max, clamp, lerp,
 *              deg_to_rad, rad_to_deg,
 *              rand, rand(max), rand_range(min,max)
 *
 * Funciones aleatorias:
 * - rand(): devuelve número aleatorio en [0, 1)
 * - rand(max): devuelve número aleatorio en [0, max) si max > 0, o [max, 0) si max < 0
 * - rand_range(min, max): devuelve número aleatorio en [min, max)
 */
public final class MathExpression {

    private MathExpression() {
    }

    public static Compiled compile(String expr) {
        if (expr == null || expr.isBlank()) {
            return null;
        }
        List<Token> tokens = tokenize(expr);
        List<Token> rpn = toRpn(tokens);
        return new Compiled(expr, rpn);
    }

    public static final class Compiled {
        private final String source;
        private final List<Token> rpn;

        private Compiled(String source, List<Token> rpn) {
            this.source = source;
            this.rpn = rpn;
        }

        public double eval(Map<String, Double> vars) {
            Deque<Double> st = new ArrayDeque<>();
            for (Token t : rpn) {
                switch (t.type) {
                    case NUMBER -> st.push(t.number);
                    case IDENT -> st.push(resolveVar(vars, t.text));
                    case OP -> applyOp(st, t.text);
                    case FUNC -> applyFunc(st, t.text);
                    default -> {
                        // ignore
                    }
                }
            }
            if (st.isEmpty()) return 0.0;
            double out = st.pop();
            if (Double.isNaN(out) || Double.isInfinite(out)) return 0.0;
            return out;
        }

        @Override
        public String toString() {
            return source;
        }
    }

    private enum TokenType {
        NUMBER,
        IDENT,
        OP,
        FUNC,
        LPAREN,
        RPAREN,
        COMMA
    }

    private static final class Token {
        final TokenType type;
        final String text;
        final double number;

        private Token(TokenType type, String text, double number) {
            this.type = type;
            this.text = text;
            this.number = number;
        }

        static Token number(double v) {
            return new Token(TokenType.NUMBER, null, v);
        }

        static Token ident(String s) {
            return new Token(TokenType.IDENT, s, 0.0);
        }

        static Token op(String s) {
            return new Token(TokenType.OP, s, 0.0);
        }

        static Token func(String s) {
            return new Token(TokenType.FUNC, s, 0.0);
        }

        static Token lparen() {
            return new Token(TokenType.LPAREN, "(", 0.0);
        }

        static Token rparen() {
            return new Token(TokenType.RPAREN, ")", 0.0);
        }

        static Token comma() {
            return new Token(TokenType.COMMA, ",", 0.0);
        }
    }

    private static List<Token> tokenize(String expr) {
        String s = expr.trim();
        List<Token> out = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                out.add(Token.lparen());
                i++;
                continue;
            }
            if (c == ')') {
                out.add(Token.rparen());
                i++;
                continue;
            }
            if (c == ',') {
                out.add(Token.comma());
                i++;
                continue;
            }

            if (isOpChar(c)) {
                // Unario: tratamos '-' como op normal; el parser (RPN) maneja '-' unario como func "neg".
                out.add(Token.op(String.valueOf(c)));
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                int start = i;
                i++;
                while (i < s.length()) {
                    char cc = s.charAt(i);
                    if (Character.isDigit(cc) || cc == '.') {
                        i++;
                        continue;
                    }
                    break;
                }
                String num = s.substring(start, i);
                try {
                    out.add(Token.number(Double.parseDouble(num)));
                } catch (Exception ignored) {
                    out.add(Token.number(0.0));
                }
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                int start = i;
                i++;
                while (i < s.length()) {
                    char cc = s.charAt(i);
                    if (Character.isLetterOrDigit(cc) || cc == '_' || cc == '.') {
                        i++;
                        continue;
                    }
                    break;
                }
                String ident = s.substring(start, i).trim();
                out.add(Token.ident(ident));
                continue;
            }

            // Caracter desconocido: se ignora.
            i++;
        }
        return out;
    }

    private static boolean isOpChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
    }

    private static int precedence(String op) {
        return switch (op) {
            case "^" -> 4;
            case "*", "/", "%" -> 3;
            case "+", "-" -> 2;
            default -> 0;
        };
    }

    private static boolean isRightAssociative(String op) {
        return "^".equals(op);
    }

    private static List<Token> toRpn(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();

        Token prev = null;
        for (int idx = 0; idx < tokens.size(); idx++) {
            Token t = tokens.get(idx);

            if (t.type == TokenType.IDENT) {
                // IDENT seguido de '(' => func
                Token next = (idx + 1) < tokens.size() ? tokens.get(idx + 1) : null;
                if (next != null && next.type == TokenType.LPAREN) {
                    stack.push(Token.func(t.text));
                } else {
                    output.add(t);
                }
            } else if (t.type == TokenType.NUMBER) {
                output.add(t);
            } else if (t.type == TokenType.OP) {
                String op = t.text;

                // Unario '-' -> func neg
                if ("-".equals(op) && (prev == null || prev.type == TokenType.OP || prev.type == TokenType.LPAREN || prev.type == TokenType.COMMA)) {
                    stack.push(Token.func("neg"));
                    prev = t;
                    continue;
                }

                while (!stack.isEmpty()) {
                    Token top = stack.peek();
                    if (top.type == TokenType.OP) {
                        int p1 = precedence(op);
                        int p2 = precedence(top.text);
                        if ((isRightAssociative(op) && p1 < p2) || (!isRightAssociative(op) && p1 <= p2)) {
                            output.add(stack.pop());
                            continue;
                        }
                    } else if (top.type == TokenType.FUNC) {
                        output.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(t);
            } else if (t.type == TokenType.COMMA) {
                while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN) {
                    output.add(stack.pop());
                }
            } else if (t.type == TokenType.LPAREN) {
                stack.push(t);
            } else if (t.type == TokenType.RPAREN) {
                while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN) {
                    output.add(stack.pop());
                }
                if (!stack.isEmpty() && stack.peek().type == TokenType.LPAREN) {
                    stack.pop();
                }
                // Si arriba hay una func, la emitimos
                if (!stack.isEmpty() && stack.peek().type == TokenType.FUNC) {
                    output.add(stack.pop());
                }
            }

            prev = t;
        }

        while (!stack.isEmpty()) {
            Token t = stack.pop();
            if (t.type == TokenType.LPAREN || t.type == TokenType.RPAREN) continue;
            output.add(t);
        }

        return output;
    }

    private static double resolveVar(Map<String, Double> vars, String keyRaw) {
        if (keyRaw == null) return 0.0;
        String key = keyRaw.trim().toLowerCase(Locale.ROOT);
        if ("pi".equals(key)) return Math.PI;
        if ("e".equals(key)) return Math.E;

        if (vars == null) return 0.0;
        Double v = vars.get(key);
        if (v != null) return v;

        // Permitir acceso case-insensitive sin duplicar mapas
        for (Map.Entry<String, Double> e : vars.entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(keyRaw)) {
                return Objects.requireNonNullElse(e.getValue(), 0.0);
            }
        }
        return 0.0;
    }

    private static void applyOp(Deque<Double> st, String op) {
        if (st == null) return;
        double b = st.isEmpty() ? 0.0 : st.pop();
        double a = st.isEmpty() ? 0.0 : st.pop();
        double out;
        switch (op) {
            case "+" -> out = a + b;
            case "-" -> out = a - b;
            case "*" -> out = a * b;
            case "/" -> out = b == 0.0 ? a : (a / b);
            case "%" -> out = b == 0.0 ? a : (a % b);
            case "^" -> out = Math.pow(a, b);
            default -> out = a;
        }
        if (Double.isNaN(out) || Double.isInfinite(out)) out = 0.0;
        st.push(out);
    }

    private static void applyFunc(Deque<Double> st, String funcRaw) {
        if (st == null) return;
        if (funcRaw == null) {
            return;
        }
        String func = funcRaw.trim().toLowerCase(Locale.ROOT);

        // funcs aridad variable: intentamos por nombre
        switch (func) {
            case "neg" -> {
                double a = st.isEmpty() ? 0.0 : st.pop();
                st.push(-a);
            }
            case "abs" -> st.push(Math.abs(pop(st)));
            case "floor" -> st.push(Math.floor(pop(st)));
            case "ceil" -> st.push(Math.ceil(pop(st)));
            case "round" -> st.push((double) Math.round(pop(st)));
            case "sqrt" -> {
                double a = pop(st);
                st.push(a < 0.0 ? 0.0 : Math.sqrt(a));
            }
            case "sin" -> st.push(Math.sin(pop(st)));
            case "cos" -> st.push(Math.cos(pop(st)));
            case "tan" -> st.push(Math.tan(pop(st)));
            case "asin" -> st.push(Math.asin(pop(st)));
            case "acos" -> st.push(Math.acos(pop(st)));
            case "atan" -> st.push(Math.atan(pop(st)));
            case "atan2" -> {
                double b = pop(st);
                double a = pop(st);
                st.push(Math.atan2(a, b));
            }
            case "min" -> {
                double b = pop(st);
                double a = pop(st);
                st.push(Math.min(a, b));
            }
            case "max" -> {
                double b = pop(st);
                double a = pop(st);
                st.push(Math.max(a, b));
            }
            case "clamp" -> {
                double max = pop(st);
                double min = pop(st);
                double v = pop(st);
                double lo = Math.min(min, max);
                double hi = Math.max(min, max);
                st.push(Math.max(lo, Math.min(hi, v)));
            }
            case "lerp" -> {
                double t = pop(st);
                double b = pop(st);
                double a = pop(st);
                st.push(a + (b - a) * t);
            }
            case "deg_to_rad" -> st.push(Math.toRadians(pop(st)));
            case "rad_to_deg" -> st.push(Math.toDegrees(pop(st)));
            case "rand" -> {
                // rand() sin argumentos devuelve [0, 1), rand(max) devuelve [0, max) o [max, 0) según signo
                double max = st.isEmpty() ? 1.0 : pop(st);
                if (max == 0.0) {
                    st.push(0.0);
                } else if (max > 0) {
                    st.push(ThreadLocalRandom.current().nextDouble(0.0, max));
                } else {
                    st.push(ThreadLocalRandom.current().nextDouble(max, 0.0));
                }
            }
            case "rand_range" -> {
                double b = pop(st);
                double a = pop(st);
                st.push(ThreadLocalRandom.current().nextDouble(Math.min(a, b), Math.max(a, b)));
            }
            default -> {
                // func desconocida: no-op
            }
        }
    }

    private static double pop(Deque<Double> st) {
        if (st == null || st.isEmpty()) return 0.0;
        Double v = st.pop();
        if (v == null || Double.isNaN(v) || Double.isInfinite(v)) return 0.0;
        return v;
    }
}
