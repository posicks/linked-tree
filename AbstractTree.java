package net.posick;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The AbstractTree is an abstract base class implementing a linked tree structure.
 * Each instance of the AbstractTree is a tree node, nodes are linked as
 * siblings, parent, or children to construct the tree.
 *
 * @author Steve Posick
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@XmlRootElement(name = "AbstractTree")
@XmlType(name="AbstractTree", propOrder = {"children"})
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractTree<T extends AbstractTree, V> implements Serializable, Cloneable
{
    private static final long serialVersionUID = 200802251417L;
    
    
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
     * The Tree defines the behavior for a Tree traversal action.
     * 
     * @author Steve Posick
     * @see Tree
     */
    public static interface TraverseAction<T extends AbstractTree>
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
    

    protected static class TreeIterator<T extends AbstractTree> implements Iterator
    {
        private Stack<AbstractTree> stack = new Stack<AbstractTree>();
        
        private boolean recursive;
        
        private boolean siblings;
        
        private AbstractTree currentNode;
        
        private AbstractTree ignoreNode;
        
        
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
            AbstractTree.remove(currentNode);
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
            AbstractTree temp = null;
            
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
            
            return (T) temp;
        }
    }
    

    protected class TreeList extends AbstractList<T>
    {
        private AbstractTree firstNode;
        
        private AbstractTree ignoreNode;
        
        private boolean recursive;
        
        private boolean siblings;
        
        
        public TreeList(AbstractTree node, boolean recursive, boolean siblings, AbstractTree ignoreNode)
        {
            AbstractTree firstNode;
            
            // Find first Sibling.
            if (node != null)
            {
                if (node.parent != null)
                {
                    firstNode = node.parent.child;
                } else
                {
                    firstNode = node;
                    while (firstNode.previousSibling != null)
                    {
                        firstNode = firstNode.previousSibling;
                    }
                }
            } else
            {
                firstNode = null;
            }
            
            this.firstNode = firstNode;
            this.ignoreNode = ignoreNode;
            this.siblings = siblings;
            this.recursive = recursive;
        }
        

        /**
         * @see java.util.AbstractCollection#add(java.lang.Object)
         */
        @Override
        public boolean add(T newNode)
        {
            if (firstNode == null)
            {
                firstNode = newNode;
                AbstractTree.add(AbstractTree.this, newNode, LASTCHILD);
            } else
            {
                AbstractTree.add(firstNode, newNode, LAST);
            }
            return true;
        }
        

        /**
         * @see java.util.AbstractCollection#remove(java.lang.Object)
         */
        @Override
        public boolean remove(Object object)
        {
            if (object instanceof AbstractTree)
            {
                AbstractTree.remove((AbstractTree) object);
                return true;
            }
            
            return false;
        }
        

        /**
         * @see java.util.AbstractCollection#iterator()
         */
        @Override
        public Iterator<T> iterator()
        {
            return new TreeIterator(firstNode, recursive, siblings, ignoreNode);
        }
        

        /**
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size()
        {
            int count = 0;
            Iterator<T> iterator = iterator();
            while (iterator.hasNext() && iterator.next() != null)
            {
                count++;
            }
            
            return count;
        }


        @Override
        public T get(int index)
        {
            int count = 0;
            Iterator<T> iterator = iterator();
            T value;
            while (iterator.hasNext() && (value = iterator.next()) != null)
            {
                if (count++ == index)
                {
                    return value;
                }
            }
            
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds, 0 thru " + (size() - 1));
        }
    }
    
    
    /**
     * Creates a new Tree node with the specified value.
     */
    public AbstractTree()
    {
    }
    

    /**
     * Creates a new Tree node with the specified value.
     * 
     * @param value The value
     */
    public AbstractTree(V value)
    {
        setValue(value);
    }
    

    /**
     * Creates a new Tree node with the specified value and automatically
     * adds it as a child to the specified node.
     * 
     * @param parent The parent node
     * @param value The value
     */
    public AbstractTree(T parent, V value)
    {
        setValue(value);
        setParent(parent);
    }
    
    
    /**
     * Creates a new Tree built with the specified structure.
     * 
     * @param ancestors The parent hierarchy
     * @param value The value
     */
    public AbstractTree(List<V> ancestors, V value)
    {
        create((Class<T>) getClass(), (T) this, ancestors, value);
    }
    
    
    private T parent;
    
    private T child;
    
    private T nextSibling;
    
    private T previousSibling;
    

    /**
     * Gets the parent for this node in the Tree.
     * 
     * @return the parent for this node in the Tree
     */
    public T getParent()
    {
        return parent;
    }
    

    /**
     * Sets the parent for this node in the Tree.
     * 
     * @param parent The parent for this node in the Tree
     */
    public void setParent(T parent)
    {
        AbstractTree.add(parent, this, LASTCHILD);
    }
    

    /**
     * Gets the first child for this node in the Tree.
     * 
     * @return the the first child for this node in the Tree
     */
    public T getFirstChild()
    {
        return child;
    }
    

    /**
     * Sets the first child for this node in the Tree.
     * 
     * @param child The first child for this node in the Tree
     */
    public void setFirstChild(T child)
    {
        AbstractTree.add(this, child, CHILD);
    }
    

    /**
     * Gets the next sibling for this node in the Tree.
     * 
     * @return The next sibling for this node in the Tree
     */
    public T getNextSibling()
    {
        return nextSibling;
    }
    

    /**
     * Sets the next sibling for this node in the Tree.
     * 
     * @param nextSibling The next sibling for this node in the Tree.
     */
    public void setNextSibling(T nextSibling)
    {
        AbstractTree.add(nextSibling, this, PREVIOUS);
    }
    

    /**
     * Gets the previous sibling for this node in the Tree.
     * 
     * @return The previous sibling for this node in the Tree
     */
    public T getPreviousSibling()
    {
        return nextSibling;
    }
    

    /**
     * Sets the previous sibling for this node in the Tree.
     * 
     * @param previousSibling The previous sibling for this node in the Tree
     */
    public void setPreviousSibling(T previousSibling)
    {
        AbstractTree.add(previousSibling, this, NEXT);
    }
    
    
    /**
     * Adds a new node to the tree using this node as a reference point.
     * 
     * @param newNode The new node to add
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     */
    public T add(T newNode, int mode)
    {
        return (T) add(this, newNode, mode);
    }
    
    
    /**
     * Adds a new node to the tree using this node as a reference point.
     * 
     * @param value The value for the new node
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     */
    public T add(V value, int mode)
    {
        return (T) add(this, newInstance((Class<? extends AbstractTree>) getClass(), value), mode);
    }
    
    
    /**
     * Adds a new child node to the tree using this node as a reference point.
     * 
     * @param value The value for new node
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     * @see add(T, LASTCHILD)
     */
    public T addChild(V value)
    {
        return (T) add(this, newInstance((Class<? extends AbstractTree>) getClass(), value), LASTCHILD);
    }
    
    
    /**
     * Adds a new sibling node to the tree using this node as a reference point.
     * 
     * @param value The value for new node
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     * @see add(T, LAST)
     */
    public T addSibling(V value)
    {
        return (T) add(this, newInstance((Class<? extends AbstractTree>) getClass(), value), LAST);
    }
    

    /**
     * Gets the first child that has this value.
     * 
     * @param value The value
     * @return The first child that has this value or null.
     */
    public T getChild(V value)
    {
        if (value != null)
        {
            List<T> children = getChildren();
            for (T node : children)
            {
                if (value.equals(node.getValue()))
                {
                    return node;
                }
            }
        }
        return null;
    }
    

    /**
     * Gets the first sibling that has this value.
     * 
     * @param value The value
     * @return The first sibling that has this value or null.
     */
    public T getSibling(V value)
    {
        if (value != null)
        {
            List<T> children = getSiblings();
            for (T node : children)
            {
                if (value.equals(node.getValue()))
                {
                    return node;
                }
            }
        }
        return null;
    }
    
    
    /**
     * Creates the specified resource with the appropriate hierarchy.
     * 
     * @param ancestors The ancestors aka. parent hierarchy
     * @param resource The resource
     */
    public void set(List<V> ancestors, V value)
    {
        create((Class<T>) getClass(), (T) this, ancestors, value);
    }
    
    
    /**
     * Adds a new child node to the tree using this node as a reference point.
     * 
     * @param newNode The new node to add
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     * @see add(T, LASTCHILD)
     */
    public T addChild(T newNode)
    {
        return (T) add(this, newNode, LASTCHILD);
    }
    
    
    /**
     * Adds a new sibling node to the tree using this node as a reference point.
     * 
     * @param newNode The new node to add
     * @param mode The node indicator, how the node will be added
     * @return The node that was added to the Tree
     * @see add(T, LAST)
     */
    public T addSibling(T newNode)
    {
        return (T) add(this, newNode, LAST);
    }
    

    /**
     * Replaces this node with the specified node.
     * 
     * @param newNode The node to replace this node with.
     */
    public void replace(AbstractTree newNode)
    {
        replace(this, newNode);
    }
    

    /**
     * Deletes this node from the tree.
     */
    public void remove()
    {
        remove(this);
    }
    

    /**
     * Tests if this node has children.
     * 
     * @return True if this node has children
     */
    public boolean hasChildren()
    {
        return child == null ? false : true;
    }
    
    
    /**
     * Returns an Iterator of all the children of this node, recursively.
     * 
     * @return The iterator of children.
     */
    public List<T> getDescendants()
    {
        return new TreeList(child, true, true, null);
    }
    

    /**
     * Returns an Iterator of all the direct children of this node.
     * 
     * @return The iterator of children.
     */
    public List<T> getChildren()
    {
        return new TreeList(child, false, true, null);
    }
    

    /**
     * Sets the children for this node in the Tree.
     * 
     * @param children The first child for this node in the Tree
     */
    @XmlElement(name="Node")
    public void setChildren(List<T> children)
    {
        for (T node : children)
        {
            AbstractTree.add(this, node, LASTCHILD);
        }
    }
    

    /**
     * Returns an Iterator of this nodes siblings. Includes this node in the
     * Iterator.
     * 
     * @return The iterator of siblings.
     */
    public List<T> getSiblings()
    {
        return new TreeList(this, false, true, this);
    }
    
    
    public abstract V getValue();
    

    public abstract void setValue(V value);
    

    /**
     * Returns the root most node of the tree.
     * 
     * @return
     */
    public T getRoot()
    {
        return (T) getRoot(this);
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        final StringBuilder buffer = new StringBuilder();
        
        traverse(getRoot(), new TraverseAction()
        {
            public int action(AbstractTree node, int level)
            {
                for (int index = 1; index < level; index++)
                {
                    buffer.append("\t");
                }
                buffer.append(node.getClass().getSimpleName()).append(" [").append(node.getValue()).append("]\n");
                return TraverseAction.CONTINUE;
            }
        });
        
        return buffer.toString();
    }
    
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {
        if (object instanceof AbstractTree)
        {
            Object thatValue = ((AbstractTree) object).getValue();
            Object thisValue = getValue();
            
            return (thatValue == thisValue) || (thatValue != null && thatValue.equals(thisValue));
        }
        
        return false;
    }
    
    
    /**
     * Adds a node to the tree.
     * 
     * @param refNod The reference node, reference point within tree
     * @param newNode The node that is to be added.
     * @param mode The node indicator, how the node will be added
     */
    public static <T extends AbstractTree> T add(AbstractTree refNode, T newNode, int mode)
    {
        switch (mode)
        {
            case PARENT:
                newNode.parent = refNode.parent;
                newNode.child = refNode;
                if (refNode.parent != null)
                {
                    refNode.parent.child = newNode;
                }
                refNode.parent = newNode;
                newNode.nextSibling = null;
                newNode.previousSibling = null;
                break;
            case LASTCHILD:
            case CHILD:
                // Add a child node. If one already exists replace
                // it and assign the current child to be a sibling.
                if (refNode.child != null)
                {
                    if (mode == CHILD)
                    {
                        refNode = (T) refNode.child;
                        if (refNode.parent.child == refNode)
                        {
                            refNode.parent.child = newNode;
                        }
                        newNode.parent = refNode.parent;
                        newNode.nextSibling = refNode;
                        newNode.previousSibling = refNode.previousSibling;
                        if (refNode.previousSibling != null)
                        {
                            refNode.previousSibling.nextSibling = newNode;
                        }
                        refNode.previousSibling = newNode;
                    } else if (mode == LASTCHILD)
                    {
                        refNode = (T) refNode.child;
                        while (refNode.nextSibling != null)
                        {
                            refNode = (T) refNode.nextSibling;
                        }
                        newNode.parent = refNode.parent;
                        newNode.nextSibling = null;
                        newNode.previousSibling = refNode;
                        refNode.nextSibling = newNode;
                    }
                } else
                {
                    refNode.child = newNode;
                    newNode.parent = refNode;
                    newNode.nextSibling = null;
                    newNode.previousSibling = null;
                }
                break;
            case NEXT:
                newNode.parent = refNode.parent;
                newNode.nextSibling = refNode.nextSibling;
                newNode.previousSibling = refNode;
                if (refNode.nextSibling != null)
                {
                    refNode.nextSibling.previousSibling = newNode;
                }
                refNode.nextSibling = newNode;
                break;
            case PREVIOUS:
                if (refNode.parent.child == refNode)
                {
                    refNode.parent.child = newNode;
                }
                newNode.parent = refNode.parent;
                newNode.nextSibling = refNode;
                newNode.previousSibling = refNode.previousSibling;
                if (refNode.previousSibling != null)
                {
                    refNode.previousSibling.nextSibling = newNode;
                }
                refNode.previousSibling = newNode;
                break;
            case LAST:
                while (refNode.nextSibling != null)
                {
                    refNode = (T) refNode.nextSibling;
                }
                newNode.parent = refNode.parent;
                newNode.nextSibling = refNode.nextSibling;
                newNode.previousSibling = refNode;
                if (refNode.nextSibling != null)
                {
                    refNode.nextSibling.previousSibling = newNode;
                }
                refNode.nextSibling = newNode;
                break;
        }
        
        return newNode;
    }
    

    /**
     * Replaces a node with another node.
     * 
     * @param oldNode The node to replace.
     * @param newNode The node that will replace the old node.
     */
    public static void replace(AbstractTree oldNode, AbstractTree newNode)
    {
        if (oldNode == null)
            return;
        if (newNode == null)
        {
            remove(oldNode);
            return;
        }
        
        add(oldNode, newNode, PREVIOUS);
        remove(oldNode);
    }
    

    /**
     * This method deletes the specified node and all its descendants.
     * 
     * @param refNod The node to be removed from the tree.
     */
    public static void remove(AbstractTree refNod)
    {
        if (refNod == null)
            return;
        
        if (refNod.parent != null && refNod.parent.child == refNod)
            refNod.parent.child = refNod.nextSibling;
        if (refNod.previousSibling != null && refNod.previousSibling.nextSibling == refNod)
            refNod.previousSibling.nextSibling = refNod.nextSibling;
        if (refNod.nextSibling != null && refNod.nextSibling.previousSibling == refNod)
            refNod.nextSibling.previousSibling = refNod.previousSibling;
        
        refNod.parent = null;
        refNod.nextSibling = null;
        refNod.previousSibling = null;
    }
    

    /**
     * Returns the root tree node for the specified node.
     * 
     * @param refNod The reference node, point of reference
     * @return The trees root node
     */
    public static AbstractTree getRoot(AbstractTree refNod)
    {
        if (refNod == null)
            return null;
        
        AbstractTree currentNode = refNod;
        
        while (currentNode.parent != null)
        {
            currentNode = currentNode.parent;
        }
        return currentNode;
    }
    

    /**
     * Traverses the tree using the reference node as a starting point.
     * 
     * @param refNode The reference node, starting point
     * @param action The action to perform for each node, flow control
     */
    public static void traverse(AbstractTree refNode, TraverseAction action)
    {
        int level;
        int result;
        Stack<AbstractTree> stack = new Stack<AbstractTree>();
        AbstractTree node;
        
        while (refNode != null)
        {
            node = refNode;
            level = stack.size() + 1;
            result = action.action(node, level);
            
            switch (result)
            {
                case TraverseAction.CONTINUE:
                    if (refNode.child != null)
                    {
                        stack.push(refNode);
                        refNode = refNode.child;
                    } else if (refNode.nextSibling != null)
                    {
                        refNode = refNode.nextSibling;
                    } else
                    {
                        refNode = null;
                        while (stack.size() > 0 && refNode == null)
                        {
                            refNode = stack.pop();
                            
                            refNode = refNode.nextSibling;
                        }
                    }
                    break;
                case TraverseAction.STOP_BRANCH:
                    if (refNode.nextSibling != null)
                    {
                        refNode = refNode.nextSibling;
                    } else
                    {
                        refNode = null;
                        while (stack.size() > 0 && refNode == null)
                        {
                            refNode = stack.pop();
                            
                            refNode = refNode.nextSibling;
                        }
                    }
                    break;
                case TraverseAction.STOP_TREE:
                    return;
            }
        }
    }
    

    /**
     * Returns a string containing serialized form of the tree as XML
     * 
     * @param tree The root node of the tree
     * @return The XML representation of the tree
     */
    public static String toXML(AbstractTree tree)
    {
        try
        {
            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(new Class[] {tree.getClass()});
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(tree, writer);
            return writer.toString();
        } catch (JAXBException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    

    /**
     * Creates and loads a tree from the provided XML.
     * 
     * @param xml The XML representing the Tree
     * @param classes The JAXB Classes required for deserialization
     * @return The tree
     * @throws JAXBException
     */
    public static <T extends AbstractTree> T loadFromXML(CharSequence xml, Class[] classes)
    throws JAXBException
    {
        JAXBContext context = JAXBContext.newInstance(classes);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(new StringReader(xml.toString()));
    }
    
    
    protected static <T extends AbstractTree> T newInstance(Class<T> clazz)
    {
        Constructor constructor;
        try
        {
            constructor = clazz.getConstructor((Class[]) null);
            return (T) constructor.newInstance((Object[]) null);
        } catch (SecurityException e)
        {
            throw e;
        } catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Cannot find zero arg constructor for \"" + clazz.getName() + "\"!", e);
        } catch (IllegalArgumentException e)
        {
            throw e;
        } catch (InstantiationException e)
        {
            throw new RuntimeException("Cannot instantiate \"" + clazz.getName() + "\"!", e);
        } catch (IllegalAccessException e)
        {
            throw new RuntimeException("Cannot instantiate \"" + clazz.getName() + "\"!", e);
        } catch (InvocationTargetException e)
        {
            throw new RuntimeException("Cannot instantiate \"" + clazz.getName() + "\"!", e);
        }
    }
    
    
    protected static <T extends AbstractTree, V> T newInstance(Class<T> clazz, V value)
    {
        Constructor constructor;
        try
        {
            constructor = clazz.getConstructor(new Class[] {value.getClass()});
            return (T) constructor.newInstance((Object[]) new Object[] {value});
        } catch (SecurityException e)
        {
            throw e;
        } catch (NoSuchMethodException e)
        {
            T node = newInstance(clazz);
            node.setValue(value);
            return node;
        } catch (IllegalArgumentException e)
        {
            throw e;
        } catch (InstantiationException e)
        {
            throw new RuntimeException("Cannot instantiate \"" + clazz.getName() + "\"!", e);
        } catch (IllegalAccessException e)
        {
            throw new RuntimeException("Cannot instantiate \"" + clazz.getName() + "\"!", e);
        } catch (InvocationTargetException e)
        {
            throw new RuntimeException("Cannot instantiate \"" + clazz.getName() + "\"!", e);
        }
    }
    
    
    /**
     * Constructs a tree to accommodate the provided structure. 
     * 
     * @param <T> The tree type
     * @param <V> The value type
     * @param clazz The class that represents the type of Tree
     * @param ancestors The ancestors aka. parent hierarchy
     * @param value The value
     * @return A tree to accommodate the provided structure
     */
    public static <T extends AbstractTree, V> T create(Class<T> clazz, List<V> ancestors, V value)
    {
        return create(clazz, null, ancestors, value);
    }
    
    
    /**
     * Constructs a tree to accommodate the provided structure.
     * 
     * @param <T> The tree type
     * @param <V> The value type
     * @param root The root node of a pre-existing tree structure or null
     * @param ancestors The ancestors aka. parent hierarchy
     * @param value The value
     * @return A tree to accommodate the provided structure
     */
    public static <T extends AbstractTree, V> T create(Class<T> clazz, T root, List<V> ancestors, V value)
    {
        if (root == null)
        {
            root = newInstance(clazz);
        }
        
        if (ancestors != null && ancestors.size() > 0)
        {
            AbstractTree currentNode = null;
            for (V ancestor : ancestors)
            {
                if (currentNode == null)
                {
                    V thisValue = (V) root.getValue();
                    if (thisValue == null || thisValue.equals(ancestor))
                    {
                        root.setValue(ancestor);
                        currentNode = root;
                    } else
                    {
                        T temp = (T) root.getSibling(ancestor);
                        if (temp != null)
                        {
                            currentNode = temp;
                        } else
                        {
                            currentNode = root.addSibling(ancestor);
                        }
                    }
                } else
                {
                    AbstractTree temp = currentNode.getChild(ancestor);
                    if (temp != null)
                    {
                        currentNode = temp;
                    } else
                    {
                        currentNode = currentNode.addChild(ancestor);
                    }
                } 
            }
            
            if (currentNode.getChild(value) == null)
            {
                currentNode.addChild(value);
            }
        } else
        {
            root.setValue(value);
        }
        
        return root;
    }
    
    
    public T clone()
    {
        try
        {
            return (T) super.clone();
        } catch (CloneNotSupportedException e)
        {
            return null;
        }
    }
}
