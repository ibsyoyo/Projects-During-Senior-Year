import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.util.*;
import javafx.util.converter.*;

/*
 * Slightly modified version of the EditCell class from lankydan's JavaFX editable table tutorial
 * https://github.com/lankydan/JavaFX-Table-Tutorial/blob/master/JavaFXTableTutorial/src/lankydan/tutorials/fxml/cell/EditCell.java
 * https://dzone.com/articles/editable-tables-in-javafx
 * 
 */

public class EditCell<S, T> extends TextFieldTableCell<S, T> {

	private TextField textField;
	private boolean escapePressed = false;
	private TablePosition<S, ?> tablePos = null;

	public EditCell(final StringConverter<T> converter) {
		super(converter);
	}

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
		return forTableColumn(new DefaultStringConverter());
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter) {
		return list -> new EditCell<S, T>(converter);
	}

	@Override
	public void startEdit() {
		//if we can't edit, return (give up)
		if (!isEditable() || !getTableView().isEditable()|| !getTableColumn().isEditable()) {
			return;
		}
		//otherwise begin edit
		super.startEdit();
		//this should cause isEditing() to return true
		if (isEditing()) {
			//first check if the field being edited is null
			if (textField == null) {
				textField = getTextField();
			}
			//if so, assign a non-null value to textField using getTextField and start the end on that
			escapePressed = false;
			startEdit(textField);
			final TableView<S> table = getTableView();
			//also update tablePos to the position of the cell being edited
			tablePos = table.getEditingCell();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void commitEdit(T newValue) {
		//if editing isn't actually in progress, this method shouldn't be called, so just return
		if (!isEditing()) {
			return;
		}
		final TableView<S> table = getTableView();
		if (table != null) {
			//if the table is not null, we can create a new editEvent and then execute it with fireEvent
			CellEditEvent editEvent = new CellEditEvent(table, tablePos, TableColumn.editCommitEvent(), newValue);
			Event.fireEvent(getTableColumn(), editEvent);
		}
		//edit is complete, so "cancel" the edit, i.e. move back to a non-editing state
		super.cancelEdit();
		//now just checking if updated cell is empty or not
		boolean t = false;
		if(newValue.toString().length() < 1) {
			t = true;
		}
		updateItem(newValue, t);
		if (table != null) {
			//hackish way to cancel editing by calling edit on <0 row value and null cell
			table.edit(-1, null);
		}
	}

	@Override
	public void cancelEdit() {
		if (escapePressed) {
			//if escape is pressed, then this is a cancel event after escape key
			super.cancelEdit();
			//so cancel the edit and restore the original text in the view
			setText(getItemText()); 
		} else {
			//otherwise, this is not a cancel event after escape key which means we want to commit the edit
			String newText = textField.getText();
			this.commitEdit(getConverter().fromString(newText));
		}
		setGraphic(null);
	}

	@Override
	//this is mandatory override of parent method updateItem, simply calls the super then runs the paramater-less updateItem method at the bottom of this class
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		updateItem();
	}

	private TextField getTextField() {
		//get the text from the converter
		final TextField textField = new TextField(getItemText());
		
		textField.setOnAction(event -> {
			//this shouldn't happen but an error will be reported if it does
			if (getConverter() == null) {
				GUI.sendError("Error: StringConverter is null.");
			}
			//otherwise commit the edit
			this.commitEdit(getConverter().fromString(textField.getText()));
			//consume marks the event as "consumed", stopping further propagation
			event.consume();
		});

		textField.focusedProperty().addListener((c, oldV, newV) -> {
			if(!newV) {
				commitEdit(getConverter().fromString(textField.getText()));
			}
		});
		
		//checks if escape key has been pressed, updated value of escapePressed accordingly
		textField.setOnKeyPressed(t -> {
			if (t.getCode() == KeyCode.ESCAPE) {
				escapePressed = true;
			}
			else {
				escapePressed = false;
			}
		});
		
		//this error also shouldn't happen but it will be reported if it does
		textField.setOnKeyReleased(t -> {
			if (t.getCode() == KeyCode.ESCAPE) {
				GUI.sendError("Error: did not expect esc key releases here.");
			}
		});

		//this handles certain keypresses:
			//escape cancels edit
			//arrow keys cancel edit and move in the direction of pressed key
			//tab key functions same as right arrow key
		textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				textField.setText(getConverter().toString(getItem()));
				cancelEdit();
				event.consume();
			} 
			else if (/*event.getCode() == KeyCode.RIGHT ||*/ event.getCode() == KeyCode.TAB) {
				getTableView().getSelectionModel().selectNext();
				event.consume();
			} 
			/*
			else if (event.getCode() == KeyCode.LEFT) {
				getTableView().getSelectionModel().selectPrevious();
				event.consume();
			} 
			else if (event.getCode() == KeyCode.UP) {
				getTableView().getSelectionModel().selectAboveCell();
				event.consume();
			} 
			else if (event.getCode() == KeyCode.DOWN) {
				getTableView().getSelectionModel().selectBelowCell();
				event.consume();
			}*/
		});

		return textField;
	}

	private String getItemText() {
		//Checks to see if getConverter and/or getItem() are null to determine what to return
		return getConverter() == null ? getItem() == null ? "" : getItem().toString() : getConverter().toString(getItem());
	}

	private void updateItem() {
		if (isEmpty()) {
			setText(null);
			setGraphic(null);
		} 
		else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getItemText());
				}
				setText(null);
				setGraphic(textField);
			} 
			else {
				setText(getItemText());
				setGraphic(null);
			}
		}
	}

	private void startEdit(final TextField textField) {
		//textField must not be null for us to edit it
		if (textField != null) {
			textField.setText(getItemText());
			setText(null);
			setGraphic(textField);
			textField.selectAll();
			//Request focus so that key input can immediately go into the TextField
			textField.requestFocus();
		}
		
	}
}