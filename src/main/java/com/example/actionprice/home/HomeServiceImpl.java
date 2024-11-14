package com.example.actionprice.home;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class HomeServiceImpl implements HomeService {

  // 이미지들을 static에 다 저장하고 경로를 입력해야 함
  // 홈페이지에 반환할 이미지들의 경로
  private final String[] imagePaths = {
          "static/image/avocado.jpeg",
          "static/image/banana.jpeg",
          "static/image/berry.jpeg",
          "static/image/cTomato.jpeg",
          "static/image/egg.jpeg",
          "static/image/garlic.jpeg",
          "static/image/mixedFruit.jpeg",
          "static/image/mixedVeggie.jpeg",
          "static/image/mixedVeggies3.jpeg",
          "static/image/morlahana.jpeg",
          "static/image/nar.jpeg",
          "static/image/onion.jpeg",
          "static/image/onion2.jpeg",
          "static/image/onion3.jpeg",
          "static/image/cherry.jpeg",
          "static/image/pear.jpeg",
          "static/image/pepper.jpeg",
          "static/image/pineapple.jpeg",
          "static/image/watermelon.jpeg",
          "static/image/pepper.jpeg",
          "static/image/walnut.jpeg",
          "static/image/crop.jpg",
          "static/image/fish.jpg",
          "static/image/fruit.jpg",
          "static/image/meat.jpg",
          "static/image/scrop.jpg",
          "static/image/ve.jpg",
          "static/image/slide_blue.jpg",
          "static/image/slide_cherry.jpg",
          "static/image/slide_chicken.jpg",
          "static/image/slide_grape.jpg",
          "static/image/slide_lab.jpg",
          "static/image/slide_lemon.jpg",
          "static/image/slide_meat.jpg",
          "static/image/slide_ve.jpg"
  };

  /**
   * 홈페이지에서 사용할 이미지를 인코딩해서 반환하는 fetch 메서드
   * @author 연상훈
   * @created 2024-11-08 오전 11:05*
   */
  @Override
  public Map<String, String> fetchImages() throws IOException {
    Map<String, String> images = new HashMap<>();

    for (String imagePath : imagePaths) {
      log.info("image path: " + imagePath);
      ClassPathResource image = new ClassPathResource(imagePath);
      byte[] imageBytes = Files.readAllBytes(image.getFile().toPath());
      String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

      // 마지막 슬래시("/") 뒤부터 맨 뒤의 점(".") 까지
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
      imageName = imageName.substring(0, imageName.lastIndexOf("."));

      images.put(imageName, base64Image);
    }

    return images;
  }
}
