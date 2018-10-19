package com.way2learnonline.repository;

import org.springframework.data.repository.CrudRepository;

import com.way2learnonline.model.Comment;

public interface CommentsRepository extends CrudRepository<Comment, Long> {

}
