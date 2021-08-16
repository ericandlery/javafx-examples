package mo;

import com.google.common.base.CaseFormat;
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
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Dev Jud Tools");

        Label leftLabel = new Label("Tool's SQL");
        Label rightLabel = new Label("MyBatis SQL");

        TextArea leftTa = new TextArea();
        leftTa.setMinHeight(500);
        leftTa.setPrefWidth(200);
        TextArea rightTa = new TextArea();
        rightTa.setMinHeight(500);
        rightTa.setPrefWidth(200);

        Button leftBt = new Button("To Right");
        Button rightBt = new Button("To Left");

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

        ToggleGroup menuToggle = new ToggleGroup();
        ToggleButton sqlBtn = new ToggleButton("SQL");
        ToggleButton camelBtn = new ToggleButton("camelCase");
        ToggleButton mockServerBtn = new ToggleButton("MockServer");
        ToggleButton embeddedServerBtn = new ToggleButton("EmbeddedServer");
        sqlBtn.setToggleGroup(menuToggle);
        camelBtn.setToggleGroup(menuToggle);
        mockServerBtn.setToggleGroup(menuToggle);
        embeddedServerBtn.setToggleGroup(menuToggle);
        sqlBtn.setSelected(true);
        VBox vMenu = new VBox(sqlBtn, camelBtn, mockServerBtn, embeddedServerBtn);

        sqlBtn.setOnAction(event -> {
            sqlBtn.setSelected(true);
            leftLabel.setText("Tool's SQL");
            rightLabel.setText("MyBatis SQL");
            rightBt.setDisable(false);

            leftBt.setOnAction(event2 -> {
                rightTa.setText(convertToMyBatis(leftTa.getText()));
            });
            rightBt.setOnAction(event2 -> {
                leftTa.setText(convertToTool(rightTa.getText()));
            });

            HBox h = new HBox(vMenu, v1, v2);
            h.setAlignment(Pos.CENTER);
            h.setSpacing(5);
            Scene s = new Scene(h, 1000, 600);
            primaryStage.setScene(s);
        });
        camelBtn.setOnAction(event -> {
            camelBtn.setSelected(true);
            leftLabel.setText("Column Names");
            rightLabel.setText("Properties");
            rightBt.setDisable(true);

            leftBt.setOnAction(event2 -> {
                rightTa.setText(convertToCamelCase(leftTa.getText()));
            });

            HBox h = new HBox(vMenu, v1, v2);
            h.setAlignment(Pos.CENTER);
            h.setSpacing(5);
            Scene s = new Scene(h, 1000, 600);
            primaryStage.setScene(s);
        });
        mockServerBtn.setOnAction(event -> {
            mockServerBtn.setSelected(true);

            Label hostLabel = new Label("host");
            TextField hostField = new TextField();
            hostField.setText("localhost");
            Label portLabel = new Label("port");
            TextField portField = new TextField();
            portField.setText("1080");

            VBox mockServerV = new VBox(hostLabel, hostField, portLabel, portField);
            mockServerV.setPrefWidth(805);
            HBox h = new HBox(vMenu, mockServerV);
            h.setAlignment(Pos.CENTER);
            h.setSpacing(5);
            Scene s = new Scene(h, 1000, 600);
            primaryStage.setScene(s);
        });
        embeddedServerBtn.setOnAction(event -> {
            if(1==1){
                System.out.println("TODO");
                return;
            }
            // TODO DEV
            MockServerTools mTool = MockServerTools.getInstance()
                    .setHost("localhost")
                    .setPort(1080)
                    .setMethod("GET")
                    .setPath("/")
                    .setStatusCode(200)
                    .setContentType(MediaType.APPLICATION_JSON_UTF_8)
                    .setBody("{\"name\":\"kimi\"}")
                    .startup();
            primaryStage.setOnCloseRequest(event1 -> {
                mTool.shutdown();
            });
        });

        HBox h = new HBox(vMenu, v1, v2);
        h.setAlignment(Pos.CENTER);
        h.setSpacing(5);

        Scene s = new Scene(h, 1000, 600);

        primaryStage.setScene(s);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private String convertToTool(String sql){
        String result = sql
                .replace("#{", ":")
                .replace("}", "");
        return result;
    }

    private String convertToMyBatis(String sql){
        String result = sql
                .replaceAll(":([a-zA-Z0-9]+)", "#{$1}");
        return result;
    }

    private String convertToCamelCase(String names){
        List<String> ognNames = Arrays.asList(names.split("\n"));
        List<String> list = new ArrayList<>();
        String result;
        list = ognNames.stream()
                .map(line
                        -> "private "
                        + (line.equals("SEQ")
                        || line.equals("ROWID")
                        || line.equals("PLNTF_COUNT")
                        || line.equals("DFDNT_COUNT")
                        ? "Long " : "String ")
                        + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line.replace(",", "")) + ";")
                .collect(Collectors.toList());
        return list.stream().collect(Collectors.joining("\n"));
    }

}
