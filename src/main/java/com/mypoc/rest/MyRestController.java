package com.mypoc.rest;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/clients")
public class MyRestController {
 
	@Autowired 
	private ClientRepository clientRepository;
    
	@RequestMapping(method = RequestMethod.POST)
    public MyClient createMyClient(
    		@RequestBody 
    		MyClient myClient) 
    {
		
		clientRepository.save(myClient);
        return myClient;
    }

	@RequestMapping(method = RequestMethod.GET)
    public Iterable<MyClient> listMyClients() {
        return clientRepository.findAll();
    }
	
	
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Optional<MyClient> getClient(
    		@PathVariable("id") 
    		Long id) 
    		//throws NotFoundException 
    {
    	Optional<MyClient> myClient = clientRepository.findById(id);
 
        if (myClient == null) {
            //throw new NotFoundException();
        }
        return myClient;
    }
 
    
   @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public MyClient updateMyClient(
    		@PathVariable("id") 
    		String id, 
    		@RequestBody 
    		MyClient myClient) 
    		//		throws NotFoundException 
    {
    	clientRepository.save(myClient);////?????????? No update
        return myClient;
    }
 
 
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteMyClient(
    		@PathVariable("id") 
    		Long id) 
    	{
    	clientRepository.deleteById(id);
    }
 
  
    
}
