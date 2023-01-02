package com.terence.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A service that allows for resizing of an image
 *
 * Example usage:
 *
 <pre>
      ...
      ImageResizerService service = new ImageResizerService();

      int percentageOfImageSizeToRetain = 40; // meaning 60% size reduction

      BufferedImage resizedImageOutput = service.resize(bufferedImageInput,percentageOfImageSizeToRetain);
      ...
 *</pre>
 * @author Terence Lee
 *
 * */
@Service
public class ImageResizerService {

    /**
     * Resizes a buffer image to become a percentage of its original size, based on the height and
     *   width of the image
     *
     *   For example, if the original image has width and height of 1000 pixels, and the percentage of
     *   original size to be retained is 40%, then the new height and width of the image will be
     *   400 pixels.
     *
     * @param imageMultipartFile - multipart file representing the image to be resized
     * @param percentageReductionOfImageSize - the percentage size reduction of the original image,
     *               between 1%(inclusive) and 99%(inclusive)
     *
     * @throws IllegalArgumentException - if percentageOfOriginalToRetain is not between 1 (inclusive)
     *      and 99 (inclusive)
     *
     * @return byte array representing the resized image (in jpeg format)
     * */
    public byte[] resizeImage(MultipartFile imageMultipartFile, int percentageReductionOfImageSize)
            throws IOException {

        validatePercentageReductionOfImageSize(percentageReductionOfImageSize);

        BufferedImage beforeResizedBufferedImage = convertMultipartFileToBufferedImage(imageMultipartFile);

        BufferedImage afterResizedBufferedImage = resizeImage(beforeResizedBufferedImage, percentageReductionOfImageSize);

        return convertBufferedImageToByteArray(afterResizedBufferedImage);
    }


    /*
    * Validate whether the percentage reduction of image size is within the valid values of
    * 1 (inclusive) and 99 (inclusive)
    * */
    private void validatePercentageReductionOfImageSize(int percentageReductionOfImageSize){

        final int MIN_PERCENTAGE_OF_ORIGINAL_TO_RETAIN_INCLUSIVE = 1;
        final int MAX_PERCENTAGE_OF_ORIGINAL_TO_RETAIN_INCLUSIVE = 99;

        if (percentageReductionOfImageSize < MIN_PERCENTAGE_OF_ORIGINAL_TO_RETAIN_INCLUSIVE ||
                percentageReductionOfImageSize > MAX_PERCENTAGE_OF_ORIGINAL_TO_RETAIN_INCLUSIVE){

            throw new IllegalArgumentException("Invalid percentageReductionOfImageSize");
        }
    }



    /*
    * Converts a MultipartFile instance to a BufferedImage instance, assuming that the MultipartFile instance
    * represents an image
    *
    * @param multipartFile - a multipart file instance
    * */
    private BufferedImage convertMultipartFileToBufferedImage(MultipartFile multipartFile) throws IOException {

        InputStream imageInputStream = multipartFile.getInputStream();

        return ImageIO.read(imageInputStream);
    }


    /*
    * Resizes a buffer image to become a percentage of its original size
    *
    *   For example, if the original image has width and height of 1000 pixels, and the percentage
    *   reduction of image size is 40%, then the new height and width of the image will be
    *   600 pixels.
    *
    * @param imageToResize - the image that is to be resized
    * @param percentageReductionOfImageSize - the percentage size reduction of the original image,
    *               between 1%(inclusive) and 99%(inclusive)
    * */
    private BufferedImage resizeImage(BufferedImage imageToResize, int percentageReductionOfImageSize){

        int originalWidth = imageToResize.getWidth();
        int originalHeight = imageToResize.getHeight();

        final int STARTING_POINT_X_COORDINATE = 0;
        final int STARTING_POINT_Y_COORDINATE = 0;
        final ImageObserver NO_IMAGE_OBSERVER = null;

        int percentageOfImageToRetain = 100 - percentageReductionOfImageSize;

        int newWidth = (originalWidth * percentageOfImageToRetain)/100;
        int newHeight = (originalHeight * percentageOfImageToRetain)/100;

        Image resultingImage = imageToResize.getScaledInstance(newWidth, newHeight,
                Image.SCALE_SMOOTH);

        BufferedImage outputImage =  new BufferedImage(newWidth, newHeight,
                BufferedImage.TYPE_INT_RGB);


        outputImage.createGraphics().drawImage(resultingImage,STARTING_POINT_X_COORDINATE,
                STARTING_POINT_Y_COORDINATE, NO_IMAGE_OBSERVER);

        return outputImage;
    }


    /*
    * Converts a BufferedImage to byte array in jpeg format
    * */
    private byte[] convertBufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);

        return baos.toByteArray();
    }

}