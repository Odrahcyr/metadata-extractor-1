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
package com.drew.metadata.jfif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string versions of the tags stored in a JfifDirectory.
 * <p/>
 * More info at: http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
 *
 * @author Yuri Binev, Drew Noakes
 */
public class JfifDescriptor extends TagDescriptor<JfifDirectory>
{
    public JfifDescriptor(@NotNull JfifDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case JfifDirectory.TAG_RESX:
                return getImageResXDescription();
            case JfifDirectory.TAG_RESY:
                return getImageResYDescription();
            case JfifDirectory.TAG_VERSION:
                return getImageVersionDescription();
            case JfifDirectory.TAG_UNITS:
                return getImageResUnitsDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getImageVersionDescription()
    {
        Integer value = _directory.getInteger(JfifDirectory.TAG_VERSION);
        if (value==null)
            return null;
        return String.format("%d.%d", (value & 0xFF00) >> 8, value & 0xFF);
    }

    @Nullable
    public String getImageResYDescription()
    {
        Integer value = _directory.getInteger(JfifDirectory.TAG_RESY);
        if (value==null)
            return null;
        return String.format("%d dot%s",
                value,
                value==1 ? "" : "s");
    }

    @Nullable
    public String getImageResXDescription()
    {
        Integer value = _directory.getInteger(JfifDirectory.TAG_RESX);
        if (value==null)
            return null;
        return String.format("%d dot%s",
                value,
                value==1 ? "" : "s");
    }

    @Nullable
    public String getImageResUnitsDescription()
    {
        Integer value = _directory.getInteger(JfifDirectory.TAG_UNITS);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "none";
            case 1: return "inch";
            case 2: return "centimetre";
            default:
                return "unit";
        }
    }
}
