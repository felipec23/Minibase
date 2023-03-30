package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;


//Create a public class Tuple  that stores X terms:
public class Tuple {

    private Term[] tuple;

    private List<Term> variables;

    public Tuple(Term... tuple) {
        this.tuple = tuple;
    }

    public Term[] getTuple() {
        return tuple;
    }

    public Term getTuple(int i) {
        return tuple[i];
    }

    /**
     * Function for converting a tuple into a string, separated by commas
     * @return
     */
    public String toString() {
        String result = "";
        for (int i = 0; i < tuple.length; i++) {

            // If it's the last element, don't add a comma:
            if (i == tuple.length - 1) {
                result += tuple[i];
                break;
            }

            result += tuple[i] + ", ";

        }
        return result;
    }

    public void setVariables(List<Term> variables) {
        this.variables = variables;
    }

    public List<Term> getVariables() {
        return variables;
    }

    /**
     * Function to get the variables of a tuple (x, y, z...) as strings
     * @return
     */
    public List<String> getVariablesAsListOfStrings() {
        List<String> variablesAsStrings = new ArrayList<>();
        for (Term variable : variables) {
            variablesAsStrings.add(variable.toString());
        }
        return variablesAsStrings;
    }

    /**
     * Function to get the variables names of a tuple (x, y, z...) as strings
     * @return
     */
    public List<String> getVariablesNames() {
        List<String> variablesNames = new ArrayList<>();
        for (Term variable : variables) {
            variablesNames.add(variable.toString());
        }
        return variablesNames;
    }


}

