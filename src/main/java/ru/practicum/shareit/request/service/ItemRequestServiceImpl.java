package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.mapper.ItemShortMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Primary
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemShortMapper itemShortMapper;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest newItemRequest = itemRequestMapper.toModel(itemRequestDto);
        newItemRequest.setRequestor(userService.getUser(userId));
        newItemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.toDTO(itemRequestRepository.save(newItemRequest));
    }

    @Override
    public ItemRequestDto connectItems(ItemRequestDto request) {
        request.setItems(getRequestItems(request));
        return request;
    }

    public List<ItemShortDto> getRequestItems(ItemRequestDto request) {
        return itemRepository.findAllByItemRequestId(request.getId()).stream()
                .map(itemShortMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllOwnItemRequests(Long userId) {
        userService.getUser(userId);
        return itemRequestRepository.findAllByRequestorId(userId).stream()
                .map(itemRequestMapper::toDTO)
                .map(this::connectItems)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsOfUsers(Long userId, Integer offset, Integer limit) {
        return itemRequestRepository.findAllByRequestorIdNot(userId,
                        PageRequest.of(offset, limit, Sort.by("created").ascending()))
                .map(itemRequestMapper::toDTO).map(this::connectItems).getContent();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userService.getUser(userId);
        return this.connectItems(itemRequestMapper.toDTO(itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Itemrequest with id - " + requestId + " not found"))));
    }
}
