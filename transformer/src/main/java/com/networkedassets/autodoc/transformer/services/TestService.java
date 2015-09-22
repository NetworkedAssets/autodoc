package com.networkedassets.autodoc.transformer.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.networkedassets.autodoc.transformer.TestManager;
import com.networkedassets.autodoc.transformer.settings.TestObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by kamil on 21.09.2015.
 */
@Path("/test")
public class TestService extends RestService {

    static final Logger log = LoggerFactory.getLogger(TestService.class);

    @Inject private TestManager testManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestObject getTest(){
        return testManager.getTestObject();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postTest(TestObject testObject){
        testManager.setTestObject(testObject);
        return SUCCESS;
    }
}
