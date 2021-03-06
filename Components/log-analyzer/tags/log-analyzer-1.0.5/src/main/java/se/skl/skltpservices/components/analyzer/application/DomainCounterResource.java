/**
 * Copyright (c) 2012, Sjukvardsradgivningen. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package se.skl.skltpservices.components.analyzer.application;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import se.skl.skltpservices.components.analyzer.LogServiceConfig;
import se.skl.skltpservices.components.analyzer.domain.Counter;


@Controller
@Path("/counters")
@Produces({"application/json; charset=UTF-8"})
public class DomainCounterResource {

    @Autowired
    private LogServiceConfig logServiceConfig;
   
    @GET
    @Path("/domains/{week}")
    public List<Counter> domains(@PathParam("week") int week) {
        return logServiceConfig.getLogStoreRepository().getDomainCounters(week);
    }

    @GET
    @Path("/contracts/{week}")
    public List<Counter> contracts(@PathParam("week") int week) {
        return logServiceConfig.getLogStoreRepository().getContractCounters(week);
    }

}