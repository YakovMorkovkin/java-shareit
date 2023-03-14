package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto connectItems(ItemRequestDto request);

    List<ItemRequestDto> getAllOwnItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequestsOfUsers(Long userId, Integer offset, Integer limit);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
