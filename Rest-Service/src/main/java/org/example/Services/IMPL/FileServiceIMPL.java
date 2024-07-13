package org.example.Services.IMPL;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.example.DAO.AppDocumentDAO;
import org.example.DAO.AppPhotoDAO;
import org.example.Entity.AppDocument;
import org.example.Entity.AppPhoto;
import org.example.Entity.BinaryContent;
import org.example.Services.FileService;
import org.example.Utils.CryptoTool;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceIMPL implements FileService {

    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //TODO добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}