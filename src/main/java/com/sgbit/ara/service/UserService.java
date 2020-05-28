package com.sgbit.ara.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sgbit.ara.DAO.UserDAO;
import com.sgbit.ara.model.User;;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("UserService")
public class UserService {
	UserDAO userDAO=new UserDAO();
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    @GET
    @Path("getMessage")
    public String getMessage() {
    	return "Success";
    }
    @GET
    @Path("getUser/{username}")
    public String getUser(@PathParam("username") String username) {
    	System.out.println("name-"+username);
    	
    	return "success";
    }
    
    @GET
    @Path("validateUser/{userId}/{password}")
    public User validateUser(@PathParam("userId") String userId , @PathParam("password") String password) {
    	System.out.println("userId-"+userId+"password-"+password);
    	
    	return userDAO.validateUser(userId, password);
    }
    @POST
    @Path("registerUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public int registerUser(User user) {
    	System.out.println("name-"+user.getUserName()+"mobileNo-"+user.getMobileNo()+"emailId-"+user.getEmailId()+"password-"+user.getPassword());
    	
    	return userDAO.registerUser(user);
    

    	
    	
    }
    @GET
    @Path("getUserDetails/{mobileNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserDetails(@PathParam("mobileNo") String mobileNo){
    	if(mobileNo.equals("444")) {
    		User user=new User();
    		user.setUserName("sdf");
    		user.setMobileNo("233");
    		user.setEmailId("sdf@gmail.com");
    		return user;
    	}
    	return null;
    	
    	 }
    @GET
    @Path("listusers")
    @Produces(MediaType.APPLICATION_JSON)
    public  void listusers( ){
    	  userDAO.listusers();
    	
    }
    
    @GET
    @Path("getAllUserDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUserDetails(){
    	
    		
    		return userDAO.listusers();
    }
}