package com.example.localcuisine.util;

import com.example.localcuisine.data.entity.CommentEntity;
import com.example.localcuisine.ui.comment.CommentItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentTreeBuilder {
    public static List<CommentNode> buildTree(List<CommentEntity> comments) {
        Map<Integer, CommentNode> map = new HashMap<>();
        List<CommentNode> roots = new ArrayList<>();

        for (CommentEntity c : comments) {
            CommentNode node = new CommentNode();
            node.comment = c;
            map.put(c.id, node);
        }

        for (CommentNode node : map.values()) {
            if (node.comment.parentCommentId == null) {
                roots.add(node);
            } else {
                CommentNode parent = map.get(node.comment.parentCommentId);
                if (parent != null) {
                    parent.replies.add(node);
                }
            }
        }
        return roots;
    }

    public static void flatten(
            CommentNode node,
            int level,
            List<CommentItem> out
    ) {
        out.add(new CommentItem(node.comment, level));
        for (CommentNode child : node.replies) {
            flatten(child, level + 1, out);
        }
    }

}
