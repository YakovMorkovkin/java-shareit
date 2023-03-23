package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.mapper.BookingShortMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.UserMismatchException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTests {

    User testOwner = User.builder()
            .email("email@email.ru")
            .name("test Owner")
            .id(1L)
            .build();
    User testUser = User.builder()
            .email("email@email.ru")
            .name("test User")
            .id(2L)
            .build();
    ItemDto testItemDto = ItemDto.builder()
            .id(1L)
            .name("test item")
            .description("Test item of test Owner")
            .available(true)
            .requestId(1L)
            .build();
    Item testItem = Item.builder()
            .id(1L)
            .name("test item")
            .description("Test item of test Owner")
            .owner(testOwner)
            .available(true)
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requestor(testUser)
            .description("Description")
            .created(LocalDateTime.now())
            .build();

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingShortMapper bookingShortMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;
    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addItem_whenDataCorrect_thenSaveItem() {
        Long userId = 1L;
        Item testItem = Item.builder()
                .id(1L)
                .name("test item")
                .description("Test item of test Owner")
                .available(true)
                .build();
        when(itemMapper.toModel(testItemDto)).thenReturn(testItem);
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));

        itemService.addItem(userId, testItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(testItem.getName(), savedItem.getName());
        assertEquals(testItem.getDescription(), savedItem.getDescription());
        assertEquals(testItem.getOwner().getId(), savedItem.getOwner().getId());
        assertEquals(testItem.getAvailable(), savedItem.getAvailable());
        assertEquals(itemRequest.getId(), savedItem.getItemRequest().getId());
    }

    @Test
    void updateItem_whenOwner_thenUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto updateItemDto = ItemDto.builder()
                .id(1L)
                .name("test item")
                .description("Test item of test Owner Updated")
                .available(true)
                .build();
        when(itemRepository.findById(any())).thenReturn(Optional.of(testItem));

        itemService.updateItem(itemId, userId, updateItemDto);

        verify(itemMapper).toDTO(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();

        assertEquals(testItem.getName(), updatedItem.getName());
        assertEquals(updateItemDto.getDescription(), updatedItem.getDescription());
        assertEquals(testItem.getOwner().getId(), updatedItem.getOwner().getId());
        assertEquals(testItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void updateItem_whenNotOwner_thenUserMismatchExceptionThrown() {
        Long userId = 2L;
        Long itemId = 1L;
        ItemDto updateItemDto = ItemDto.builder()
                .id(1L)
                .name("test item")
                .description("Test item of test Owner Updated")
                .available(true)
                .build();
        when(itemRepository.findById(any())).thenReturn(Optional.of(testItem));

        assertThrows(UserMismatchException.class, () -> itemService.updateItem(itemId, userId, updateItemDto));
    }

    @Test
    void getItemById_whenItemFoundAndOwner_thenReturnBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(Booking.builder()
                .id(1L)
                .status(Status.WAITING)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build());
        bookings.add(Booking.builder()
                .id(2L)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());

        when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);
        when(bookingShortMapper.toDTO(any())).thenReturn(bookingShortDto);
        when(itemRepository.findById(any())).thenReturn(Optional.of(testItem));
        when(itemMapper.toDTO(any())).thenReturn(testItemDto);
        when(commentRepository.findAllByItem_Id(any())).thenReturn(new ArrayList<>());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem));


        ItemDto actualItemDto = itemService.getItemById(userId, itemId);

        assertNotNull(actualItemDto.getNextBooking());
        assertNotNull(actualItemDto.getLastBooking());
        assertNotNull(actualItemDto.getComments());
    }

    @Test
    void getItemById_whenItemNotExists_thenItemNotFoundExceptionThrown() {
        Long itemId = 1L;
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getItem(itemId));
    }

    @Test
    void getAllItemsOfUserWithId_whenDataCorrect_thenReturnList() {
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        when(itemRepository.findByOwnerId(any(), any())).thenReturn(Page.empty());

        assertEquals(0, itemService.getAllItemsOfUserWithId(userId, offset, limit).size());
    }

    @Test
    void searchItem_whenTextEmptyOrNull_ThenReturnEmptyList() {
        String text = "";
        Integer offset = 0;
        Integer limit = 10;
        assertTrue(itemService.searchItem(text, offset, limit).isEmpty());
    }

    @Test
    void searchItem_whenTextNotEmptyOrNull_ThenReturnList() {
        String text = "test";
        Integer offset = 0;
        Integer limit = 10;
        when(itemRepository.findByAvailableIsTrueAndDescriptionContainsIgnoreCaseOrNameContainsIgnoreCase(any(), any(),
                any())).thenReturn(Page.empty());
        assertEquals(0, itemService.searchItem(text, offset, limit).size());
    }

    @Test
    void addComment_whenDataCorrect_thenSaveComment() {
        Long userId = 2L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Comment text")
                .authorName(testUser.getName())
                .created(LocalDateTime.now())
                .build();

        Comment comment = Comment.builder()
                .text("Comment text")
                .author(testUser)
                .item(testItem)
                .created(LocalDateTime.now())
                .build();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(testItem)
                .booker(testUser)
                .status(Status.APPROVED)
                .build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingRepository.findAllByItemIdAndBookerId(itemId, userId)).thenReturn(bookings);
        when(commentRepository.save(any())).thenReturn(comment);

        itemService.addComment(itemId, userId, commentDto);

        verify(commentMapper).toDTO(commentArgumentCaptor.capture());
        Comment savedComment = commentArgumentCaptor.getValue();

        assertEquals(comment.getText(), savedComment.getText());
        assertEquals(comment.getItem().getId(), savedComment.getItem().getId());
        assertEquals(comment.getAuthor().getId(), savedComment.getAuthor().getId());
    }

    @Test
    void addComment_whenStartAndOrEndIncorrect_thenUnsupportedStatusExceptionThrown() {
        Long userId = 2L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Comment text")
                .authorName(testUser.getName())
                .created(LocalDateTime.now())
                .build();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(testItem)
                .booker(testUser)
                .status(Status.APPROVED)
                .build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingRepository.findAllByItemIdAndBookerId(itemId, userId)).thenReturn(bookings);

        assertThrows(UnsupportedStatusException.class, () -> itemService.addComment(itemId, userId, commentDto));
    }

    @Test
    void addComment_whenNoBookings_thenUnsupportedStatusExceptionThrown() {
        Long userId = 2L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Comment text")
                .authorName(testUser.getName())
                .created(LocalDateTime.now())
                .build();
        List<Booking> bookings = new ArrayList<>();

        when(bookingRepository.findAllByItemIdAndBookerId(itemId, userId)).thenReturn(bookings);

        assertThrows(UnsupportedStatusException.class, () -> itemService.addComment(itemId, userId, commentDto));
    }

    @Test
    void connectBooking_whenDataCorrect_thenReturnLastAndNext() {
        Long itemId = 1L;
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(Booking.builder()
                .id(1L)
                .status(Status.WAITING)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build());
        bookings.add(Booking.builder()
                .id(2L)
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());

        when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);
        when(bookingShortMapper.toDTO(any())).thenReturn(bookingShortDto);

        assertNotNull(itemService.connectBooking(testItemDto).getLastBooking());
        assertNotNull(itemService.connectBooking(testItemDto).getNextBooking());
    }


}