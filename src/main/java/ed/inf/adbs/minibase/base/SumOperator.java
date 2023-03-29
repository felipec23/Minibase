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

    private List<String> headVarsString;

    public SumOperator(Operator child, SumAggregate sumAggregate, Query query) {
        super(null);
        this.child = child;
        this.query = query;
        this.sumAggregate = sumAggregate;
        this.headVariables = query.getHead().getVariables();
        this.productTerms = sumAggregate.getProductTerms();
        this.headVarsString = setHeadVarsString();
    }

    // Function to convert the head variables from variables to strings:
    private List<String> setHeadVarsString() {
        List<String> headVarsString = new ArrayList<>();
        for (int i = 0; i < headVariables.size(); i++) {
            headVarsString.add(headVariables.get(i).toString());
        }
        return headVarsString;
    }

    @Override
    public void dump() {
        Tuple tuple = null;
        sum = 0;

        // If it's a simple sum:
        if (headVariables.size() == 0) {

            // Here, we just return 1 number. Example: Q(SUM(1)), Q(SUM(x)), Q(SUM(x*x))

            // Check it productTerms is a constant, that is, 1:
            if (productTerms.get(0) instanceof Constant) {
                // int numberOfTuples = 0;
                while ((tuple = child.getNextTuple()) != null) {
                    sum++;
                }
            }

            else {
                // If the product/s term/s is/are a variable:
                while ((tuple = child.getNextTuple()) != null) {
                    System.out.println("TUPLERECEIVED: " + tuple);

                    int sumOfProductTerms = getSumOfProductTerms(tuple);

                    // Cast the term to an integer and add it to the sum:
                    //int integer = Integer.parseInt(termTuple.toString());
                    sum += sumOfProductTerms;

                    // Print:
                    System.out.println("Sum: " + sum);


                }
            }

            // Print final sum:
            System.out.println("Final sum: " + sum);
        }

        // If it's a sum with group by:

        // Examples: Q(x, SUM(x), Q(SUM(x*x))
        else {
            // Create a hashmap with the group by variables as keys and the sum as values:
            LinkedHashMap<String, Integer> sumMap = new LinkedHashMap<>();

            // Iterate through the tuples:
            while ((tuple = child.getNextTuple()) != null) {
                // Print the tuple:

                System.out.println("tuplereceived: " + tuple);

                // Create a string to be used as a key in the hashmap:
                String mapKey = createMapKey(tuple);

                System.out.println("mapKey: " + mapKey);

                // Print the size of productTerms
                System.out.println("productTerms size: " + productTerms.size());


                // If the tuple is already in the hashmap:
                if (sumMap.containsKey(mapKey)) {
                    // Get the sum of the tuple:
                    int sumOfTuple = sumMap.get(mapKey);

                    // Get the sum of the product terms:
                    // Call the function:
                    int sumOfProductTerms = getSumOfProductTerms(tuple);

                    // Add the sum of the product terms to the sum of the tuple:
                    sumOfTuple += sumOfProductTerms;

                    // Put the tuple and the new sum in the hashmap:
                    sumMap.put(mapKey, sumOfTuple);
                }

                // If the tuple is not in the hashmap:
                else {
                    // Get the sum of the product terms:
                    int sumOfProductTerms = 0;

                    // Call the function:
                    sumOfProductTerms = getSumOfProductTerms(tuple);

                    // Put the tuple and the sum of the product terms in the hashmap:
                    sumMap.put(mapKey, sumOfProductTerms);
                }
            }

            // Print the hashmap:
            System.out.println("HashMap: " + sumMap);
        }





    }

    // Function to write the result of the sum in a file:
    public void writeSingleResult() {
        // TODO

    }


    // Function to create a string to be used as a key in the hashmap:
    public String createMapKey(Tuple tuple) {
        String mapKey = "";
        for (int i = 0; i < headVarsString.size(); i++) {
            // Find where the head variable is in the tuple:

            // Get current head variable
            String stringTerm = headVarsString.get(i);

            // See where the head variable is in the tuple:
            int index = tuple.getVariablesNames().indexOf(stringTerm);

            // Get the term in that index:
            Term term = tuple.getTuple(index);

            // Add the term as string to the mapKey, separated by a comma:
            mapKey += term.toString() + ",";

        }
        return mapKey;
    }


    // Function to get the sum of the product terms:
    public int getSumOfProductTerms(Tuple tuple) {


        // Special case if there are more than one product term:
        if (productTerms.size() > 1) {

            // If the product terms are bigger than 1, then we multiply them:
            int sumOfProductTerms = 1;

            for (Term term : productTerms) {
                if (term instanceof Constant) {
                    sumOfProductTerms *= Integer.parseInt(term.toString());
                }
                else {
                    int index = 0;
                    for (int i = 0; i < tuple.getVariables().size(); i++) {
                        if (tuple.getVariables().get(i).equals(term)) {
                            index = i;
                        }
                    }

                    int value = ((IntegerConstant) tuple.getTuple(index)).getValue();
                    sumOfProductTerms *= value;

                }
            }

            return sumOfProductTerms;
        }

        // If there is only one product term:
        else {
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

            return sumOfProductTerms;
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
