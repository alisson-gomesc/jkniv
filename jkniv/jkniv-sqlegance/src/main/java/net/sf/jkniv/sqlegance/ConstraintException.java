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

import java.util.Map;

/**
 * Thrown when model data violate the rules with JSR Bean Validation  
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class ConstraintException extends RepositoryException
{
    private static final long serialVersionUID = -1607171467309827392L;

    private final Map<String, String> violations;
    
    /**
     * Constructor for ConstraintException without message detail
     * @param violations violated constraints from model
     */
    public ConstraintException(Map<String, String> violations)
    {
        super();
        this.violations = violations;
    }
    
    /**
     * Map with violations where key is {@code javax.validation.ConstraintViolation.getPropertyPath()} name with 
     * violation message.
     * @return set of violation messages from Bean Validation
     */
    public Map<String, String> getViolations()
    {
        return violations;
    }
    
}
