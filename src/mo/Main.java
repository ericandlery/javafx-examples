package mo;

import com.google.common.base.CaseFormat;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mockserver.model.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {

    private MockServerTools mTool;

    @Override
    public void init() throws Exception {
        mTool = MockServerTools.getInstance();
        super.init();
    }

    @Override
    public void stop() throws Exception {
        mTool.shutdown();
        super.stop();
    }

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

            Label methodLabel = new Label("method");
            RadioButton mb1 = new RadioButton("GET");
            RadioButton mb2 = new RadioButton("POST");
            RadioButton mb3 = new RadioButton("PUT");
            RadioButton mb4 = new RadioButton("DELETE");
            ToggleGroup methodGroup = new ToggleGroup();
            mb1.setToggleGroup(methodGroup);
            mb1.setSelected(true);
            mb2.setToggleGroup(methodGroup);
            mb3.setToggleGroup(methodGroup);
            mb4.setToggleGroup(methodGroup);
            HBox methodBox = new HBox(mb1, mb2, mb3, mb4);
            methodBox.setSpacing(20);

            Label pathLabel = new Label("path");
            TextField pathText = new TextField();
            pathText.setText("/");
            Label scLabel = new Label("status code");
            TextField scText = new TextField();
            scText.setText("200");

            Label ctLabel = new Label("content-type");
            ChoiceBox ctBoxes = new ChoiceBox();
            ctBoxes.getItems().add(MediaType.TEXT_PLAIN);
            ctBoxes.getItems().add(MediaType.APPLICATION_JSON_UTF_8);
            ctBoxes.getItems().add(MediaType.APPLICATION_XML_UTF_8);
            ctBoxes.getItems().add(MediaType.TEXT_HTML_UTF_8);
            ctBoxes.getItems().add(MediaType.MULTIPART_FORM_DATA);
            ctBoxes.setValue(MediaType.TEXT_PLAIN);

            Label bodyLabel = new Label("body");
            TextArea bodyTa = new TextArea();

            Button connBtn = new Button("Connect");
            Button disConnBtn = new Button("Disconnect");
            disConnBtn.setDisable(true);
            connBtn.setOnAction(connEvent -> {
                mTool.shutdown();
                if(!mTool.isUp()){
                    mTool.setHost(hostField.getText())
                            .setPort(Integer.valueOf(portField.getText()))
                            .setMethod(((RadioButton)methodGroup.getSelectedToggle()).getText())
                            .setPath(pathText.getText())
                            .setStatusCode(Integer.valueOf(scText.getText()))
                            .setContentType((MediaType) ctBoxes.getValue())
                            .setBody(bodyTa.getText())
                            .startup();
                }
                connBtn.setDisable(true);
                disConnBtn.setDisable(false);
            });
            disConnBtn.setOnAction(disConnEvent -> {
                mTool.shutdown();
                connBtn.setDisable(false);
                disConnBtn.setDisable(true);
            });

            if(mTool.isUp()){
                connBtn.setDisable(true);
                disConnBtn.setDisable(false);
            }

            HBox buttonHBox = new HBox(connBtn, disConnBtn);

            VBox mockServerV = new VBox(hostLabel, hostField, portLabel, portField, methodLabel, methodBox, pathLabel, pathText, scLabel, scText,
                    ctLabel, ctBoxes, bodyLabel, bodyTa, buttonHBox);
            mockServerV.setSpacing(5);
            mockServerV.setPrefWidth(805);
            HBox h = new HBox(vMenu, mockServerV);
            h.setAlignment(Pos.CENTER);
            h.setSpacing(5);
            Scene s = new Scene(h, 1000, 600);
            primaryStage.setScene(s);
        });
        embeddedServerBtn.setOnAction(event -> {
            // TODO DEV
        });
        embeddedServerBtn.setDisable(true);

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

    public String convertToCamelCase(String names){
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
