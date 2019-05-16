import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

import com.sun.javafx.scene.control.skin.VirtualFlow;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.awt.event.ActionEvent;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.util.converter.*;

@SuppressWarnings("unused")
public class GUI extends Application {

	private Stage mainStage;

	private static TextArea infoBox; 
	private static TextArea errorBox;

	private static String infoStore;
	private static String errorStore;

	private List<Row> dataList;
	private List<Row> dataListBackup;

	private static List<RowTableData> newDataList;
	private static List<RowTableData> newDataListBackup;

	private static ObservableList<RowTableData> observableDataList;
	private static TableView<RowTableData> dataTable;

	private TableColumn<RowTableData, Integer> rowColumn;
	private TableColumn<RowTableData, Integer> priorityColumn;
	private TableColumn<RowTableData, String> addressColumn;
	private TableColumn<RowTableData, String> crossColumn;
	private TableColumn<RowTableData, String> dispatchColumn;
	private TableColumn<RowTableData, String> arrivalColumn;
	private TableColumn<RowTableData, String> responseColumn;
	private TableColumn<RowTableData, String> dayColumn;
	private TableColumn<RowTableData, String> staffingColumn;
	private TableColumn<RowTableData, String> ssaColumn;
	private TableColumn<RowTableData, Integer> districtColumn;
	private TableColumn<RowTableData, String> inoriColumn;
	private TableColumn<RowTableData, String> inincColumn;
	private TableColumn<RowTableData, String> inunitColumn;
	private TableColumn<RowTableData, String> inbdgColumn;
	private TableColumn<RowTableData, String> cadincColumn;
	private TableColumn<RowTableData, String> initypeColumn;
	private TableColumn<RowTableData, String> insectColumn;
	private TableColumn<RowTableData, String> servtypeColumn;
	private TableColumn<RowTableData, String> fuaColumn;
	private TableColumn<RowTableData, String> validColumn;

	private final LocalDate TODAYS_DATE = LocalDate.now();

	private static boolean hasBeenEdited;

	private boolean saveSuccessful;

	public static void setEdited() {
		hasBeenEdited = true;
	}

	public static void sendInfo(String infoString) {
		if(infoStore == null) infoStore = "";

		if(infoBox != null) {
			if(infoString.equals("")) {
				infoBox.appendText(infoStore);
				infoStore = "";
			}
			else {
				infoBox.appendText(infoStore+"\n"+infoString);
				infoStore = "";
			}
		}
		else {
			infoStore = infoStore+"\n"+infoString;
		}
	}

	public static void sendError(String errorString) {
		if(errorStore == null) errorStore = ""; 

		if(errorBox != null) {
			if(errorString.equals("")) {
				errorBox.appendText(errorStore);
				errorStore = "";
			}
			else {
				errorBox.appendText(errorStore+"\n"+errorString);
				errorStore = "";
			}
		}
		else {
			errorStore = errorStore+"\n"+errorString;
		}
	}

	@SuppressWarnings("unchecked")
	private void editFocusedCell() {
		final TablePosition<RowTableData, ?> focusedCell = dataTable.focusModelProperty().get().focusedCellProperty().get();
		dataTable.edit(focusedCell.getRow(), focusedCell.getTableColumn());
	}

	private static TableColumn<RowTableData, ?> getTableColumn(final TableColumn<RowTableData, ?> column, int offset) {
		int columnIndex = dataTable.getVisibleLeafIndex(column);
		int newColumnIndex = columnIndex + offset;
		return dataTable.getVisibleLeafColumn(newColumnIndex);
	}

	@SuppressWarnings("unchecked")
	private void selectPrevious() {
		if (dataTable.getSelectionModel().isCellSelectionEnabled()) {
			TablePosition<RowTableData, ?> pos = dataTable.getFocusModel().getFocusedCell();
			if (pos.getColumn() - 1 >= 0) {
				dataTable.getSelectionModel().select(pos.getRow(), getTableColumn(pos.getTableColumn(), -1));
			} 
			else if (pos.getRow() < dataTable.getItems().size()) {
				dataTable.getSelectionModel().select(pos.getRow() - 1, dataTable.getVisibleLeafColumn(dataTable.getVisibleLeafColumns().size() - 1));
			}
		} 
		else {
			int focusIndex = dataTable.getFocusModel().getFocusedIndex();
			if (focusIndex == -1) {
				dataTable.getSelectionModel().select(dataTable.getItems().size() - 1);
			} 
			else if (focusIndex > 0) {
				dataTable.getSelectionModel().select(focusIndex - 1);
			}
		}
	}

	private void setTableEditable() {
		if(dataTable == null) return;

		dataTable.setEditable(true);
		dataTable.getSelectionModel().cellSelectionEnabledProperty().set(true);

		dataTable.setOnKeyPressed(event -> {
			if(event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
				editFocusedCell();
			}
			else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.TAB) {
				dataTable.getSelectionModel().selectNext();
				event.consume();
			} 
			else if (event.getCode() == KeyCode.LEFT) {
				selectPrevious();
				event.consume();
			}
		});
	}

	private void makePriorityColumn() {
		priorityColumn.setCellFactory(EditCell.<RowTableData, Integer>forTableColumn(new IntegerStringConverter()));
		priorityColumn.setOnEditCommit(event -> {
			final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setPriority(value);
			dataTable.refresh();
		});
	}

	private void makeAddressColumn() {
		addressColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		addressColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setAddress(value);
			dataTable.refresh();
		});
	}

	private void makeCrossColumn() {
		crossColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		crossColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setCross(value);
			dataTable.refresh();
		});
	}

	private void makeDistrictColumn() {
		districtColumn.setCellFactory(EditCell.<RowTableData, Integer>forTableColumn(new IntegerStringConverter()));
		districtColumn.setOnEditCommit(event -> {
			final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setDistrict(value);
			dataTable.refresh();
		});
	}

	private void makeDispatchColumn() {
		dispatchColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		dispatchColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setDispatch(value);
			dataTable.refresh();
		});
	}

	private void makeArrivalColumn() {
		arrivalColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		arrivalColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setArrive(value);
			dataTable.refresh();
		});
	}

	private void makeStaffingColumn() {
		staffingColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		staffingColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setStaffing(value);
			dataTable.refresh();
		});
	}

	private void makeSSAColumn() {
		ssaColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		ssaColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setSsa(value);
			dataTable.refresh();
		});
	}

	private void makeDayColumn() {
		dayColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		dayColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setDay(value);
			dataTable.refresh();
		});
	}

	private void makeInoriColumn() {
		inoriColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		inoriColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setInori(value);
			dataTable.refresh();
		});
	}

	private void makeInbdgColumn() {
		inbdgColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		inbdgColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setInbdg(value);
			dataTable.refresh();
		});
	}

	private void makeInunitColumn() {
		inunitColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		inunitColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setInunit(value);
			dataTable.refresh();
		});
	}

	private void makeInitypeColumn() {
		initypeColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		initypeColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setInitype(value);
			dataTable.refresh();
		});
	}

	private void makeServTypeColumn() {
		servtypeColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		servtypeColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setServtype(value);
			dataTable.refresh();
		});
	}

	private void makeFUAColumn() {
		fuaColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		fuaColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setFua(value);
			dataTable.refresh();
		});
	}

	private void makeInsectColumn() {
		insectColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		insectColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setInsect(value);
			dataTable.refresh();
		});
	}

	private void makeInincColumn() {
		inincColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		inincColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setIninc(value);
			dataTable.refresh();
		});
	}

	private void makeCadincColumn() {
		cadincColumn.setCellFactory(EditCell.<RowTableData, String>forTableColumn(new DefaultStringConverter()));
		cadincColumn.setOnEditCommit(event -> {
			final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setCadinc(value);
			dataTable.refresh();
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public void start(Stage stage) throws Exception {
		//need to instantiate stage first thing to allow FileChooser to work
		mainStage = new Stage();
		//get the directory based on the directory the program is being run from
		File thisDirectory = new File(System.getProperty("user.dir"));
		//create the FileChooser object and set the title
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select CSV File to Open");
		//create a list of valid Extensions
		//debating whether or not to allow Excel files since we ideally want csv only
		List<String> validExtensions = new ArrayList<>();
		validExtensions.add("*.csv");

		//ExtensionFilter forces user to select a CSV file
		ExtensionFilter extensionFilter = new ExtensionFilter("CSV files only", validExtensions);
		//Add the extension filter and set the directory
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setInitialDirectory(thisDirectory);

		//get the file selected by the user
		File selectedFile = fileChooser.showOpenDialog(mainStage);
		//if the user didn't select a file, i.e. they hit cancel instead, exit to avoid errors
		if(selectedFile == null) {
			System.exit(0);
		}

		//otherwise we have a selected file, so get the filename
		String fileName = selectedFile.getName();
		String line = "";
		String splitBy = ",";
		//instantiate the dataList -- this is our ArrayList of Rows that contains all the original data
		dataList = new ArrayList<>();
		//try/catch for reading the file
		//this part needs some work !@!
		int lineCount;
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			lineCount = 1;
			while((line = br.readLine()) != null) {
				if(lineCount != 1) {
					String[] input = line.split(splitBy);
					if(input.length == 20) {
						//tbd
						sendError("Error: incorrect number of elements in row input array at line "+lineCount);
						System.out.println("20 element error");
					}
					else if(input.length != 19) {
						sendError("Error: incorrect number of elements in row input array at line "+lineCount);
						System.out.println("not-19 element error");
					}
					else {
						Row newRow = Methods.createRow(input, lineCount);
						if(newRow != null) {
							dataList.add(newRow);
						}
						else {
							sendError("Error: row somehow returned null for row "+lineCount);
							System.out.println("null row error");
						}
					}
				}
				lineCount++;
			}
		} 
		catch(IOException e) {
			System.out.println("Failed to read from file");
			e.printStackTrace();
		}

		hasBeenEdited = false;

		saveSuccessful = true;

		observableDataList = FXCollections.observableArrayList();

		dataTable = new TableView<>();

		newDataList = new ArrayList<>();

		for(Row r : dataList) {
			newDataList.add(new RowTableData(r));
		}		

		rowColumn = new TableColumn<>("Row Number");
		rowColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, Integer>("rownum"));

		priorityColumn = new TableColumn<>("Priority");
		priorityColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, Integer>("priority"));

		addressColumn = new TableColumn<>("Street Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("address"));

		crossColumn = new TableColumn<>("Cross Street");
		crossColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("cross"));

		dispatchColumn = new TableColumn<>("Dispatch Time");
		dispatchColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("dispatch"));

		arrivalColumn = new TableColumn<>("Arrival Time");
		arrivalColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("arrive"));

		responseColumn = new TableColumn<>("Response Time");
		responseColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("response"));

		staffingColumn = new TableColumn<>("Staffing");
		staffingColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("staffing"));

		dayColumn = new TableColumn<>("Day of Week");
		dayColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("day"));

		ssaColumn = new TableColumn<>("SSA");
		ssaColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("ssa"));

		districtColumn = new TableColumn<>("District");
		districtColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, Integer>("district"));

		inoriColumn = new TableColumn<>("INORI#");
		inoriColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("inori"));

		inincColumn = new TableColumn<>("ININC#");
		inincColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("ininc"));

		inunitColumn = new TableColumn<>("INUNIT1");
		inunitColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("inunit"));

		inbdgColumn = new TableColumn<>("INBDG1");
		inbdgColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("inbdg"));

		initypeColumn = new TableColumn<>("INITYPE");
		initypeColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("initype"));

		insectColumn = new TableColumn<>("INSECT");
		insectColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("insect"));

		cadincColumn = new TableColumn<>("CAD_INC#");
		cadincColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("cadinc"));

		servtypeColumn = new TableColumn<>("Service Type");
		servtypeColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("servtype"));

		fuaColumn = new TableColumn<>("First Unit Arrived");
		fuaColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("fua"));

		validColumn = new TableColumn<>("Valid Data");
		validColumn.setCellValueFactory(new PropertyValueFactory<RowTableData, String>("valid"));

		dataTable.getColumns().addAll(rowColumn, priorityColumn, addressColumn, crossColumn, dispatchColumn, arrivalColumn, 
				responseColumn, staffingColumn, dayColumn, ssaColumn, districtColumn, inoriColumn, inincColumn, inunitColumn, 
				inbdgColumn, initypeColumn, insectColumn, cadincColumn, servtypeColumn, fuaColumn, validColumn);

		observableDataList.setAll(newDataList.stream().collect(Collectors.toList()));

		dataTable.setItems(observableDataList);
		setTableEditable();

		VBox mainVBox = new VBox();

		HBox textBoxesHBox = new HBox();
		textBoxesHBox.setSpacing(5);
		textBoxesHBox.setPadding(new Insets(10,10,10,10));

		VBox infoBoxVBox = new VBox();
		infoBoxVBox.setSpacing(5);
		infoBoxVBox.setPadding(new Insets(10,10,10,10));

		VBox errorBoxVBox = new VBox();
		errorBoxVBox.setSpacing(5);
		errorBoxVBox.setPadding(new Insets(10,10,10,10));

		Text infoBoxTitle = new Text("Info");
		Text errorBoxTitle = new Text("Errors");

		infoBox = new TextArea();
		infoBox.setEditable(false);
		infoBox.setWrapText(true);
		infoBox.setPrefRowCount(10);
		infoBox.setMinWidth(400);

		errorBox = new TextArea();
		errorBox.setEditable(false);
		errorBox.setWrapText(true);
		errorBox.setPrefRowCount(10);
		errorBox.setMinWidth(400);

		infoBoxVBox.getChildren().addAll(infoBoxTitle,infoBox);
		errorBoxVBox.getChildren().addAll(errorBoxTitle,errorBox);

		sendError("");

		textBoxesHBox.getChildren().addAll(infoBoxVBox, errorBoxVBox);

		HBox datePickerHBox = new HBox();
		datePickerHBox.setSpacing(5);
		datePickerHBox.setPadding(new Insets(0,10,10,10));

		VBox startDatePickerVBox = new VBox();
		VBox endDatePickerVBox = new VBox();

		Label complianceReportSettingsLabel = new Label("Compliance Report Settings:");
		HBox complianceReportSettingsLabelHBox = new HBox();
		complianceReportSettingsLabelHBox.setSpacing(5);
		complianceReportSettingsLabelHBox.setPadding(new Insets(10,10,0,10));
		complianceReportSettingsLabelHBox.getChildren().addAll(complianceReportSettingsLabel);

		Label startDateLabel = new Label("Start Date");
		Label endDateLabel = new Label("End Date");

		DatePicker startDatePicker = new DatePicker();
		DatePicker endDatePicker = new DatePicker();

		startDatePicker.setOnAction(value -> scrollToRowBasedOnDate(startDatePicker.getValue()));

		LocalDate initialStartDate = TODAYS_DATE.minusMonths(1);
		//remove this before shipping
		initialStartDate = initialStartDate.minusYears(1);
		//^^
		initialStartDate = initialStartDate.withDayOfMonth(1);

		LocalDate initialEndDate = initialStartDate.withDayOfMonth(initialStartDate.lengthOfMonth());

		startDatePicker.setValue(initialStartDate);
		endDatePicker.setValue(initialEndDate);

		startDatePickerVBox.getChildren().addAll(startDateLabel, startDatePicker);
		endDatePickerVBox.getChildren().addAll(endDateLabel, endDatePicker);

		Button complianceExportButton = new Button("Create Compliance Report");
		complianceExportButton.setStyle("-fx-font-size: 32px");

		complianceExportButton.setMinSize(650, 150);

		VBox complianceExportVBox = new VBox();
		complianceExportVBox.setSpacing(20);
		complianceExportVBox.setPadding(new Insets(20,20,20,20));
		complianceExportVBox.getChildren().addAll(complianceExportButton);

		datePickerHBox.getChildren().addAll(startDatePickerVBox, endDatePickerVBox);

		VBox complianceReportVBox = new VBox();
		complianceReportVBox.setSpacing(5);
		complianceReportVBox.setPadding(new Insets(10,10,10,10));

		Button lastMonthButton = new Button("Last Month");
		Button lastQuarterButton = new Button("Last Quarter");
		Button lastFiscalYearButton = new Button("Last Fiscal Year");
		Button lastCalendarYearButton = new Button("Last Calendar Year");

		HBox dateButtonHBox = new HBox();
		dateButtonHBox.setSpacing(5);
		dateButtonHBox.setPadding(new Insets(0,10,10,10));

		HBox checkBoxesHBox = new HBox();
		checkBoxesHBox.setSpacing(5);
		checkBoxesHBox.setPadding(new Insets(10,10,10,10));

		Label checkLabel = new Label("Select Service Type(s):    ");
		CheckBox fireCheckBox = new CheckBox("Fire     ");
		fireCheckBox.setSelected(true);
		CheckBox emsCheckBox = new CheckBox("EMS    ");
		emsCheckBox.setSelected(true);

		checkBoxesHBox.getChildren().addAll(checkLabel, fireCheckBox, emsCheckBox);

		complianceReportVBox.getChildren().addAll(complianceReportSettingsLabelHBox, datePickerHBox, dateButtonHBox, checkBoxesHBox);
		HBox complianceReportHBox = new HBox();
		complianceReportHBox.getChildren().addAll(complianceReportVBox, complianceExportVBox);

		List<Button> dateButtonList = new ArrayList<>();
		dateButtonList.add(lastMonthButton);
		dateButtonList.add(lastQuarterButton);
		dateButtonList.add(lastFiscalYearButton);
		dateButtonList.add(lastCalendarYearButton);

		for(Button b : dateButtonList) {
			b.setStyle("-fx-font-size: 14px");
			dateButtonHBox.getChildren().add(b);
		}

		lastMonthButton.setOnAction(value -> {
			LocalDate startOfMonth = TODAYS_DATE.minusMonths(1);

			startOfMonth = startOfMonth.withDayOfMonth(1);

			LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

			startDatePicker.setValue(startOfMonth);
			endDatePicker.setValue(endOfMonth);
			scrollToRowBasedOnDate(startOfMonth);
		});

		lastQuarterButton.setOnAction(value -> {
			//to avoid errors:
			LocalDate startOfQuarter = LocalDate.now();
			LocalDate endOfQuarter = LocalDate.now();

			if(TODAYS_DATE.getMonthValue() < 4) {
				//last quarter of previous year
				int currentYear = TODAYS_DATE.getYear()-1;
				startOfQuarter = LocalDate.of(currentYear, 10, 1);
				endOfQuarter = LocalDate.of(currentYear, 12, 31);
			}
			else if(TODAYS_DATE.getMonthValue() < 7) {
				//first quarter of current year
				startOfQuarter = TODAYS_DATE.withDayOfYear(1);
				endOfQuarter = LocalDate.of(TODAYS_DATE.getYear(), 3, 31);
			}
			else if(TODAYS_DATE.getMonthValue() < 10) {
				//second quarter of current year
				startOfQuarter = LocalDate.of(TODAYS_DATE.getYear(), 4, 1);
				endOfQuarter = LocalDate.of(TODAYS_DATE.getYear(), 6, 30);
			}
			else {
				//third quarter of current year
				startOfQuarter = LocalDate.of(TODAYS_DATE.getYear(), 7, 1);
				endOfQuarter = LocalDate.of(TODAYS_DATE.getYear(), 9, 30);
			}

			startDatePicker.setValue(startOfQuarter);
			endDatePicker.setValue(endOfQuarter);

			scrollToRowBasedOnDate(startOfQuarter);
		});

		lastFiscalYearButton.setOnAction(value -> {
			LocalDate startOfFiscalYear = LocalDate.now();
			LocalDate endOfFiscalYear = LocalDate.now();

			if(TODAYS_DATE.getMonthValue() >= 7) {
				startOfFiscalYear = LocalDate.of(TODAYS_DATE.getYear()-1, 7, 1);
				endOfFiscalYear = LocalDate.of(TODAYS_DATE.getYear(), 6, 30);
			}
			else {
				startOfFiscalYear = LocalDate.of(TODAYS_DATE.getYear()-2, 7, 1);
				endOfFiscalYear = LocalDate.of(TODAYS_DATE.getYear()-1, 6, 30);
			}

			startDatePicker.setValue(startOfFiscalYear);
			endDatePicker.setValue(endOfFiscalYear);

			scrollToRowBasedOnDate(startOfFiscalYear);
		});

		lastCalendarYearButton.setOnAction(value -> {
			LocalDate startOfCalendarYear = LocalDate.of(TODAYS_DATE.getYear()-1, 1, 1);
			LocalDate endOfCalendarYear = LocalDate.of(TODAYS_DATE.getYear()-1, 12, 31);

			startDatePicker.setValue(startOfCalendarYear);
			endDatePicker.setValue(endOfCalendarYear);

			scrollToRowBasedOnDate(startOfCalendarYear);
		});	

		Button clearCellButton = new Button("Clear Cross Street");
		Button deleteRowButton = new Button("Delete Row");
		Button validateResponseTimesButton = new Button("Validate Response Times");
		Button saveChangesToCsvButton = new Button("Save Changes to CSV");
		Button changeCsvButton = new Button("Open Different CSV");

		saveChangesToCsvButton.setOnAction(value -> {
			FileChooser saveFileChooser = new FileChooser();
			saveFileChooser.setTitle("Select where to save updated CSV file");
			saveFileChooser.setInitialDirectory(thisDirectory);
			saveFileChooser.setInitialFileName("newCSV.csv");
			saveFileChooser.getExtensionFilters().add(extensionFilter);
			File saveFile = saveFileChooser.showSaveDialog(mainStage);

			if(saveFile != null) {
				//save data to new csv
				List<Row> replacementDataList = new ArrayList<>();
				List<RowTableData> replacementNewDataList = new ArrayList<>();

				dataListBackup = dataList;
				newDataListBackup = newDataList;

				for(RowTableData rtd : observableDataList) {
					replacementNewDataList.add(rtd);
				}

				newDataList = replacementNewDataList;

				for(RowTableData rtd : newDataList) {
					Row newRow = createRowFromRowTableData(rtd);
					replacementDataList.add(newRow);
				}

				dataList = replacementDataList;

				observableDataList.setAll(newDataList.stream().collect(Collectors.toList()));
				dataTable.setItems(observableDataList);
				dataTable.refresh();

				//header for rows
				String firstRow = "INPRTY,INOSTR,INOXST,DISPATCH,ARRIVE,RESPONSE,Staffing,DayOfWk,SSA,DISTRICT"+
						",INORI#,ININC#,INUNIT1,INBDG1,INITYPE,INSECT,CAD_INC#,SERVICE_TYPE,FRSTUNITARRV";

				try(BufferedWriter newCsvWriter = new BufferedWriter(new FileWriter(saveFile.getName()))) {
					newCsvWriter.write(firstRow);

					for(Row currentRow : dataList) {
						//only write the valid rows to the file
						if(currentRow.isValid()) {
							newCsvWriter.newLine();
							newCsvWriter.write(currentRow.toString());
						}
					}

					Alert writeSuccessAlert = new Alert(AlertType.INFORMATION);
					writeSuccessAlert.setTitle("Success!");
					writeSuccessAlert.setHeaderText("Success!");
					writeSuccessAlert.setContentText("Successfully wrote to file "+saveFile.getName());
					writeSuccessAlert.show();
					sendInfo("Successfully wrote data in table to CSV file. Any compliance reports generated will now draw from this updated data.");
					saveSuccessful = true;
					hasBeenEdited = false;
				} catch(IOException e) {
					GUI.sendError("Error writing to file "+saveFile.getName());
				}

			}
			else {
				saveSuccessful = false;
			}
		});

		clearCellButton.setOnAction(value -> {
			RowTableData rowToBeCleared = dataTable.getSelectionModel().getSelectedItems().get(0);
			rowToBeCleared.setCross("");

			newDataList.set(rowToBeCleared.getRownum()-2, rowToBeCleared);
			observableDataList.setAll(newDataList.stream().collect(Collectors.toList()));
			dataTable.refresh();
			setEdited();
		});

		deleteRowButton.setOnAction(value -> {
			ObservableList<RowTableData> fullList = dataTable.getItems();
			ObservableList<RowTableData> toBeRemovedList = dataTable.getSelectionModel().getSelectedItems();

			Alert deletionConfirmationAlert = new Alert(AlertType.CONFIRMATION);
			boolean rowsNeedDeletion = true;

			if(toBeRemovedList.isEmpty()) {
				rowsNeedDeletion = false;
			}
			else {
				deletionConfirmationAlert.setTitle("Delete Row?");
				deletionConfirmationAlert.setContentText("Are you sure you want to delete the selected row?");
			}

			if(rowsNeedDeletion) {
				Optional<ButtonType> userSelection = deletionConfirmationAlert.showAndWait();
				if(userSelection.get() == ButtonType.OK) {
					String numberOfDeletedRow = toBeRemovedList.get(0).getRownum()+"";
					for(RowTableData t : toBeRemovedList) {
						fullList.remove(t);
					}
					dataTable.setItems(fullList);
					dataTable.refresh();
					sendInfo("Row "+numberOfDeletedRow+" deleted successfully.");
					setEdited();
				}

			}
		});

		validateResponseTimesButton.setOnAction(value -> {
			newDataList.forEach(RowTableData::updateResponse);
			observableDataList.setAll(newDataList.stream().collect(Collectors.toList()));
			dataTable.refresh();
			sendInfo("All response times validated! Any errors reported in error log.");
		});

		complianceExportButton.setOnAction(value -> {
			//first thing we need to do is validate all data
			//should also keep track if any changes were made to table and ask user if want to apply changes to data
			//this will be done on next push

			boolean canceled = false;

			if(hasBeenEdited) {
				ButtonType yesButton = ButtonType.YES;
				ButtonType noButton = ButtonType.NO;
				ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
				Alert unsavedChangesAlert = new Alert(AlertType.CONFIRMATION, 
						"Do you wish to save the changes made in the table and apply them to the data before generating compliance report?",
						yesButton, noButton, cancelButton);
				unsavedChangesAlert.setTitle("Unsaved changes to data in table!");
				unsavedChangesAlert.setHeaderText("Save Changes?");
				Optional<ButtonType> userSelection = unsavedChangesAlert.showAndWait();

				if(userSelection.get() == ButtonType.YES) {
					//save and update
					//can do this by simulating the press of the save to CSV and update button
					saveChangesToCsvButton.fire();
					if(!saveSuccessful) {
						canceled = true;
					}
					saveSuccessful = true;
				}
				else {
					if(userSelection.get() != ButtonType.NO) {
						canceled = true;
					}
				}

			}

			//now do rest
			if(!canceled) {
				boolean fireIsChecked = fireCheckBox.isSelected();
				boolean emsIsChecked = emsCheckBox.isSelected();

				if(!emsIsChecked && !fireIsChecked) {
					Alert uncheckedBoxesAlert = new Alert(AlertType.ERROR);
					uncheckedBoxesAlert.setTitle("Error!");
					uncheckedBoxesAlert.setContentText("Must select at least one service type.");
					uncheckedBoxesAlert.show();
				}
				else {
					List<Row> nonCompliantRows = new ArrayList<>();

					LocalDateTime filterStartTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(0, 0));
					LocalDateTime filterEndTime = LocalDateTime.of(endDatePicker.getValue(), LocalTime.of(23, 59, 59));

					List<Row> focusList = Methods.filterDate(dataList, filterStartTime, filterEndTime);

					if(emsIsChecked && fireIsChecked) {
						//begin analyzing compliance using focusList
					}
					else if(fireIsChecked) {
						//get fire rows only from focusList
						List<Row> fireFocusList = Methods.filterServiceType(focusList, ServiceType.FIRE);
						//get the compliance analysis results from fireSsaCompliance method in Methods.class
						//save these results to complianceResults array
						double[][] complianceResults = Methods.fireSsaComplianceArrayOutput(fireFocusList);

						//now we choose where to save the compliance data
						//this will be a CSV file, default directory is same directory from which program was run
						//FileChooser object is created and used to pick the save file name/location
						//The save file name/location is then stored in a File object called fireComplianceFile
						//If no file is chosen (i.e. user hits 'x' or cancel) then fireComplianceFile will be null (and thus export is cancelled)
						FileChooser fireComplianceExportFileChooser = new FileChooser();
						fireComplianceExportFileChooser.setTitle("Select where to save compliance data");
						fireComplianceExportFileChooser.setInitialDirectory(thisDirectory);
						fireComplianceExportFileChooser.setInitialFileName("FireComplianceExport.csv");
						fireComplianceExportFileChooser.getExtensionFilters().add(extensionFilter);
						File fireComplianceFile = fireComplianceExportFileChooser.showSaveDialog(mainStage);

						//check to ensure fireComplianceFile is not null else we get big errors
						if(fireComplianceFile != null) {
							//Surround BufferedWriter block with try/catch to handle any exceptions
							try(BufferedWriter fireComplianceCsvWriter = new BufferedWriter(new FileWriter(fireComplianceFile.getName()))) {
								//firstRow = first row of export CSV, contains description of contents (Fire Compliance Data) plus date range
								//secondRow = headers for compliance data
								String firstRow = "Fire Compliance Data,"+startDatePicker.getValue().toString()+","+endDatePicker.getValue().toString();
								String secondRow = "District Number,Compliance Threshold,Percent Compliant,Total Incidents,Compliant Incidents,Average Response Time";
								
								//write first and second row
								//note that newLine() must be called on the fireComplianceCsvWriter or else the write() will continue on the same
								//line as the previous write() call
								fireComplianceCsvWriter.write(firstRow);
								fireComplianceCsvWriter.newLine();
								fireComplianceCsvWriter.write(secondRow);

								//loop through first 16 rows of data array to get compliance data for the 16 districts
								for(int arrayRowNumber = 0; arrayRowNumber < 16; arrayRowNumber++) {
									//convert array of double values to String array
									//remove spaces and brackets generated by the toString() function using replaceAll()
									String currentArrayRow = Arrays.toString(complianceResults[arrayRowNumber]);
									currentArrayRow = currentArrayRow.replaceAll("\\s","");
									currentArrayRow = currentArrayRow.replaceAll("\\[","");
									currentArrayRow = currentArrayRow.replaceAll("\\]","");

									//move to next line and write the current array row data to the file
									//by calling newLine() before writing data, we avoid having an extra line at the end
									fireComplianceCsvWriter.newLine();
									fireComplianceCsvWriter.write(currentArrayRow);
								}

								//2 line gap before summary results
								//these are the totals for Inside SSA, Outside SSA, and Countywide
								fireComplianceCsvWriter.newLine();
								fireComplianceCsvWriter.newLine();
								//first row is the Inside SSA totals
								fireComplianceCsvWriter.write("Inside SSA,"+complianceResults[16][1]+","+complianceResults[16][2]+","
										+complianceResults[16][3]+","+complianceResults[16][4]+","+complianceResults[16][5]);

								//second row is the Outside SSA totals
								fireComplianceCsvWriter.newLine();
								fireComplianceCsvWriter.write("Outside SSA,"+complianceResults[17][1]+","+complianceResults[17][2]+","
										+complianceResults[17][3]+","+complianceResults[17][4]+","+complianceResults[17][5]);

								//third row is the Countywide totals (combination of inside and outside SSA)
								fireComplianceCsvWriter.newLine();
								fireComplianceCsvWriter.write("County Total, ,"+complianceResults[18][2]+","
										+complianceResults[18][3]+","+complianceResults[18][4]+","+complianceResults[18][5]);

								//now skip two lines before the noncompliant incidents section
								fireComplianceCsvWriter.newLine();
								fireComplianceCsvWriter.newLine();
								//add section title and row headers
								fireComplianceCsvWriter.write("Noncompliant Incidents:");
								fireComplianceCsvWriter.newLine();
								fireComplianceCsvWriter.write("INPRTY,INOSTR,INOXST,DISPATCH,ARRIVE,RESPONSE,Staffing,DayOfWk,SSA,DISTRICT"+
										",INORI#,ININC#,INUNIT1,INBDG1,INITYPE,INSECT,CAD_INC#,SERVICE_TYPE,FRSTUNITARRV");

								//Now, we need to get the list of noncompliant incidents
								//Iterate through rows in the list being examined
								for(Row row : fireFocusList) {
									//we only care about valid rows
									if(row.isValid()) {
										//compliance threshold depends on inside/outside SSA (for FIRE)
										LocalTime complianceThresholdTime;

										//so check whether or not each incident is inside or outside SSA
										if(row.getSsa().compareToIgnoreCase("Inside") == 0) {
											//if inside, then must be under 9 minutes
											complianceThresholdTime = LocalTime.of(0, 9);
										}
										else {
											//if outside, then must be under 15 minutes
											complianceThresholdTime = LocalTime.of(0, 15);
										}

										//now compare the response time of each incident to its respective compliance threshold time
										if(row.getResponseTime().compareTo(complianceThresholdTime) >= 0) {
											//if the response time exceeds the threshold, add it to the list of noncompliant rows
											nonCompliantRows.add(row);
										}

									}
								}
								
								//if the noncompliant rows list is empty
								if(!nonCompliantRows.isEmpty()) {
									//sort it by district number for nicer appearance
									nonCompliantRows.sort(Comparator.comparing(Row::getDistrictNumber));
									
									//then print the contents of each row to the file
									for(Row row : nonCompliantRows) {
										fireComplianceCsvWriter.newLine();
										fireComplianceCsvWriter.write(row.toString());
									}
								}
								
								Alert writeSuccessAlert = new Alert(AlertType.INFORMATION);
								writeSuccessAlert.setTitle("Success!");
								writeSuccessAlert.setHeaderText("Compliance Export Success!");
								writeSuccessAlert.setContentText("Successfully exported FIRE Compliance data to file "+fireComplianceFile.getName());
								writeSuccessAlert.show();
								//sendInfo("Successfully wrote data in table to CSV file. Any compliance reports generated will now draw from this updated data.");
							} catch(IOException e) {
								GUI.sendError("Error writing to file "+fireComplianceFile.getName());
							}
						}
					}
					else {
						//get ems rows only from focusList
						List<Row> emsFocusList = Methods.filterServiceType(focusList, ServiceType.EMS);
					} 
				}
			}
			else {
				Alert complianceExportCancelledAlert = new Alert(AlertType.INFORMATION);
				complianceExportCancelledAlert.setTitle("Info");
				complianceExportCancelledAlert.setHeaderText("Compliance Export Cancelled.");
				complianceExportCancelledAlert.setContentText("Export of compliance data to CSV cancelled.");
				complianceExportCancelledAlert.show();
			}
		});

		HBox tableButtonsHBox = new HBox();
		tableButtonsHBox.setSpacing(5);
		tableButtonsHBox.setPadding(new Insets(10,10,10,10));
		tableButtonsHBox.getChildren().addAll(clearCellButton, deleteRowButton, validateResponseTimesButton, saveChangesToCsvButton, changeCsvButton);

		VBox dataTableVBox = new VBox();
		dataTableVBox.setSpacing(5);
		dataTableVBox.setPadding(new Insets(10,10,10,10));
		dataTableVBox.getChildren().addAll(dataTable, tableButtonsHBox);

		mainVBox.getChildren().addAll(dataTableVBox, textBoxesHBox, complianceReportHBox);
		Scene scene = new Scene(mainVBox, 1200, 700);
		mainStage.setTitle("Compliance Report Generator");
		mainStage.setScene(scene);
		mainStage.centerOnScreen();
		mainStage.requestFocus();
		mainStage.show();
		mainStage.toFront();
		mainStage.setAlwaysOnTop(true);
		mainStage.setAlwaysOnTop(false);

		makeColumns();

		setTableEditable();
		sendInfo("Note: Row Number starts at 2 to reflect Excel/CSV file, which has column titles in Row 1.");
		scrollToRowBasedOnDate(startDatePicker.getValue());

		changeCsvButton.setOnAction(value -> {
			Alert csvChangeConfirmationAlert = new Alert(AlertType.CONFIRMATION);
			csvChangeConfirmationAlert.setTitle("Are you sure?");
			csvChangeConfirmationAlert.setContentText("Are you sure you want to open a new CSV? Any changes made to the current data will not be saved.");

			Optional<ButtonType> userSelection = csvChangeConfirmationAlert.showAndWait();

			if(userSelection.get() == ButtonType.OK) {
				mainStage.close();
				Platform.runLater(() -> {
					try {
						new GUI().start(new Stage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		});
	}

	private Row createRowFromRowTableData(RowTableData inputRowTableData) {
		Row newRow = new Row(inputRowTableData.getRownum());

		newRow.setAddress(inputRowTableData.getAddress());
		newRow.setCross(inputRowTableData.getCross());
		newRow.setPriority(inputRowTableData.getPriority());
		newRow.setDistrict(inputRowTableData.getDistrict());
		try {
			DateTimeFormatter dateAndTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss");
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm[:ss]");

			newRow.setDispatch(LocalDateTime.parse(inputRowTableData.getDispatch(), dateAndTimeFormatter));
			newRow.setArrive(LocalDateTime.parse(inputRowTableData.getArrive(), dateAndTimeFormatter));
			newRow.setResponseTime(LocalTime.parse(inputRowTableData.getResponse(), timeFormatter));

		} catch(DateTimeParseException e) {
			GUI.sendError("Date formatted incorrectly for row "+inputRowTableData.getRownum()+".");
		}
		newRow.setStaffing(inputRowTableData.getStaffing());
		newRow.setValid(Boolean.parseBoolean(inputRowTableData.getValid()));
		try {
			newRow.setInsect(Integer.parseInt(inputRowTableData.getInsect()));
			newRow.setIninc(Long.parseLong(inputRowTableData.getIninc()));
			newRow.setCadinc(Long.parseLong(inputRowTableData.getCadinc()));
		} catch(NumberFormatException e) {
			GUI.sendError("Invalid value for INSECT, ININC, or CAD_INC for row "+inputRowTableData.getRownum());
		}
		newRow.setInori(inputRowTableData.getInori());
		newRow.setInunit(inputRowTableData.getInunit());
		newRow.setInitype(inputRowTableData.getInitype());
		newRow.setInbdg(inputRowTableData.getInbdg());
		newRow.setFua(inputRowTableData.getFua());
		newRow.setDay(inputRowTableData.getDay());
		newRow.setSsa(inputRowTableData.getSsa());

		if(inputRowTableData.getServtype().compareTo("FIRE") == 0) {
			newRow.setServiceType(ServiceType.FIRE);
		}
		else if(inputRowTableData.getServtype().compareTo("EMS") == 0) {
			newRow.setServiceType(ServiceType.EMS);
		}
		else {
			//no service type
			newRow.setValid(false);
			newRow.setServiceType(null);
		}

		return newRow;
	}

	private void makeColumns() {
		makePriorityColumn();
		makeAddressColumn();
		makeCrossColumn();
		makeDistrictColumn();
		makeDispatchColumn();
		makeArrivalColumn();
		makeDayColumn();
		makeSSAColumn();
		makeStaffingColumn();
		makeInoriColumn();
		makeInunitColumn();
		makeInbdgColumn();
		makeInitypeColumn();
		makeServTypeColumn();
		makeFUAColumn();
		makeInsectColumn();
		makeInincColumn();
		makeCadincColumn();
	}

	//long function name but whatever
	private void scrollToRowBasedOnDate(LocalDate inputDate) {
		DateTimeFormatter dateAndTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm[:ss]");
		int selectThisRow = 0;
		for(RowTableData rtd : newDataList) {
			try {
				LocalDate dispatchLocalDate = LocalDate.parse(rtd.getDispatch(), dateAndTimeFormatter);
				if(dispatchLocalDate.compareTo(inputDate) >= 0) {
					selectThisRow = rtd.getRownum();
					break;
				}

			} catch(DateTimeParseException e) {
				GUI.sendError("Invalid Dispatch value for row "+rtd.getRownum()+".");
			}

		}
		if(selectThisRow <= 2) {
			dataTable.scrollTo(0);
		}
		else {
			dataTable.scrollTo(selectThisRow-2);
		}
	}
}
