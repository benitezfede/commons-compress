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
package org.apache.commons.compress.archivers.dump;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.compress.AbstractTestCase;
import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Test;

public class DumpArchiveInputStreamTest extends AbstractTestCase {

    @Test
    public void testNotADumpArchive() throws Exception {
        try (FileInputStream is = new FileInputStream(getFile("bla.zip"))) {
            new DumpArchiveInputStream(is);
            fail("expected an exception");
        } catch (final ArchiveException ex) {
            // expected
            assertTrue(ex.getCause() instanceof ShortFileException);
        }
    }

    @Test
    public void testNotADumpArchiveButBigEnough() throws Exception {
        try (FileInputStream is = new FileInputStream(getFile("zip64support.tar.bz2"))) {
            new DumpArchiveInputStream(is);
            fail("expected an exception");
        } catch (final ArchiveException ex) {
            // expected
            assertTrue(ex.getCause() instanceof UnrecognizedFormatException);
        }
    }

    @Test
    public void testConsumesArchiveCompletely() throws Exception {
        final InputStream is = DumpArchiveInputStreamTest.class
            .getResourceAsStream("/archive_with_trailer.dump");
        final DumpArchiveInputStream dump = new DumpArchiveInputStream(is);
        while (dump.getNextDumpEntry() != null) {
            // just consume the archive
        }
        final byte[] expected = new byte[] {
            'H', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd', '!', '\n'
        };
        final byte[] actual = new byte[expected.length];
        is.read(actual);
        assertArrayEquals(expected, actual);
        dump.close();
    }

}
