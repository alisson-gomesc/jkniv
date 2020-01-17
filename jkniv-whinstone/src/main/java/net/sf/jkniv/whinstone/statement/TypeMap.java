package net.sf.jkniv.whinstone.statement;

import net.sf.jkniv.sqlegance.types.ColumnType;

class TypeMap
{
    Class    type;
    ColumnType columnType;

    public TypeMap(Class type, ColumnType columnType)
    {
        super();
        this.type = type;
        this.columnType = columnType;
    }

    public ColumnType getColumnType()
    {
        return columnType;
    }
    
    public Class getType()
    {
        return type;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnType == null) ? 0 : columnType.hashCode());
        result = prime * result + ((type == null) ? 0 : type.getCanonicalName().hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeMap other = (TypeMap) obj;
        if (columnType != other.columnType)
            return false;
        if (type == null)
        {
            if (other.type != null)
                return false;
        }
        else if (!type.getCanonicalName().equals(other.type.getCanonicalName()))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "TypeMap [type=" + type + ", columnType=" + columnType + "]";
    }
    
    
}
