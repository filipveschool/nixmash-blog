package com.nixmash.blog.solr;

import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.solr.model.PostDoc;
import com.nixmash.blog.solr.repository.custom.CustomPostDocRepository;
import com.nixmash.blog.solr.service.PostDocService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class SolrPostReindexTests extends SolrContext {

    private static final int INITIAL_POST_COUNT = 7;
    private static final int BOOTSTRAP_POST_COUNT = 1;
    private List<Post> posts;
    private int postCount = 0;

    @Resource
    CustomPostDocRepository postDocRepository;

    @Autowired
    private PostDocService postDocService;

    @Autowired
    PostService postService;

    @Autowired
    SolrOperations solrOperations;

    @Before
    public void cleanIndex() {
        Query query = new SimpleQuery(new SimpleStringCriteria("doctype:post"));
        solrOperations.delete(query);
        solrOperations.commit();
    }

    @After
    public void populateIndex() {
        posts = postService.getAllPublishedPosts();
        postCount = posts.size();
            postDocService.addAllToIndex(posts);
    }

    @Test
    public void cleanAndReindexPostDocuments_AddAsList() throws Exception {

        posts = postService.getAllPublishedPosts();
        postCount = posts.size();
        postDocService.addAllToIndex(posts);

        List<PostDoc> postDocs = postDocService.getAllPostDocuments();
        assertEquals(postDocs.size(), postCount);
    }

    @Test
    @Ignore(value = "It works, no need to wait for it to run each time")
    public void cleanAndReindexPostDocuments_AddIndividually() throws Exception {

        posts = postService.getAllPublishedPosts();
        postCount = posts.size();
        for (Post post : posts) {
            postDocService.addToIndex(post);
        }

        List<PostDoc> postDocs = postDocService.getAllPostDocuments();
        assertEquals(postDocs.size(), postCount);

        postDocs = postDocService.getPostsWithUserQuery("bootstrap");
        assertEquals(BOOTSTRAP_POST_COUNT, postDocs.size());

        Query query = new SimpleQuery(new SimpleStringCriteria("doctype:post"));
        solrOperations.delete(query);
        solrOperations.commit();
    }

}
