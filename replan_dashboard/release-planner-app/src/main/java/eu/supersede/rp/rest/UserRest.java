/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package eu.supersede.rp.rest;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.wso2.carbon.user.core.UserStoreException;

import eu.supersede.fe.exception.InternalServerErrorException;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.integration.ProxyWrapper;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.integration.api.security.types.Role;
import eu.supersede.rp.jpa.ProfilesJpa;
import eu.supersede.rp.jpa.UsersJpa;
import eu.supersede.rp.model.Profile;
import eu.supersede.rp.model.User;

@RestController
@RequestMapping("/user")
public class UserRest {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ProxyWrapper proxy;

	@Autowired
	private UsersJpa users;

	@Autowired
	private ProfilesJpa profiles;

	// @PreAuthorize("hasAuthority('ADMIN')")
	// @Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(Authentication authentication, @RequestBody User user) {

		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		String tenant = currentUser.getTenantId();

		if(currentUser.getToken() != null)
		{
			try {
				proxy.getIFAuthenticationManager(tenant).addUser(toSecurityUser(user, tenant), user.getPassword(), false);
			} catch (UserStoreException e) {
				log.error("IFAuthenticationManager thrown an exception: ");
				e.printStackTrace();
				throw new InternalServerErrorException(e.getMessage());
			}
		}
		else
		{
			log.warn("IF Authentication Manager disable, user token is NULL");
		}
		
		// re-attach detached profiles
		List<Profile> ps = user.getProfiles();
		for (int i = 0; i < ps.size(); i++) {
			ps.set(i, profiles.findOne(ps.get(i).getProfileId()));
		}

		user = users.save(user);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(user.getUserId()).toUri());
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	}

	private eu.supersede.integration.api.security.types.User toSecurityUser(User u, String tenant) throws UserStoreException{
		eu.supersede.integration.api.security.types.User user = new eu.supersede.integration.api.security.types.User();

		user.setUserName(u.getUsername());
		user.setFirstname(u.getFirstName());
		user.setLastname(u.getLastName());
		user.setEmail(u.getEmail());
    	
    	//Adding roles
    	Set<Role>roles = new HashSet<Role>();
    	Set<Role> allRoles = proxy.getIFAuthenticationManager(tenant).getAllRoles();
    	for (Role role: allRoles){
    		if (role.getRoleName().contains("Supersede")){
    			roles.add(role);
    		}
    	}
    	user.setRoles(roles);
		return user;
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
	public User editUser(@PathVariable Long userId, Authentication authentication, @RequestBody User user) {
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		String tenant = currentUser.getTenantId();
		
		if(currentUser.getToken() != null)
		{
			try {
				proxy.getIFAuthenticationManager(tenant).updateUser(toSecurityUser(user, tenant));
			} catch (UserStoreException e) {
				log.error("IFAuthenticationManager thrown an exception: ");
				e.printStackTrace();
				throw new InternalServerErrorException(e.getMessage());
			} catch (MalformedURLException e) {
				log.error("IFAuthenticationManager thrown an exception: ");
				e.printStackTrace();
				throw new InternalServerErrorException(e.getMessage());
			}
		}
		else
		{
			log.warn("IF Authentication Manager disable, user token is NULL");
		}
		
		
		// re-attach detached profiles
		List<Profile> ps = user.getProfiles();
		for (int i = 0; i < ps.size(); i++) {
			ps.set(i, profiles.findOne(ps.get(i).getProfileId()));
		}

		user = users.save(user);
		
		return user;
	}
	
	// @Secured({"ROLE_ADMIN"})
	@RequestMapping("/{userId}")
	public User getUser(@PathVariable Long userId) {
		User u = users.findOne(userId);
		if (u == null) {
			throw new NotFoundException();
		}

		return u;
	}

	// @Secured({"ROLE_ADMIN"})
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<User> getUsers() {
		return users.findAll();
	}

	// @Secured({"ROLE_ADMIN"})
	@RequestMapping("/count")
	public Long count() {
		return users.count();
	}
}
