package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

/**
 * This class abstracts the concept of comparison atom and should be extended for each specific
 * comparison operator. It defines the fields all the comparison atoms need and provide their getters
 * and setters. It also defines the evaluate method that is used to evaluate the comparison atom.
 * It keeps track of the indexes. For example, let's say it's evaluating a tuple of the form: R[b, x] and that
 * the current comparison atom per se is: x != 5. Then, the indexes list will be [1] and the relationsNames
 * list will be [R]. If  there are more than one relation included in the comparison, then the indexes list
 * and relationsNames list will have the same size and the indexes will be in the same order as the relations
 * names. For example, let's say it's evaluating a tuple of the form: R[b, x] and S[c, y] and that
 * the current comparison atom per se is: x != y. Then, the indexes list will be [1, 1] and the relationsNames
 * list will be [R, S]. We also defined several types of comparison atoms. The default type is "default". But there
 * are more possible types:
 * "equi-join": when the same variable appears in 2 or more terms.
 * "different atoms": when there's a comparison between 2 different atoms.
 * "between atom": when the comparison is only inside a single atom.
 *
 */
public class ComparisonAtom extends Atom {

    private Term term1;

    private Term term2;

    private ComparisonOperator op;

    private String type;

    private List<Integer> indexes;

    private List<String> relationsNames;

    public ComparisonAtom(Term term1, Term term2, ComparisonOperator op) {
        this.term1 = term1;
        this.term2 = term2;
        this.op = op;
        this.type = "default";
        this.indexes = new ArrayList<>();
        this.relationsNames = new ArrayList<>();
    }

    public void setRelationsNames(List<String> relationsNames) {
    	this.relationsNames = relationsNames;
    }

    public void setIndexes(List<Integer> indexes) {
    	this.indexes = indexes;
    }


    public void setType(String type) {
    	this.type = type;
    }

    public String getType() {
    	return this.type;
    }

    public List<Integer> getIndexes() {
    	return this.indexes;
    }

    public List<String> getRelationsNames() {
    	return this.relationsNames;
    }

    public Term getTerm1() {
        return term1;
    }

    public Term getTerm2() {
        return term2;
    }

    public boolean evaluate(Tuple tuple) {
        return op.evaluate(tuple.getTuple(0), tuple.getTuple(1));

    }


    @Override
    public String toString() {
        return term1 + " " + op + " " + term2;
    }

}
