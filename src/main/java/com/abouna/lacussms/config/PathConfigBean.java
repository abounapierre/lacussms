package com.abouna.lacussms.config;

public class PathConfigBean {
    private String rootPath;

    public PathConfigBean(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public static PathConfigBean getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(PathConfigBean.class);
    }
}
