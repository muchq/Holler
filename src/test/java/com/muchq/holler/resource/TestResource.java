package com.muchq.holler.resource;

import com.muchq.holler.model.Widget;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

  @GET
  @Path("{id}")
  public Widget getWidget(@PathParam("id") int id, @QueryParam("name") String name) {
    return Widget.builder().setId(id).setName(name).build();
  }

  @POST
  public Widget upCaseAndNegateId(Widget widget) {
    int id = widget.getId();
    String name = widget.getName();
    return Widget.builder()
        .setId(id * -1)
        .setName(name.toUpperCase())
        .build();
  }

  @PUT
  public Widget putWidget(Widget widget) {
    return widget;
  }

  @DELETE
  @Path("{id}")
  public void deleteWidget(@PathParam("id") int id) {

  }
}
