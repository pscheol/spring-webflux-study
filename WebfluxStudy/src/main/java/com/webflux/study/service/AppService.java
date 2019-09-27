package com.webflux.study.service;

import com.webflux.study.domain.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.webflux.study.common.Constants.UPLOAD_ROOT;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppService {


    private final ResourceLoader resourceLoader; //파일을 관리하기 위한 스프링 유틸리티 클래스

    public Flux<Image> getImages() {
        try {
            return Flux.fromIterable(Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)))
                    .map(path ->
                            new Image(Integer.toString(path.hashCode()), path.getFileName().toString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Mono<Resource> getImage(String fileName) {
        try {
            return Mono.fromSupplier(() -> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + fileName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Mono<Void> uploads(Flux<FilePart> files) {
        return files.flatMap(file -> file.transferTo(Paths.get(UPLOAD_ROOT, file.filename()).toFile())).then();
    }

    public Mono<Void> delete(String fileName) {
        return Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
