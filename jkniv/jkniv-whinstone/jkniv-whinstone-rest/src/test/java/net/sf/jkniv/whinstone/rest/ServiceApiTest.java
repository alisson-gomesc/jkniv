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
package net.sf.jkniv.whinstone.rest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.expect;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class ServiceApiTest extends EmbeddedJetyServerTest
{
    @Test @Ignore
    public void whenWhinstoneRunningData()
    {
        given()
          .contentType(ContentType.JSON)
        .expect()
          .statusCode(200)
          .contentType(ContentType.TEXT)
        .when()
          .get("/api/whinstone/ping");
    }
}
