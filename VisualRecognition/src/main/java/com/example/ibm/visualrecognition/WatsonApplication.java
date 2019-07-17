package com.example.ibm.visualrecognition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author John
 * @create 2019/7/10 9:41
 */
@Slf4j
@SpringBootApplication
public class WatsonApplication {

    public static void main(String[] args) {
        SpringApplication.run(WatsonApplication.class,args);
    }

    @PostConstruct
    public void createTempDir(){
        String tempPath = null;
        try {
            tempPath = ResourceUtils.getFile("").getAbsolutePath() + File.separatorChar + "temp" + File.separatorChar;
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }
        File temp = new File(tempPath);
        if (!temp.exists()) {
            temp.mkdirs();
        }

    }
}
