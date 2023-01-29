package es.codeurjc.mca.practica_1_pruebas_ordinaria.mockMVCTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.Event;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventDto;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventRepository;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventService;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.image.ImageService;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.UserComponent;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // no se lanza el servidor, sino el contexto -> existen todas las clases
public class EventRestControllerTest {
    @Autowired
    private MockMvc mvc;

    // aqui no se mete la anotacion, no estamos mockeando el servicio, solo el repositorio. El servicio debe funcionar igual.
    private EventService eventService;

    @MockBean
    private ImageService imageService;
    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserComponent userComponent;
    @InjectMocks
    private ModelMapper mapper;
    private List<Event> eventList;
    private EventDto eventDto;
    private Event event;
    private User user;
    private String apiPrefix = "/api/events";

    @BeforeEach
    public void initData() {
        User user = new User("admin", "admin@urjc.es", "pass", User.ROLE_ADMIN);
        user.setId(1L);
        Calendar c1 = Calendar.getInstance();
        c1.set(2021, Calendar.MAY, 2, 18, 30);

        Event event = new Event("Concierto municipal de Móstoles", "Concierto ofrecido por ...", c1.getTime(), 19.99, 50);
        event.setId(1L);
        event.setCreator(user);
        EventDto eventDto = this.mapper.map(event, EventDto.class);

        this.eventList = Arrays.asList(event);
        this.eventDto = eventDto;
        this.event = event;
        this.user = user;
    }

    @Test
    public void getAllTest() throws Exception {
        // given
        List<Event> eventList = this.eventList;
        // when
        when(this.eventRepository.findAll()).thenReturn(eventList);
        // then
        this.mvc.perform(get(this.apiPrefix + "/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo(eventList.get(0).getName())));
    }

    @Test
    @WithMockUser(username = "Patxi", password = "pass", roles = "ORGANIZER")
    public void createEventTest() throws Exception {
        // given
        User user = this.user;
        Event event = this.event;
        ObjectMapper objectMapper = new ObjectMapper();

        // when
        when(this.imageService.createImage(this.eventDto.getMultiparImage())).thenReturn("test path");
        when(this.userComponent.getLoggedUser()).thenReturn(user);
        when(this.eventRepository.save(any(Event.class))).thenReturn(event);

        // then
        this.mvc.perform(
                post(this.apiPrefix + "/")
                        .content(objectMapper.writeValueAsString(this.eventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo(event.getName())));
    }

    @Test
    @WithMockUser(username = "admin", password = "pass", roles = "ADMIN")
    public void deleteEventTest() throws Exception {
        // given
        Event event = this.event;
        User user = this.user;
        
        // when
        when(this.eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(this.userComponent.getLoggedUser()).thenReturn(user);

        // then
        this.mvc.perform(delete(this.apiPrefix + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Event deleted successfully"));

        verify(this.eventRepository).delete(event);
        verify(this.imageService).deleteImage(event.getImage());
    }


}
