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

package org.geotools.filter.text.cql2;

import org.geotools.api.filter.Filter;
import org.geotools.api.filter.Not;
import org.geotools.filter.text.commons.CompilerUtil;
import org.geotools.filter.text.commons.Language;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test boolean value expressions.
 *
 * <p>
 *
 * <pre>
 *  &lt;boolean value expression &gt; ::=
 *          &lt;boolean term &gt;
 *      |   &lt;boolean value expression &gt; OR  &lt;boolean term &gt;
 *  &lt;boolean term &gt; ::=
 *          &lt;boolean factor &gt;
 *      |   &lt;boolean term &gt; AND  &lt;boolean factor&gt;
 * </pre>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @since 2.6
 */
public class CQLBooleanValueExpressionTest {
    protected final org.geotools.filter.text.commons.Language language;

    public CQLBooleanValueExpressionTest() {

        this(Language.CQL);
    }

    protected CQLBooleanValueExpressionTest(final Language language) {

        assert language != null : "language cannot be null value";

        this.language = language;
    }

    protected Filter parseFilter(String filter) throws CQLException {
        return CompilerUtil.parseFilter(language, filter);
    }

    /** Sample: ATTR1 < 10 AND ATTR2 < 2 */
    @Test
    public void and() throws CQLException {
        Filter result = parseFilter(FilterCQLSample.FILTER_AND);

        Assert.assertNotNull("filter expected", result);

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.FILTER_AND);

        Assert.assertEquals("ATTR1 < 10 AND ATTR2 < 2 was expected", expected, result);
    }

    /** Sample: "ATTR1 > 10 OR ATTR2 < 2" */
    @Test
    public void or() throws CQLException {
        // "ATTR1 > 10 OR ATTR2 < 2"
        Filter result = parseFilter(FilterCQLSample.FILTER_OR);

        Assert.assertNotNull("filter expected", result);

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.FILTER_OR);

        Assert.assertEquals("ATTR1 > 10 OR ATTR2 < 2 was expected", expected, result);
    }

    /** Sample 1: ATTR1 < 10 AND ATTR2 < 2 OR ATTR3 > 10 Sample 2: ATTR3 < 4 AND (ATT1 > 10 OR ATT2 < 2) */
    @Test
    public void andOr() throws CQLException {
        // ATTR1 < 10 AND ATTR2 < 2 OR ATTR3 > 10
        Filter result = parseFilter(FilterCQLSample.FILTER_OR_AND);

        Assert.assertNotNull("filter expected", result);

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.FILTER_OR_AND);

        Assert.assertEquals("a bad filter was expected", expected, result);

        // ATTR3 < 4 AND (ATT1 > 10 OR ATT2 < 2)
        result = parseFilter(FilterCQLSample.FILTER_OR_AND_PARENTHESIS);

        Assert.assertNotNull("filter expected", result);

        expected = FilterCQLSample.getSample(FilterCQLSample.FILTER_OR_AND_PARENTHESIS);

        Assert.assertEquals("a bad filter was expected", expected, result);
    }

    /** Sample: NOT ATTR1 < 10 */
    @Test
    public void not() throws Exception {

        final String stmt = "NOT " + FilterCQLSample.LESS_FILTER_SAMPLE;
        Filter result = parseFilter(stmt);

        Assert.assertNotNull("filter expected", result);

        Assert.assertTrue(result instanceof Not);

        Not notFilter = (Not) result;

        Filter actual = notFilter.getFilter();

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.LESS_FILTER_SAMPLE);

        Assert.assertEquals(FilterCQLSample.LESS_FILTER_SAMPLE + "was expected", expected, actual);
    }

    /**
     *
     *
     * <pre>
     * Sample:
     * ATTR3 < 4 AND (NOT( ATTR1 < 10 AND ATTR2 < 2))
     * ATTR1 < 1 AND (NOT (ATTR2 < 2)) AND ATTR3 < 3
     * </pre>
     */
    @Test
    public void andNot() throws Exception {

        // ATTR3 < 4 AND (NOT( ATTR1 < 10 AND ATTR2 < 2))
        Filter result = parseFilter(FilterCQLSample.FILTER_AND_NOT_AND);

        Assert.assertNotNull("filter expected", result);

        Filter expected = FilterCQLSample.getSample(FilterCQLSample.FILTER_AND_NOT_AND);

        Assert.assertEquals("a bad filter was produced", expected, result);

        // "ATTR1 < 1 AND (NOT (ATTR2 < 2)) AND ATTR3 < 3"
        result = parseFilter(FilterCQLSample.FILTER_AND_NOT_COMPARASION);

        Assert.assertNotNull("filter expected", result);

        expected = FilterCQLSample.getSample(FilterCQLSample.FILTER_AND_NOT_COMPARASION);

        Assert.assertEquals("a bad filter was produced", expected, result);
    }
}
