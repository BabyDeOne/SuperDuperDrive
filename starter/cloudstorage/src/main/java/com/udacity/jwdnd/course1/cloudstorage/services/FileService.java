package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper){
        this.fileMapper = fileMapper;
    }

    public void addFile(MultipartFile fileUpload, int userid) throws IOException{
        File file = new File();
        try{
            file.setContentType(fileUpload.getContentType());
            file.setFileData(fileUpload.getBytes());
            file.setFileName(fileUpload.getOriginalFilename());
            file.setFileSize(Long.toString(fileUpload.getSize()));
            file.setUserId(userid);
        }catch(IOException e){
            throw e;
        }
        fileMapper.storeFile(file);
    }

    public List<File> getUploadedFiles(Integer userId){
        return fileMapper.getAllFiles(userId);
    }

    public boolean isFileAvailable(String filename, Integer userid){
        File file = fileMapper.getFile(userid, filename);

        if(file != null){
            return false;
        }
        return true;
    }

    public int deleteFile(int fileId){
        return fileMapper.deleteFile(fileId);
    }

    public File getFileById(Integer fileId){
        return fileMapper.getFileById(fileId);
    }
}
