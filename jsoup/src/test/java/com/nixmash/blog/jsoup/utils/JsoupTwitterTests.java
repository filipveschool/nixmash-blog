package com.nixmash.blog.jsoup.utils;

import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jsoup.JsoupContext;
import com.nixmash.blog.jsoup.base.JsoupHtmlParser;
import com.nixmash.blog.jsoup.dto.TestDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class JsoupTwitterTests extends JsoupContext {

    private Document doc;
    private TestDTO testDTO;

    @Autowired
    private PostService postService;

    @Autowired
    @Qualifier("testDTOParser")
    JsoupHtmlParser<TestDTO> testDTOParser;

    @Before
    public void setup() throws IOException {
        File in = JsoupTestUtil.getFile("/html/testTwitter.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        testDTO = testDTOParser.parse(doc);
    }

    @Test
    public void extractTitleFromJsoupDemoHTML() {
        String title = doc.title();
        assertThat(title, is("This is my Twitter Test Page Title"));
    }

    @Test
    public void twitterDTOisNullWhenNoCardMetaTag() throws IOException {
        File in = JsoupTestUtil.getFile("/html/testdto.html");
        doc = Jsoup.parse(in, null, "http://example.com");
        testDTO = testDTOParser.parse(doc);
        assertNull(testDTO.getTwitterDTO());
    }

    @Test
    public void twitterDTOIsNotNullWithCardMetaTag() {
        assertNotNull(testDTO.getTwitterDTO());
    }

    @Test
    public void twitterCreaterIsNullWithNoCreaterMetaTag() {
        assertNotNull(testDTO.getTwitterDTO());
        assertNull(testDTO.getTwitterDTO().getTwitterCreator());
    }

}
