package com.terence.demo.controllers;

import com.terence.demo.services.ImageResizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


/**
 * A controller that provides routing for the server
 * */
@Controller
public class ImageResizerController {

    @Autowired
    private ImageResizerService imageResizerService;


    @PostMapping(value = "/resize-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> resizeImage(
            @RequestParam("percentageReductionOfImageSize") int percentageReductionOfImageSize,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {


        byte [] afterResizedImageByteArray =
                        this.imageResizerService.resizeImage(imageFile, percentageReductionOfImageSize);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(afterResizedImageByteArray);

    }


    @ExceptionHandler({ IllegalArgumentException.class})
    public ResponseEntity<String> handleIllegalArgumentException() {
        return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Invalid percentageReductionOfImageSize value");
    }




    @RequestMapping("/error")
    public String error(Exception e, WebRequest request) {

        return request.getContextPath();
    }
}
