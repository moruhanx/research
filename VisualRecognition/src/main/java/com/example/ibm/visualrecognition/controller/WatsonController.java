package com.example.ibm.visualrecognition.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.ibm.visualrecognition.config.WatsonVisualRecognitionConfig;
import com.example.ibm.visualrecognition.model.BaseResponse;
import com.example.ibm.visualrecognition.service.FacialRecognitionClassifierService;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Classifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author John
 * @create 2019/7/16 9:32
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
public class WatsonController {

    private static final String CLASSIFIER_STATUS = "training";
    private static final int MIN_IMAGE_NUMBERS = 10;

    private WatsonVisualRecognitionConfig config;
    private FacialRecognitionClassifierService classifierService;

    public WatsonController(FacialRecognitionClassifierService classifierService, WatsonVisualRecognitionConfig config) {
        this.classifierService = classifierService;
        this.config = config;
    }

    /**
     * 图像识别接口
     * @param file 待识别图像文件
     * @return 通用响应对象
     */
    @PostMapping("/recognition")
    public BaseResponse recognizeImg(@RequestParam("file") MultipartFile file) {

        try {
            ClassifiedImages classifiedImages = classifierService.recognizeImg(file.getInputStream(), file.getOriginalFilename());
            JSONObject jsonObject = JSONObject.parseObject(classifiedImages.toString());

            JSONArray jsonImageArray = jsonObject.getJSONArray("images");
            for (int i = 0; i < jsonImageArray.size(); i++) {
                JSONObject jsonClassifier = jsonImageArray.getJSONObject(i);
                JSONArray jsonClassifierArray = jsonClassifier.getJSONArray("classifiers");
                for (int j = 0; j < jsonClassifierArray.size(); j++) {
                    Classifier classifier = jsonClassifierArray.getObject(j, Classifier.class);
                    if (classifier.getClasses().size() > 0) {
                        return BaseResponse.success("true");
                    }
                }
            }

            return BaseResponse.success("false");
        } catch (IOException e) {
            log.error("获取 " + file.getName() + " 文件流异常");
        }
        return new BaseResponse(500, "图像识别失败！");
    }

    /**
     * 图像信息入库
     * @param files 10张以上的图像文件
     * @return 通用响应对象
     */
    @PostMapping("/register")
    public BaseResponse register(@RequestParam("file") MultipartFile[] files,@RequestParam("id") String id) {

        if (files == null || files.length < MIN_IMAGE_NUMBERS) {
            return new BaseResponse(500, "请提供至少10张图像数据！");
        }

        try {
            String tempPath = ResourceUtils.getFile("").getAbsolutePath() + File.separatorChar + "temp" + File.separatorChar;

            File zipFile = new File(tempPath + UUID.randomUUID() + ".zip");
            zipFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(zipFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            for (MultipartFile file : files) {
                InputStream inputStream = file.getInputStream();

                ZipEntry zipEntry = new ZipEntry(file.getOriginalFilename());
                zipOutputStream.putNextEntry(zipEntry);
                byte[] buffer = new byte[8 * 1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
                inputStream.close();
            }
            zipOutputStream.flush();
            zipOutputStream.close();
            outputStream.close();

            Map<String, String> positiveExample = new LinkedHashMap<>();
            positiveExample.put("register", zipFile.getPath());
            List<String> negativeExamples = new ArrayList<>();
            negativeExamples.add(config.getNegativeExamplesPath());
            Classifier classifier = classifierService.createClassifier(id, positiveExample, negativeExamples);
            JSONObject jsonObject = JSONObject.parseObject(classifier.toString());
            String status = (String) jsonObject.get("status");
            if (CLASSIFIER_STATUS.equals(status)) {
                zipFile.delete();
                return BaseResponse.success("注册成功");
            }

        } catch (IOException e) {
            log.error(e.getMessage());
            return new BaseResponse(500, "注册异常！");
        }
        return new BaseResponse(500, "处理异常！");
    }
}
