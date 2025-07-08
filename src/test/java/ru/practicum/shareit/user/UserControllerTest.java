package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.AbstractControllerTest;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HttpMethodEnum.*;

class UserControllerTest extends AbstractControllerTest {

    @Test
    void createUserTest() throws Exception {
        User user = createUser();
        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void createUserWithExistingEmail() throws Exception {
        User user = createUser();

        String userJson = createJson(Map.of(
                "name", "Another User",
                "email", user.getEmail()
        ));

        performRequest(POST, "/users", userJson)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message.error").exists());
    }

    @Test
    void updateUser() throws Exception {
        User user = createUser();
        System.out.println(user);
        String updatedUserJson = createJson(Map.of(
                "name", "Updated Name",
                "email", "updated@example.com"
        ));

        performRequest(PATCH, "/users/" + user.getId(), updatedUserJson)
                .andExpect(status().isOk());

        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void getNonExistentUserById() throws Exception {
        performRequest(GET, "/users/999")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message.error").exists());
    }

    @Test
    void getUserById() throws Exception {
        User user = createUser();

        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void deleteUser() throws Exception {
        User user = createUser();

        performRequest(DELETE, "/users/" + user.getId())
                .andExpect(status().isOk());

        performRequest(GET, "/users/" + user.getId())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentUser() throws Exception {
        performRequest(DELETE, "/users/999")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message.error").exists());
    }
}