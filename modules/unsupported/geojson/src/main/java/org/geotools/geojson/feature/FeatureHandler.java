/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geojson.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.DelegatingHandler;
import org.geotools.geojson.GeoJSONUtil;
import org.geotools.geojson.IContentHandler;
import org.geotools.geojson.geom.GeometryCollectionHandler;
import org.geotools.geojson.geom.GeometryHandler;
import org.json.simple.parser.ParseException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class FeatureHandler extends DelegatingHandler<SimpleFeature> {

    private int fid = 0;

    private String separator = "-";

    String id;

    Geometry geometry;

    List<Object> values;

    List<String> properties;

    CoordinateReferenceSystem crs;

    SimpleFeatureBuilder builder;

    AttributeIO attio;

    SimpleFeature feature;

    private String baseId = "feature";

    /** should we attempt to automatically build fids */
    private boolean autoFID = false;

    public FeatureHandler() {
        this(null, new DefaultAttributeIO());
    }

    public FeatureHandler(SimpleFeatureBuilder builder, AttributeIO attio) {
        this.builder = builder;
        this.attio = attio;
    }

    @Override
    public boolean startObject() throws ParseException, IOException {
        if (delegate != NULL) {
            return delegate.startObject();
        }

        if (properties == NULL_LIST) {
            properties = new ArrayList<>();
        } else if (properties != null) {
            // this must be the start of a complex object
            ComplexPropertyHandler propertyHandler = new ComplexPropertyHandler();
            delegate = propertyHandler;
            delegate.startObject();
            values.add(propertyHandler.getValue());
            return true;
        }

        return super.startObject();
    }

    @Override
    public boolean startObjectEntry(String key) throws ParseException, IOException {
        if (delegate != NULL) {
            return delegate.startObjectEntry(key);
        }
        if ("id".equals(key) && properties == null) {
            id = "";
            return true;
        } else if ("crs".equals(key) && properties == null /* it's top level, not a property */) {
            delegate = new CRSHandler();
            return true;
        } else if ("geometry".equals(key) && properties == null /* it's top level, not a property */) {
            delegate = new GeometryHandler(new GeometryFactory());
            return true;
        } else if ("properties".equals(key) && delegate == NULL) {
            properties = NULL_LIST;
            values = new ArrayList<>();
        } else if (properties != null && delegate == NULL) {
            properties.add(key);
            return true;
        }

        return super.startObjectEntry(key);
    }

    @Override
    public boolean startArray() throws ParseException, IOException {
        if (properties != null && delegate == NULL) {
            // array inside of properties
            ComplexPropertyHandler propertyHandler = new ComplexPropertyHandler();
            delegate = propertyHandler;
            delegate.startArray();
            values.add(propertyHandler.getValue());
            return true;
        }

        return super.startArray();
    }

    @Override
    public boolean endArray() throws ParseException, IOException {
        if (!delegate.endArray()) {
            // Delegate is done
            delegate = NULL;
        }
        return true; // But we're not
    }

    @Override
    public boolean endObject() throws ParseException, IOException {
        if (delegate instanceof IContentHandler) {
            boolean keepGoing = delegate.endObject();

            if (delegate instanceof GeometryHandler) {
                GeometryHandler geometryHandler = (GeometryHandler) delegate;
                Geometry g = geometryHandler.getValue();
                if (g != null || !(((GeometryHandler) delegate).getDelegate() instanceof GeometryCollectionHandler)) {
                    if (properties != null) {
                        // this is a regular property
                        values.add(g);
                    } else {
                        // its the default geometry
                        geometry = g;
                    }
                    delegate = NULL;
                }
            } else if (delegate instanceof CRSHandler) {
                crs = ((CRSHandler) delegate).getValue();
                delegate = UNINITIALIZED;
            }
            if (!keepGoing && delegate instanceof ComplexPropertyHandler) {
                // Oh, perhaps we added something that should have been a Geometry object
                int valueCount = values.size();
                if (valueCount > 0) {
                    Object justAdded = values.get(valueCount - 1);
                    Object perhapsReplacedGeometry = GeoJSONUtil.replaceGeometry(justAdded);
                    values.set(valueCount - 1, perhapsReplacedGeometry);
                }
                delegate = NULL;
            }

            return true;
        } else if (delegate == UNINITIALIZED) {
            delegate = NULL;
            return true;
        } else if (properties != null) {
            if (builder == null) {
                // no builder specified, build on the fly
                builder = createBuilder();
            }
            for (int i = 0; i < properties.size(); i++) {
                String att = properties.get(i);
                Object val = values.get(i);

                if (val instanceof String) {
                    val = attio.parse(att, (String) val);
                }

                builder.set(att, val);
            }

            properties = null;
            values = null;
            return true;
        } else {
            feature = buildFeature();
            id = null;
            geometry = null;
            properties = null;
            values = null;

            return true;
        }
    }

    @Override
    public boolean primitive(Object value) throws ParseException, IOException {
        if (delegate instanceof GeometryHandler && value == null) {
            delegate = NULL;
            return true;
        } else if ("".equals(id)) {
            id = value.toString();
            setFID(id);
            return true;
        } else if (values != null && delegate == NULL) {
            // use the attribute parser
            values.add(value);
            return true;
        }

        return super.primitive(value);
    }

    @Override
    public SimpleFeature getValue() {
        return feature;
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    public void setCRS(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public void init() {
        feature = null;
    }

    SimpleFeatureBuilder createBuilder() {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("feature");
        typeBuilder.setNamespaceURI("http://geotools.org");
        typeBuilder.setCRS(crs);

        if (properties != null) {
            for (int i = 0; i < properties.size(); i++) {
                String prop = properties.get(i);
                Object valu = values.get(i);
                typeBuilder.add(prop, valu != null ? valu.getClass() : Object.class);
            }
        }
        if (geometry != null) {
            addGeometryType(typeBuilder, geometry);
        }

        return new SimpleFeatureBuilder(typeBuilder.buildFeatureType());
    }

    void addGeometryType(SimpleFeatureTypeBuilder typeBuilder, Geometry geometry) {
        typeBuilder.add("geometry", geometry != null ? geometry.getClass() : Geometry.class);
        typeBuilder.setDefaultGeometry("geometry");
    }

    SimpleFeature buildFeature() {

        SimpleFeatureBuilder builder = this.builder != null ? this.builder : createBuilder();
        SimpleFeatureType featureType = builder.getFeatureType();
        SimpleFeature f = builder.buildFeature(getFID());
        if (geometry != null) {
            if (featureType.getGeometryDescriptor() == null) {
                // GEOT-4293, case of geometry coming after properties, we have to retype
                // the builder
                // JD: this is ugly, we should really come up with a better way to store internal
                // state of properties, and avoid creating the builder after the properties object
                // is completed
                SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
                typeBuilder.init(featureType);
                addGeometryType(typeBuilder, geometry);

                featureType = typeBuilder.buildFeatureType();
                SimpleFeatureBuilder newBuilder = new SimpleFeatureBuilder(featureType);
                newBuilder.init(f);
                f = newBuilder.buildFeature(getFID());
            }
            f.setAttribute(featureType.getGeometryDescriptor().getLocalName(), geometry);
        }
        incrementFID();
        return f;
    }
    // "{" +
    // " 'type': 'Feature'," +
    // " 'geometry': {" +
    // " 'type': 'Point'," +
    // " 'coordinates': [" + val + "," + val + "]" +
    // " }, " +
    // "' properties': {" +
    // " 'int': 1," +
    // " 'double': " + (double)val + "," +
    // " 'string': '" + toString(val) + "'" +
    // " }," +
    // " 'id':'widgets." + val + "'" +
    // "}";

    /** Add one to the current ID */
    private void incrementFID() {
        fid = fid + 1;
    }

    private void setFID(String f) {
        int index = f.lastIndexOf('.');
        if (index < 0) {
            index = f.indexOf('-');
            if (index >= 0) {
                separator = "-";
            } else {
                autoFID = false;
                id = f;
                return;
            }
        } else {
            separator = ".";
        }
        baseId = f.substring(0, index);
        try {
            fid = Integer.parseInt(f.substring(index + 1));
        } catch (NumberFormatException e) {
            autoFID = false;
            id = f;
        }
    }

    private String getFID() {
        if (id == null || autoFID) {
            return baseId + separator + fid;
        } else {
            return id;
        }
    }
}
