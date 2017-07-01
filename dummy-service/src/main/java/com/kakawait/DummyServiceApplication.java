package com.kakawait;

import java.security.Principal;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Thibaud Leprêtre
 */
@SpringBootApplication
@EnableResourceServer
@EnableDiscoveryClient
public class DummyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DummyServiceApplication.class, args);
    }


    @Profile("!cloud")
    @Bean
    RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }
    
    @Autowired
  	private ResourceServerProperties sso;

  	@Bean
  	public ResourceServerTokenServices myUserInfoTokenServices() {
  		return new CustomUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
  	}
  	
    @Controller
    @RequestMapping("/")
    public static class DummyController {

        @RequestMapping(method = RequestMethod.GET)
        @ResponseBody
        public String helloWorld(Principal principal) {
            return principal == null ? "Hello anonymous" : "Hello " + principal.getName();
        }

        @PreAuthorize("#oauth2.hasScope('openid') and hasRole('ROLE_ADMIN')")
        @RequestMapping(value = "secret", method = RequestMethod.GET)
        @ResponseBody
        public String helloSecret(Principal principal) {
            return principal == null ? "Hello anonymous" : "S3CR3T  - Hello " + principal.getName();
        }
        
        
        @RequestMapping("/user")
    	@ResponseBody
    	public Principal home(Principal user) {
    		return user;
    	}
    }
}
