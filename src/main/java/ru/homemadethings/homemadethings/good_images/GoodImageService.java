package ru.homemadethings.homemadethings.good_images;

import org.springframework.stereotype.Service;

@Service
public class GoodImageService {

    private final GoodImagesRepository goodImagesRepository;

    public GoodImageService(GoodImagesRepository goodImagesRepository) {
        this.goodImagesRepository = goodImagesRepository;
    }


    public GoodImage save(GoodImage goodImage) {
        return goodImagesRepository.save(goodImage);
    }
}
