package com.example.localcuisine.util;

import com.example.localcuisine.data.entity.CommentEntity;

import java.util.ArrayList;
import java.util.List;

public class CommentNode {
    public CommentEntity comment;
    public List<CommentNode> replies = new ArrayList<>();
}
