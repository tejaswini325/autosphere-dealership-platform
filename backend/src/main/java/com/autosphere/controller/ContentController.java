package com.autosphere.controller;
import com.autosphere.model.DealerContent;import com.autosphere.repository.DealerContentRepository;import jakarta.validation.Valid;import jakarta.validation.constraints.NotBlank;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api") public class ContentController {
 private final DealerContentRepository repo;public ContentController(DealerContentRepository r){repo=r;}
 public record Input(@NotBlank String type,@NotBlank String title,String body,String imageUrl,boolean active){}
 @GetMapping("/content/{type}") public List<DealerContent> list(@PathVariable String type){return repo.findByTypeAndActiveTrue(type.toUpperCase());}
 @GetMapping("/admin/content") public List<DealerContent> all(){return repo.findAll();}
 @PostMapping("/admin/content") public DealerContent add(@Valid @RequestBody Input i){DealerContent c=new DealerContent();c.type=i.type().toUpperCase();c.title=i.title();c.body=i.body();c.imageUrl=i.imageUrl();c.active=i.active();return repo.save(c);}
 @PutMapping("/admin/content/{id}") public DealerContent update(@PathVariable Long id,@Valid @RequestBody Input i){DealerContent c=repo.findById(id).orElseThrow();c.type=i.type().toUpperCase();c.title=i.title();c.body=i.body();c.imageUrl=i.imageUrl();c.active=i.active();return repo.save(c);}
}
