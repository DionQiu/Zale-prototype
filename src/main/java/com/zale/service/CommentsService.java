package com.zale.service;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.core.OrderBy;
import com.blade.jdbc.page.Page;
import com.blade.kit.BladeKit;
import com.blade.kit.DateKit;
import com.vdurmont.emoji.EmojiParser;
import com.zale.exception.TipException;
import com.zale.init.TaleConst;
import com.zale.model.dto.Comment;
import com.zale.model.entity.Comments;
import com.zale.model.entity.Contents;
import com.zale.utils.FilterBadWord;
import com.zale.utils.TaleUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论Service
 *
 * @author qyw
 * @since 1.3.1
 */
@Bean
public class CommentsService {

    @Inject
    private ContentsService contentsService;

    /**
     * 保存评论
     *
     * @param comments
     */
    public void saveComment(Comments comments) {

        Contents contents = new Contents().where("cid", comments.getCid()).find();
        if (null == contents) {
            throw new TipException("不存在的文章");
        }
        try {
            comments.setOwner_id(contents.getAuthorId());
            comments.setCreated(DateKit.nowUnix());
            comments.setParent(comments.getCoid());
            comments.setCoid(null);
            comments.save();

            Contents temp = new Contents();
            temp.setCommentsNum(contents.getCommentsNum() + 1);
            temp.update(contents.getCid());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 删除评论
     *
     * @param coid
     * @param cid
     * @throws Exception
     */
    public void delete(Integer coid, Integer cid) {
        new Comments().delete(coid);
        Contents contents = new Contents().find(cid);
        if (null != contents && contents.getCommentsNum() > 0) {
            Contents temp = new Contents();
            temp.setCommentsNum(contents.getCommentsNum() - 1);
            temp.update(cid);
        }
    }

    /**
     * 获取文章下的评论
     *
     * @param cid
     * @param page
     * @param limit
     * @return
     */
    public Page<Comment> getComments(Integer cid, int page, int limit) {
        if (null != cid) {
            Page<Comments> cp = new Comments().where("cid", cid).and("parent", 0).page(page, limit, "coid desc");
            return cp.map(parent -> {
                Comment        comment  = new Comment(parent);
                List<Comments> children = new ArrayList<>();
                getChildren(children, comment.getCoid());
                comment.setChildren(children);
                if (BladeKit.isNotEmpty(children)) {
                    comment.setLevels(1);
                }
                return comment;
            });
        }
        return null;
    }

    /**
     * 获取该评论下的追加评论
     *
     * @param coid
     * @return
     */
    private void getChildren(List<Comments> list, Integer coid) {
        List<Comments> cms = new Comments().where("parent", coid).findAll(OrderBy.asc("coid"));
        if (null != cms) {
            list.addAll(cms);
            cms.forEach(c -> getChildren(list, c.getCoid()));
        }
    }

    /**
     * 根据主键查询评论
     *
     * @param coid
     * @return
     */
    public Comments byId(Integer coid) {
        if (null != coid) {
            return new Comments().find(coid);
        }
        return null;
    }

    /**
     * 对评论内容进行处理,过滤xss与敏感词汇,emoji替换
     *
     * @param comments
     * @return
     */

    public Comments filterComments(Comments comments){
        String authorXssed = TaleUtils.cleanXSS(comments.getAuthor());
        String contentXssed = TaleUtils.cleanXSS(comments.getContent());
        comments.setAuthor(EmojiParser.parseToAliases(FilterBadWord.replace(authorXssed, TaleConst.SENSITIVE_REPLACE_CHAR)));
        comments.setContent(EmojiParser.parseToAliases(FilterBadWord.replace(contentXssed,TaleConst.SENSITIVE_REPLACE_CHAR)));
        return comments;
    }

    /*public static void main(String[] args) {
        System.out.println(TaleUtils.cleanXSS("<script>alert(1)</script>"));
    }*/

}
