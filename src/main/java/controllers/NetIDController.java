package controllers;

import javax.ws.rs.*;

@Path("/netid")
public class NetIDController {
    @GET
    public String getNetID () { return "jm2644"; }
}
