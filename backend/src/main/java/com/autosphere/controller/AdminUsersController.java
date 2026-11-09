package com.autosphere.controller;
import com.autosphere.model.*;import com.autosphere.repository.UserRepository;import org.springframework.web.bind.annotation.*;import jakarta.validation.Valid;import jakarta.validation.constraints.*;import java.util.List;import java.util.Map;
@RestController @RequestMapping("/api/admin/users") public class AdminUsersController {
 private final UserRepository users;public AdminUsersController(UserRepository u){users=u;}
 @GetMapping public List<Map<String,Object>> list(){return users.findAll().stream().map(u->Map.<String,Object>of("id",u.id,"name",u.name,"email",u.email,"role",u.role.name())).toList();}
 public record RoleInput(@NotNull Role role){}
 @PatchMapping("/{id}/role") public Map<String,Object> role(@PathVariable Long id,@Valid @RequestBody RoleInput i){AppUser u=users.findById(id).orElseThrow();u.role=i.role();users.save(u);return Map.of("id",u.id,"email",u.email,"role",u.role.name());}
}
