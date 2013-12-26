package net.posick.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class TreeIterator<T extends Tree<T, V>, V> implements Iterator<T>
{
    private Stack<T> stack = new Stack<T>();
    
    private boolean recursive;
    
    private boolean siblings;
    
    private T currentNode;
    
    private T ignoreNode;
    
    
    public TreeIterator(T node, boolean recursive, boolean siblings, T ignore)
    {
        currentNode = node;
        this.recursive = recursive;
        this.siblings = siblings;
        this.ignoreNode = ignore;
    }
    

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove()
    {
        currentNode.remove();
    }
    

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext()
    {
        if (currentNode != null)
        {
            if (currentNode == ignoreNode)
            {
                currentNode = currentNode.getNextSibling();
            }
            
            return currentNode != null;
        } else
        {
            return false;
        }
    }
    

    /**
     * @see java.util.Iterator#next()
     */
    public T next()
    {
        T temp = null;
        
        if (currentNode != null)
        {
            temp = currentNode;
            
            if (recursive)
            {
                if (currentNode.getFirstChild() != null)
                {
                    stack.push(currentNode);
                    currentNode = currentNode.getFirstChild();
                } else if (currentNode.getNextSibling() != null)
                {
                    if (stack.size() > 0)
                    {
                        currentNode = currentNode.getNextSibling();
                    } else
                    {
                        if (siblings)
                        {
                            currentNode = currentNode.getNextSibling();
                        } else
                        {
                            currentNode = null;
                        }
                    }
                } else
                {
                    currentNode = null;
                    
                    while (stack.size() > 0 && currentNode == null)
                    {
                        currentNode = stack.pop();
                        
                        if (stack.size() > 0)
                        {
                            currentNode = currentNode.getNextSibling();
                        } else
                        {
                            if (siblings)
                            {
                                currentNode = currentNode.getNextSibling();
                            } else
                            {
                                currentNode = null;
                            }
                        }
                    }
                }
            } else
            {
                currentNode = currentNode.getNextSibling();
            }
            
            // If we are supposed to ignore this node get next node.
            if (temp == ignoreNode)
            {
                temp = next();
            }
        } else
        {
            throw new NoSuchElementException();
        }
        
        return temp;
    }
}
