package gui.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.RequestClient;
import logic.ResponseClient;

import communication.JsonClient;

/**
 * Created by Ronnie on 12.02.14.
 * 
 * Denne snakker med HttpClient og JsonClient
 * 
 */
public class ServerCommHandler {

	public static int[] getAllImageIds() {
		int[] allImageId = null;
		JsonClient getAllImagesJson = new JsonClient(new RequestClient(
				"getAllImages"));
		if (getAllImagesJson.sendJsonToServer()) {
			ResponseClient getAllImagesResponse = getAllImagesJson
					.receiveJsonFromServer();
			if (getAllImagesResponse.getSuccess()) {
				allImageId = getAllImagesResponse.getImageIdArray();
			}
			getAllImagesJson.closeHttpConnection();
		} else {
			return null;
		}
		return allImageId;
	}

	public static int[] getImagesInFolderAndSubfolders(int id) {
		int[] allImageId = null;
		JsonClient getAllImagesJson = new JsonClient(new RequestClient(
				"getImagesInFolderAndSubfolders", id));
		if (getAllImagesJson.sendJsonToServer()) {
			ResponseClient getAllImagesResponse = getAllImagesJson
					.receiveJsonFromServer();
			if (getAllImagesResponse.getSuccess()) {
				allImageId = getAllImagesResponse.getImageIdArray();
			}
			getAllImagesJson.closeHttpConnection();
		} else {
			return null;
		}

		return allImageId;
	}

	public static int[] getImagesInFolder(int id) {
		int[] allImageId = null;
		JsonClient getAllImagesJson = new JsonClient(new RequestClient(
				"getImagesInFolder", id));
		if (getAllImagesJson.sendJsonToServer()) {
			ResponseClient getAllImagesResponse = getAllImagesJson
					.receiveJsonFromServer();
			if (getAllImagesResponse.getSuccess()) {
				allImageId = getAllImagesResponse.getImageIdArray();
			}
			getAllImagesJson.closeHttpConnection();
		} else {
			return null;
		}

		return allImageId;
	}

	public static ImageView getThumbnail(int imageID) {

		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient(
				"getThumbnail", imageID));
		if (getThumbnailJson.sendJsonToServer()) {
			ResponseClient getThumbnailResponse = getThumbnailJson
					.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()) {
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

		// For testing
		if (image == null) {
			image = new Image("testthumbnail.png");
		}

		return new ImageView(image);

	}

	public static boolean SendImageToServer(File fileToSend) {

		boolean success = false;

		int nextAvailableId = -1;
		String fileName = null;

		JsonClient sendImageJson1 = new JsonClient(new RequestClient(
				"getNextImageId"));
		if (sendImageJson1.sendJsonToServer()) {
			ResponseClient sendImageResponse1 = sendImageJson1
					.receiveJsonFromServer();
			nextAvailableId = sendImageResponse1.getImageId();
			System.out.println("Next available id: " + nextAvailableId);
			sendImageJson1.closeHttpConnection();
		}
		fileName = fileToSend.getName();
		System.out.println("Filtype til nytt bilde: " + fileName + " id: "
				+ nextAvailableId);
		if (nextAvailableId != -1) {
			boolean imageWasSentSuccessful = false;
			while (imageWasSentSuccessful == false) {
				JsonClient sendImageJson = new JsonClient(new RequestClient(
						"addNewFullImage", nextAvailableId, fileName));
				if (sendImageJson.sendJsonToServer()) {
					try {
						System.out.println("Pr�ver � sende bilde: "
								+ fileToSend.getPath());
						sendImageJson.sendFileToServer(fileToSend.getPath());
						ResponseClient response = sendImageJson
								.receiveJsonFromServer();
						imageWasSentSuccessful = response.getSuccess();
						if (response.getSuccess()) {
							success = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					sendImageJson.closeHttpConnection();
				}
			}
		}
		return success;
	}

	public static Hashtable<Integer, String> getSubFoldersIdAndName(int id) {

		int[] subFoldersId = null;
		String[] subFoldersName = null;
		Hashtable<Integer, String> idAndNames = new Hashtable<>();

		JsonClient json = new JsonClient(new RequestClient("getSubFolders", id,
				null));

		if (json.sendJsonToServer()) {

			ResponseClient response = json.receiveJsonFromServer();

			System.out.println(response.toString());

			if (response.getSuccess()) {
				System.out.println("sucess");
				subFoldersId = response.getImageIdArray();
				subFoldersName = response.getStringArray();

			}
			json.closeHttpConnection();
		} else {
			System.out.println("fail2");

		}

		for (int i = 0; i < subFoldersId.length; i++) {
			idAndNames.put(subFoldersId[i], subFoldersName[i]);
		}

		System.out.println(idAndNames.toString());

		return idAndNames;

	}

	public static int[] getSubFoldersIdArray(int id) {

		int[] subFoldersId = null;

		JsonClient json = new JsonClient(new RequestClient("getSubFolders", id,
				null));

		if (json.sendJsonToServer()) {

			ResponseClient response = json.receiveJsonFromServer();

			System.out.println(response.toString());

			if (response.getSuccess()) {
				System.out.println("sucess");
				subFoldersId = response.getImageIdArray();

			}
			json.closeHttpConnection();
		} else {
			System.out.println("fail2");

		}

		return subFoldersId;

	}

	public static int[] getAllImagesInFolder(int id) {

		int[] imageIdArray = null;

		JsonClient json = new JsonClient(new RequestClient("getImagesInFolder",
				id, null));

		if (json.sendJsonToServer()) {

			ResponseClient response = json.receiveJsonFromServer();

			System.out.println(response.toString());

			if (response.getSuccess()) {
				System.out.println("sucess");
				imageIdArray = response.getImageIdArray();

			}
			json.closeHttpConnection();
		} else {
			System.out.println("fail2");

		}

		return imageIdArray;

	}

	public static ImageView getFullImage(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient(
				"getFullImage", imageID));
		if (getThumbnailJson.sendJsonToServer()) {
			ResponseClient getThumbnailResponse = getThumbnailJson
					.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()) {
				bufImage = getThumbnailJson.receiveImageFromServer();
			}
			getThumbnailJson.closeHttpConnection();
		}

		// TODO: FOR DEBUG

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

		// For testing
		if (image == null) {
			image = new Image("testfull.png");
		}

		return new ImageView(image);
	}

	public static ImageView getMediumImage(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getMediumJson = new JsonClient(new RequestClient(
				"getFullImageWithDimensions", imageID, "500;500"));
		if (getMediumJson.sendJsonToServer()) {
			ResponseClient getThumbnailResponse = getMediumJson
					.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()) {
				bufImage = getMediumJson.receiveImageFromServer();
			}
			getMediumJson.closeHttpConnection();
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

		// For testing
		if (image == null) {
			image = new Image("testmedium.png");
		}

		return new ImageView(image);
	}

	public static String[] getMetaData(int imageID) {

		String[] metaData = null;

		JsonClient getMetaDataJson = new JsonClient(new RequestClient(
				"getMetadata", imageID));
		if (getMetaDataJson.sendJsonToServer()) {
			ResponseClient getMetaDataResponse = getMetaDataJson
					.receiveJsonFromServer();
			if (getMetaDataResponse.getSuccess()) {
				metaData = getMetaDataResponse.getStringArray();
			}
			getMetaDataJson.closeHttpConnection();
		}

		System.out.println(metaData[0] + " " + metaData[1] + " " + metaData[2]
				+ " " + metaData[3] + " " + metaData[4]);

		return metaData;
	}

	public static Boolean modifyTitle(int imageID, String string) {

		Boolean success = false;

		JsonClient modifyTitle = new JsonClient(new RequestClient(
				"modifyTitle", imageID, string));
		if (modifyTitle.sendJsonToServer()) {
			ResponseClient modifyTitleResponse = modifyTitle
					.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()) {
				success = true;
			}
			modifyTitle.closeHttpConnection();
		}

		return success;

	}

	public static Boolean modifyDesc(int imageID, String string) {

		Boolean success = false;

		JsonClient modifyDescription = new JsonClient(new RequestClient(
				"modifyDescription", imageID, string));
		if (modifyDescription.sendJsonToServer()) {
			ResponseClient modifyTitleResponse = modifyDescription
					.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()) {
				success = true;
			}
			modifyDescription.closeHttpConnection();
		}

		return success;

	}

	public static Boolean modifyRating(int imageID, String string) {

		Boolean success = false;

		JsonClient modifyRating = new JsonClient(new RequestClient(
				"modifyRating", imageID, string));
		if (modifyRating.sendJsonToServer()) {
			ResponseClient modifyTitleResponse = modifyRating
					.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()) {
				success = true;
			}
			modifyRating.closeHttpConnection();
		}

		return success;

	}

	public static Boolean addTag(int imageID, String string) {

		Boolean success = false;

		JsonClient addTag = new JsonClient(new RequestClient("addTag", imageID,
				string));
		if (addTag.sendJsonToServer()) {
			ResponseClient modifyTitleResponse = addTag.receiveJsonFromServer();
			if (modifyTitleResponse.getSuccess()) {
				success = true;
			}
			addTag.closeHttpConnection();
		}

		return success;

	}

	public static ImageView getRotLeft(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient(
				"rotate90CounterClock", imageID));
		if (getThumbnailJson.sendJsonToServer()) {
			ResponseClient getThumbnailResponse = getThumbnailJson
					.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()) {
				bufImage = getThumbnailJson.receiveImageFromServer();
			}
			getThumbnailJson.closeHttpConnection();
		}

		// TODO: FOR DEBUG

		// String text = String.valueOf(imageID)
		// + " rotated left: Ikkje klart p� serveren enn�";
		Graphics2D g = null;
		try {
			g = (Graphics2D) bufImage.createGraphics();
		} catch (Exception e) {
			return null;
		}
		Font font = new Font("Verdana", Font.ITALIC, 24);
		g.setFont(font);
		g.setColor(Color.RED);
		// g.drawString(text, 20, 20);

		// TODO: Kommer avogtil nullpointerexception her. Kanskje fordi jeg
		// blander swing og JavaFX?
		Image image = SwingFXUtils.toFXImage(bufImage, null);

		return new ImageView(image);
	}

	public static ImageView getRotRight(int imageID) {
		BufferedImage bufImage = null;
		JsonClient getThumbnailJson = new JsonClient(new RequestClient(
				"rotate90Clock", imageID));
		if (getThumbnailJson.sendJsonToServer()) {
			ResponseClient getThumbnailResponse = getThumbnailJson
					.receiveJsonFromServer();
			if (getThumbnailResponse.getSuccess()) {
				bufImage = getThumbnailJson.receiveImageFromServer();
			}
			getThumbnailJson.closeHttpConnection();
		}

		// TODO: FOR DEBUG

		// String text = String.valueOf(imageID)
		// + " rotated right: Ikkje klart p� serveren enn�";

		Graphics2D g;
		try {
			g = (Graphics2D) bufImage.createGraphics();
		} catch (Exception e) {
			return null;
		}
		Font font = new Font("Verdana", Font.ITALIC, 24);
		g.setFont(font);
		g.setColor(Color.RED);
		// g.drawString(text, 20, 20);

		// TODO: Kommer avogtil nullpointerexception her. Kanskje fordi jeg
		// blander swing og JavaFX?
		Image image = SwingFXUtils.toFXImage(bufImage, null);

		return new ImageView(image);
	}

}
