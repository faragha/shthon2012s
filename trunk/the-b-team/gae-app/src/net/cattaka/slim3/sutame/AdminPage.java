package net.cattaka.slim3.sutame;

import java.util.Date;
import java.util.List;

import net.cattaka.slim3.sutame.meta.ImageModelMeta;
import net.cattaka.slim3.sutame.meta.ImageSummaryModelMeta;
import net.cattaka.slim3.sutame.meta.ImageThumbnailModelMeta;
import net.cattaka.slim3.sutame.model.ImageModel;
import net.cattaka.slim3.sutame.model.ImageSummaryModel;
import net.cattaka.slim3.sutame.model.ImageThumbnailModel;

import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import scenic3.ScenicPage;
import scenic3.annotation.ActionPath;
import scenic3.annotation.Page;

@Page("/admin/")
public class AdminPage extends ScenicPage {
    private ImageModelMeta imageModelMeta = new ImageModelMeta();
    private ImageThumbnailModelMeta imageThumbnailModelMeta = new ImageThumbnailModelMeta();
    private ImageSummaryModelMeta imageSummaryModelMeta = new ImageSummaryModelMeta();
    
    @ActionPath("fillDeleted")
    public Navigation delete() {
        /*
        int limit = 10;
        Date currentDate = new Date();
        Date lastDate = currentDate;
        {
            while(true) {
                List<ImageModel> imList =  Datastore.query(ImageModel.class)
                        .filter(imageModelMeta.updatedAt.lessThan(lastDate))
                        .sort(imageModelMeta.updatedAt.desc)
                        .limit(limit)
                        .asList();
                for (ImageModel im : imList) {
                    if (im.getDeleted() == null) {
                        im.setDeleted(false);
                        Datastore.put(im);
                    }
                    lastDate = im.getUpdatedAt();
                }
                if (imList.size() < limit) {
                    break;
                }
            }
        }
        {
            while(true) {
                List<ImageThumbnailModel> imList =  Datastore.query(ImageThumbnailModel.class)
                        .filter(imageThumbnailModelMeta.updatedAt.lessThan(lastDate))
                        .sort(imageThumbnailModelMeta.updatedAt.desc)
                        .limit(limit)
                        .asList();
                for (ImageThumbnailModel im : imList) {
                    if (im.getDeleted() == null) {
                        im.setDeleted(false);
                        Datastore.put(im);
                    }
                    lastDate = im.getUpdatedAt();
                }
                if (imList.size() < limit) {
                    break;
                }
            }
        }
        {
            while(true) {
                List<ImageSummaryModel> imList =  Datastore.query(ImageSummaryModel.class)
                        .filter(imageSummaryModelMeta.updatedAt.lessThan(lastDate))
                        .sort(imageSummaryModelMeta.updatedAt.desc)
                        .limit(limit)
                        .asList();
                for (ImageSummaryModel im : imList) {
                    if (im.getDeleted() == null) {
                        im.setDeleted(false);
                        Datastore.put(im);
                    }
                    lastDate = im.getUpdatedAt();
                }
                if (imList.size() < limit) {
                    break;
                }
            }
        }
        */
        return redirect("/");
    }
}