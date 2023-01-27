package es.codeurjc.mca.practica_1_pruebas_ordinaria.mockMVCtest;

import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.Event;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventRepository;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.event.EventService;
import es.codeurjc.mca.practica_1_pruebas_ordinaria.user.User;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class EventServiceTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EventService eventService;

    private List<Event> eventList;

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
    @Description("First mock eventRepository behavior. Then call eventService which inside calls to the repository," +
            "and then verify if that repository have been called. Finally assert result.")
    public void findAllTest() {
        // given
        List<Event> eventList = this.eventList;
        // when
        when(this.eventRepository.findAll()).thenReturn(eventList);

        // TODO why this service is not mocked??
        List<Event> mockedEvents = (List<Event>) this.eventService.findAll();
        verify(this.eventRepository).findAll();
        // then
        assertThat(mockedEvents, hasSize(2));
        assertThat(mockedEvents.get(0).getName(), equalTo("Test1"));
    }


}
