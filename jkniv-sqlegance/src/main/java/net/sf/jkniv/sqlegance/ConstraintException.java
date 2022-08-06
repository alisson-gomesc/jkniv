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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jkniv.sqlegance.validation.ValidationMessage;

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
    private final List<ValidationMessage> violationsList;

    /**
     * Constructor for ConstraintException without message detail
     * @param param the name of parameter with error
     * @param message the user message
     */
    public ConstraintException(String param, String message)
    {
        super(message);
        this.violations = new TreeMap<String, String>();
        this.violationsList = new ArrayList<ValidationMessage>();
        this.violations.put(param, message);
    }

    /**
     * Constructor for ConstraintException with a set of messages
     * @param violations violated constraints from model, with pair of values field and message
     */
    public ConstraintException(Map<String, String> violations)
    {
        super();
        this.violationsList = new ArrayList<ValidationMessage>();
        this.violations = violations;
    }

    /**
     * Constructor for ConstraintException with a set of messages
     * @param violationsList violated constraints from model, with list of values field, message and key message
     */
    public ConstraintException(List<ValidationMessage> violationsList)
    {
        super();
        this.violationsList = violationsList;
        this.violations = new TreeMap<String, String>();
        for(ValidationMessage message : violationsList)
            this.violations.put(message.getField(), message.getValue());
    }
    
    /**
     * Map with violations where key is {@code javax.validation.ConstraintViolation.getPropertyPath()} name with 
     * violation message.
     * @return set of violation messages from Bean Validation
     */
    public Map<String, String> getViolations()
    {
        return Collections.unmodifiableMap(violations);
    }
    
    /**
     * Map with violations where key is {@code javax.validation.ConstraintViolation.getPropertyPath()} name with 
     * violation message and the key for internationalization
     * @return set of violation messages from Bean Validation
     */
    public List<ValidationMessage> getViolationsI18n()
    {
        return Collections.unmodifiableList(violationsList);
    }
    
    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder(getClass().getName()+": ");
        sb.append(super.getMessage() == null ? "There " + 
                (violations.size() > 1 ? "are " : "is ") + violations.size() + 
                " data violations"  : super.getMessage());
        for(String k : violations.keySet())
            sb.append("\n\t["+k+"]="+violations.get(k));
        return sb.toString();        
    }
    
    @Override
    public String toString()
    {
        return getMessage();
    }
}
