/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.env;

//import static org.hamcrest.Matchers.*;
//import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class EnvPropertyResolverTest
{
    @Test
    public void whenCheckPropertyValues()
    {
        EnvPropertyResolver resolver = new EnvPropertyResolver();
        
        assertThat(resolver.getValue("CONSUL_HOST"), is("CONSUL_HOST"));
        assertThat(resolver.getValue("${CONSUL_HOST:test.catchfy.me}:${CONSUL_PORT:8500}"), is("test.catchfy.me:8500"));
        assertThat(resolver.getValue("${consul_host:test.catchfy.me}:${consul_port:8500}"), is("consul_host:consul_port"));
        assertThat(resolver.getValue("${PATH}"), is(not("PATH")));
        assertThat(resolver.getValue("${PATH}"), is(notNullValue()));
        

    }

}
