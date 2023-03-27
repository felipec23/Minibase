package ed.inf.adbs.minibase.base;

public class ComparisonAtom extends Atom {

    private Term term1;

    private Term term2;

    private ComparisonOperator op;

    private String type;

    private Integer index;

    private String relationName;

    public ComparisonAtom(Term term1, Term term2, ComparisonOperator op) {
        this.term1 = term1;
        this.term2 = term2;
        this.op = op;
        this.type = "default";
        this.index = -1;
        this.relationName = "default";
    }

    public void setRelationName(String relationName) {
    	this.relationName = relationName;
    }

    public void setIndex(Integer index) {
    	this.index = index;
    }

    public void setType(String type) {
    	this.type = type;
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
