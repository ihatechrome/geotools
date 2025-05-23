/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.xml.handlers.xsi;

import org.geotools.xml.XSIElementHandler;
import org.geotools.xml.schema.ElementGrouping;
import org.xml.sax.SAXException;

/**
 * Allows the developer to avoid instanceof operators when wishing to compress.
 *
 * @author dzwiers www.refractions.net
 */
public abstract class ElementGroupingHandler extends XSIElementHandler {
    /**
     * This will compress the given element based on it's type and the parent schema, allowing for references to be
     * resolved.
     */
    protected abstract ElementGrouping compress(SchemaHandler parent) throws SAXException;
}
