package org.qi4j.api.query.grammar;

import org.qi4j.api.composite.Composite;
import org.qi4j.api.property.Property;

/**
 * TODO
 */
public class PropertyNotNullSpecification<T>
    extends ExpressionSpecification
{
    PropertyFunction<T> property;

    public PropertyNotNullSpecification( PropertyFunction<T> property )
    {
        this.property = property;
    }

    public PropertyFunction<T> getProperty()
    {
        return property;
    }

    @Override
    public boolean satisfiedBy( Composite item )
    {
        Property<T> prop = property.map( item );

        if( prop == null )
        {
            return false;
        }

        return prop.get() != null;
    }

    @Override
    public String toString()
    {
        return property.toString() + "is not null";
    }
}
