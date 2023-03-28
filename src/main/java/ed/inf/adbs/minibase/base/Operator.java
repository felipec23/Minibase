package ed.inf.adbs.minibase.base;

import java.util.List;
import java.util.function.Predicate;

public abstract class Operator {
    public Operator(Query query) {
    }


    public abstract List<Term> getTermsOfRelationalAtom();

    public abstract List<Term> getTermsOfRelationalAtom(String relationAtomName);

    public Tuple getNextTuple() {
        return null;
    }

    public void reset() {
    }

    public void open() {
    }

    public void close() {
    }

    public void dump() {
    }

    public void dump(String fileName) {
    }

    public String getRelationName() {
        return null;
    }

    public List<String> getSchemaOfRelation() {
        return null;
    }

    public boolean hasNext() {
        return false;
    }
}
