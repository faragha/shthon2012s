package net.cattaka.slim3.sutame;

import java.io.IOException;

import net.cattaka.slim3.sutame.meta.ImageModelMeta;
import net.cattaka.slim3.sutame.model.ImageModel;
import net.cattaka.slim3.sutame.model.UserModel;
import net.cattaka.slim3.sutame.service.ImageService;
import net.cattaka.slim3.sutame.service.UserService;

import org.slim3.controller.Navigation;
import org.slim3.controller.upload.FileItem;

import scenic3.ScenicPage;
import scenic3.annotation.ActionPath;
import scenic3.annotation.Default;
import scenic3.annotation.Page;

@Page("/m/")
public class ManagePage extends ScenicPage {
    private UserService mUserService = new UserService();
    private ImageService mImageService = new ImageService();
    private ImageModelMeta imageModelMeta = new ImageModelMeta();
    
    @ActionPath("delete")
    public Navigation delete() {
        // TODO
        return redirect("/");
    }

    @ActionPath("upload")
    public Navigation upload()  throws IOException {
        FileItem fileItem = requestScope("file");
        
        ImageModel imageModel = null;
        if (fileItem != null && fileItem.getData() != null && fileItem.getData().length > 0) {
            imageModel = new ImageModel();
            imageModel.setImageData(fileItem.getData());
            
            UserModel userModel = mUserService.getGuestUser();
            mImageService.registerImage(imageModel, userModel);
        }
        
        if (imageModel != null) {
            ImageModel result = new ImageModel();
            result.setImageId(imageModel.getImageId());
            
            response.getWriter().write(imageModelMeta.modelToJson(result));
            return null;
        } else {
            response.setStatus(500);
            response.getWriter().write("file is not attached or bad file.");
            return null;
        }
    }

    // /
    @Default
    public Navigation index() {
        return forward("/index.jsp");
    }
}