package org.qi4j.tutorials.services.step3;

import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

@Mixins( LibraryMixin.class )
public interface LibraryService
    extends Library, ServiceComposite
{
}