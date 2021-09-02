package employees;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class EmployeeNotFoundException extends AbstractThrowableProblem {
    public EmployeeNotFoundException(Long id) {
        super(URI.create("employees/employee-not-found"),
                "Not found",
                Status.NOT_FOUND,
                String.format("Employee with id %d not found", id));
    }
}
