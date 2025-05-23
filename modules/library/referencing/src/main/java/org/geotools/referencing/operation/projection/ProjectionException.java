/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2001-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.referencing.operation.projection;

import java.text.MessageFormat;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.measure.Latitude;
import org.geotools.metadata.i18n.ErrorKeys;

/**
 * Thrown by {@link MapProjection} when a map projection failed.
 *
 * @since 2.0
 * @version $Id$
 * @author André Gosselin
 * @author Martin Desruisseaux (IRD)
 */
public class ProjectionException extends TransformException {
    /** Serial number for interoperability with different versions. */
    private static final long serialVersionUID = 3031350727691500915L;

    /** Constructs a new exception with no detail message. */
    public ProjectionException() {}

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param pattern Error message format.
     * @param value An argument value to be formatted.
     */
    ProjectionException(final String pattern, final Object value) {
        super(MessageFormat.format(pattern, value));
    }

    /** Constructs a new exception with a detail message formatted for a latitude too close from a pole. */
    ProjectionException(final double latitude) {
        super(MessageFormat.format(ErrorKeys.POLE_PROJECTION_$1, new Latitude(Math.toDegrees(latitude))));
    }

    /** Constructs a new exception with the specified detail message. */
    public ProjectionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @since 2.5
     */
    public ProjectionException(final Throwable cause) {
        super(cause.getLocalizedMessage(), cause);
    }

    /** Constructs a new exception with the specified detail message and cause. */
    public ProjectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
