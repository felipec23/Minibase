# Minibase
This repository contains a small version of database implemented in Java using recursion and operators.
It receives a query as a string, parses it and apply the filters or joins contained in the query. It
reads from the CSV files the data and outputs the resulting tuples to another CSV file.

In the following lines I will explain the implementation 
steps for tasks 2 and 3. You may still find detailed descriptions on
each of the functions found in the files.

You can jump to the root folder using this [link:](https://github.com/felipec23/Minibase/tree/4714c856592ceed0b01e956fa1b2a74c229979f5/src/main/java/ed/inf/adbs/minibase)

## Extraction of conditions from body
Besides the normal atom comparisons the goal was to extract the
implicit and the equi-join conditions. For the first type ([x, 6, y]),
we look for atoms with constant objects. When we find a match, we save the
position of each constant as well as the name of the relation where  the
condition is being applied. This is very useful for further operators.
For the second type ([x, y], [x, u]), we iterate
over all the terms and find common terms between two different atoms. 
We also save the indexes and relations information. For both cases,
all this data is stored in a new comparison atom. Thus, in one variable
we have stored both the explicit conditions/comparisons, as well as
the implicit and equi-join types.

Later on, before doing any join, for each separate relation, we apply the filters
that apply to each relation, separately. And then we do apply the joins,
using also the same variable that stores both the atom comparisons as the
join conditions. Here we make sure that joins process the least possible
amount of tuples/rows, for optimization purposes.


## Optimization step
The idea for optimizing the query evaluation was to use, even before
scanning, only those variables that are actually needed in the process.
We discard those variables that won't ever be used in any operator. Most of 
the functions with this implementation are found in the Query Plan, given this
is being done from the very beginning. With this approach, we avoid sending
not-needed tuples to operators, saving memory.

Some further optimizations are the following: each element in a Tuple object
is casted originally as a term. This is an advantage in the Sum Operator since
we don't need to convert from Object, to String and then to Integer: since the 
IntegerConstant is instance of a Term, we just cast it and get the value. 

A HashMap is used for storing and updating the results of the sums. In that way, we iterate
only once through the tuples coming from the child, and efficiently store
the results. Furthermore, the key for each map is, for example: "1, 2, ", in the case
1 and 2 represent X and Y, and for those cases when 2 group-by variables are present.
This allows a faster writing process, since the key is a string that is ready to be
written to the file, without any further transformations.

Finally, we use an iterator for writing the Sum Operator results in the files.
By using an iterator, the entries are loaded into memory one at a time, 
rather than all at once. This can help reduce the memory footprint of the program, 
especially when dealing with large maps/files. Furthermore, we added a BufferedWriter
to efficiently buffering the output in memory before writing it to the file. It provides a more efficient 
way of writing to a file by reducing the number of times data is written to the disk.
