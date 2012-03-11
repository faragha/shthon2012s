package net.cattaka.slim3.sutame;

import java.io.IOException;
import java.util.List;

import net.cattaka.slim3.sutame.meta.ImageModelMeta;
import net.cattaka.slim3.sutame.meta.ImageSummaryModelMeta;
import net.cattaka.slim3.sutame.model.ImageModel;
import net.cattaka.slim3.sutame.model.ImageSummaryModel;
import net.cattaka.slim3.sutame.service.ImageService;

import org.slim3.controller.Navigation;

import com.sun.imageio.plugins.common.ImageUtil;

import scenic3.ScenicPage;
import scenic3.annotation.ActionPath;
import scenic3.annotation.Default;
import scenic3.annotation.Page;
import scenic3.annotation.Var;

@Page("/")
public class FrontPage extends ScenicPage {
    private ImageService mImageService = new ImageService();
    private ImageModelMeta imageModelMeta = new ImageModelMeta();
    private ImageSummaryModelMeta imageSummaryModelMeta = new ImageSummaryModelMeta();
    
    @ActionPath("i/{id}")
    public Navigation image(@Var("id") String id) throws IOException {
        super.request.setAttribute("id", id);
        Long imageId = null;
        try {
            imageId = new Long(id);
        } catch (NumberFormatException e) {
            // ignore
        }
        
        ImageModel imageModel = null;
        if (imageId != null) {
            imageModel = mImageService.getImage(imageId);
        }
        if (imageModel != null) {
            response.setContentType(imageModel.getContentType());
            response.getOutputStream().write(imageModel.getImageData());
            return null;
        } else {
            response.setStatus(404);
            response.getWriter().append("Not found. id=" + id);
            return null;
        }
    }
    
    @ActionPath("jsonList")
    public Navigation jsonList() throws IOException {
        List<ImageSummaryModel> imageSummaryModels = mImageService.getImageSummaryModels();
        String json = imageSummaryModelMeta.modelsToJson(imageSummaryModels);
        response.getWriter().append(json);
        
        return null;
    }

    @ActionPath("jsonpList")
    public Navigation jsonpList() throws IOException {
        String func = param("func");
        String requestCode = param("requestCode");
        
        List<ImageSummaryModel> imageSummaryModels = mImageService.getImageSummaryModels();
        String json = imageSummaryModelMeta.modelsToJson(imageSummaryModels);
        response.getWriter().append(func);
        response.getWriter().append('(');
        response.getWriter().append(json);
        response.getWriter().append(',');
        if (requestCode != null) {
            response.getWriter().append("\"" + requestCode+ "\"");
        } else {
            response.getWriter().append("null");
        }
        response.getWriter().append(')');
        
        return null;
    }

    @Default
    public Navigation index() {
        return forward("/index.jsp");
    }

    @ActionPath("devel")
    public Navigation devel() {
        return forward("/devel.jsp");
    }

    @ActionPath("list")
    public Navigation list() {
        return forward("/list.jsp");
    }
}