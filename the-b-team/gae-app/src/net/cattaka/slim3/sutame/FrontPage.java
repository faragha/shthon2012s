package net.cattaka.slim3.sutame;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import net.cattaka.slim3.sutame.meta.ImageModelMeta;
import net.cattaka.slim3.sutame.meta.ImageSummaryModelMeta;
import net.cattaka.slim3.sutame.model.ImageModel;
import net.cattaka.slim3.sutame.model.ImageSummaryModel;
import net.cattaka.slim3.sutame.model.ImageThumbnailModel;
import net.cattaka.slim3.sutame.service.ImageService;

import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.util.StringUtil;

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
            if (imageModel.getContentType() != null) {
                response.addHeader("Content-Type", imageModel.getContentType());
            } else {
                response.addHeader("Content-Type", "image/png");
            }
            response.getOutputStream().write(imageModel.getImageData());
            return null;
        } else {
            response.setStatus(404);
            response.getWriter().append("Not found. imageId=" + id);
            return null;
        }
    }

    @ActionPath("t/{id}")
    public Navigation thumbnail(@Var("id") String id) throws IOException {
        super.request.setAttribute("id", id);
        Long imageId = null;
        try {
            imageId = new Long(id);
        } catch (NumberFormatException e) {
            // ignore
        }
        
        ImageThumbnailModel imageModel = null;
        if (imageId != null) {
            imageModel = mImageService.getImageThumbnail(imageId);
        }
        if (imageModel != null) {
            response.addHeader("Content-Type", imageModel.getContentType());
            response.getOutputStream().write(imageModel.getImageData());
            return null;
        } else {
            response.setStatus(404);
            response.getWriter().append("Not found. imageId=" + id);
            return null;
        }
    }

    @ActionPath("jsonList")
    public Navigation jsonList() throws IOException {
        Validators v = new Validators(request);
        v.add("lastCreatedAt", v.longType());
        if (!doValidate(v)) {
            return null;
        }
        
        String lastCreatedAtStr = param("lastCreatedAt");
        Date lastCreatedAt = (!StringUtil.isEmpty(lastCreatedAtStr)) ? new Date(Long.parseLong(lastCreatedAtStr)) : null;

        List<ImageSummaryModel> imageSummaryModels = mImageService.getImageSummaryModels(lastCreatedAt);
        
        response.setContentType(StameConstants.CONTENT_TYPE);
        
        String json = imageSummaryModelMeta.modelsToJson(imageSummaryModels);
        response.getWriter().append(json);
        
        return null;
    }

    @ActionPath("jsonpList")
    public Navigation jsonpList() throws IOException {
        Validators v = new Validators(request);
        v.add("func", v.required());
        v.add("lastCreatedAt", v.longType());
        if (!doValidate(v)) {
            return null;
        }
        
        String lastCreatedAtStr = param("lastCreatedAt");
        Date lastCreatedAt = (!StringUtil.isEmpty(lastCreatedAtStr)) ? new Date(Long.parseLong(lastCreatedAtStr)) : null;

        String func = param("func");
        String requestCode = param("requestCode");
        
        response.setContentType(StameConstants.CONTENT_TYPE);
        
        List<ImageSummaryModel> imageSummaryModels = mImageService.getImageSummaryModels(lastCreatedAt);
        String json = imageSummaryModelMeta.modelsToJson(imageSummaryModels);
        response.getWriter().append(func);
        response.getWriter().append('(');
        response.getWriter().append(json);
        response.getWriter().append(',');
        response.getWriter().append(String.valueOf(imageSummaryModels.size() == StameConstants.THUMBNAIL_NUM));
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

    private boolean doValidate(Validators v) throws IOException {
        if (v.validate()) {
            // OK
            return true;
        } else {
            // 入力値エラー
            Errors errors = v.getErrors();
            response.setStatus(400);
            PrintWriter writer = response.getWriter();
            for (String msg :errors.values()) {
                writer.write(msg);
                writer.write('\n');
            }
            return false;
        }
    }
}