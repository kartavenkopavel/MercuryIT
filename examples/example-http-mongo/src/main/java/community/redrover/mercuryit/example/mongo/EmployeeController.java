package community.redrover.mercuryit.example.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public List<EmployeeEntity> getList() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public EmployeeEntity getById(@PathVariable String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find employee with id: " + id));
    }

    @PostMapping("/create")
    public EmployeeEntity create(@RequestBody EmployeeEntity employee) {
        employee.setId(null);
        return employeeRepository.save(employee);
    }

    @PutMapping("/edit")
    public EmployeeEntity edit(@RequestBody EmployeeEntity employee) {
        getById(employee.getId());
        return employeeRepository.save(employee);
    }

    @PatchMapping("/update/{id}")
    public EmployeeEntity update(@RequestBody Map<String, Object> employeeMap, @PathVariable String id) {
        EmployeeEntity employee = getById(id);

        for (String key : employeeMap.keySet()) {
            switch (key) {
                case EmployeeEntity.NAME_FIELD:
                    employee.setName((String)employeeMap.get(EmployeeEntity.NAME_FIELD));
                    break;

                case EmployeeEntity.TITLE_FIELD:
                    employee.setTitle((String)employeeMap.get(EmployeeEntity.TITLE_FIELD));
                    break;
            }
        }

        return employeeRepository.save(employee);
    }

    @DeleteMapping("/delete/{id}")
    public void remove(@PathVariable String id) {
        employeeRepository.deleteById(id);
    }
}
