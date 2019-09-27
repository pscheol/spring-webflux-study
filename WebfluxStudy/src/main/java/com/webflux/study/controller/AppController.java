package com.webflux.study.controller;

import com.webflux.study.service.AppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AppController {

    private final AppService appService;

    @GetMapping(value = "/images/view/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> getImage(@PathVariable String fileName) {
        log.debug("fileName===>>>> : {}", fileName);
        return appService.getImage(fileName)
                .map(resource -> {
                    try {
                        return ResponseEntity.ok()
                                .contentLength(resource.contentLength())
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (Exception e) {
                        return ResponseEntity.badRequest()
                                .body("Couldn't find " + fileName + " -> " + e.getMessage());
                    }
                });
    }

    @PostMapping(value = "/images")
    public Mono<String> upload(@RequestPart(name = "file") Flux<FilePart> files) {
        return appService.uploads(files).then(Mono.just("redirect:/"));
    }

    @DeleteMapping("/images/delete/{fileName:.+}")
    public Mono<String> deleteFile(@PathVariable String fileName) {
        log.debug("delete FileName : " + fileName);
        return appService.delete(fileName)
                .then(Mono.just("redirect:/"));
    }

    @GetMapping("/")
    public Mono<String> index(Model model) {
        model.addAttribute("images", appService.getImages());
        return Mono.just("index");
    }
}
