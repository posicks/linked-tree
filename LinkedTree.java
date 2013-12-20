package net.posick;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "LinkedTree")
@XmlType(name="LinkedTree", propOrder = {"value"})
@XmlAccessorType(XmlAccessType.NONE)
public class LinkedTree<V> extends AbstractTree<LinkedTree<V>, V>
{
    private static final long serialVersionUID = -4969044733002136970L;
    
    @XmlElement(name="Value")
    private V value;
    
    public LinkedTree()
    {
        super();
    }
    
    
    public LinkedTree(V value)
    {
        super(value);
    }
    
    
    public LinkedTree(LinkedTree<V> parent, V value)
    {
        super(parent, value);
    }
    
    
    public LinkedTree(List<V> ancestors, V value)
    {
        super(ancestors, value);
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
