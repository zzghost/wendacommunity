package com.example.wenda.service;

import com.example.wenda.dao.CommentDAO;
import com.example.wenda.model.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
    }
    public List<Comment> selectCommentByEntity(int entityId, int entityType){
        return commentDAO.selectCommentByEntity(entityId, entityType);
    }
    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId, entityType);
    }
    public boolean updateStatus(int id, int status){
        return commentDAO.updateStatus(id, status);
    }

    public Comment getCommentById(int id){
        return commentDAO.getCommentById(id);
    }

    public int getUserCommentCount(int userId){
        return commentDAO.getUserCommentCount(userId);
    }
}
