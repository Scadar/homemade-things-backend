package ru.homemadethings.homemadethings.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.homemadethings.homemadethings.auth.model.CustomUserDetails;
import ru.homemadethings.homemadethings.auth.model.User;
import ru.homemadethings.homemadethings.auth.repository.UserRepository;
import ru.homemadethings.homemadethings.good_images.GoodImage;
import ru.homemadethings.homemadethings.good_images.GoodImageService;
import ru.homemadethings.homemadethings.specifications.Specification;
import ru.homemadethings.homemadethings.specifications.SpecificationRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class GoodService {

    @Value("${upload.path}")
    private String uploadPath;

    private final GoodRepository goodRepository;
    private final SpecificationRepository specificationRepository;
    private final GoodImageService goodImageService;
    private final UserRepository userRepository;

    @Autowired
    public GoodService(GoodRepository goodRepository, SpecificationRepository specificationRepository, GoodImageService goodImageService, UserRepository userRepository) {
        this.goodRepository = goodRepository;
        this.specificationRepository = specificationRepository;
        this.goodImageService = goodImageService;
        this.userRepository = userRepository;
    }

    public Page<Good> findGoodsWithPagination(int offset, int pageSize) {
        return goodRepository.findAll(PageRequest.of(offset, pageSize));
    }

    @Transactional
    public Good addGood(CustomUserDetails user, GoodRequest good, List<MultipartFile> images) {

        User userFromDb = userRepository
                .findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Good newGood = new Good();
        newGood.setTitle(good.getTitle());
        newGood.setPrice(good.getPrice());
        newGood.setDiscount(good.getDiscount());
        newGood.setDescription(good.getDescription());
        newGood.setUser(userFromDb);

        Good goodFromDb = goodRepository.save(newGood);

        List<Specification> specifications = parseSpecifications(good.getSpecifications());

        specifications.forEach(s -> {
            s.setGood(goodFromDb);
        });

        List<Specification> specificationsFromDb = specificationRepository.saveAll(specifications);

        goodFromDb.setSpecifications(specificationsFromDb);

        List<GoodImage> goodImages = new ArrayList<>();

        if(images != null) {
            try {
                images.forEach(img -> {
                    File uploadDir = new File(uploadPath + "/" + user.getId() + "/" + goodFromDb.getId());

                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    String uuidFile = UUID.randomUUID().toString();
                    String resultFileName = uuidFile + "." + img.getOriginalFilename();

                    try {
                        img.transferTo(new File(uploadDir + "/" + resultFileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Не удалось записать картинки");
                    }

                    GoodImage goodImage = new GoodImage();
                    goodImage.setPath(uploadDir + "/" + resultFileName);
                    goodImage.setTitle(img.getOriginalFilename());
                    goodImage.setGood(goodFromDb);
                    goodImages.add(goodImageService.save(goodImage));
                });
            } catch (Exception error) {
                throw new RuntimeException("Не удалось записать картинки");
            }
        }

        goodFromDb.setGoodImages(goodImages);

        return goodFromDb;
    }

    private List<Specification> parseSpecifications(List<String> specifications) {
        List<Specification> result = new ArrayList<>();
        specifications.forEach(s -> {
            String[] splitSpecifications = s.split(":");
            Specification specification = new Specification();
            specification.setTitle(splitSpecifications[0]);
            specification.setValue(splitSpecifications[1]);
            result.add(specification);
        });
        return result;
    }

    public List<Good> getGoodsByUser(CustomUserDetails user) {
        return goodRepository.findAllByUser(user);
    }
}
