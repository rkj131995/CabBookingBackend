package com.way2learnonline.dto;

import com.way2learnonline.model.Comment;


public class CommentsDTO {
	
	Long id;
	String author;
	String text;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
	public static CommentsDTO createCommentDTO(Comment comment){
		CommentsDTO commentsDTO= new CommentsDTO();
		commentsDTO.setId(comment.getCommentId());
		commentsDTO.setAuthor(comment.getAuthor());
		commentsDTO.setText(comment.getText());
		return commentsDTO;
	}
	public Comment createNewComent() {
		Comment comment= new Comment();
		comment.setAuthor(author);
//		comment.setCommentId(id);
		comment.setText(text);
		return comment;
	}
	


}
