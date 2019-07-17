package com.example.ibm.visualrecognition.service;

import com.example.ibm.visualrecognition.config.WatsonVisualRecognitionConfig;
import com.ibm.watson.developer_cloud.service.exception.RequestTooLargeException;
import com.ibm.watson.developer_cloud.service.exception.ServiceResponseException;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author John
 * @create 2019/7/15 13:28
 */
@Service
public class FacialRecognitionClassifierService {

    private WatsonVisualRecognitionConfig watsonVisualRecognitionConfig;

    public FacialRecognitionClassifierService(WatsonVisualRecognitionConfig watsonVisualRecognitionConfig){
        this.watsonVisualRecognitionConfig=watsonVisualRecognitionConfig;
    }

    /**
     * 创建分级器
     * @param name 分级器名称
     * @param positiveExamples 正面训练素材
     * @param negativeExamples 反面训练素材
     * @return
     */
    public Classifier createClassifier(String name, Map<String, String> positiveExamples, List<String> negativeExamples) {
        try {
            IamOptions options = new IamOptions.Builder().apiKey(watsonVisualRecognitionConfig.getApikey()).build();
            VisualRecognition service = new VisualRecognition(watsonVisualRecognitionConfig.getVersion(), options);
            service.setEndPoint(watsonVisualRecognitionConfig.getEndpoint());
            CreateClassifierOptions.Builder builder = new CreateClassifierOptions.Builder();
            builder.name(name);
            positiveExamples.forEach((k,v)->{
                try {
                    builder.addPositiveExamples(k, new File(v));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            negativeExamples.forEach((k)->{
                try {
                    builder.negativeExamples(new File(k));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            CreateClassifierOptions createClassifierOptions = builder.build();

            return service.createClassifier(createClassifierOptions).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有分级器
     * @return
     */
    public Classifiers retrieveClassifierList() {
        try {

            IamOptions options = new IamOptions.Builder().apiKey(watsonVisualRecognitionConfig.getApikey()).build();
            VisualRecognition service = new VisualRecognition(watsonVisualRecognitionConfig.getVersion(), options);
            service.setEndPoint(watsonVisualRecognitionConfig.getEndpoint());

            ListClassifiersOptions listClassifiersOptions = new ListClassifiersOptions.Builder()
                    .verbose(true)
                    .build();
            Classifiers classifiers = service.listClassifiers(listClassifiersOptions).execute();
            return classifiers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询指定分级器详细信息
     * @param classifierId 分级器id
     * @return
     */
    public Classifier retrieveClassifierDetail(String classifierId) {
        try {

            IamOptions options = new IamOptions.Builder().apiKey(watsonVisualRecognitionConfig.getApikey()).build();
            VisualRecognition service = new VisualRecognition(watsonVisualRecognitionConfig.getVersion(), options);
            service.setEndPoint(watsonVisualRecognitionConfig.getEndpoint());
            GetClassifierOptions getClassifierOptions = new GetClassifierOptions.Builder(classifierId).build();
            Classifier classifier = service.getClassifier(getClassifierOptions).execute();
            return classifier;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新指定分级器
     * @param classifierId 分级器id
     * @return
     */
    public Classifier updateClassifier(String classifierId,String positiveFilePath,String negativeFilePath) {
        try {

            IamOptions options = new IamOptions.Builder().apiKey(watsonVisualRecognitionConfig.getApikey()).build();
            VisualRecognition service = new VisualRecognition(watsonVisualRecognitionConfig.getVersion(), options);
            service.setEndPoint(watsonVisualRecognitionConfig.getEndpoint());

            File positiveFile = new File(positiveFilePath);
            File negativeFile = new File(negativeFilePath);

            UpdateClassifierOptions updateClassifierOptions = new UpdateClassifierOptions.Builder()
                    .classifierId(classifierId)
                    .addPositiveExamples(positiveFile.getName(),new FileInputStream(positiveFile))
                    .negativeExamples(new FileInputStream(negativeFile))
                    .negativeExamplesFilename(negativeFile.getName())
                    .build();

            return service.updateClassifier(updateClassifierOptions).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除指定分级器
     * @param classifierId 分级器id
     * @return
     */
    public boolean deleteClassifier(String classifierId) {
        try {

            IamOptions options = new IamOptions.Builder().apiKey(watsonVisualRecognitionConfig.getApikey()).build();
            VisualRecognition service = new VisualRecognition(watsonVisualRecognitionConfig.getVersion(), options);
            service.setEndPoint(watsonVisualRecognitionConfig.getEndpoint());

            DeleteClassifierOptions deleteClassifierOptions =
                    new DeleteClassifierOptions.Builder(classifierId).build();
            service.deleteClassifier(deleteClassifierOptions).execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 识别图像
     * @param classifierId 分级器Id
     * @param filePath 待识别图像路径
     * @param fileName 待识别图像名称
     * @return
     */
    public ClassifiedImages recognizeImg(String classifierId, String filePath, String fileName) {
        try {
//			String classifierId = "dogs_1477088859";
            IamOptions options = new IamOptions.Builder().apiKey(watsonVisualRecognitionConfig.getApikey()).build();
            VisualRecognition service = new VisualRecognition(watsonVisualRecognitionConfig.getVersion(), options);
            service.setEndPoint(watsonVisualRecognitionConfig.getEndpoint());

			InputStream imagesStream = new FileInputStream(filePath);
			ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
					.imagesFile(imagesStream)
					.imagesFilename(fileName)
					.classifierIds(Arrays.asList(classifierId))
					.build();
			ClassifiedImages result = service.classify(classifyOptions).execute();

            /*InputStream imagesStream = new FileInputStream(filePath);
            ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                    .imagesFile(imagesStream)
                    .imagesFilename(fileName)
                    .threshold((float) 0.6)
                    .owners(Arrays.asList("me"))
                    .build();
            ClassifiedImages result = service.classify(classifyOptions).execute();*/
            return result;
        } catch (FileNotFoundException e) {
            System.out.println("Handle Not Found (404) exception");
            e.printStackTrace();
        } catch (RequestTooLargeException e) {
            System.out.println("Handle Request Too Large (413) exception");
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            System.out.println("Service returned status code " + e.getStatusCode() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * 识别图像
     * @param imagesStream 待识别图像输入流
     * @param fileName 待识别图像名称
     * @return
     */
    public ClassifiedImages recognizeImg(InputStream imagesStream, String fileName) {
        try {
            IamOptions options = new IamOptions.Builder().apiKey(watsonVisualRecognitionConfig.getApikey()).build();
            VisualRecognition service = new VisualRecognition(watsonVisualRecognitionConfig.getVersion(), options);
            service.setEndPoint(watsonVisualRecognitionConfig.getEndpoint());

            ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                    .imagesFile(imagesStream)
                    .imagesFilename(fileName)
                    .threshold((float) 0.8)
                    .owners(Arrays.asList("me"))
                    .build();
            ClassifiedImages result = service.classify(classifyOptions).execute();
            return result;
        } catch (RequestTooLargeException e) {
            System.out.println("Handle Request Too Large (413) exception");
            e.printStackTrace();
        } catch (ServiceResponseException e) {
            System.out.println("Service returned status code " + e.getStatusCode() + ": " + e.getMessage());
        }
        return null;
    }
}

