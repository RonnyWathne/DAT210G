package gui.model;

import java.util.ArrayList;
import java.util.List;

import gui.ThumbnailsModeController;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FolderTree {

	private TreeView<String> treeView;
	
	private ThumbnailsModeController thumbnailsModeController;
	private Model model;
	private Node folderIcon;
	private ArrayList<String> folders = new ArrayList<String>();
	

	
	public FolderTree(ThumbnailsModeController thumbnailsModeController, Model model) {
		
		this.thumbnailsModeController = thumbnailsModeController;
		this.model = model;		
		
		folders.add("Dogs");
		folders.add("Clowns");
		folders.add("Stupid");
		folders.add("screenshots");
		folders.add("Wow");
		folders.add("Ferie2012");
		folders.add("Pr0n");
		folders.add("Jul1914");
		folders.add("wallpapers");
        
		folderIcon = new ImageView(new Image(getClass().getResourceAsStream("folderIcon.png")));
		
		
		TreeItem<String> rootNode = new TreeItem<String> ("/", folderIcon);
		
		/*
        TreeItem<String> folderA = new TreeItem<String>("Folder A");
        TreeItem<String> folderA1 = new TreeItem<String>("Folder A1");
        TreeItem<String> folderA2 = new TreeItem<String>("Folder A2");
        TreeItem<String> folderA3 = new TreeItem<String>("Folder A3");
        TreeItem<String> folderA4 = new TreeItem<String>("Folder A4");
        
        folderA.getChildren().add(folderA1);
        folderA.getChildren().add(folderA2);
        folderA.getChildren().add(folderA3);
        folderA.getChildren().add(folderA4);
        
        TreeItem<String> folderB = new TreeItem<String>("Folder B");
        TreeItem<String> folderB1 = new TreeItem<String>("Folder B1");
        TreeItem<String> folderB2 = new TreeItem<String>("Folder B2");
        TreeItem<String> folderB3 = new TreeItem<String>("Folder B3");
        TreeItem<String> folderB4 = new TreeItem<String>("Folder B4");
        
        folderB.getChildren().add(folderB1);
        folderB.getChildren().add(folderB2);
        folderB.getChildren().add(folderB3);
        folderB.getChildren().add(folderB4);
        
        rootNode.getChildren().add(folderA);
        rootNode.getChildren().add(folderB);
        */
		
		for (String string : folders) {
			
			TreeItem<String> foldersLeaf = new TreeItem<String>(string);
			rootNode.getChildren().add(foldersLeaf);
			
		}
        
        
        
        
        treeView = new TreeView<String>();
        treeView.setShowRoot(true);
        treeView.setRoot(rootNode);
        rootNode.setExpanded(true);
        

	}


	public TreeView<String> getTree() {
		return treeView;
	}
	
	
}