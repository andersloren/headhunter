package se.sprinta.headhunterbackend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.sprinta.headhunterbackend.system.StatusCode;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    List<User> users;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setId("d66a3164-0a9d-4efb-943b-de64057aab14");
        u1.setUsername("Mikael");
        u1.setPassword("123456");
        u1.setRoles("admin user");

        User u2 = new User();
        u2.setId("d66a3164-0a9d-4efb-943b-de64057aab15");
        u2.setUsername("Anders");
        u2.setPassword("654321");
        u2.setRoles("user");

        this.users = new ArrayList<>();
        this.users.add(u1);
        this.users.add(u2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllUsersSuccess() throws Exception {
        // Given

        given(this.userService.findAll()).willReturn(this.users);

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data[0].username").value("Mikael"))
                .andExpect(jsonPath("$.data[0].roles").value("admin user"))
                .andExpect(jsonPath("$.data[1].username").value("Anders"))
                .andExpect(jsonPath("$.data[1].roles").value("user"));
    }
}