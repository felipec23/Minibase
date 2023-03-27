package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

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
//        return op.evaluate(term1.evaluate(tuple), term2.evaluate(tuple));
        return op.evaluate(tuple.getTuple(0), tuple.getTuple(1));

    }
//    Get operator:


    public ComparisonOperator getOp() {
        return op;
    }

    @Override
    public String toString() {
        return term1 + " " + op + " " + term2;
    }

}
