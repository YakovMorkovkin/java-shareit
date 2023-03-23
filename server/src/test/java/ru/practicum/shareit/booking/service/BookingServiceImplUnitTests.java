package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTests {
    User testOwner = User.builder()
            .email("email@email.ru")
            .name("test Owner")
            .id(1L)
            .build();
    UserDto testBookerDto = UserDto.builder()
            .email("email@email.ru")
            .name("test User")
            .id(2L)
            .build();
    User testBooker = User.builder()
            .email("email@email.ru")
            .name("test User")
            .id(2L)
            .build();
    ItemDto testItemDto = ItemDto.builder()
            .id(1L)
            .name("test item")
            .description("Test item of test Owner")
            .available(true)
            .build();
    Item testItem = Item.builder()
            .id(1L)
            .name("test item")
            .description("Test item of test Owner")
            .owner(testOwner)
            .available(true)
            .build();
    Booking expectedBooking = Booking.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .item(testItem)
            .booker(testBooker)
            .status(Status.WAITING)
            .build();
    BookingDto expectedBookingDto = BookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .item(testItemDto)
            .booker(testBookerDto)
            .build();

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemService itemService;
    @Captor
    private ArgumentCaptor<Booking> argumentCaptor;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void getBookingById_whenBookingFound_thenReturnBooking() {
        Long bookingId = 1L;
        Long userId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
        when(bookingMapper.toDTO(any())).thenReturn(expectedBookingDto);

        BookingDto actualBooking = bookingService.getBookingById(bookingId, userId);

        assertEquals(expectedBookingDto, actualBooking);
    }

    @Test
    void getBookingById_whenBookingNotFound_thenBookingNotFoundExceptionThrown() {
        Long bookingId = 1L;
        Long userId = 2L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getBookingById_whenUserIncorrect_thenBookingNotFoundExceptionThrown() {
        Long bookingId = 1L;
        Long userId = 0L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getAllBookingsOfUser_whenStateALL_thenReturnList() {
        String state = "ALL";
        Long userId = 0L;
        Integer offset = 0;
        Integer page = 0;
        Integer limit = 10;
        when(bookingRepository.findAllByBookerId(userId, PageRequest.of(page, limit, Sort.by("id")
                .descending()))).thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(new User());

        List<BookingDto> actual = bookingService.getAllBookingsOfUser(state, userId, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllBookingsOfUser_whenStateCURRENT_thenReturnList() {
        String state = "CURRENT";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(new User());

        List<BookingDto> actual = bookingService.getAllBookingsOfUser(state, userId, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllBookingsOfUser_whenStatePAST_thenReturnList() {
        String state = "PAST";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(new User());

        List<BookingDto> actual = bookingService.getAllBookingsOfUser(state, userId, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllBookingsOfUser_whenStateFUTURE_thenReturnList() {
        String state = "FUTURE";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        when(bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(new User());

        List<BookingDto> actual = bookingService.getAllBookingsOfUser(state, userId, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllBookingsOfUser_whenStateWAITING_thenReturnList() {
        String state = "WAITING";
        Long userId = 0L;
        Integer offset = 0;
        Integer page = 0;
        Integer limit = 10;
        when(bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING, PageRequest.of(page, limit, Sort.by("id")
                .descending()))).thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(new User());

        List<BookingDto> actual = bookingService.getAllBookingsOfUser(state, userId, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllBookingsOfUser_whenStateREJECTED_thenReturnList() {
        String state = "REJECTED";
        Long userId = 0L;
        Integer offset = 0;
        Integer page = 0;
        Integer limit = 10;
        when(bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED, PageRequest.of(page, limit, Sort.by("id")
                .descending()))).thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(new User());

        List<BookingDto> actual = bookingService.getAllBookingsOfUser(state, userId, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllBookingsOfUser_whenUnknownState_thenUnsupportedStatusExceptionThrown() {
        String state = "TEST";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        when(userService.getUser(userId)).thenReturn(testOwner);

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingsOfUser(state, userId, offset, limit));
    }

    @Test
    void getAllItemsBookingsOfOwner_whenStateALL_thenReturnList() {
        String state = "ALL";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(testItem);
        when(bookingRepository.findAllByBookerIdNotAndItemIn(any(), any(), any())).thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        List<BookingDto> actual = bookingService.getAllItemsBookingsOfOwner(userId, state, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllItemsBookingsOfOwner_whenStateCURRENT_thenReturnList() {
        String state = "CURRENT";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(testItem);
        when(bookingRepository.findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any(),
                any())).thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        List<BookingDto> actual = bookingService.getAllItemsBookingsOfOwner(userId, state, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllItemsBookingsOfOwner_whenStatePAST_thenReturnList() {
        String state = "PAST";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(testItem);
        when(bookingRepository.findAllByBookerIdNotAndItemInAndStartIsBeforeAndEndIsBefore(any(), any(), any(), any(),
                any())).thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        List<BookingDto> actual = bookingService.getAllItemsBookingsOfOwner(userId, state, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllItemsBookingsOfOwner_whenStateFUTURE_thenReturnList() {
        String state = "FUTURE";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(testItem);
        when(bookingRepository.findAllByBookerIdNotAndItemInAndStartIsAfterAndEndIsAfter(any(), any(), any(), any(),
                any())).thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        List<BookingDto> actual = bookingService.getAllItemsBookingsOfOwner(userId, state, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllItemsBookingsOfOwner_whenStateWAITING_thenReturnList() {
        String state = "WAITING";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(testItem);
        when(bookingRepository.findAllByBookerIdNotAndItemInAndStatus(any(), any(), any(), any()))
                .thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        List<BookingDto> actual = bookingService.getAllItemsBookingsOfOwner(userId, state, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllItemsBookingsOfOwner_whenStateREJECTED_thenReturnList() {
        String state = "REJECTED";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(testItem);
        when(bookingRepository.findAllByBookerIdNotAndItemInAndStatus(any(), any(), any(), any()))
                .thenReturn(Page.empty());
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        List<BookingDto> actual = bookingService.getAllItemsBookingsOfOwner(userId, state, offset, limit);
        assertEquals(0, actual.size());
    }

    @Test
    void getAllItemsBookingsOfOwner_whenNoItems_thenUserHaveNotAnyItemExceptionThrown() {
        String state = "ALL";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        assertThrows(UserHaveNotAnyItemException.class, () -> bookingService.getAllItemsBookingsOfOwner(userId, state,
                offset, limit));
    }

    @Test
    void getAllItemsBookingsOfOwner_whenUnknownState_thenUnsupportedStatusExceptionThrown() {
        String state = "TEST";
        Long userId = 0L;
        Integer offset = 0;
        Integer limit = 10;
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(testItem);
        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemRepository.findByOwnerId(userId)).thenReturn(ownerItems);

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllItemsBookingsOfOwner(userId, state,
                offset, limit));
    }

    @Test
    void addBooking_whenEndBeforeStart_thenEndBeforeStartExceptionThrown() {
        Long userId = 0L;
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now())
                .itemId(1L)
                .build();
        assertThrows(StartEndMismatchException.class, () -> bookingService.addBooking(userId, bookingDtoIn));
    }

    @Test
    void addBooking_whenUserIsOwner_thenBookingUnavailableExceptionThrown() {
        Long userId = 0L;
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(5))
                .itemId(1L)
                .build();

        when(userService.getUser(userId)).thenReturn(testOwner);
        when(itemService.getItem(bookingDtoIn.getItemId())).thenReturn(testItem);

        assertThrows(BookingUnavailableException.class, () -> bookingService.addBooking(userId, bookingDtoIn));
    }

    @Test
    void addBooking_whenItemUnavailable_thenItemUnavailableExceptionThrown() {
        Long userId = 0L;
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(5))
                .itemId(1L)
                .build();
        testItem.setAvailable(false);

        when(userService.getUser(userId)).thenReturn(testBooker);
        when(itemService.getItem(bookingDtoIn.getItemId())).thenReturn(testItem);

        assertThrows(ItemUnavailableException.class, () -> bookingService.addBooking(userId, bookingDtoIn));
        testItem.setAvailable(true);
    }

    @Test
    void addBooking_whenDataCorrect_thenSave() {
        Long userId = 0L;
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(5))
                .itemId(1L)
                .build();

        when(userService.getUser(userId)).thenReturn(testBooker);
        when(itemService.getItem(bookingDtoIn.getItemId())).thenReturn(testItem);
        when(bookingRepository.save(any())).thenReturn(expectedBooking);
        when(bookingMapper.toDTO(any())).thenReturn(expectedBookingDto);

        assertEquals(expectedBookingDto, bookingService.addBooking(userId, bookingDtoIn));
    }

    @Test
    void approveBooking_whenBookingApprovedDouble_thenDoubleApprovingExceptionThrown() {
        Long userId = 1L;
        Booking booking = Booking.builder()
                .id(0L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(testItem)
                .booker(testBooker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThrows(DoubleApprovingException.class, () -> bookingService.approveBooking(booking.getId(), userId, true));
    }

    @Test
    void approveBooking_whenDataCorrectAndApprovedIsTrue_thenStatusApprovedReturn() {
        Long userId = 1L;
        Booking booking = Booking.builder()
                .id(0L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(testItem)
                .booker(testBooker)
                .status(Status.WAITING)
                .build();

        Booking expectedBooking = Booking.builder()
                .id(0L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(testItem)
                .booker(testBooker)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        bookingService.approveBooking(booking.getId(), userId, true);

        verify(bookingMapper).toDTO(argumentCaptor.capture());
        Booking updatedBooking = argumentCaptor.getValue();

        assertEquals(expectedBooking.getStatus(), updatedBooking.getStatus());
    }

    @Test
    void approveBooking_whenDataCorrectAndApprovedIsFalse_thenStatusRejectedReturn() {
        Long userId = 1L;
        Booking booking = Booking.builder()
                .id(0L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(testItem)
                .booker(testBooker)
                .status(Status.WAITING)
                .build();

        Booking expectedBooking = Booking.builder()
                .id(0L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(testItem)
                .booker(testBooker)
                .status(Status.REJECTED)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        bookingService.approveBooking(booking.getId(), userId, false);

        verify(bookingMapper).toDTO(argumentCaptor.capture());
        Booking updatedBooking = argumentCaptor.getValue();

        assertEquals(expectedBooking.getStatus(), updatedBooking.getStatus());
    }

    @Test
    void approveBooking_whenUserIsNotOwner_thenBookingUnavailableExceptionThrown() {
        Long userId = 2L;
        Booking booking = Booking.builder()
                .id(0L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(testItem)
                .booker(testBooker)
                .status(Status.WAITING)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThrows(BookingUnavailableException.class, () -> bookingService.approveBooking(booking.getId(), userId, true));
    }
}