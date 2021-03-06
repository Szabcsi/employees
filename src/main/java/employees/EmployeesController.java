package employees;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Operations on employees")
public class EmployeesController {

    private EmployeesService employeesService;

    public EmployeesController(EmployeesService employeeService) {
        this.employeesService = employeeService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<EmployeeDto> listEmployees(@RequestParam Optional<String> prefix) {
        return employeesService.listEmployees(prefix);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EmployeeDto findEmployeeById(@PathVariable("id") Long id) {
        return employeesService.findEmployeeById(id);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity findEmployeeById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(employeesService.findEmployeeById(id));
        }
        catch(IllegalArgumentException iae){
            return ResponseEntity.notFound().build();
        }
    }*/


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "creates an employee")
    @ApiResponse(responseCode = "201", description = "employee has benn created")
    public EmployeeDto createEmployee(@Valid @RequestBody CreateEmployeeCommand command) {
        return employeesService.createEmployee(command);
    }

    @PutMapping("/{id}")
    public EmployeeDto updateEmployee(@PathVariable("id") Long id, @RequestBody UpdateEmployeeCommand command) {
        return employeesService.updateEmployee(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable("id") Long id) {
        employeesService.deleteEmployee(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Problem> handleNotFound(IllegalArgumentException iae) {
        Problem problem = Problem.builder().withType(URI.create("employees/not-found"))
                .withTitle(("Not found"))
                .withStatus(Status.NOT_FOUND)
                .withDetail(iae.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidException(MethodArgumentNotValidException exception) {
        List<Violation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        Problem problem = Problem.builder().withType(URI.create("employees/not-valid"))
                .withTitle("Validation error")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .with("violations", violations)
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

}
