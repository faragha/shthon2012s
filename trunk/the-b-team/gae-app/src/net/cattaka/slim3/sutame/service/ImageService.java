package net.cattaka.slim3.sutame.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.cattaka.slim3.sutame.meta.ImageModelMeta;
import net.cattaka.slim3.sutame.meta.ImageSummaryModelMeta;
import net.cattaka.slim3.sutame.meta.ImageThumbnailModelMeta;
import net.cattaka.slim3.sutame.StameConstants;
import net.cattaka.slim3.sutame.model.ImageModel;
import net.cattaka.slim3.sutame.model.ImageSummaryModel;
import net.cattaka.slim3.sutame.model.ImageThumbnailModel;
import net.cattaka.slim3.sutame.model.UserModel;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.Transform;


public class ImageService {
    private UserService userService = new UserService();
    private ImageModelMeta imageModelMeta = new ImageModelMeta();
    private ImageThumbnailModelMeta imageThumbnailModelMeta = new ImageThumbnailModelMeta();
    private ImageSummaryModelMeta imageSummaryModelMeta = new ImageSummaryModelMeta();
    
    public ImageModel getImage(Long imageId) {
        return Datastore.query(ImageModel.class).filter(imageModelMeta.imageId.equal(imageId)).asSingle();
    }
    public ImageThumbnailModel getImageThumbnail(Long imageId) {
        return Datastore.query(ImageThumbnailModel.class).filter(imageThumbnailModelMeta.imageId.equal(imageId)).asSingle();
    }
    public ImageSummaryModel getImageSummary(Long imageId) {
        return Datastore.query(ImageSummaryModel.class).filter(imageSummaryModelMeta.imageId.equal(imageId)).asSingle();
    }
    public ImageModel getImage(Key imageKey) {
        return Datastore.get(ImageModel.class, imageKey);
    }
    public boolean registerImage(ImageModel imageModel, UserModel userModel) {
        Long imageId = GenerateIdService.generateId(StameConstants.UQ_IMAGE_ID);
        Image resizedImage = null;
        {   // サムネイルデータを作成する
            resizedImage = createResizedData(imageModel.getImageData(), StameConstants.THUMBNAIL_SIZE_WIDTH, StameConstants.THUMBNAIL_SIZE_HEIGHT);
        }
        
        if (resizedImage == null) { 
            return false;
        } else {
            Key imageKey = Datastore.allocateId(ImageModel.class);
            {
                imageModel.setKey(imageKey);
                imageModel.setImageId(imageId);
                imageModel.setUserKey(userModel.getKey());
                Datastore.put(imageModel);;
            }
            registerRelatedData(imageModel, userModel, resizedImage);
            return true;
        }
    }
    public boolean refleshImage(Long imageId) {
        ImageModel imageModel = getImage(imageId);
        if (imageModel != null) {
            Image resizedImage = null;
            UserModel userModel = null;
            {   // サムネイルデータを作成する
                resizedImage = createResizedData(imageModel.getImageData(), StameConstants.THUMBNAIL_SIZE_WIDTH, StameConstants.THUMBNAIL_SIZE_HEIGHT);
                userModel = userService.getUser(imageModel.getUserKey());
            }
            {   // 古いデータを消す
                ImageSummaryModel sModel = getImageSummary(imageId);
                if (sModel != null) {
                    Datastore.delete(sModel.getKey());
                }
                ImageThumbnailModel tModel = getImageThumbnail(imageId);
                if (tModel != null) {
                    Datastore.delete(tModel.getKey());
                }
            }
            registerRelatedData(imageModel, userModel, resizedImage);
            return true;
        } else {
            return false;
        }
    }
    
    private boolean registerRelatedData(ImageModel imageModel, UserModel userModel, Image resizedImage) {
        {
            ImageThumbnailModel itModel = new ImageThumbnailModel();
            itModel.setUserKey(userModel.getKey());
            itModel.setImageId(imageModel.getImageId());
            itModel.setTitle(imageModel.getTitle());
            itModel.setContentType("image/"+resizedImage.getFormat().name());
            itModel.setImageData(resizedImage.getImageData());
            Datastore.put(itModel);
        }
        {
            ImageSummaryModel imageSummaryModel = new ImageSummaryModel();
            imageSummaryModel.setUserKey(userModel.getKey());
            imageSummaryModel.setImageId(imageModel.getImageId());
            imageSummaryModel.setTitle(imageModel.getTitle());
            imageSummaryModel.setContentType("image/"+resizedImage.getFormat().name());
            Datastore.put(imageSummaryModel);
        }
        return true;
    }
    
    private Image createResizedData(byte[] imageData, int w, int h) {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        // 縦横の比率を保ったまま短辺にあわせてリサイズ
        Image image = ImagesServiceFactory.makeImage(imageData);
        float scaleW = ((float) w ) / image.getWidth();
        float scaleH = ((float) h ) / image.getHeight();
        float scale = Math.min(scaleW, scaleH);
        
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);
        newWidth = Math.min(StameConstants.THUMBNAIL_SIZE_WIDTH, Math.max(1, newWidth));
        newHeight = Math.min(StameConstants.THUMBNAIL_SIZE_HEIGHT, Math.max(1, newHeight));
        
        List<Transform> ts = new ArrayList<Transform>();
        ts.add(ImagesServiceFactory.makeResize(newWidth, newHeight));
        
        Transform t = ImagesServiceFactory.makeCompositeTransform(ts);
        Image tmpImage = imagesService.applyTransform(t, image);
        List<Composite> composites = new ArrayList<Composite>();
        Composite composite = ImagesServiceFactory.makeComposite(tmpImage, 0, 0, 1.0f, Anchor.CENTER_CENTER);
        composites.add(composite);
        Image result = imagesService.composite(composites, StameConstants.THUMBNAIL_SIZE_WIDTH, StameConstants.THUMBNAIL_SIZE_HEIGHT, 0);
        
        return result;
    }
    public List<ImageSummaryModel> getImageSummaryModels(Date lastCreatedAt) {
        ModelQuery<ImageSummaryModel> query = Datastore.query(ImageSummaryModel.class);
        if (lastCreatedAt != null) {
            query = query.filter(imageSummaryModelMeta.updatedAt.lessThan(lastCreatedAt));
        }
        query = query.sort(imageSummaryModelMeta.updatedAt.desc);
        return query.limit(StameConstants.THUMBNAIL_NUM).asList();
    }
}


