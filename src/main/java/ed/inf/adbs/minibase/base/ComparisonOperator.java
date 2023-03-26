package ed.inf.adbs.minibase.base;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

public enum ComparisonOperator {
    EQ("=") {

        @Override
        public boolean evaluate(Term left, Term right) {
            System.out.println("left: " + left);
            System.out.println("right: " + right);
//            Print operator:
            System.out.println("op: " + "=");

//            Print type of each and say it:
            System.out.println("left type: " + left.getClass());
            System.out.println("right type: " + right.getClass());

            return left.equals(right);
        }
    },
    NEQ("!="){
        @Override
        public boolean evaluate(Term left, Term right) {
            System.out.println("left: " + left);
            System.out.println("right: " + right);
            return !left.equals(right);
        }
    },

    GT(">"){
        @Override
        public boolean evaluate(Term left, Term right) {
            if (left instanceof IntegerConstant && right instanceof IntegerConstant) {
                return  ((IntegerConstant) left).getValue() > ((IntegerConstant) right).getValue();
            }
            throw new IllegalArgumentException("Comparison operator not supported for given data types.");
        }
    },
    GEQ(">="){
        @Override
        public boolean evaluate(Term left, Term right) {
            if (left instanceof IntegerConstant && right instanceof IntegerConstant) {
                return  ((IntegerConstant) left).getValue() >= ((IntegerConstant) right).getValue();
            }
            throw new IllegalArgumentException("Comparison operator not supported for given data types.");
        }
    },
    LT("<"){
        @Override
        public boolean evaluate(Term left, Term right) {
            if (left instanceof IntegerConstant && right instanceof IntegerConstant) {
                return  ((IntegerConstant) left).getValue() < ((IntegerConstant) right).getValue();
            }
            throw new IllegalArgumentException("Comparison operator not supported for given data types.");
        }
    },
    LEQ("<="){
        @Override
        public boolean evaluate(Term left, Term right) {
            if (left instanceof IntegerConstant && right instanceof IntegerConstant) {
                return  ((IntegerConstant) left).getValue() <= ((IntegerConstant) right).getValue();
            }
            throw new IllegalArgumentException("Comparison operator not supported for given data types.");
        }
    };

    private final String text;

    ComparisonOperator(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public abstract boolean evaluate(Term left, Term right);




    public static ComparisonOperator fromString(String s) throws NoSuchElementException {
        return Arrays.stream(values())
                .filter(op -> op.text.equalsIgnoreCase(s))
                .findFirst().get();
    }

}
