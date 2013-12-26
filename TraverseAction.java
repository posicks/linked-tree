package net.posick.tree;

/**
 * The Tree defines the behavior for a Tree traversal action.
 * 
 * @author Steve Posick
 * 
 * @see Tree
 */
public interface TraverseAction<T extends Tree<T, V>, V>
{
    /**
     * The action code that tells the Tree traverser to continue with
     * the traversal of the Tree as normal.
     */
    public static final int CONTINUE = 0;
    
    /**
     * The action code that tells the Tree traverser to stop the
     * traversal of the Tree.
     */
    public static final int STOP_TREE = 1;
    
    /**
     * The action code that tells the Tree traverser to stop the
     * traversal of the current branch and move onto the next.
     */
    public static final int STOP_BRANCH = 2;
    
    
    /**
     * Fired for each Tree during traversal. Allows programmatic
     * control of the traversal as will as providing a simple means to
     * perform some function on the tree's data.
     * 
     * @see Tree
     * @param node
     * @param level
     * @return The Action code.
     */
    public int action(T node, int level);
}
