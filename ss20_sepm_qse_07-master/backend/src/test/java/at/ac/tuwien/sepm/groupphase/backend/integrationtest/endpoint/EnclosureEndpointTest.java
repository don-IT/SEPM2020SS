package at.ac.tuwien.sepm.groupphase.backend.integrationtest.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.EnclosureEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EnclosureMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import springfox.documentation.spi.service.contexts.SecurityContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.PICTURE_LION_ENCLOSURE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EnclosureEndpointTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private EnclosureEndpoint enclosureEndpoint;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EnclosureMapper enclosureMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Enclosure enclosureDetailed = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private Enclosure enclosureMinimal = Enclosure.builder()
        .name("Wolf Enclosure")
        .description(null)
        .publicInfo(null)
        .picture(null)
        .build();
    private Enclosure enclosureMinimal2 = Enclosure.builder()
        .id(1L)
        .name("Wolf Enclosure")
        .description(null)
        .publicInfo(null)
        .picture(null)
        .build();

    private Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    @BeforeEach
    public void beforeEach() {
        enclosureRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        enclosureRepository.deleteAll();
    }

    @Test
    public void givenOneEnclosure_whenGet_thenOK() throws Exception {
        enclosureRepository.save(enclosureDetailed);
        MvcResult mvcResult = this.mockMvc.perform(get(ENCLOSURE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get(ENCLOSURE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EnclosureDto> enclosureDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EnclosureDto[].class));

        assertEquals(0, enclosureDtos.size());
    }



    @Test
    public void givenNonExistingEnclosure_whenGet_thenOK() throws Exception {
        enclosureRepository.save(enclosureDetailed);
        MvcResult mvcResult = this.mockMvc.perform(get(ENCLOSURE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void givenEnclosure_whenPostSuccessed_then201() throws Exception {
        enclosureRepository.deleteAll();

       EnclosureDto enclosureDto = enclosureMapper.enclosureToEnclosureDto(enclosureMinimal);
        String body = objectMapper.writeValueAsString(enclosureDto);

        MvcResult mvcResult = this.mockMvc.perform(post(ENCLOSURE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

    }


    @Test
    public void whenAdminCreateEmployeeWithInvalidData_statusBadRequest() throws Exception{

        EnclosureDto enclosureNull = EnclosureDto.builder()
            .name(null)
            .description(null)
            .publicInfo(null)
            .picture(null)
            .build();

        String body = objectMapper.writeValueAsString(enclosureNull);

        MvcResult mvcResult = this.mockMvc.perform(post(ENCLOSURE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(),  response.getStatus());
    }


    @Test
    public void adminDeletesEnclosure() throws Exception {

        enclosureRepository.save(enclosureMinimal2);
        List<Enclosure> enclosures= enclosureRepository.findAll();
        Enclosure enclosure = enclosures.get(0);

        MvcResult mvcResult = this.mockMvc.perform(delete(ENCLOSURE_BASE_URI+'/'+enclosure.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(),  response.getStatus());
    }

    @Test
    public void adminDeletesEnclosureNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(ENCLOSURE_BASE_URI+'/'+enclosureMinimal2.getId())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(),  response.getStatus());
    }

    @Test
    public void editEnclosureName() throws Exception {
        enclosureRepository.save(enclosureMinimal);
        List<Enclosure> enclosures = enclosureRepository.findAll();
        EnclosureDto enclosureDto = enclosureMapper.enclosureToEnclosureDto(enclosures.get(0));

        enclosureDto.setName("Don");
        String body = objectMapper.writeValueAsString(enclosureDto);

        MvcResult mvcResult = this.mockMvc.perform(put(ENCLOSURE_BASE_URI +"/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        enclosures = enclosureRepository.findAll();
        assertEquals("Don",enclosures.get(0).getName());
        assertEquals(HttpStatus.OK.value(), response.getStatus());

    }
}
