package com.example.Config;

import com.example.Entity.Role;
import com.example.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public DatabaseSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            List<Role> roles = Arrays.asList(
                    createRole("ROLE_USER", "Default user role"),
                    createRole("ROLE_ADMIN", "Administrator role with full access"),
                    createRole("ROLE_MANAGER", "Manager role for managing specific entities"));
            roleRepository.saveAll(roles);
            System.out.println("Seeded database with default roles.");
        }
    }

    private Role createRole(String name, String description) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return role;
    }
}
