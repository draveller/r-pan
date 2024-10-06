package com.imooc.pan.server.modules.file.converter;

import com.imooc.pan.server.modules.file.context.CreateFolderContext;
import com.imooc.pan.server.modules.file.context.DeleteFileContext;
import com.imooc.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.imooc.pan.server.modules.file.context.FileChunkMergeContext;
import com.imooc.pan.server.modules.file.context.FileChunkSaveContext;
import com.imooc.pan.server.modules.file.context.FileChunkUploadContext;
import com.imooc.pan.server.modules.file.context.FileSaveContext;
import com.imooc.pan.server.modules.file.context.FileUploadContext;
import com.imooc.pan.server.modules.file.context.QueryUploadedChunksContext;
import com.imooc.pan.server.modules.file.context.SecUploadFileContext;
import com.imooc.pan.server.modules.file.context.UpdateFilenameContext;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.po.CreateFolderPO;
import com.imooc.pan.server.modules.file.po.DeleteFilePO;
import com.imooc.pan.server.modules.file.po.FileChunkMergePO;
import com.imooc.pan.server.modules.file.po.FileChunkUploadPO;
import com.imooc.pan.server.modules.file.po.FileUploadPO;
import com.imooc.pan.server.modules.file.po.QueryUploadedChunksPO;
import com.imooc.pan.server.modules.file.po.SecUploadPO;
import com.imooc.pan.server.modules.file.po.UpdateFilenamePO;
import com.imooc.pan.server.modules.file.vo.FolderTreeNodeVO;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;
import com.imooc.pan.storage.engine.core.context.StoreFileChunkContext;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-06T22:02:30+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 1.8.0_151 (Oracle Corporation)"
)
@Component
public class FileConverterImpl implements FileConverter {

    @Override
    public CreateFolderContext convertPO2Context(CreateFolderPO createFolderPO) {
        if ( createFolderPO == null ) {
            return null;
        }

        CreateFolderContext createFolderContext = new CreateFolderContext();

        createFolderContext.setFolderName( createFolderPO.getFolderName() );

        createFolderContext.setParentId( com.imooc.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()) );
        createFolderContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return createFolderContext;
    }

    @Override
    public UpdateFilenameContext convertPO2Context(UpdateFilenamePO po) {
        if ( po == null ) {
            return null;
        }

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();

        updateFilenameContext.setNewFilename( po.getNewFilename() );

        updateFilenameContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );
        updateFilenameContext.setFileId( com.imooc.pan.core.utils.IdUtil.decrypt(po.getFileId()) );

        return updateFilenameContext;
    }

    @Override
    public DeleteFileContext convertPO2Context(DeleteFilePO deleteFilePO) {
        if ( deleteFilePO == null ) {
            return null;
        }

        DeleteFileContext deleteFileContext = new DeleteFileContext();

        deleteFileContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return deleteFileContext;
    }

    @Override
    public SecUploadFileContext convertPO2Context(SecUploadPO secUploadPO) {
        if ( secUploadPO == null ) {
            return null;
        }

        SecUploadFileContext secUploadFileContext = new SecUploadFileContext();

        secUploadFileContext.setFilename( secUploadPO.getFilename() );
        secUploadFileContext.setIdentifier( secUploadPO.getIdentifier() );

        secUploadFileContext.setParentId( com.imooc.pan.core.utils.IdUtil.decrypt(secUploadPO.getParentId()) );
        secUploadFileContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return secUploadFileContext;
    }

    @Override
    public FileUploadContext convertPO2Context(FileUploadPO fileUploadPO) {
        if ( fileUploadPO == null ) {
            return null;
        }

        FileUploadContext fileUploadContext = new FileUploadContext();

        fileUploadContext.setFilename( fileUploadPO.getFilename() );
        fileUploadContext.setIdentifier( fileUploadPO.getIdentifier() );
        fileUploadContext.setTotalSize( fileUploadPO.getTotalSize() );
        fileUploadContext.setFile( fileUploadPO.getFile() );

        fileUploadContext.setParentId( com.imooc.pan.core.utils.IdUtil.decrypt(fileUploadPO.getParentId()) );
        fileUploadContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return fileUploadContext;
    }

    @Override
    public FileSaveContext fileUploadContext2FileSaveContext(FileUploadContext context) {
        if ( context == null ) {
            return null;
        }

        FileSaveContext fileSaveContext = new FileSaveContext();

        fileSaveContext.setFilename( context.getFilename() );
        fileSaveContext.setIdentifier( context.getIdentifier() );
        fileSaveContext.setTotalSize( context.getTotalSize() );
        fileSaveContext.setFile( context.getFile() );
        fileSaveContext.setUserId( context.getUserId() );

        return fileSaveContext;
    }

    @Override
    public FileChunkUploadContext convertPO2Context(FileChunkUploadPO fileChunkUploadPO) {
        if ( fileChunkUploadPO == null ) {
            return null;
        }

        FileChunkUploadContext fileChunkUploadContext = new FileChunkUploadContext();

        fileChunkUploadContext.setFilename( fileChunkUploadPO.getFilename() );
        fileChunkUploadContext.setIdentifier( fileChunkUploadPO.getIdentifier() );
        fileChunkUploadContext.setTotalChunks( fileChunkUploadPO.getTotalChunks() );
        fileChunkUploadContext.setChunkNumber( fileChunkUploadPO.getChunkNumber() );
        fileChunkUploadContext.setCurrentChunkSize( fileChunkUploadPO.getCurrentChunkSize() );
        fileChunkUploadContext.setTotalSize( fileChunkUploadPO.getTotalSize() );
        fileChunkUploadContext.setFile( fileChunkUploadPO.getFile() );

        fileChunkUploadContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return fileChunkUploadContext;
    }

    @Override
    public FileChunkSaveContext fileChunkUploadContext2FileChunkSaveContext(FileChunkUploadContext context) {
        if ( context == null ) {
            return null;
        }

        FileChunkSaveContext fileChunkSaveContext = new FileChunkSaveContext();

        fileChunkSaveContext.setFilename( context.getFilename() );
        fileChunkSaveContext.setIdentifier( context.getIdentifier() );
        fileChunkSaveContext.setTotalChunks( context.getTotalChunks() );
        fileChunkSaveContext.setChunkNumber( context.getChunkNumber() );
        fileChunkSaveContext.setCurrentChunkSize( context.getCurrentChunkSize() );
        fileChunkSaveContext.setTotalSize( context.getTotalSize() );
        fileChunkSaveContext.setFile( context.getFile() );
        fileChunkSaveContext.setUserId( context.getUserId() );

        return fileChunkSaveContext;
    }

    @Override
    public StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context) {
        if ( context == null ) {
            return null;
        }

        StoreFileChunkContext storeFileChunkContext = new StoreFileChunkContext();

        storeFileChunkContext.setFilename( context.getFilename() );
        storeFileChunkContext.setIdentifier( context.getIdentifier() );
        storeFileChunkContext.setTotalSize( context.getTotalSize() );
        storeFileChunkContext.setTotalChunks( context.getTotalChunks() );
        storeFileChunkContext.setChunkNumber( context.getChunkNumber() );
        storeFileChunkContext.setCurrentChunkSize( context.getCurrentChunkSize() );
        storeFileChunkContext.setUserId( context.getUserId() );

        return storeFileChunkContext;
    }

    @Override
    public QueryUploadedChunksContext convertPO2Context(QueryUploadedChunksPO queryUploadedChunksPO) {
        if ( queryUploadedChunksPO == null ) {
            return null;
        }

        QueryUploadedChunksContext queryUploadedChunksContext = new QueryUploadedChunksContext();

        queryUploadedChunksContext.setIdentifier( queryUploadedChunksPO.getIdentifier() );

        queryUploadedChunksContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return queryUploadedChunksContext;
    }

    @Override
    public FileChunkMergeContext convertPO2Context(FileChunkMergePO fileChunkMergePO) {
        if ( fileChunkMergePO == null ) {
            return null;
        }

        FileChunkMergeContext fileChunkMergeContext = new FileChunkMergeContext();

        fileChunkMergeContext.setFilename( fileChunkMergePO.getFilename() );
        fileChunkMergeContext.setIdentifier( fileChunkMergePO.getIdentifier() );
        fileChunkMergeContext.setTotalSize( fileChunkMergePO.getTotalSize() );
        if ( fileChunkMergePO.getParentId() != null ) {
            fileChunkMergeContext.setParentId( Long.parseLong( fileChunkMergePO.getParentId() ) );
        }

        fileChunkMergeContext.setUserId( com.imooc.pan.server.common.utils.UserIdUtil.get() );

        return fileChunkMergeContext;
    }

    @Override
    public FileChunkMergeAndSaveContext fileChunkMergeContext2FileChunkMergeAndSaveContext(FileChunkMergeContext context) {
        if ( context == null ) {
            return null;
        }

        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = new FileChunkMergeAndSaveContext();

        fileChunkMergeAndSaveContext.setFilename( context.getFilename() );
        fileChunkMergeAndSaveContext.setIdentifier( context.getIdentifier() );
        fileChunkMergeAndSaveContext.setTotalSize( context.getTotalSize() );
        fileChunkMergeAndSaveContext.setParentId( context.getParentId() );
        fileChunkMergeAndSaveContext.setUserId( context.getUserId() );
        fileChunkMergeAndSaveContext.setRecord( context.getRecord() );

        return fileChunkMergeAndSaveContext;
    }

    @Override
    public FolderTreeNodeVO rPanUserFile2FolderTreeNodeVO(RPanUserFile rPanUserFile) {
        if ( rPanUserFile == null ) {
            return null;
        }

        FolderTreeNodeVO folderTreeNodeVO = new FolderTreeNodeVO();

        folderTreeNodeVO.setLabel( rPanUserFile.getFilename() );
        folderTreeNodeVO.setId( rPanUserFile.getFileId() );
        folderTreeNodeVO.setParentId( rPanUserFile.getParentId() );

        folderTreeNodeVO.setChildren( new java.util.ArrayList() );

        return folderTreeNodeVO;
    }

    @Override
    public RPanUserFileVO rPanUserFile2RPanUserFileVO(RPanUserFile record) {
        if ( record == null ) {
            return null;
        }

        RPanUserFileVO rPanUserFileVO = new RPanUserFileVO();

        rPanUserFileVO.setFileId( record.getFileId() );
        rPanUserFileVO.setParentId( record.getParentId() );
        rPanUserFileVO.setFilename( record.getFilename() );
        rPanUserFileVO.setFileSizeDesc( record.getFileSizeDesc() );
        rPanUserFileVO.setFolderFlag( record.getFolderFlag() );
        rPanUserFileVO.setFileType( record.getFileType() );
        rPanUserFileVO.setUpdateTime( record.getUpdateTime() );

        return rPanUserFileVO;
    }
}
