/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process;

import org.apache.tinkerpop.gremlin.AbstractGremlinTest;
import org.apache.tinkerpop.gremlin.process.traversal.engine.ComputerTraversalEngine;
import org.apache.tinkerpop.gremlin.process.util.MapHelper;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Base test class for Gremlin Process tests.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public abstract class AbstractGremlinProcessTest extends AbstractGremlinTest {

    protected static final String TRAVERSAL_NOT_SUPPORTED_BY_COMPUTER = "Traversal not supported by ComputerTraversalEngine.computer";

    /**
     * Determines if a graph meets requirements for execution.  All gremlin process tests should check this
     * method as part of a call to {@code assumeTrue} to ensure that the test doesn't require the computer
     * feature or if it does require the computer feature then ensure that the graph being tested supports it.
     */
    protected boolean graphMeetsTestRequirements() {
        return !hasGraphComputerRequirement() || g.features().graph().supportsComputer();
    }

    private boolean hasGraphComputerRequirement() {
        final UseEngine useEngine = this.getClass().getAnnotation(UseEngine.class);
        if (null == useEngine)
            throw new RuntimeException(String.format("The %s expects all tests to be annotated with %s",
                    AbstractGremlinProcessTest.class.getName(), UseEngine.class.getName()));

        return useEngine.value().equals(TraversalEngine.Type.COMPUTER);
    }

    @Before
    public void setupTest() {
        assumeTrue(graphMeetsTestRequirements());
        if (hasGraphComputerRequirement()) g.engine(ComputerTraversalEngine.computer);
    }

    public <T> void checkResults(final List<T> expectedResults, final Traversal<?, T> traversal) {
        final List<T> results = traversal.toList();
        assertFalse(traversal.hasNext());
        assertEquals("Checking result size", expectedResults.size(), results.size());
        for (T t : results) {
            if (t instanceof Map) {
                assertTrue("Checking map result existence: " + t, expectedResults.stream().filter(e -> e instanceof Map).filter(e -> checkMap((Map) e, (Map) t)).findAny().isPresent());
            } else {
                assertTrue("Checking result existence: " + t, expectedResults.contains(t));
            }
        }
        final Map<T, Long> expectedResultsCount = new HashMap<>();
        final Map<T, Long> resultsCount = new HashMap<>();
        assertEquals("Checking indexing is equivalent", expectedResultsCount.size(), resultsCount.size());
        expectedResults.forEach(t -> MapHelper.incr(expectedResultsCount, t, 1l));
        results.forEach(t -> MapHelper.incr(resultsCount, t, 1l));
        expectedResultsCount.forEach((k, v) -> assertEquals("Checking result group counts", v, resultsCount.get(k)));
        assertFalse(traversal.hasNext());
    }

    public <T> void checkResults(final Map<T, Long> expectedResults, final Traversal<?, T> traversal) {
        final List<T> list = new ArrayList<>();
        expectedResults.forEach((k, v) -> {
            for (int i = 0; i < v; i++) {
                list.add(k);
            }
        });
        checkResults(list, traversal);
    }

    private <A, B> boolean checkMap(final Map<A, B> expectedMap, final Map<A, B> actualMap) {
        final List<Map.Entry<A, B>> actualList = actualMap.entrySet().stream().sorted((a, b) -> a.getKey().toString().compareTo(b.getKey().toString())).collect(Collectors.toList());
        final List<Map.Entry<A, B>> expectedList = expectedMap.entrySet().stream().sorted((a, b) -> a.getKey().toString().compareTo(b.getKey().toString())).collect(Collectors.toList());

        if (expectedList.size() > actualList.size()) {
            return false;
        } else if (actualList.size() > expectedList.size()) {
            return false;
        }

        for (int i = 0; i < actualList.size(); i++) {
            if (!actualList.get(i).getKey().equals(expectedList.get(i).getKey())) {
                return false;
            }
            if (!actualList.get(i).getValue().equals(expectedList.get(i).getValue())) {
                return false;
            }
        }
        return true;
    }

    public <A, B> List<Map<A, B>> makeMapList(final int size, final Object... keyValues) {
        final List<Map<A, B>> mapList = new ArrayList<>();
        for (int i = 0; i < keyValues.length; i = i + (2 * size)) {
            final Map<A, B> map = new HashMap<>();
            for (int j = 0; j < (2 * size); j = j + 2) {
                map.put((A) keyValues[i + j], (B) keyValues[i + j + 1]);
            }
            mapList.add(map);
        }
        return mapList;
    }
}