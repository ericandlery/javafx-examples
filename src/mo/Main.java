package mo;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Jud Tool-MyBatis SQL Converter");

        Label leftLabel = new Label("Tool's SQL");
        Label rightLabel = new Label("MyBatis SQL");

        TextArea leftTa = new TextArea();
        leftTa.setMinHeight(500);
        leftTa.setPrefWidth(200);
        TextArea rightTa = new TextArea();
        rightTa.setMinHeight(500);
        rightTa.setPrefWidth(200);

        Button leftBt = new Button("To myBatis");
        Button rightBt = new Button("To Tool");

        leftBt.setOnAction(event -> {
            rightTa.setText(convertToMyBatis(leftTa.getText()));
        });
        rightBt.setOnAction(event -> {
            leftTa.setText(convertToTool(rightTa.getText()));
        });

        VBox v1 = new VBox(leftLabel, leftTa, leftBt);
        v1.setStyle("-fx-border-color: black;");
        v1.setMinWidth(400);
        v1.setAlignment(Pos.CENTER);
        v1.setSpacing(10);
        VBox v2 = new VBox(rightLabel, rightTa, rightBt);
        v2.setMinWidth(400);
        v2.setStyle("-fx-border-color: aqua;");
        v2.setAlignment(Pos.CENTER);
        v2.setSpacing(10);

        HBox h = new HBox(v1, v2);
        h.setAlignment(Pos.CENTER);
        h.setSpacing(5);

        Scene s = new Scene(h, 1000, 600);

        primaryStage.setScene(s);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public String convertToTool(String sql){
        String result = sql
                .replace("#{", ":")
                .replace("}", "");
        return result;
    }

    public String convertToMyBatis(String sql){
        String result = sql
                .replaceAll(":([a-zA-Z0-9]+)", "#{$1}");
        return result;
    }

}
