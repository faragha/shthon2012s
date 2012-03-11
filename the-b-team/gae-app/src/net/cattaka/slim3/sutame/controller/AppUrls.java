package net.cattaka.slim3.sutame.controller;

import scenic3.UrlsImpl;
import net.cattaka.slim3.sutame.controller.matcher.FrontPageMatcher;
import net.cattaka.slim3.sutame.controller.matcher.ManagePageMatcher;

public class AppUrls extends UrlsImpl {

    public AppUrls() {
        excludes("/_ah/*","/css/*", "/js/*", "/image/*");
        add(ManagePageMatcher.get());
        add(FrontPageMatcher.get());
        // TODO Add your own new PageMatcher

    }
}