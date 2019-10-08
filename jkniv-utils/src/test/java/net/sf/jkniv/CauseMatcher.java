/* 
 * JKNIV ,
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

package net.sf.jkniv;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class CauseMatcher extends TypeSafeMatcher<Throwable>
{
    private final Class<? extends Throwable> type;
    private final String expectedMessage;
 
    public CauseMatcher(Class<? extends Throwable> type, String expectedMessage) {
        this.type = type;
        this.expectedMessage = expectedMessage;
    }
 
    @Override
    protected boolean matchesSafely(Throwable item) {
        return item.getClass().isAssignableFrom(type)
                && item.getMessage().contains(expectedMessage);
    }
 
    //@Override
    public void describeTo(Description description) {
        description.appendText("expects type ")
                .appendValue(type)
                .appendText(" and a message ")
                .appendValue(expectedMessage);
    }    
}
