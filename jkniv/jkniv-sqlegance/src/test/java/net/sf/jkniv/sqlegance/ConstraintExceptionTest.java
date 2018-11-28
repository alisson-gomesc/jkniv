/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConstraintExceptionTest
{
    private static final String CLASS_NAME_EX = "net.sf.jkniv.sqlegance.ConstraintException";
    private static final String MESSAGE_ARE     = CLASS_NAME_EX+": There are 2 data violations";
    private static final String MESSAGE_IS      = CLASS_NAME_EX+": There is 1 data violations";
    private static final String MESSAGE_IS_ZERO = CLASS_NAME_EX+": There is 0 data violations";
                                                 //net.sf.jkniv.sqlegance.ConstraintException: There is 0 data violations
    @Rule public ExpectedException    catcher         = ExpectedException.none();
    
    @Test
    public void whenThrowConstraintZeroMessage()
    {
        catcher.expect(ConstraintException.class);
        catcher.expectMessage(MESSAGE_IS_ZERO);
        catcher.reportMissingExceptionWithMessage("No Constraint Exception was throw"); 
        throw new ConstraintException(new HashMap<String,String>());
    }

    @Test
    public void whenThrowConstraintOneMessage()
    {
        catcher.expect(ConstraintException.class);
        catcher.expectMessage(CLASS_NAME_EX +": required" + "\n\t[name]=required");
        catcher.reportMissingExceptionWithMessage("No Constraint Exception was throw"); 
        throw new ConstraintException("name", "required");
    }

    @Test
    public void whenThrowConstraintOneMapMessage()
    {
        catcher.expect(ConstraintException.class);
        catcher.expectMessage(MESSAGE_IS + "\n\t[name]=required");
        catcher.reportMissingExceptionWithMessage("No Constraint Exception was throw");
        Map<String, String> violations = new TreeMap<String,String>();
        violations.put("name", "required");
        throw new ConstraintException(violations);
    }

    @Test
    public void whenThrowConstraintTwoMessages()
    {
        catcher.expect(ConstraintException.class);
        catcher.expectMessage(MESSAGE_ARE + "\n\t[age]=max age is 18\n\t[name]=required");
        catcher.reportMissingExceptionWithMessage("No Constraint Exception was throw");
        Map<String, String> violations = new TreeMap<String,String>();
        violations.put("age", "max age is 18");
        violations.put("name", "required");
        throw new ConstraintException(violations);
    }

    @Test
    public void whenThrowConstraintAndViolationsAreUnmodified()
    {
        catcher.expect(UnsupportedOperationException.class);
        ConstraintException constraints = new ConstraintException("name", "required");
        Map<String, String> violations =  constraints.getViolations();
        assertThat(violations.size(), is(1));
        violations.put("new field", "Cannot add new Field, is an immutable Map");
    }
    
    @Test @Ignore("Visual test")
    public void whenPrintStackTrace()
    {
        try
        {
            Map<String, String> violations = new HashMap<String, String>();
            violations.put("v1", "is required");
            violations.put("v2", "max length is 5");
            violations.put("v3", "mask must be yyyy-MM-dd");
            throw new ConstraintException(violations);
        }
        catch (ConstraintException e)
        {
            e.printStackTrace();
        }
    }

}

