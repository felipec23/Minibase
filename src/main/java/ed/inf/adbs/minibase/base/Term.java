package ed.inf.adbs.minibase.base;

//public class Term {
//
//
////    public Term evaluate(Tuple tuple) {
////        return tuple;
////    }
//}
public class Term {


    //    Create a function to check if two constants are equal:
    public boolean equals(Term right) {

//        System.out.println("Comparing from Term: " + this.toString() + " and " + right.toString());
        if (this.toString().equals(right.toString())) {
//            System.out.println("They are equal");
            return true;
        }
        else{
//            System.out.println("They are not equal");
            return false;
        }

    }

//    public Term evaluate(Tuple tuple) {
//        return tuple;
//    }
}


