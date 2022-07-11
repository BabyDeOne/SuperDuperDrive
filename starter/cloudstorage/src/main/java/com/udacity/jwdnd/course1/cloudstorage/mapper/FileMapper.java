package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    @Select("SELECT * FROM FILES WHERE fileName = #{fileName}")
    File getFileByName(String fileName);

    @Select("SELECT * FROM FILES WHERE fileId = #{fileId}")
    File getFileById(Integer fileId);

    @Insert("INSERT INTO FILES (fileName, contentType, fileSize, userId) VALUES(#{fileName}, #{contentType}, #{fileSize}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int storeFile(File file);

    @Select("SELECT * FROM FILES WHERE userId = #{userId}")
    List<File> getAllFiles(Integer userId);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
    int deleteFile(int fileId);

    @Select("SELECT * FROM FILES WHERE userId = #{userId} AND fileName = #{fileName}")
    File getFile(Integer userId, String filename);
}
