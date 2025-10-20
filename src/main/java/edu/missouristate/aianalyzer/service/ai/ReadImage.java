package edu.missouristate.aianalyzer.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ReadImage {
    public static byte[] convertImageToBytes(Path imagePath, String fileType) throws IOException {
        File imageFile = new File(String.valueOf(imagePath));
        BufferedImage originalImage = ImageIO.read(imageFile);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(originalImage, fileType, baos);

        byte[] imageInBytes = baos.toByteArray();

        return imageInBytes;
    }
}
