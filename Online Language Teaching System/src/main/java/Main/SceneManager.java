package Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage stage;
    private String[] sceneFiles;
    private  static Scene[] scenes=new Scene[10];

    public SceneManager(Stage stage) {
        this.stage=stage;
        this.sceneFiles= new String[]{
                "StudentScene.fxml", //0
                "about.fxml", //1
                "createlesson.fxml", //2
                "createquiz.fxml", //3
                "lesson.fxml",  //4
                "login.fxml",  //5
                "mainScene.fxml",  //6
                "quiz.fxml",  //7
                "signup.fxml",  //8
                "teacherScene.fxml"  //9
        };
//        for (int i = 0; i < scenes.length; i++) {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/" + sceneFiles[i]));
//                Scene scene = new Scene(loader.load());
//                System.out.println(sceneFiles[i] + " loaded successfully.");
//                scenes[i]=scene;
//            } catch (IOException e) {
//                System.out.println("Error loading: " + sceneFiles[i]);
//                e.printStackTrace();
//            }
//        }
        }






    public static Object switchToMainScene(int i) {

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/scenes/" + getSceneFile(i)));
            Scene scene = new Scene(loader.load());
            System.out.println(getSceneFile(i) + " loaded successfully.");
            stage.setScene(scene);
            return loader.getController();

        } catch (IOException e) {
            System.out.println("Error loading: " +getSceneFile(i));
//          e.printStackTrace();
        }

        return null;
    }

    public static String getSceneFile(int i) {
        String[] sceneFiles = {
                "StudentScene.fxml", //0
                "about.fxml", //1
                "createlesson.fxml", //2
                "createquiz.fxml", //3
                "lesson.fxml",  //4
                "login.fxml",  //5
                "mainScene.fxml",  //6
                "quiz.fxml",  //7
                "signup.fxml",  //8
                "teacherScene.fxml"  //9
        };
        return sceneFiles[i];
    }


}