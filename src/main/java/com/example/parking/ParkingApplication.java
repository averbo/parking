package com.example.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class,UserDetailsServiceAutoConfiguration.class })
public class ParkingApplication {

	public static void main(String[] args)  {
		/*new ParkingApplication()
		.configure(new SpringApplicationBuilder(ParkingApplication.class))
		.run(args);*/
		SpringApplication.run(ParkingApplication.class, args);
		
	}
}
