package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemsByOwner(long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        String path = UriComponentsBuilder.fromPath("/search")
                .queryParam("text", text)
                .toUriString();
        return get(path);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentCreateDto commentCreateDto) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }
}
