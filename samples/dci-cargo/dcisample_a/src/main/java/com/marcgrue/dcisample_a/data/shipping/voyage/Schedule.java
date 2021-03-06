package com.marcgrue.dcisample_a.data.shipping.voyage;

import java.util.List;
import org.qi4j.api.property.Property;
import org.qi4j.api.value.ValueComposite;

/**
 * A schedule is a series of {@link CarrierMovement}s.
 *
 * List of carrier movements is mandatory and immutable.
 */
public interface Schedule
    extends ValueComposite
{
    Property<List<CarrierMovement>> carrierMovements();
}
