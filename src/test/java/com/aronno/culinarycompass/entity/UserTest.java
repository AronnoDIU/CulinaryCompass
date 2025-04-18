package com.aronno.culinarycompass.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {

    @Test
    void testUserCreation() {
        // Create a user
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setDateOfBirth(new Date());
        user.setRole("USER");
        user.setPhoneNumber("1234567890");
        user.setAddress("123 Main St, City, Country");

        // Assert that the user properties are set correctly
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertNotNull(user.getDateOfBirth());
        assertEquals("USER", user.getRole());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("123 Main St, City, Country", user.getAddress());
    }

    @Test
    void testUserToString() {
        // Create a user
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");

        // Assert that toString() method doesn't throw an exception
        String userString = user.toString();
        assertNotNull(userString);
    }
}
