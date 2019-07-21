package com.docs.thumbnail;

import java.io.IOException;

import com.docs.thumbnail.ThumbnailGenerator.DocType;


public class App 
{	
    public static void main(String[] args) throws IOException
    {
    	DocLogger.init("doc_thumbnail");
    	ThumbnailGenerator.generate(DocType.TXT.value());
    }
}
