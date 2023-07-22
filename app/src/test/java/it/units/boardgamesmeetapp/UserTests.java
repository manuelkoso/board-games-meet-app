package it.units.boardgamesmeetapp;

import org.junit.Test;

import static org.junit.Assert.*;

import it.units.boardgamesmeetapp.models.User;

public class UserTests {

    @Test
    public void testEquals() {
        User userOne = new User("id_1", "Fabio", "Rossi", "test@test.com");
        User userTwo = new User("id_1", "Mario", "Rossi", "test@test.com");
        assertNotEquals(userOne, userTwo);
    }

}
