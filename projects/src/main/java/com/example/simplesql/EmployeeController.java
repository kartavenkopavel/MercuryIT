package com.example.simplesql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/list")
    public List<EmployeeEntity> getList() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public EmployeeEntity getById(@PathVariable Long id) {
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

    @PatchMapping("/update")
    public EmployeeEntity update(@RequestBody Map<String, Object> employeeMap) {
        Long id = null;
        if (employeeMap.get(EmployeeEntity.ID_FIELD) == null) {
            id = Long.decode(employeeMap.get(EmployeeEntity.ID_FIELD).toString());
        }
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

        return employee;
    }

    @DeleteMapping("/delete/{id}")
    public void remove(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }
}
