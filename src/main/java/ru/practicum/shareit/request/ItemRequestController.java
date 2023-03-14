package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static ru.practicum.shareit.Constants.ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@NotEmpty @RequestHeader(ID) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllOwnItemRequests(@NotEmpty @RequestHeader(ID) Long userId) {
        return itemRequestService.getAllOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequestsOfUsers(@NotEmpty @RequestHeader(ID) Long userId,
                                                          @RequestParam(value = "from", defaultValue = "0",
                                                                  required = false) @Min(0) Integer offset,
                                                          @RequestParam(value = "size", defaultValue = "10",
                                                                  required = false) @Min(1) @Max(50) Integer limit) {
        return itemRequestService.getAllItemRequestsOfUsers(userId, offset, limit);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@NotEmpty @RequestHeader(ID) Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
