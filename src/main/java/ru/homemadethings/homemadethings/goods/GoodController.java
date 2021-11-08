package ru.homemadethings.homemadethings.goods;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.homemadethings.homemadethings.auth.annotation.CurrentUser;
import ru.homemadethings.homemadethings.auth.model.CustomUserDetails;

import java.util.List;


@RestController
@RequestMapping("/api/good")
public class GoodController {

    private final GoodService goodService;

    public GoodController(GoodService goodService) {
        this.goodService = goodService;
    }

    @GetMapping("/{offset}/{pageSize}")
    public ResponseEntity<Page<Good>> getGoodsWithSort(@PathVariable int offset, @PathVariable int pageSize){
        return ResponseEntity.ok(goodService.findGoodsWithPagination(offset, pageSize));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
    @ApiModelProperty
    public ResponseEntity<Good> addGood(@CurrentUser CustomUserDetails user, @ModelAttribute GoodRequest good, @RequestParam(required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(goodService.addGood(user, good, images));
    }

    @GetMapping
    @ApiModelProperty
    public ResponseEntity<List<Good>> addGood(@CurrentUser CustomUserDetails user) {
        return ResponseEntity.ok(goodService.getGoodsByUser(user));
    }
}
