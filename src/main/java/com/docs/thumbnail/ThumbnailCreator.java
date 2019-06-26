package com.docs.thumbnail;

public interface ThumbnailCreator {

	public static String imagePrefix = ".png";

	public abstract void generate(String filePath, String thumbnailPath);

}
