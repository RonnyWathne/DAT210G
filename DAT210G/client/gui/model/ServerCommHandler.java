package gui.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;

import communication.JsonClient;



import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;









import logic.RequestClient;
import logic.ResponseClient;

/**
 * Created by Ronnie on 12.02.14.
 *
 * Denne snakker med HttpClient og JsonClient
 * 
 */
public class ServerCommHandler {



	public int[] getAllImageIds() {
		int[] allImageId = null;
		JsonClient getAllImagesJson = new JsonClient(new RequestClient("getAllImages"));
		if (getAllImagesJson.sendJsonToServer()){
			ResponseClient getAllImagesResponse = getAllImagesJson.receiveJsonFromServer();
			if (getAllImagesResponse.getSuccess()){
				allImageId = getAllImagesResponse.getImageIdArray();
			}
			getAllImagesJson.closeHttpConnection();
		} else {			
			return null;
		}
		return allImageId;
	}

	public ImageView getThumbnail(int imageID) {
				
		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient("getThumbnail", imageID));
		if (getThumbnailJson.sendJsonToServer()){
			ResponseClient getThumbnailResponse = getThumbnailJson.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()){
				bufImage = getThumbnailJson.receiveImageFromServer();
			}
			getThumbnailJson.closeHttpConnection();
		}


		Image image = null;
		
		if (bufImage != null) {
			// TODO: FOR DEBUG 

			Graphics2D g = (Graphics2D) bufImage.createGraphics();
			Font font = new Font("Verdana", Font.ITALIC, 24);
			g.setFont(font);
			g.setColor(Color.RED);
			g.drawString(String.valueOf(imageID), 15, 15);
			
			image = SwingFXUtils.toFXImage(bufImage, null);
		} 

		
		return new ImageView(image);
	}
	public void SendImageToServer(File fileToSend){
		int nextAvailableId = -1;
		BufferedImage image = null;
		String fileType = null;
		try {
			image = ImageIO.read(fileToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonClient sendImageJson1 = new JsonClient(new RequestClient("getNextImageId"));
		if (sendImageJson1.sendJsonToServer()){
			ResponseClient sendImageResponse1 = sendImageJson1.receiveJsonFromServer();
			nextAvailableId = sendImageResponse1.getImageId();
			System.out.println("Next available id: "+nextAvailableId);
			sendImageJson1.closeHttpConnection();
		}
		int i = fileToSend.getAbsolutePath().lastIndexOf('.');
		if (i > 0) {
			fileType = fileToSend.getAbsolutePath().substring(i+1);
			System.out.println("Filtype til nytt bilde: " + fileType + " id: " + nextAvailableId);
		}
		if (nextAvailableId != -1){
			boolean imageWasSentSuccessful = false;
			while (imageWasSentSuccessful == false){
				JsonClient sendImageJson = new JsonClient(new RequestClient("addNewFullImage", nextAvailableId, fileType));
				if (sendImageJson.sendJsonToServer()){ 
					//sendImageJson.sendImageToServer(image, fileType);
					try {
						System.out.println("Pr�ver � sende bilde: " + fileToSend.getPath());
						sendImageJson.sendFileToServer(fileToSend.getPath());
						ResponseClient response = sendImageJson.receiveJsonFromServer();
						imageWasSentSuccessful = response.getSuccess();
					} catch (Exception e) {
						e.printStackTrace();
					}
					sendImageJson.closeHttpConnection();
				}
			}
		}

	}

	public ImageView getFullImage(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient("getFullImage", imageID));
		if (getThumbnailJson.sendJsonToServer()){
			ResponseClient getThumbnailResponse = getThumbnailJson.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()){
				bufImage = getThumbnailJson.receiveImageFromServer();
			}
			getThumbnailJson.closeHttpConnection();
		}

		// TODO: FOR DEBUG 

		Graphics2D g = (Graphics2D) bufImage.createGraphics();
		Font font = new Font("Verdana", Font.ITALIC, 24);
		g.setFont(font);
		g.setColor(Color.RED);
		g.drawString(String.valueOf(imageID), 20, 20); 

		//TODO: Kommer avogtil nullpointerexception her. Kanskje fordi jeg blander swing og JavaFX?
		Image image = SwingFXUtils.toFXImage(bufImage, null);


		return new ImageView(image);
	}

	
	public ImageView getMediumImage(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getMediumJson = new JsonClient(new RequestClient("getFullImageWithDimensions", imageID, "500;500"));
		if (getMediumJson.sendJsonToServer()){
			ResponseClient getThumbnailResponse = getMediumJson.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()){
				bufImage = getMediumJson.receiveImageFromServer();
			}
			getMediumJson.closeHttpConnection();
		}

		// TODO: FOR DEBUG 

		Graphics2D g = (Graphics2D) bufImage.createGraphics();
		Font font = new Font("Verdana", Font.ITALIC, 24);
		g.setFont(font);
		g.setColor(Color.RED);
		g.drawString(String.valueOf(imageID), 20, 20); 

		//TODO: Kommer avogtil nullpointerexception her. Kanskje fordi jeg blander swing og JavaFX?
		Image image = SwingFXUtils.toFXImage(bufImage, null);


		return new ImageView(image);
	}


	public String[] getMetaData(int imageID) {

		String[] metaData = null;	

		JsonClient getMetaDataJson = new JsonClient(new RequestClient("getMetadata", imageID));
		if (getMetaDataJson.sendJsonToServer()){
			ResponseClient getMetaDataResponse = getMetaDataJson.receiveJsonFromServer();
			if (getMetaDataResponse.getSuccess()){
				metaData = getMetaDataResponse.getStringArray();
			}
			getMetaDataJson.closeHttpConnection();
		}

		System.out.println(metaData[0] + " " + metaData[1] + " " + metaData[2] + " " + metaData[3] + " " + metaData[4] );


		return metaData;
	}
	
	public Boolean modifyTitle(int imageID, String string) {
			
		Boolean success = false;

		JsonClient modifyTitle = new JsonClient(new RequestClient("modifyTitle", imageID, string));
		if (modifyTitle.sendJsonToServer()){
			ResponseClient modifyTitleResponse = modifyTitle.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()){
				success = true;
			}
			modifyTitle.closeHttpConnection();
		}
		
		return success;
		
	}
	
	public Boolean modifyDesc(int imageID, String string) {
		
		Boolean success = false;

		JsonClient modifyDescription = new JsonClient(new RequestClient("modifyDescription", imageID, string));
		if (modifyDescription.sendJsonToServer()){
			ResponseClient modifyTitleResponse = modifyDescription.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()){
				success = true;
			}
			modifyDescription.closeHttpConnection();
		}
		
		return success;
		
	}
	
	public Boolean modifyRating(int imageID, String string) {
		
		Boolean success = false;

		JsonClient modifyRating = new JsonClient(new RequestClient("modifyRating", imageID, string));
		if (modifyRating.sendJsonToServer()){
			ResponseClient modifyTitleResponse = modifyRating.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()){
				success = true;
			}
			modifyRating.closeHttpConnection();
		}
		
		return success;
		
	}
	
	public Boolean addTag(int imageID, String string) {
		
		Boolean success = false;

		JsonClient addTag = new JsonClient(new RequestClient("addTag", imageID, string));
		if (addTag.sendJsonToServer()){
			ResponseClient modifyTitleResponse = addTag.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()){
				success = true;
			}
			addTag.closeHttpConnection();
		}
		
		return success;
		
	}
	
	
	public ImageView getRotLeft(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient("getFullImage", imageID));
		if (getThumbnailJson.sendJsonToServer()){
			ResponseClient getThumbnailResponse = getThumbnailJson.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()){
				bufImage = getThumbnailJson.receiveImageFromServer();
			}
			getThumbnailJson.closeHttpConnection();
		}

		// TODO: FOR DEBUG 

		String text = String.valueOf(imageID) + " rotated left";
		
		Graphics2D g = (Graphics2D) bufImage.createGraphics();
		Font font = new Font("Verdana", Font.ITALIC, 24);
		g.setFont(font);
		g.setColor(Color.RED);
		g.drawString(text, 20, 20); 

		//TODO: Kommer avogtil nullpointerexception her. Kanskje fordi jeg blander swing og JavaFX?
		Image image = SwingFXUtils.toFXImage(bufImage, null);


		return new ImageView(image);
	}
	
	public ImageView getRotRight(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient("getFullImage", imageID));
		if (getThumbnailJson.sendJsonToServer()){
			ResponseClient getThumbnailResponse = getThumbnailJson.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()){
				bufImage = getThumbnailJson.receiveImageFromServer();
			}
			getThumbnailJson.closeHttpConnection();
		}

		// TODO: FOR DEBUG 

		String text = String.valueOf(imageID) + " rotated right";
		
		Graphics2D g = (Graphics2D) bufImage.createGraphics();
		Font font = new Font("Verdana", Font.ITALIC, 24);
		g.setFont(font);
		g.setColor(Color.RED);
		g.drawString(text, 20, 20); 

		//TODO: Kommer avogtil nullpointerexception her. Kanskje fordi jeg blander swing og JavaFX?
		Image image = SwingFXUtils.toFXImage(bufImage, null);


		return new ImageView(image);
	}





}
