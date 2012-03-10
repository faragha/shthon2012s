package net.cattaka.slim3.sutame.service;

import java.util.List;

import net.cattaka.slim3.sutame.meta.ImageModelMeta;
import net.cattaka.slim3.sutame.meta.ImageSummaryModelMeta;
import net.cattaka.slim3.sutame.StameConstants;
import net.cattaka.slim3.sutame.model.ImageModel;
import net.cattaka.slim3.sutame.model.ImageSummaryModel;
import net.cattaka.slim3.sutame.model.UserModel;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;


public class ImageService {
    private ImageModelMeta imageModelMeta = new ImageModelMeta();
    private ImageSummaryModelMeta imageSummaryModelMeta = new ImageSummaryModelMeta();
    
    public ImageModel getImage(Long imageId) {
        return Datastore.query(ImageModel.class).filter(imageModelMeta.imageId.equal(imageId)).asSingle();
    }
    public ImageModel getImage(Key imageKey) {
        return Datastore.get(ImageModel.class, imageKey);
    }
    public boolean registerImage(ImageModel imageModel, UserModel userModel) {
        Long imageId = GenerateIdService.generateId(StameConstants.UQ_IMAGE_ID);
        
        Key imageKey = Datastore.allocateId(ImageModel.class);
        {
            imageModel.setKey(imageKey);
            imageModel.setImageId(imageId);
            imageModel.setUserKey(userModel.getKey());
            Datastore.put(imageModel);;
        }
        {
            ImageSummaryModel imageSummaryModel = new ImageSummaryModel();
            imageSummaryModel.setUserKey(userModel.getKey());
            imageSummaryModel.setImageId(imageId);
            Datastore.put(imageSummaryModel);
        }
        
        return true;
    }
    
    public List<ImageSummaryModel> getImageSummaryModels() {
        return Datastore.query(ImageSummaryModel.class).sort(imageSummaryModelMeta.updatedAt.desc).asList();
    }
}


