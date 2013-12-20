package net.posick;

import javax.xml.bind.JAXBException;

public class Test
{
    
    public Test()
    {
    }
    
    
    /**
     * @param args
     * @throws JAXBException 
     */
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
