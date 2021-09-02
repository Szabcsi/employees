package employees;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class EmployeesService {

    private ModelMapper modelMapper;

    private AtomicLong idGenerator = new AtomicLong();

    public EmployeesService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    private List<Employee> employees = Collections.synchronizedList(new ArrayList<>(List.of(
            new Employee(idGenerator.incrementAndGet(), "John Doe"),
            new Employee(idGenerator.incrementAndGet(), "Jack Doe")
    )));

    public List<EmployeeDto> listEmployees(Optional<String> prefix) {
        Type targetListType = new TypeToken<List<EmployeeDto>>(){}.getType();
        List<Employee> filtered = employees.stream().filter(f -> prefix.isEmpty() || f.getName().toLowerCase().startsWith(prefix.get().toLowerCase()))
                .collect((Collectors.toList()));
        return modelMapper.map(filtered,targetListType);
    }

    public EmployeeDto findEmployeeById(Long id) {
       return modelMapper.map(employees.stream().filter(f -> f.getId() == id).findAny()
                .orElseThrow(() -> new EmployeeNotFoundException(id)),
               EmployeeDto.class);
    }

    public EmployeeDto createEmployee(CreateEmployeeCommand command) {
        Employee employee = new Employee(idGenerator.incrementAndGet(),command.getName());
        employees.add(employee);
        return modelMapper.map(employee,EmployeeDto.class);
    }

    public EmployeeDto updateEmployee(Long id, UpdateEmployeeCommand command) {
        Employee employee = employees.stream().filter(f -> f.getId() == id)
                .findFirst().orElseThrow(() -> new EmployeeNotFoundException(id));
        employee.setName(command.getName());
        return modelMapper.map(employee, EmployeeDto.class);
    }

    public void deleteEmployee(Long id) {
        Employee employee = employees.stream().filter(f -> f.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(("Employee not found: " + id)));
        employees.remove(employee);
    }
}
