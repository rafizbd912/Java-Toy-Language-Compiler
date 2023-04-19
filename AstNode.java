import java.util.ArrayList;
import java.util.List;

public class AstNode {
    private final AstNodeType type;
    private final String value;
    private final List<AstNode> children;

    public AstNode(AstNodeType type) {
        this(type, null);
    }

    public AstNode(AstNodeType type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public AstNodeType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void addChild(AstNode child) {
        children.add(child);
    }

    public List<AstNode> getChildren() {
        return children;
    }
}
