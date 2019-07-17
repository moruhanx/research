package com.example.lbm.visualrecognition;

import com.example.ibm.visualrecognition.WatsonApplication;
import com.example.ibm.visualrecognition.service.FacialRecognitionClassifierService;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Classifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Classifiers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author John
 * @create 2019/7/15 10:44
 */
@SpringBootTest(classes = WatsonApplication.class)
@RunWith(SpringRunner.class)
public class FacialRecognitionClassifierServiceTest {

    @Autowired
    private FacialRecognitionClassifierService classifierService;

    @Test
    public void testCreateClassifier() {
        Map<String,String> positiveExamples = new LinkedHashMap<>();
        //positiveExamples.put("file","D:\\notes v2\\Dev\\IBM Watson\\AI\\6745500229_6960f466a6_b.zip");
        positiveExamples.put("beagle","D:\\notes v2\\Dev\\IBM Watson\\AI\\beagle.zip");
        positiveExamples.put("husky","D:\\notes v2\\Dev\\IBM Watson\\AI\\husky.zip");
        //positiveExamples.put("golden-retriever","D:\\notes v2\\Dev\\IBM Watson\\AI\\golden-retriever.zip");
        List<String> negativeExamples = new ArrayList<>();
        //negativeExamples.add("D:\\notes v2\\Dev\\IBM Watson\\91d7df951db4ff10f3f6ce5d11201becb5f12a46820ea.zip");
        negativeExamples.add("D:\\notes v2\\Dev\\IBM Watson\\AI\\cats.zip");
        Classifier dogs = classifierService.createClassifier("dogs", positiveExamples, negativeExamples);
        System.out.println(dogs);
    }

    @Test
    public void testRetrieveClassifierList(){
        Classifiers classifiers = classifierService.retrieveClassifierList();
        System.out.println(classifiers);
    }

    //dogs_1233639102
    @Test
    public void testUpdateClassifier(){
        //classifierService.updateClassifier()
    }

    @Test
    public void testRecognizeImg() throws FileNotFoundException {
        InputStream imageInputStream = new FileInputStream("D:\\notes v2\\Dev\\IBM Watson\\AI\\6745500229_6960f466a6_b.jpg");
        ClassifiedImages result = classifierService.recognizeImg(imageInputStream, "golden_retriever");
        System.out.println(result);
    }
}
