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
package net.sf.jkniv.whinstone.couchdb.commands;

import net.sf.jkniv.whinstone.commands.Command;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public interface CouchCommand extends Command
{
    /** 200 OK */
    static final int HTTP_OK = 200;
    /**  201 Created */
    static final int HTTP_CREATED = 201;
    /** 202 Accepted */
    static final int HTTP_ACCEPTED = 202;
    /** 204 No Content */
    static final int HTTP_NO_CONTENT = 204;
    /** 205 Reset Content */
    static final int HTTP_RESET_CONTENT = 205;
    /**  304 Not Modified */
    static final int HTTP_NOT_MODIFIED = 304;
    /**  404 Not Found */
    static final int HTTP_NOT_FOUND = 404;
    /** 400 Bad Request */
    static final int HTTP_BAD_REQUEST = 400;
    /**  417 Expectation FaileD */
    static final int HTTP_EXPECTATION_FAILED = 407;
    
    HttpMethod asPut();

    HttpMethod asPost();
    
    HttpMethod asDelete();

    HttpMethod asGet();

    HttpMethod asHead();

    String getBody();
}
