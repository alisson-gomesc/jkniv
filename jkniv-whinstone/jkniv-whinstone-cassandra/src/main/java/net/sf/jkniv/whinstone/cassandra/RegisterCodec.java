/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.cassandra;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.TypeCodec;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;

/**
 * Registry codec for Cassandra
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
@SuppressWarnings("rawtypes")
public class RegisterCodec
{
    private static final Logger        LOG = LoggerFactory.getLogger(RegisterCodec.class);
    public final Map<String, CodecMap> codecs;
    
    public RegisterCodec()
    {
        codecs = new HashMap<String, CodecMap>();
        codecs.put("arrays.DoubleArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.DoubleArrayCodec"));
        codecs.put("arrays.FloatArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.FloatArrayCodec"));
        codecs.put("arrays.IntArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.IntArrayCodec"));
        codecs.put("arrays.LongArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.LongArrayCodec"));
        codecs.put("arrays.ObjectArrayCodec", new CodecMap("com.datastax.driver.extras.codecs.arrays.ObjectArrayCodec"));
        
        codecs.put("date.SimpleDateCodec", new CodecMap("com.datastax.driver.extras.codecs.date.SimpleDateCodec"));
        codecs.put("date.SimpleTimestampCodec", new CodecMap("com.datastax.driver.extras.codecs.date.SimpleTimestamp"));
        
        codecs.put("enums.EnumNameCodec", new CodecMap("com.datastax.driver.extras.codecs.enums.EnumNameCodec"));
        codecs.put("enums.EnumOrdinalCodec", new CodecMap("com.datastax.driver.extras.codecs.enums.EnumOrdinalCodec"));
        
        codecs.put("guava.OptionalCodec", new CodecMap("com.datastax.driver.extras.codecs.guava.OptionalCodec"));
        
        codecs.put("jdk8.InstantCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.InstantCodec"));
        codecs.put("jdk8.LocalDateCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateCodec"));
        codecs.put("jdk8.LocalDateTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateTimeCodec"));
        codecs.put("jdk8.LocalTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalTimeCodec"));
        codecs.put("jdk8.OptinalCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.OptionalCodec"));
        codecs.put("jdk8.ZonedDateTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.ZonedDateTimeCodec"));
        codecs.put("jdk8.ZonaIdCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.ZoneIdCodec"));
        
        codecs.put("joda.DateTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.DateTimeCodec"));
        codecs.put("joda.InstantCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.InstantCodec"));
        codecs.put("joda.LocalDateCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.LocalDateCodec"));
        codecs.put("joda.LocalTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.joda.LocalTimeCodec"));
        codecs.put("json.JacksonJsonCodec", new CodecMap("com.datastax.driver.extras.codecs.json.JacksonJsonCodec"));
        
        codecs.put("InstantCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.InstantCodec"));
        codecs.put("LocalDateCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateCodec"));
        codecs.put("LocalDateTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalDateTimeCodec"));
        codecs.put("LocalTimeCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.LocalTimeCodec"));
        codecs.put("OptinalCodec", new CodecMap("com.datastax.driver.extras.codecs.jdk8.OptionalCodec"));
    }
    
    public void register(Cluster cluster, String codecName, boolean enable)
    {
        if (enable)
        {
            CodecMap codecMap = codecs.get(codecName);
            Field field = null;
            if (codecMap != null)
            {
                ObjectProxy<TypeCodec<?>> proxy = ObjectProxyFactory.of(codecMap.codecClassName);
                field = proxy.getDeclaredField("instance");
                if (field != null)
                {
                    TypeCodec<?> instance = null;
                    try
                    {
                        instance = (TypeCodec<?>) field.get(null);
                        cluster.getConfiguration().getCodecRegistry().register(instance);
                        codecMap.instance = instance;
                        if (codecName.endsWith("InstantCodec"))
                            codecs.get("InstantCodec").instance = instance;
                        else if (codecName.endsWith("LocalDateCodec"))
                            codecs.get("LocalDateCodec").instance = instance;
                        else if (codecName.endsWith("LocalTimeCodec"))
                            codecs.get("LocalTimeCodec").instance = instance;
                        else if (codecName.endsWith("LocalDateTimeCodec"))
                            codecs.get("LocalDateTimeCodec").instance = instance;
                        else if (codecName.endsWith("OptinalCodec"))
                            codecs.get("OptinalCodec").instance = instance;
                        
                        LOG.info("The codec {} was registered with {}", codecName, codecMap.codecClassName);
                    }
                    catch (Exception e)//IllegalArgumentException, IllegalAccessException
                    {
                        LOG.error("Error registering codec {}", codecName, e);
                    }
                }
            }
            if (codecMap == null || field == null)
                LOG.error("Codec {} not found ", codecName);
        }
    }
    
    public CodecMap getCodec(String codecName)
    {
        return codecs.get(codecName);
    }
    
    public static class CodecMap
    {
        public final String codecClassName;
        public TypeCodec    instance;
        
        CodecMap(String codecClassName)
        {
            this.codecClassName = codecClassName;
        }
    }
}
