package storing;


import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DeleteFromDatabase {
	private static boolean succesful;

	public static boolean deletePicture(int picId) {
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction dbTransaction = null;
		try {
			dbTransaction = dbSession.beginTransaction();
			Query query = dbSession.createQuery("FROM PictureDb WHERE id=:picId");
			query.setParameter("picId", picId);
			PictureDb picFromDb = (PictureDb) query.uniqueResult();
			Set<TagDb> tagsToPic = removeTagOnPictureDelete(picFromDb);
			picFromDb.getTags().removeAll(tagsToPic);
			dbSession.delete(picFromDb);
			dbTransaction.commit();
			succesful = true;
		} catch (HibernateException e) {
			if (dbTransaction != null) dbTransaction.rollback();
			succesful = false;
		} finally {
			dbSession.close();
			HibernateUtil.shutdown();
		}
		return succesful;
	}


	private static Set<TagDb> removeTagOnPictureDelete(PictureDb picFromDb) {
		Set<TagDb> tagsToPic = picFromDb.getTags();
		for (TagDb tag: tagsToPic) {
			tag.getPics().remove(picFromDb);
		}
		return tagsToPic;
	}

	public static boolean deleteTagFromPicture(int pictureId, String tag) {
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction dbTransaction = null;
		try {
			dbTransaction = dbSession.beginTransaction();
			PictureDb pictureFromDb = (PictureDb) dbSession.load(PictureDb.class, pictureId);
			pictureFromDb.getTags().remove(tag);
			TagDb tagFromDb = (TagDb) dbSession.load(TagDb.class, tag);
			tagFromDb.getPics().remove(pictureFromDb);
			dbTransaction.commit();
			succesful = true;
		} catch (HibernateException e) {
			if (dbTransaction != null) dbTransaction.rollback();
			succesful = false;
		} finally {
			dbSession.close();
			HibernateUtil.shutdown();
		}
		return succesful;
	}

	public static boolean deleteFolderAndContent(int folderId) {
		int[] folderAndSubfolderId = ReadFromDatabase.getFolderAndSubFolderId(folderId);
		Session dbSession = HibernateUtil.getSessionFactory().openSession();
		Transaction dbTransaction = null;
		try {
			dbTransaction = dbSession.beginTransaction();
			Query query = null;

			query = dbSession.createQuery("SELECT lft FROM ParentFolderDb WHERE folderId=:folderId");
			query.setParameter("folderId", folderId);
			int lft = (int) query.uniqueResult();
			query = dbSession.createQuery("SELECT rgt FROM ParentFolderDb WHERE folderId=:folderId");
			query.setParameter("folderId", folderId);
			int rgt = (int) query.uniqueResult();
			int width = rgt - lft + 1;

			deleteFolders(dbSession, lft, rgt);
			updateRemainingFolders(dbSession, lft, rgt, width);
			deletePicturesInDeletedFolders(folderAndSubfolderId, dbSession);

			dbTransaction.commit();
			succesful = true;
		} catch (HibernateException e) {
			if (dbTransaction != null) dbTransaction.rollback();
			e.printStackTrace();
			succesful = false;
		} finally {
			dbSession.close();
			HibernateUtil.shutdown();
		}
		return succesful;
	}


	@SuppressWarnings("unchecked")
	private static void deletePicturesInDeletedFolders(
			int[] folderAndSubfolderId, Session dbSession) {
		List<PictureDb> tmpList;
		Query query;
		for (int i: folderAndSubfolderId) {
			query = dbSession.createQuery("FROM PictureDb WHERE parentFolderId=:folderID");
			query.setParameter("folderID", i);
			tmpList = query.list();
			for (PictureDb p: tmpList) {
				Set<TagDb> tagsToPic = removeTagOnPictureDelete(p);
				p.getTags().removeAll(tagsToPic);
				dbSession.delete(p);
			}
		}
	}

	private static void updateRemainingFolders(Session dbSession, int lft,
			int rgt, int width) {
		Query query = dbSession.createQuery("UPDATE ParentFolderDb SET rgt = rgt - :width WHERE rgt > :rgt");
		query.setParameter("width", width);
		query.setParameter("rgt", rgt);
		query.executeUpdate();

		query = dbSession.createQuery("UPDATE ParentFolderDb SET lft = lft - :width WHERE lft > :lft");
		query.setParameter("width", width);
		query.setParameter("lft", lft);
		query.executeUpdate();
	}

	private static void deleteFolders(Session dbSession, int lft, int rgt) {
		Query query = dbSession.createQuery("DELETE FROM ParentFolderDb WHERE lft BETWEEN :lft AND :rgt");
		query.setParameter("lft", lft);
		query.setParameter("rgt", rgt);
		query.executeUpdate();
	}




}
