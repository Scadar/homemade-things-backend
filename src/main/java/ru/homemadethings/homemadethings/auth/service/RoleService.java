/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.homemadethings.homemadethings.auth.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.homemadethings.homemadethings.auth.model.Role;
import ru.homemadethings.homemadethings.auth.model.RoleName;
import ru.homemadethings.homemadethings.auth.model.User;
import ru.homemadethings.homemadethings.auth.repository.RoleRepository;
import ru.homemadethings.homemadethings.auth.repository.UserRepository;

import java.util.Collection;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Find all roles from the database
     */
    public Collection<Role> findAll() {
        return roleRepository.findAll();
    }

    public void addSellerRoleToUser(final Long userId) throws NotFoundException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        Set<Role> userRoles = user.getRoles();

        boolean userHasSellerRole = userRoles
                .stream()
                .map(Role::getRole)
                .anyMatch(role -> role.equals(RoleName.ROLE_SELLER));

        if (userHasSellerRole) {
            throw new RuntimeException("The user with id " + userId + " already has the role of the seller");
        }

        Role sellerRole = roleRepository
                .findByRole(RoleName.ROLE_SELLER)
                .orElseThrow(() -> new NotFoundException("Role " + RoleName.ROLE_SELLER.name() + " not found"));

        user.addRole(sellerRole);

        userRepository.save(user);
    }

    public Role findByRoleName(RoleName roleName){
        return roleRepository.findByRole(roleName).orElseThrow(() -> new RuntimeException("Role not found"));
    }

}
