package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SumOperator extends Operator {

    private int sum;

    private Operator child;

    private SumAggregate sumAggregate;

    private List<Variable> headVariables;

    private Query query;

    private List<Term> productTerms;

    public SumOperator(Operator child, SumAggregate sumAggregate, Query query) {
        super(null);
        this.child = child;
        this.query = query;
        this.sumAggregate = sumAggregate;
        this.headVariables = query.getHead().getVariables();
        this.productTerms = sumAggregate.getProductTerms();
    }

    @Override
    public void dump() {
        Tuple tuple = null;
        sum = 0;

        // If it's a simple sum:
        if (headVariables.size() == 0 && productTerms.size() == 1) {

            // Example: SELECT SUM(A) FROM R;

            // Check it productTerms is a constant, that is, 1:
            if (productTerms.get(0) instanceof Constant) {
                // int numberOfTuples = 0;
                while ((tuple = child.getNextTuple()) != null) {
                    sum++;
                }
            }

            else {
                // If it's a variable:
                while ((tuple = child.getNextTuple()) != null) {
                    System.out.println("TUPLERECEIVED: " + tuple);
                    List<Term> tupleVariables = tuple.getVariables();

                    // Find where the product term is in the tuple
                    int index = 0;
                    for (int i = 0; i < tupleVariables.size(); i++) {
                        if (tupleVariables.get(i).equals(productTerms.get(0))) {
                            index = i;
                        }
                    }
                    Term termTuple = tuple.getTuple(index);

                    // Cast term as IntegerConstant
                    IntegerConstant integer = (IntegerConstant) termTuple;

                    // Cast the term to an integer and add it to the sum:
                    //int integer = Integer.parseInt(termTuple.toString());
                    sum += integer.getValue();

                    // Print both:
                    System.out.println("integer: " + integer);
                    System.out.println("Sum: " + sum);


                }
            }

            // Print final sum:
            System.out.println("Final sum: " + sum);
        }

        // If it's a sum with group by:
        else {
            // Create a hashmap with the group by variables as keys and the sum as values:
            LinkedHashMap<String, Integer> sumMap = new LinkedHashMap<>();

            // Iterate through the tuples:
            while ((tuple = child.getNextTuple()) != null) {
                // Print the tuple:

                System.out.println("tuplereceived: " + tuple);

                List<String> tupleVariablesNames = tuple.getVariablesNames();


                // Access where the head variables are in the tuple:
                // Iterate through the head variables:

                // Create a list of terms to be used as a key in the hashmap:
                String mapKey = "";
                for (int i = 0; i < headVariables.size(); i++) {
                    // Find where the head variable is in the tuple:

                    // Get current head variable and cast as Term:
                    //Term headVariable = (Term) headVariables.get(i);
                    String stringTerm = headVariables.get(i).toString();

//                    System.out.println("headTerm: " + headTerm);
//
//                    // Print type of headTerm:
//                    System.out.println("headTerm type: " + headTerm.getClass());

                    int index = tupleVariablesNames.indexOf(stringTerm);
                    // Get the term in that index:
                    Term term = tuple.getTuple(index);
                    // Add the term as string to the mapKey, separated by a comma:

                    mapKey += term.toString() + ",";
                }


                System.out.println("mapKey: " + mapKey);

                // Print the size of productTerms
                System.out.println("productTerms size: " + productTerms.size());



                // If the tuple is already in the hashmap:
                if (sumMap.containsKey(mapKey)) {
                    // Get the sum of the tuple:
                    int sumOfTuple = sumMap.get(mapKey);

                    // Get the sum of the product terms:
                    int sumOfProductTerms = 0;
                    for (Term term : productTerms) {
                        if (term instanceof Constant) {
                            // Add the constant (1) to the sum of the product terms:
                            sumOfProductTerms += Integer.parseInt(term.toString());
                        }
                        else {
                            int index = 0;
                            for (int i = 0; i < tuple.getVariables().size(); i++) {
                                if (tuple.getVariables().get(i).equals(term)) {
                                    index = i;
                                }
                            }
                            //sumOfProductTerms += Integer.parseInt(tuple.getTuple(index).toString());
                            int value = ((IntegerConstant) tuple.getTuple(index)).getValue();
                            sumOfProductTerms += value;
                        }
                    }

                    // Add the sum of the product terms to the sum of the tuple:
                    sumOfTuple += sumOfProductTerms;

                    // Put the tuple and the new sum in the hashmap:
                    sumMap.put(mapKey, sumOfTuple);
                }

                // If the tuple is not in the hashmap:
                else {
                    // Get the sum of the product terms:
                    int sumOfProductTerms = 0;
                    for (Term term : productTerms) {
                        if (term instanceof Constant) {
                            sumOfProductTerms += Integer.parseInt(term.toString());
                        }
                        else {
                            int index = 0;
                            for (int i = 0; i < tuple.getVariables().size(); i++) {
                                if (tuple.getVariables().get(i).equals(term)) {
                                    index = i;
                                }
                            }
                            int value = ((IntegerConstant) tuple.getTuple(index)).getValue();
                            sumOfProductTerms += value;

                        }
                    }

                    // Put the tuple and the sum of the product terms in the hashmap:
                    sumMap.put(mapKey, sumOfProductTerms);
                }
            }

            // Print the hashmap:
            System.out.println("HashMap: " + sumMap);
        }





    }




    @Override
    public List<Term> getTermsOfRelationalAtom() {
        return null;
    }

    @Override
    public List<Term> getTermsOfRelationalAtom(String relationAtomName) {
        return null;
    }

}
