package net.sf.jkniv.whinstone.jdbc.domain.flat;

import net.sf.jkniv.whinstone.Filter;

public class ItemFilter implements Filter<Item>
{
    Float price;
    
    public ItemFilter(Float price)
    {
        this.price = price;
    }
    
    @Override
    public boolean isEqual(Item item)
    {
        return item.getPrice() >= 150;
    }
    
}
