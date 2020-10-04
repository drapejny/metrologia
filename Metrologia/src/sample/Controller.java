package sample;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Match;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    @FXML
    private Button choosButton;
    @FXML
    private Button openButton;
    @FXML
    private TextField filePathTextField;
    @FXML
    private TableView<Operator> operatorTable;
    @FXML
    private TableColumn<Operator, Integer> jColumn;
    @FXML
    private TableColumn<Operator, String> operatorColumn;
    @FXML
    private TableColumn<Operator, Integer> f1Column;
    @FXML
    private TableView<Operand> operandTable;
    @FXML
    private TableColumn<Operand, Integer> iColumn;
    @FXML
    private TableColumn<Operand, String> operandColumn;
    @FXML
    private TableColumn<Operand, Integer> f2Column;
    @FXML
    private Button showButton;
    @FXML
    private Text n1Text;
    @FXML
    private Text n2Text;
    @FXML
    private Text N1Text;
    @FXML
    private Text N2Text;
    @FXML
    private Text nText;
    @FXML
    private Text NText;
    @FXML
    private Text VText;


    File file;
    String fileString = "";
    String fileStringCopy = "";
    List<Operator> operators = new ArrayList<>();
    List<Operand> operands = new ArrayList<>();
    ObservableList<Operator> observableList_1 = FXCollections.observableArrayList();
    ObservableList<Operand> observableList_2 = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws IOException {

        showButton.setVisible(false);
        openButton.setVisible(false);

        choosButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAVA", "*.java"));
            file = fileChooser.showOpenDialog(choosButton.getScene().getWindow());
            filePathTextField.setText(file.getPath());
            showButton.setVisible(true);
            openButton.setVisible(true);

        });

        openButton.setOnAction(actionEvent -> {

            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            try {
                desktop.open(file);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        showButton.setOnAction(actionEvent -> {
            operators.clear();
            operands.clear();
            try {
                readBasicOperators();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setFileString();
            findOperators();
            findIf();
            findCycles();
            findSwitch();
            findTryCatch();
            findMethods();
            deleteClasses();

            try {
                deleteKeyWords();
                deleteOtherOperators();
                deleteArrays();
                findFloatNumbers();
                deleteTrash();
                findOperands();
                findLiterals();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showData();

        });
    }

    public void readBasicOperators() throws IOException {
        List<String> lines = Files.readAllLines(new File("src//sample//files//basicOperators_2").toPath());
        for (String s : lines) {
            String parts[] = s.split(" __ ");
            operators.add(new Operator(parts[0], parts[1].substring(1, parts[1].length() - 1)));
        }
    }

    public void setFileString() {
        if (file == null)
            file = new File(filePathTextField.getText());

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte bytes[] = new byte[fileInputStream.available()];
            for (int i = 0; i < bytes.length; i++) {
                fileInputStream.read(bytes);
            }
            fileString = new String(bytes);
        } catch (Exception e) {
            e.getMessage();
        }
        fileStringCopy = fileString;
        Pattern p = Pattern.compile("\"[^\"]*\"");
        Matcher m = p.matcher(fileString);
        fileString = m.replaceAll("\"\"");
        p = Pattern.compile("\'[^\']*\'");
        m = p.matcher(fileString);
        fileString = m.replaceAll("\'\'");
        p = Pattern.compile("\\/{2,}.*\\s");
        m = p.matcher(fileString);
        fileString = m.replaceAll("");
        p = Pattern.compile("(\\/\\*){1}[^*]*\\*\\/");
        m = p.matcher(fileString);
        fileString = m.replaceAll("");
        p = Pattern.compile("package \\S+");
        m = p.matcher(fileString);
        fileString = m.replaceAll("");
        p = Pattern.compile("import \\S+");
        m = p.matcher(fileString);
        fileString = m.replaceAll("");

    }

    public void findOperators() {
        for (int i = 0; i < operators.size(); i++) {
            operators.get(i).setCount(0);
            Pattern p = Pattern.compile(operators.get(i).getRegex());
            Matcher m = p.matcher(fileString);
            while (m.find()) {
                operators.get(i).incCount();
            }
            fileString = m.replaceAll("");
        }
    }

    public void findIf() {
        Pattern p = Pattern.compile("\\sif \\(");
        Matcher m = p.matcher(fileString);
        Operator _if = new Operator("if", 0);
        operators.add(_if);
        List<Integer> brackets = new ArrayList<>();
        while (m.find()) {
            brackets.add(findBracket(m.end() - 1));
            _if.incCount();
        }
        Collections.sort(brackets);

        int offset = 0;
        for (int i = 0; i < brackets.size(); i++) {
            deleteChar(brackets.get(i) - offset);
            offset++;
        }
        p = Pattern.compile("\\sif \\(");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");

        p = Pattern.compile("\\selse ");
        m = p.matcher(fileString);
        Operator _else = new Operator("if...else", 0);
        operators.add(_else);
        while (m.find())
            _else.incCount();
        fileString = m.replaceAll(" ");

        _if.setCount(_if.getCount() - _else.getCount());
    }

    public void findCycles() {
        List<Operator> cycles = new ArrayList<>();
        Operator _for = new Operator("for", 0);
        Pattern p = Pattern.compile("\\sfor \\(");
        Matcher m = p.matcher(fileString);
        while (m.find()) {
            _for.incCount();
            deleteChar(findBracket(m.end() - 1));
        }
        p = Pattern.compile("\\sfor \\(");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
        Operator _while = new Operator("while", 0);
        p = Pattern.compile("\\swhile \\(");
        m = p.matcher(fileString);
        while (m.find()) {
            _while.incCount();
            deleteChar(findBracket(m.end() - 1));
        }
        p = Pattern.compile("\\swhile \\(");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");

        Operator _do_while = new Operator("do...while", 0);
        p = Pattern.compile("\\sdo \\{");
        m = p.matcher(fileString);
        while (m.find()) {
            _do_while.incCount();
            deleteRange(m.start(), m.start() + 3);
        }
        _while.setCount(_while.getCount() - _do_while.getCount());
        cycles.add(_for);
        cycles.add(_while);
        cycles.add(_do_while);
        operators.addAll(cycles);
    }

    public void findSwitch() {
        Pattern p = Pattern.compile("\\sswitch \\(");
        Matcher m = p.matcher(fileString);
        Operator _switch = new Operator("switch", 0);
        operators.add(_switch);
        List<Integer> brackets = new ArrayList<>();
        while (m.find()) {
            brackets.add(findBracket(m.end() - 1));
            _switch.incCount();
        }
        Collections.sort(brackets);
        int offset = 0;
        for (int i = 0; i < brackets.size(); i++) {
            deleteChar(brackets.get(i) - offset);
            offset++;
        }
        p = Pattern.compile("\\sswitch \\(");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");

        Operator _case = new Operator("case", 0);
        p = Pattern.compile("\\scase ");
        m = p.matcher(fileString);
        operators.add(_case);
        while (m.find()) {
            _case.incCount();
        }
        p = Pattern.compile("\\scase ");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");

        Operator _default = new Operator("default", 0);
        p = Pattern.compile("\\sdefault\\:");
        m = p.matcher(fileString);
        operators.add(_default);
        while (m.find())
            _default.incCount();
        p = Pattern.compile("\\sdefault\\:");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
    }

    public void findTryCatch() {
        Pattern p = Pattern.compile("\\stry ");
        Matcher m = p.matcher(fileString);
        Operator _try = new Operator("try", 0);
        operators.add(_try);
        while (m.find())
            _try.incCount();
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\\scatch \\(");
        m = p.matcher(fileString);
        Operator _catch = new Operator("catch", 0);
        operators.add(_catch);
        List<Integer> brackets = new ArrayList<>();
        while (m.find()) {
            brackets.add(findBracket(m.end() - 1));
            _catch.incCount();
        }
        Collections.sort(brackets);
        int offset = 0;
        for (int i = 0; i < brackets.size(); i++) {
            deleteChar(brackets.get(i) - offset);
            offset++;
        }
        p = Pattern.compile("\\scatch \\(");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");

    }

    public void findMethods() {

        List<Operator> methods = new ArrayList<>();
        Pattern p = Pattern.compile("\\w+\\(");
        Matcher m = p.matcher(fileString);
        while (m.find()) {
            boolean flag = true;
            String methodName = fileString.substring(m.start(), m.end()) + ")";
            for (int i = 0; i < methods.size(); i++) {
                if (methods.get(i).getName().contentEquals(methodName)) {
                    methods.get(i).incCount();
                    flag = false;
                    break;
                }
            }
            if (flag)
                methods.add(new Operator(methodName));
        }
        p = Pattern.compile("\\w+\\(");
        m = p.matcher(fileString);
        List<Integer> brackets = new ArrayList<>();
        while (m.find()) {
            brackets.add(findBracket(m.end() - 1));
        }
        Collections.sort(brackets);

        int offset = 0;
        for (int i = 0; i < brackets.size(); i++) {
            deleteChar(brackets.get(i) - offset);
            offset++;
        }
        p = Pattern.compile("\\w+\\(");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");

        operators.addAll(methods);
    }

    public void deleteClasses() {
        Pattern p = Pattern.compile("[A-Z]\\w+");
        Matcher m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
    }

    public void deleteKeyWords() throws IOException {
        List<String> lines = Files.readAllLines(new File("src//sample//files//deleteWords").toPath());
        for (String s : lines) {
            String parts[] = s.split(" __ ");
            String regex = parts[1].substring(1, parts[1].length() - 1);
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(fileString);
            fileString = m.replaceAll(" ");
        }
    }

    public void deleteOtherOperators() throws IOException {
        List<String> lines = Files.readAllLines(new File("src//sample//files//otherOperators").toPath());
        for (String s : lines) {
            String parts[] = s.split(" __ ");
            String regex = parts[1].substring(1, parts[1].length() - 1);
            Operator operator = new Operator(parts[0], regex);
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(fileString);
            operators.add(operator);
            while (m.find()) {
                operator.incCount();
            }
            fileString = m.replaceAll(" ");
        }
    }

    public void deleteArrays() throws IOException {
        List<Operand> arrayOperands = new ArrayList<>();
        Pattern p = Pattern.compile("\\s\\w+\\[");
        Matcher m = p.matcher(fileString);
        while (m.find()) {
            boolean flag = true;
            String arrayName = fileString.substring(m.start() + 1, m.end()) + "]";
            for (int i = 0; i < arrayOperands.size(); i++) {
                if (arrayOperands.get(i).getName().contentEquals(arrayName)) {
                    arrayOperands.get(i).incCount();
                    flag = false;
                    break;
                }
            }
            if (flag)
                arrayOperands.add(new Operand(arrayName));
        }
        fileString = m.replaceAll(" ");
        List<String> lines = Files.readAllLines(new File("src//sample//files//arrays").toPath());
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < arrayOperands.size(); j++) {
                if (arrayOperands.get(j).getName().contentEquals(lines.get(i) + "]"))
                    arrayOperands.remove(j);
            }
        }
        operands.addAll(arrayOperands);
        p = Pattern.compile("\\[");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\\]");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
    }

    public void findFloatNumbers() {
        List<Operand> floatNumbers = new ArrayList<>();
        Pattern p = Pattern.compile("\\d+\\.\\d+");
        Matcher m = p.matcher(fileString);
        while (m.find()) {
            boolean flag = true;
            String floatNumber = fileString.substring(m.start(), m.end());
            for (int i = 0; i < floatNumbers.size(); i++) {
                if (floatNumbers.get(i).getName().contentEquals(floatNumber)) {
                    floatNumbers.get(i).incCount();
                    flag = false;
                    break;
                }
            }
            if (flag)
                floatNumbers.add(new Operand(floatNumber));
        }
        operands.addAll(floatNumbers);
    }

    public void deleteTrash() {
        Pattern p = Pattern.compile("\\{");
        Matcher m = p.matcher(fileString);
        Operator operator1 = new Operator("{", 0);
        while (m.find())
            operator1.incCount();
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\\}");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\\(");
        m = p.matcher(fileString);
        Operator operator2 = new Operator("( )", 0);
        while (m.find())
            operator2.incCount();
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\\)");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\"");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\'");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
        p = Pattern.compile("\\.");
        m = p.matcher(fileString);
        fileString = m.replaceAll(" ");
        operators.add(operator1);
        operators.add(operator2);
    }

    public void findOperands() {
        List<Operand> finalOperands = new ArrayList<>();
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(fileString);

        while (m.find()) {
            boolean flag = true;
            String operandName = fileString.substring(m.start(), m.end());
            for (int i = 0; i < finalOperands.size(); i++) {
                if (finalOperands.get(i).getName().contentEquals(operandName)) {
                    finalOperands.get(i).incCount();
                    flag = false;
                    break;
                }
            }
            if (flag)
                finalOperands.add(new Operand(operandName));
        }
        operands.addAll(finalOperands);


    }

    public void findLiterals() {

        List<Operand> literals = new ArrayList<>();
        Pattern p = Pattern.compile("\"[^\"]*\"");
        Matcher m = p.matcher(fileStringCopy);
        while (m.find()) {
            boolean flag = true;
            String literal = fileStringCopy.substring(m.start(), m.end());
            System.out.println(literal);
            for (int i = 0; i < literals.size(); i++) {
                if (literals.get(i).getName().contentEquals(literal)) {
                    literals.get(i).incCount();
                    flag = false;
                    break;
                }
            }
            if (flag)
                literals.add(new Operand(literal));
        }
        p = Pattern.compile("\'[^\']*\'");
        m = p.matcher(fileStringCopy);
        while (m.find()) {
            boolean flag = true;
            String literal = fileStringCopy.substring(m.start(), m.end());
            for (int i = 0; i < literals.size(); i++) {
                if (literals.get(i).getName().contentEquals(literal)) {
                    literals.get(i).incCount();
                    flag = false;
                    break;
                }
            }
            if (flag)
                literals.add(new Operand(literal));
        }
        operands.addAll(literals);
    }


    public int findBracket(int firstBracket) {
        int num = 0;
        int i = firstBracket + 1;
        while (true) {
            if (fileString.charAt(i) == ')' && num == 0)
                break;
            else if (fileString.charAt(i) == ')' && num != 0)
                num--;
            if (fileString.charAt(i) == '(')
                num++;
            i++;
        }
        return i;
    }

    public void deleteRange(int start, int end) {
        fileString = fileString.substring(0, start).concat(fileString.substring(end + 1));
    }

    public void deleteChar(int pos) {
        fileString = fileString.substring(0, pos).concat(fileString.substring(pos + 1));
    }


    public void showData() {
        observableList_1.clear();
        observableList_2.clear();
        int n1 = 1;
        int n2 = 1;
        int N1 = 0;
        int N2 = 0;
        int n = 0;
        int N = 0;
        int V = 0;
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).getCount() != 0) {
                operators.get(i).setNumber(n1);
                n1++;
                N1 += operators.get(i).getCount();
                observableList_1.add(operators.get(i));
            }
        }
        n1--;
        for (int i = 0; i < operands.size(); i++) {
            if (operands.get(i).getCount() != 0) {
                operands.get(i).setNumber(n2);
                n2++;
                N2 += operands.get(i).getCount();
                observableList_2.add(operands.get(i));
            }
        }
        n2--;
        n = n1 + n2;
        N = N1 + N2;
        V = (int) (N * Math.log((double) (n / 2)));
        n1Text.setText("n1 = " + n1);
        n2Text.setText("n2 = " + n2);
        N1Text.setText("N1 = " + N1);
        N2Text.setText("N2 = " + N2);
        nText.setText("n = " + n);
        NText.setText("N = " + N);
        VText.setText("V = " + V);

        jColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        f1Column.setCellValueFactory(new PropertyValueFactory<>("count"));
        operatorTable.setItems(observableList_1);
        iColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        operandColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        f2Column.setCellValueFactory(new PropertyValueFactory<>("count"));
        operandTable.setItems(observableList_2);
    }
}
