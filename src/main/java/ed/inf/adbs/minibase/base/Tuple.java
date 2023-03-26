package ed.inf.adbs.minibase.base;

import java.util.List;

// Create a public class Tuple that stores X objects:
//public class Tuple {
//
//    private Object[] tuple;
//
//    public Tuple(Object... tuple) {
//        this.tuple = tuple;
//    }
//
//    public Object[] getTuple() {
//        return tuple;
//    }
//
//    public Object getTuple(int i) {
//        return tuple[i];
//    }
//
//    public String toString() {
//        String result = "";
//        for (int i = 0; i < tuple.length; i++) {
//            result += tuple[i] + " ";
//        }
//        return result;
//    }
//}


//Create a public class Tuple  that stores X terms:
public class Tuple {

    private Term[] tuple;

    public Tuple(Term... tuple) {
        this.tuple = tuple;
    }

    public Term[] getTuple() {
        return tuple;
    }

    public Term getTuple(int i) {
        return tuple[i];
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < tuple.length; i++) {
            result += tuple[i] + " ";
        }
        return result;
    }
}



//
//public class Tuple<T, U> {
//    public final T first;
//    public final U second;
//
//    public Tuple(T first, U second) {
//        this.first = first;
//        this.second = second;
//    }
//
//    public T getFirst() {
//        return first;
//    }
//
//    public U getSecond() {
//        return second;
//    }
//
//    public String toString() {
//        return "(" + first + ", " + second + ")";
//    }
//
//}












//   Function to get the tuple as a list:
//    public List<Object> toList() {
//        return List.of(first, second);
//    }

//    Tuple<String, Integer> tuple = new Tuple<>("hello", 42);



//  Create a class to represent a tuple. It can receive any number of arguments:

//public class Tuple {
//
//    private Object[] tuple;
//
//    public Tuple(Object... tuple) {
//        this.tuple = tuple;
//    }
//
//    public Object[] getTuple() {
//        return tuple;
//    }
//
//    public Term getTuple(int i) {
//        return tuple[i];
//    }
//
//    public String toString() {
////        Return the tuple as a string:
//        String tupleString = "(";
//        for (int i = 0; i < tuple.length; i++) {
//            tupleString += tuple[i];
//            if (i != tuple.length - 1) {
//                tupleString += ", ";
//            }
//        }
//        tupleString += ")";
//        return tupleString;
//    }
//
//}
