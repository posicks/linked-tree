package net.posick;

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
    private static final long serialVersionUID = -5911418043977126488L;
    
    @XmlElement(name="Value")
    private V value;

    public LinkedTree()
    {
        // TODO Auto-generated constructor stub
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
