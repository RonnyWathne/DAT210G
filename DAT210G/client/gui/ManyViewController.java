package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gui.model.FolderTree;
import gui.model.OneImage;
import gui.model.ServerCommHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class ManyViewController {

	private FolderTree folderTreeModel;

	private Main main;

	@FXML
	private AnchorPane anchorPaneForMany;
	@FXML
	private Button importBtn;
	@FXML
	private TextField titleTextField;
	@FXML
	private TextField descTextField;
	@FXML
	private TextField ratingTextField;
	@FXML
	private TextField dateTextField;
	@FXML
	private TextField tagsTextField;
	@FXML
	private Slider zoomSlider;
	@FXML
	private HBox hboxForTree;

	ScrollPane scrollPane;

	private ZoomLevel currentZooom;

	private enum ZoomLevel {
		SMALL, MEDIUM
	}

	@FXML
	public void goToSingleView() {

		main.setSingleMode();

	}

	/**
	 * Called when the user clicks import.
	 */
	@FXML
	private void handleImportBtn() {

		openFileChooser();

		main.setManyMode(true);

	}

	public void updateFolderTree() {

		hboxForTree.getChildren().add(folderTreeModel.getTree());

	}

	public void start() {

		if (!folderTreeModel.isReady()) {

			ProgressIndicator bar = new ProgressIndicator();
			bar.progressProperty().bind(
					folderTreeModel.getTask().progressProperty());

			anchorPaneForMany.getChildren().add(bar);

		} else {

			setModel();

			makeGridAndDisplayImages();

			folderTreeModel.setManyViewController(this);

		}

	}

	public void makeGridAndDisplayImages() {

		anchorPaneForMany.getChildren().clear();

		scrollPane = new ScrollPane();

		if (currentZooom == ZoomLevel.SMALL) {
			scrollPane.setContent(smallZoomLevelImages());
		} else if (currentZooom == ZoomLevel.MEDIUM) {
			scrollPane.setContent(mediumZoomLevelImages());
		}

		scrollPane.prefViewportWidthProperty().bind(
				anchorPaneForMany.widthProperty());
		scrollPane.prefViewportHeightProperty().bind(
				anchorPaneForMany.heightProperty());

		anchorPaneForMany.getChildren().add(scrollPane);

	}

	private FlowPane smallZoomLevelImages() {

		FlowPane smallPane = new FlowPane();

		smallPane.setPadding(new Insets(5, 0, 5, 0));
		smallPane.setVgap(4);
		smallPane.setHgap(4);

		smallPane.prefWrapLengthProperty().bind(
				anchorPaneForMany.widthProperty());

		ArrayList<OneImage> imagesToBeDisplayed = folderTreeModel
				.getImagesInThisFolderAndDown();

		for (OneImage image : imagesToBeDisplayed) {

			smallPane.getChildren().add(image.getThumbnailImage());

		}

		return smallPane;

	}

	private FlowPane mediumZoomLevelImages() {

		FlowPane mediumPane = new FlowPane();

		mediumPane.setPadding(new Insets(5, 0, 5, 0));
		mediumPane.setVgap(4);
		mediumPane.setHgap(4);

		mediumPane.prefWrapLengthProperty().bind(
				anchorPaneForMany.widthProperty());

		ArrayList<OneImage> imagesToBeDisplayed = folderTreeModel
				.getImagesInThisFolderAndDown();

		for (OneImage image : imagesToBeDisplayed) {

			mediumPane.getChildren().add(image.getMediumImage());

		}

		return mediumPane;
	}

	private void openFileChooser() {

		// TODO: hvordan f� til importere directories?

		ArrayList<String> fileTypes = new ArrayList<>();
		fileTypes.add("*.jpg");
		fileTypes.add("*.jpeg");
		fileTypes.add("*.png");
		fileTypes.add("*.bmp");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import images");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", fileTypes));

		List<File> fileList = fileChooser.showOpenMultipleDialog(main
				.getPrimaryStage());

		if (fileList != null) {
			for (File file : fileList) {

				ServerCommHandler.SendImageToServer(file);

			}
		}

		/*
		 * final DirectoryChooser directoryChooser = new DirectoryChooser();
		 * final File selectedDirectory =
		 * directoryChooser.showDialog(mainController.getPrimaryStage()); if
		 * (selectedDirectory != null) { selectedDirectory.getAbsolutePath(); }
		 */

	}

	public void setMainController(Main mainController) {
		this.main = mainController;
		currentZooom = ZoomLevel.SMALL;
	}

	private void setModel() {

		updateFolderTree();

		zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {

				if (newValue.intValue() == 1) {
					currentZooom = ZoomLevel.MEDIUM;

					makeGridAndDisplayImages();
				} else if (newValue.intValue() == 0) {
					currentZooom = ZoomLevel.SMALL;

					makeGridAndDisplayImages();
				}

			}
		});

	}

	public void setFolderTreeModel(FolderTree folderTreeModel) {
		this.folderTreeModel = folderTreeModel;
	}

}
