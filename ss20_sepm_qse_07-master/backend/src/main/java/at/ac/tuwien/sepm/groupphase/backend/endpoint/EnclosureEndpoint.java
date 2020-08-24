package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EnclosureMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/enclosure")
public class EnclosureEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EnclosureService enclosureService;
    private final EnclosureMapper enclosureMapper;
    private final EnclosureRepository enclosureRepository;
    private final AnimalService animalService;
    private final AnimalMapper animalMapper;
    private final EmployeeService employeeService;

    @Autowired
    public EnclosureEndpoint(EnclosureService enclosureService, EnclosureMapper enclosureMapper,
                             EnclosureRepository enclosureRepository, AnimalService animalService,
                             AnimalMapper animalMapper, EmployeeService employeeService){
        this.employeeService = employeeService;
        this.enclosureService = enclosureService;
        this.enclosureMapper = enclosureMapper;
        this.enclosureRepository = enclosureRepository;
        this.animalService = animalService;
        this.animalMapper = animalMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "")
    @ApiOperation(value = "Create new enclosure", authorizations = {@Authorization(value = "apiKey")})
    public EnclosureDto createEnclosure(@RequestBody @Valid EnclosureDto enclosureDto){
        LOGGER.info("POST /api/v1/enclosure body: {}",enclosureDto);

        return enclosureMapper.enclosureToEnclosureDto(enclosureService.create(enclosureMapper.enclosureDtoToEnclosure(enclosureDto)));
    }


    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @ApiOperation(value = "Get list of all enclosures", authorizations = {@Authorization(value = "apiKey")})
    public List<EnclosureDto> getAllEnclosures(Authentication authentication){
        LOGGER.info("GET /api/v1/enclosure");
        List<Enclosure> enclosures = enclosureService.getAll();
        List<EnclosureDto> enclosureDtos = new LinkedList<>();
        for(Enclosure e: enclosures){
            enclosureDtos.add(enclosureMapper.enclosureToEnclosureDto(e));
        }
        return enclosureDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{enclosureId}")
    @ApiOperation(value = "Get detailed information about a specific employee",
        authorizations = {@Authorization(value = "apiKey")})
    public EnclosureDto getEnclosureById(@PathVariable Long enclosureId, Authentication authentication) {
        LOGGER.info("GET /api/v1/enclosure/{}", enclosureId);
        return enclosureMapper.enclosureToEnclosureDto(enclosureService.findById(enclosureId));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/animal/{enclosureId}")
    @ApiOperation(value = "Assign Animal to Enclosure",
        authorizations = {@Authorization(value = "apiKey")})
    public void assignAnimalToEnclosure(@RequestBody @NotNull AnimalDto animalDto, @PathVariable Long enclosureId) {
        LOGGER.info("POST /api/v1/enclosure/animal/{}", enclosureId);
        animalService.addAnimalToEnclosure(animalMapper.AnimalDtoToAnimal(animalDto), enclosureId);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/animal/{animalId}")
    @ApiOperation(value = "Get Enclosure of Animal",
        authorizations = {@Authorization(value = "apiKey")})
    public EnclosureDto getEnclosureByAnimalId(@PathVariable Long animalId) {
        LOGGER.info("GET /api/v1/enclosure/animal/{}", animalId);
        return enclosureMapper.enclosureToEnclosureDto(enclosureService.findByAnimalId(animalId));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete enclosure", authorizations = {@Authorization(value = "apiKey")})
    public void deleteEnclosure(@PathVariable Long id){
        LOGGER.info("DELETE /api/v1/enclosure/{}",id);
        enclosureService.deleteEnclosure(id);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/employee/{username}")
    @ApiOperation(value = "Get Enclosures of Employee",
        authorizations = {@Authorization(value = "apiKey")})
    public List<EnclosureDto> getEnclosuresByEmployeeUsername(@PathVariable String username) {
        LOGGER.info("GET /api/v1/enclosure/employee/{}", username);
        List<Enclosure> enclosures = employeeService.findAssignedEnclosures(username);
        List<EnclosureDto> enclosureDtos = new LinkedList<>();
        for(Enclosure e: enclosures){
            enclosureDtos.add(enclosureMapper.enclosureToEnclosureDto(e));
        }
        return enclosureDtos;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/edit")
    @ApiOperation(value = "Edit enclosure", authorizations = {@Authorization(value = "apiKey")})
    public EnclosureDto editEnclosure(@RequestBody @Valid EnclosureDto enclosureDto){
        LOGGER.info("PUT /api/v1/enclosure/edit body: {}",enclosureDto);
        return enclosureMapper.enclosureToEnclosureDto(enclosureService.editEnclosure(enclosureMapper.enclosureDtoToEnclosure(enclosureDto)));
    }
}
