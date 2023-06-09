package ed.inf.adbs.minibase.base;
import java.util.List;

/**
 * Class created for the purpose of storing the name, schema and filepath of a table/relation.
 */
public class Relation {

    private String name;

    private List<String> schema;

    private String filePath;

    public Relation(String name, List schema, String filePath) {
        this.name = name;
        this.schema = schema;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public List<String> getSchema() {
        return schema;
    }

    public String getFilePath() {
        return filePath;
    }
}
