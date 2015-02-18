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
package org.apache.tinkerpop.gremlin.process.graph.traversal.step.filter

import org.apache.tinkerpop.gremlin.process.Path
import org.apache.tinkerpop.gremlin.process.Traversal
import org.apache.tinkerpop.gremlin.process.ComputerTestHelper
import org.apache.tinkerpop.gremlin.process.TraversalEngine
import org.apache.tinkerpop.gremlin.process.UseEngine
import org.apache.tinkerpop.gremlin.process.graph.traversal.step.filter.CyclicPathTest
import org.apache.tinkerpop.gremlin.structure.Vertex

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class GroovyCyclicPathTest {

    @UseEngine(TraversalEngine.Type.STANDARD)
    public static class StandardTest extends CyclicPathTest {

        @Override
        public Traversal<Vertex, Vertex> get_g_VX1X_outXcreatedX_inXcreatedX_cyclicPath(final Object v1Id) {
            g.V(v1Id).out('created').in('created').cyclicPath
        }

        @Override
        public Traversal<Vertex, Path> get_g_VX1X_outXcreatedX_inXcreatedX_cyclicPath_path(final Object v1Id) {
            g.V(v1Id).out('created').in('created').cyclicPath.path
        }
    }

    @UseEngine(TraversalEngine.Type.COMPUTER)
    public static class ComputerTest extends CyclicPathTest {

        @Override
        Traversal<Vertex, Vertex> get_g_VX1X_outXcreatedX_inXcreatedX_cyclicPath(final Object v1) {
            ComputerTestHelper.compute("g.V(${v1}).out('created').in('created').cyclicPath", g);
        }

        @Override
        Traversal<Vertex, Path> get_g_VX1X_outXcreatedX_inXcreatedX_cyclicPath_path(final Object v1) {
            ComputerTestHelper.compute("g.V(${v1}).out('created').in('created').cyclicPath().path()", g);
        }
    }
}