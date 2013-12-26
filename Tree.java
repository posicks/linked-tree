package net.posick.tree;

import java.util.List;

/**
 * @author Steve Posick
 *
 * @param <T> The Tree node type.
 * @param <V> The value contained within the Tree node
 */
public interface Tree<T extends Tree<T, V>, V>
{
    /**
     * <code>PARENT</code> node indicator.
     */
    public static final int PARENT = 0;
    
    /**
     * <code>CHILD</code> node indicator.
     */
    public static final int CHILD = 1;
    
    /**
     * <code>LASTCHILD</code> node indicator.
     */
    public static final int LASTCHILD = 2;
    
    /**
     * <code>NEXT</code> node indicator.
     */
    public static final int NEXT = 3;
    
    /**
     * <code>PREVIOUS</code> node indicator.
     */
    public static final int PREVIOUS = 4;
    
    /**
     * <code>LAST</code> node indicator.
     */
    public static final int LAST = 5;
    
    
    /**
     * Gets the parent for this node in the Tree.
     * 
     * @return the parent for this node in the Tree
     */
    public T getParent();
    
    
    /**
     * Sets the parent for this node.
     * 
     * @return the parent for this node
     */
    public void setParent(T parent);
    
    
    /**
     * Gets the first child for this node in the Tree.
     * 
     * @return the the first child for this node in the Tree
     */
    public T getFirstChild();
    
    
    /**
     * Sets the first child for this node in the Tree.
     * 
     * @return the the first child for this node in the Tree
     */
    public void setFirstChild(T child);
    
    
    /**
     * Gets the next sibling for this node in the Tree.
     * 
     * @return The next sibling for this node in the Tree
     */
    public T getNextSibling();
    
    
    /**
     * Sets the next sibling for this node in the Tree.
     * 
     * @return The next sibling for this node in the Tree
     */
    public void setNextSibling(T sibling);
    
    
    /**
     * Gets the previous sibling for this node in the Tree.
     * 
     * @return The previous sibling for this node in the Tree
     */
    public T getPreviousSibling();
    
    
    /**
     * Sets the previous sibling for this node in the Tree.
     * 
     * @return The previous sibling for this node in the Tree
     */
    public void setPreviousSibling(T sibling);
    
    
    /**
     * Adds a new node to the tree using this node as a reference point.
     * 
     * @param newNode The new node to add
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     */
    public T add(T newNode, int mode);
    
    
    /**
     * Adds a new node to the tree using this node as a reference point.
     * 
     * @param value The value for the new node
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     */
    public T add(V value, int mode);
    
    
    /**
     * Gets the first child that has this value.
     * 
     * @param value The value
     * @return The first child that has this value or null.
     */
    public T getChild(V value);
    
    
    /**
     * Gets the first sibling that has this value.
     * 
     * @param value The value
     * @return The first sibling that has this value or null.
     */
    public T getSibling(V value);
    
    
    /**
     * Replaces this node with the specified node.
     * 
     * @param newNode The node to replace this node with.
     */
    public void replace(T newNode);
    
    
    /**
     * Deletes this node from the tree.
     */
    public void remove();
    
    
    /**
     * Tests if this node has children.
     * 
     * @return True if this node has children
     */
    public boolean hasChildren();
    
    
    /**
     * Returns an Iterator of all the children of this node, recursively.
     * 
     * @return The iterator of children.
     */
    public List<T> getDescendants();
    
    
    /**
     * Returns an Iterator of all the direct children of this node.
     * 
     * @return The iterator of children.
     */
    public List<T> getChildren();
    
    
    /**
     * Returns an Iterator of this nodes siblings. Includes this node in the
     * Iterator.
     * 
     * @return The iterator of siblings.
     */
    public List<T> getSiblings();
    
    
    /**
     * Returns the value contained within this node of the Tree.
     * 
     * @return The value contained within this node of the Tree
     */
    public V getValue();
    
    
    /**
     * Sets the value to store within this node of the Tree.
     * 
     * @param value The value to store within this node of the Tree
     */
    public void setValue(V value);
    
    
    /**
     * Returns the root most node of the tree.
     * 
     * @return
     */
    public T getRoot();
    
    
    /**
     * Traverses the tree using this node as a starting point.  The traverse action is executed for
     * each node encountered.
     * 
     * @param action The action to perform for each node, flow control
     */
    public void traverse(TraverseAction<T, V> action);
}