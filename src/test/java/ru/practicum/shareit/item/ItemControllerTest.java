package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.AbstractControllerTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.RandomUtils;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HttpMethodEnum.*;

class ItemControllerTest extends AbstractControllerTest {
    @Test
    void createItemTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());

        ItemDto itemDto = RandomUtils.getRandomItem();
        String itemDtoJson = createJson(itemDtoToMap(itemDto));

        performRequest(POST, "/items", itemDtoJson, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItem() throws Exception {
        User user = createUser();

        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());
        Item item = createItem(headers, true);

        String updatedItemJson = createJson(Map.of(
                "name", "Updated Item",
                "description", "Updated Description",
                "available", false
        ));

        performRequest(PATCH, "/items/" + item.getId(), updatedItemJson, headers)
                .andExpect(status().isOk());

        performRequest(GET, "/items/" + item.getId(), headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").isBoolean());
    }

    @Test
    void createItemWithInvalidData() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());

        String invalidItemJson = createJson(Map.of(
                "name", "",
                "description", "Description",
                "available", true
        ));

        performRequest(POST, "/items", invalidItemJson, headers)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.name").value("Наименование не может быть пустым"));
    }

    @Test
    void createItemWithoutUserHeader() throws Exception {
        String itemJson = createJson(Map.of(
                "name", "Item",
                "description", "Description",
                "available", true
        ));

        performRequest(POST, "/items", itemJson)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.error").value("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"));
    }

    @Test
    void updateNonExistentItem() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());

        String updatedItemJson = createJson(Map.of(
                "name", "Updated Item",
                "description", "Updated Description",
                "available", false
        ));

        performRequest(PATCH, "/items/999", updatedItemJson, headers)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message.error").exists());
    }

    @Test
    void getItemById() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());

        Item item = createItem(headers, true);

        performRequest(GET, "/items/" + item.getId(), headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()));
    }

    @Test
    void getNonExistentItemByIdTest() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());

        performRequest(GET, "/items/999", headers)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message.error").exists());
    }

    @Test
    void getItemsByOwner() throws Exception {
        User user = createUser();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());

        Item item1 = createItem(headers, true);
        Item item2 = createItem(headers, true);

        performRequest(GET, "/items", headers)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[1].name").value(item2.getName()))
                .andExpect(jsonPath("$[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[1].available").value(item2.getAvailable()));
    }

    @Test
    void searchItems() throws Exception {
        User user = createUser();
        Item item = createItem(createHeaders(X_SHARER_USER_ID, user.getId().toString()), true);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());
        params.add("text", item.getName().substring(0, 3));

        performRequest(GET, "/items/search", params, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(item.getName()));
    }

    @Test
    void searchItemsWithEmptyRequest() throws Exception {
        User user = createUser();
        Item item = createItem(createHeaders(X_SHARER_USER_ID, user.getId().toString()), true);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> headers = createHeaders(X_SHARER_USER_ID, user.getId().toString());
        params.add("text", "");

        performRequest(GET, "/items/search", params, headers)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}