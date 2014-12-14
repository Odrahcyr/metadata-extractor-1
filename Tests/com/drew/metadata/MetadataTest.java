/*
 * Copyright 2002-2014 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.iptc.IptcDirectory;

/**
 * JUnit test case for class Metadata.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class MetadataTest
{
    @Test public void testGetDirectoryWhenNotExists()
    {
        assertNull(new Metadata().getDirectory(ExifSubIFDDirectory.class));
    }

    @Test public void testGetOrCreateDirectoryWhenNotExists()
    {
        assertNotNull(new Metadata().getOrCreateDirectory(ExifSubIFDDirectory.class));
    }

    @Test public void testGetDirectoryReturnsSameInstance()
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        assertSame(directory, metadata.getDirectory(ExifSubIFDDirectory.class));
    }

    @Test public void testGetOrCreateDirectoryReturnsSameInstance()
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        assertSame(directory, metadata.getOrCreateDirectory(ExifSubIFDDirectory.class));
        assertNotSame(directory, metadata.getOrCreateDirectory(IptcDirectory.class));
    }

    @Test
    public void testHasErrors() throws Exception
    {
        Metadata metadata = new Metadata();
        assertFalse(metadata.hasErrors());
        final ExifSubIFDDirectory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.addError("Test Error 1");
        assertTrue(metadata.hasErrors());
    }

    @Test
    public void testGetErrors() throws Exception
    {
        Metadata metadata = new Metadata();
        assertFalse(metadata.hasErrors());
        final ExifSubIFDDirectory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.addError("Test Error 1");
        assertTrue(metadata.hasErrors());
    }

    @Test
    public void testToString()
    {
        Metadata metadata = new Metadata();
        assertEquals("Metadata (0 directories)", metadata.toString());
        metadata.getOrCreateDirectory(ExifIFD0Directory.class);
        assertEquals("Metadata (1 directory)", metadata.toString());
        metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        assertEquals("Metadata (2 directories)", metadata.toString());
    }
}
