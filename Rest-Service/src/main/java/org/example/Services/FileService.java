package org.example.Services;

import org.example.Entity.AppDocument;
import org.example.Entity.AppPhoto;
import org.example.Entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {

    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
