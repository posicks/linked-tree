A generic, general purpose, extensible Java linked tree data structure.  Implemented as a tree of linked nodes.  The tree structure is represented by 1 class, AbstractTree, that may be extended to contain custom payloads.  The tree node is JAXB annotated to support XML serialization.

Contains the abstract base tree node (AbstractTree) and an simple extension called LinkedTree.

Example:
```
@XmlRootElement(name = "LinkedTree")
@XmlType(name="LinkedTree", propOrder = {"value"})
@XmlAccessorType(XmlAccessType.NONE)
public class LinkedTree<V> extends AbstractTree<LinkedTree<V>, V>
{
    private static final long serialVersionUID = -5911418043977126488L;
    
    @XmlElement(name="Value")
    private V value;

    public LinkedTree()
    {
    }
    

    @Override
    public V getValue()
    {
        return value;
    }
    
    
    @Override
    public void setValue(V value)
    {
        this.value = value;
    }
}
```

Example Program:
```
public class Test
{
    
    public Test()
    {
    }
    
    
    public static void main(String[] args)
    throws JAXBException
    {
        LinkedTree<String> tree = new LinkedTree<String>();
        
        tree.setValue("Root");
        LinkedTree<String> child1 = tree.addChild("Child 1");
        LinkedTree<String> child11 = child1.addChild("Child 1-1");
        LinkedTree<String> child111 = child11.addChild("Child 1-1-1");
        LinkedTree<String> child12 = child1.addChild("Child 1-2");
        LinkedTree<String> child13 = child1.addChild("Child 1-3");
        LinkedTree<String> child2 = tree.addChild("Child 2");
        LinkedTree<String> child21 = child2.addChild("Child 2-1");
        LinkedTree<String> child211 = child21.addChild("Child 2-1-1");
        LinkedTree<String> child22 = child2.addChild("Child 2-2");
        LinkedTree<String> child23 = child2.addChild("Child 2-3");
        LinkedTree<String> child3 = tree.addChild("Child 3");
        LinkedTree<String> child31 = child3.addChild("Child 3-1");
        LinkedTree<String> child311 = child31.addChild("Child 3-1-1");
        LinkedTree<String> child32 = child3.addChild("Child 3-2");
        LinkedTree<String> child33 = child3.addChild("Child 3-3");
        
        System.out.println(tree.toString());
        System.out.println("====================================================");
        String xml = AbstractTree.toXML(tree);
        System.out.println(xml);
        System.out.println("====================================================");
        tree = AbstractTree.loadFromXML(xml, new Class[]{LinkedTree.class});
        System.out.println(tree.toString());
        xml = AbstractTree.toXML(tree);
        System.out.println(xml);
    }
}
```