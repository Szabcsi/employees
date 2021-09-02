package employees;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeesService {

    private ModelMapper modelMapper;

    public EmployeesService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    private List<Employee> employees = Collections.synchronizedList(new ArrayList<>(List.of(
            new Employee(1L, "John Doe"),
            new Employee(2L, "Jack Doe")
    )));

    public List<EmployeeDto> listEmployees(Optional<String> prefix) {
        Type targetListType = new TypeToken<List<EmployeeDto>>(){}.getType();
        List<Employee> filtered = employees.stream().filter(f -> prefix.isEmpty() || f.getName().toLowerCase().startsWith(prefix.get().toLowerCase()))
                .collect((Collectors.toList()));
        return modelMapper.map(filtered,targetListType);
    }

    public EmployeeDto findEmployeeById(Long id) {
       return modelMapper.map(employees.stream().filter(f -> f.getId() == id).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id)),
               EmployeeDto.class);
    }
}
