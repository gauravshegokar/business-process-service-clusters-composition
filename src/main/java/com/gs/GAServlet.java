package com.gs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/ga")
public class GAServlet {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String search(@QueryParam("populationsize") String populationSize,
                         @QueryParam("evolutions") String evolutions,
                         @QueryParam("mutationrate") String mutationRate) {
        GAWebService gaWebService = new GAWebService();
        return gaWebService.getResults(Integer.parseInt(populationSize), Double.parseDouble(mutationRate), Integer.parseInt(evolutions));
    }

}
