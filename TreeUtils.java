package net.posick.tree;

@SuppressWarnings({"rawtypes","unchecked"})
public class TreeUtils
{
    /**
     * Adds a node to the tree.
     * 
     * @param refNod The reference node, reference point within tree
     * @param newNode The node that is to be added.
     * @param mode The node indicator, how the node will be added
     */
    public static <T extends Tree> T add(T refNode, T newNode, int mode)
    {
        switch (mode)
        {
            case Tree.PARENT:
                newNode.setParent(refNode.getParent());
                newNode.setFirstChild(refNode);
                if (refNode.getParent() != null)
                {
                    refNode.getParent().setFirstChild(newNode);
                }
                refNode.setParent(newNode);
                newNode.setNextSibling(null);
                newNode.setPreviousSibling(null);
                break;
            case Tree.LASTCHILD:
            case Tree.CHILD:
                // Add a child node. If one already exists replace
                // it and assign the current child to be a sibling.
                if (refNode.getFirstChild() != null)
                {
                    if (mode == Tree.CHILD)
                    {
                        refNode = (T) refNode.getFirstChild();
                        if (refNode.getParent().getFirstChild() == refNode)
                        {
                            refNode.getParent().setFirstChild(newNode);
                        }
                        newNode.setParent(refNode.getParent());
                        newNode.setNextSibling(refNode);
                        newNode.setPreviousSibling(refNode.getPreviousSibling());
                        if (refNode.getPreviousSibling() != null)
                        {
                            refNode.getPreviousSibling().setNextSibling(newNode);
                        }
                        refNode.setPreviousSibling(newNode);
                    } else if (mode == Tree.LASTCHILD)
                    {
                        refNode = (T) refNode.getFirstChild();
                        while (refNode.getNextSibling() != null)
                        {
                            refNode = (T) refNode.getNextSibling();
                        }
                        newNode.setParent(refNode.getParent());
                        newNode.setNextSibling(null);
                        newNode.setPreviousSibling(refNode);
                        refNode.setNextSibling(newNode);
                    }
                } else
                {
                    refNode.setFirstChild(newNode);
                    newNode.setParent(refNode);
                    newNode.setNextSibling(null);
                    newNode.setPreviousSibling(null);
                }
                break;
            case Tree.NEXT:
                newNode.setParent(refNode.getParent());
                newNode.setNextSibling(refNode.getNextSibling());
                newNode.setPreviousSibling(refNode);
                if (refNode.getNextSibling() != null)
                {
                    refNode.getNextSibling().setPreviousSibling(newNode);
                }
                refNode.setNextSibling(newNode);
                break;
            case Tree.PREVIOUS:
                if (refNode.getParent().getFirstChild() == refNode)
                {
                    refNode.getParent().setFirstChild(newNode);
                }
                newNode.setParent(refNode.getParent());
                newNode.setNextSibling(refNode);
                newNode.setPreviousSibling(refNode.getPreviousSibling());
                if (refNode.getPreviousSibling() != null)
                {
                    refNode.getPreviousSibling().setNextSibling(newNode);
                }
                refNode.setPreviousSibling(newNode);
                break;
            case Tree.LAST:
                while (refNode.getNextSibling() != null)
                {
                    refNode = (T) refNode.getNextSibling();
                }
                newNode.setParent(refNode.getParent());
                newNode.setNextSibling(refNode.getNextSibling());
                newNode.setPreviousSibling(refNode);
                if (refNode.getNextSibling() != null)
                {
                    refNode.getNextSibling().setPreviousSibling(newNode);
                }
                refNode.setNextSibling(newNode);
                break;
        }
        
        return (T) newNode;
    }
    

    /**
     * Replaces a node with another node.
     * 
     * @param oldNode The node to replace.
     * @param newNode The node that will replace the old node.
     */
    public static <T extends Tree> void replace(T oldNode, T newNode)
    {
        if (oldNode == null)
        {
            return;
        }
        
        if (newNode == null)
        {
            remove(oldNode);
        } else
        {
            newNode.setParent(oldNode.getParent());
            if (oldNode.getParent() != null && oldNode.getParent().getFirstChild() == oldNode)
            {
                oldNode.getParent().setFirstChild(newNode);
            }
            
            newNode.setNextSibling(oldNode.getNextSibling());
            if (oldNode.getNextSibling() != null)
            {
                oldNode.getNextSibling().setPreviousSibling(newNode);
            }
            
            newNode.setPreviousSibling(oldNode.getPreviousSibling());
            if (oldNode.getPreviousSibling() != null)
            {
                oldNode.getPreviousSibling().setNextSibling(newNode);
            }
            
            oldNode.setParent(null);
            oldNode.setNextSibling(null);
            oldNode.setPreviousSibling(null);
        }
    }
    

    /**
     * This method deletes the specified node and all its descendants.
     * 
     * @param refNode The node to be removed from the tree.
     */
    public static <T extends Tree> void remove(T refNode)
    {
        if (refNode == null)
        {
            return;
        }
        
        if (refNode.getParent() != null && refNode.getParent().getFirstChild() == refNode)
        {
            refNode.getParent().setFirstChild(refNode.getNextSibling());
        }
        if (refNode.getPreviousSibling() != null && refNode.getPreviousSibling().getNextSibling() == refNode)
        {
            refNode.getPreviousSibling().setNextSibling(refNode.getNextSibling());
        }
        if (refNode.getNextSibling() != null && refNode.getNextSibling().getPreviousSibling() == refNode)
        {
            refNode.getNextSibling().setPreviousSibling(refNode.getPreviousSibling());
        }
        
        refNode.setParent(null);
        refNode.setNextSibling(null);
        refNode.setPreviousSibling(null);
    }
}
