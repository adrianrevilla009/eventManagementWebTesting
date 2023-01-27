package es.codeurjc.mca.practica_1_pruebas_ordinaria.mockMVCtest;

import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.Event;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventDto;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventRepository;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventService;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.xmlunit.util.Mapper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    private List<Event> eventList;

    private String apiPrefix = "/api/events";

    @InjectMocks
    private ModelMapper mapper;

    @BeforeEach
    public void initData() {
        User user = new User("Test", "test@urjc.es", "test", User.ROLE_CUSTOMER);
        Calendar c1 = Calendar.getInstance();
        c1.set(2021, Calendar.MAY, 2, 18, 30);

        Event event1 = new Event("Test1", "Test1", c1.getTime(), 10.0, 50);
        event1.setCreator(user);
        Event event2 = new Event("Test2", "Test2", c1.getTime(), 20.0, 100);
        event1.setCreator(user);

        this.eventList = Arrays.asList(event1, event2);
    }

    @Test
    public void getAllTest() throws Exception {
        // given
        List<Event> eventList = this.eventList;
        // when
        when(this.eventService.findAll()).thenReturn(eventList);
        // then
        this.mvc.perform(get(this.apiPrefix + "/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo(eventList.get(0).getName())));
    }

    @Test
    @WithMockUser(username = "Patxi", password = "pass", roles = "ORGANIZER")
    public void createEventTest() throws Exception {
        // given
        EventDto eventDto = this.mapper.map(this.eventList.get(0), EventDto.class);
        Event savedEvent = new Event(eventDto.getName(), eventDto.getDescription(), eventDto.getDate(), eventDto.getPrice(), eventDto.getMax_capacity());
        savedEvent.setId(1L);
        // when
        when(this.eventService.createEvent(eventDto)).thenReturn(savedEvent);
        // when(this.eventRepository.save(savedEvent)).thenReturn(savedEvent);
        // then
        this.mvc.perform(post(this.apiPrefix + "/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo(savedEvent.getName())));
    }

    @Test
    @WithMockUser(username = "admin", password = "pass", roles = "ADMIN")
    public void deleteEventTest() throws Exception {
        // given
        EventDto eventDto = this.mapper.map(this.eventList.get(0), EventDto.class);
        Event savedEvent = new Event(eventDto.getName(), eventDto.getDescription(), eventDto.getDate(), eventDto.getPrice(), eventDto.getMax_capacity());
        savedEvent.setId(1L);
        // when
        when(this.eventService.getEvent(1L)).thenReturn(savedEvent);
        when(this.eventService.belongsToMe(savedEvent)).thenReturn(true);
        // then
        this.mvc.perform(delete(this.apiPrefix + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Event deleted successfully"));
    }


}
