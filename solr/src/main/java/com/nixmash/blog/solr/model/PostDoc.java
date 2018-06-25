/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nixmash.blog.solr.model;

import com.nixmash.blog.jpa.model.Tag;
import com.nixmash.blog.solr.enums.SolrDocType;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.jsoup.Jsoup;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@SolrDocument(solrCoreName = "nixmash")
public class PostDoc implements Serializable, IPostDoc {

    private static final long serialVersionUID = -6567393464918733512L;

    // region Properties

    @Field(ID)
    private String postId;

    @Field(POST_TITLE)
    private String postTitle;

    @Field(POST_AUTHOR)
    private String postAuthor;

    @Field(POST_NAME)
    private String postName;

    @Field(POST_LINK)
    private String postLink;

    @Field(POST_DATE)
    private Date postDate;

    @Field(RANGE_DATE)
    private String rangeDate;

    @Field(POST_TYPE)
    private String postType;

    @Field(HTML)
    private String postHTML;

    @Field(POST_TEXT)
    private String postText;

    @Field(POST_SOURCE)
    private String postSource;

    @Field(TAG)
    private List<String> tags;

    @Field(DOCTYPE)
    private String docType;


    // endregion

    // region Constructors

    public PostDoc() {
    }

    // endregion


    // region toString

    @Override
    public String toString() {
        return "PostDoc{" +
                "postId='" + postId + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", postAuthor='" + postAuthor + '\'' +
                ", postName='" + postName + '\'' +
                ", postLink='" + postLink + '\'' +
                ", postDate=" + postDate +
                ", postType='" + postType + '\'' +
                ", postHTML='" + postHTML + '\'' +
                ", postText='" + postText + '\'' +
                ", postSource='" + postSource + '\'' +
                ", tags=" + tags +
                ", docType='" + docType + '\'' +
                '}';
    }

    // endregion

    // region Builders

    public static Builder getBuilder(Long postId, String postTitle, String postAuthor,
                                     String postName, String postLink, String postHTML,
                                     String postSource, String postType) {
        return new PostDoc.Builder(postId, postTitle, postAuthor, postName,
                postLink, postHTML, postSource, postType);
    }


    public static class Builder {
        private PostDoc built;

        public Builder(Long postId, String postTitle, String postAuthor, String postName,
                       String postLink, String postHTML, String postSource, String postType) {
            built = new PostDoc();
            built.postId = postId.toString();
            built.postTitle = postTitle;
            built.postAuthor = postAuthor;
            built.postName = postName;
            built.postLink = postLink;
            built.postHTML = postHTML;
            built.postText = Jsoup.parse(postHTML).text();
            built.postSource = postSource;
            built.postType = postType;
            built.docType = SolrDocType.POST;
        }

        public Builder tags(Set<Tag> tags) {
            List<String> tagsList = tags
                    .stream()
                    .map(Tag::getTagValue)
                    .collect(Collectors.toList());
            built.tags = tagsList;
            return this;
        }

        public Builder postDate(ZonedDateTime postDate) {
            built.postDate = Date.from(postDate.toInstant());
            return this;
        }

        public Builder rangeDate(ZonedDateTime postDate) {
            built.rangeDate = postDate.toInstant().toString();
            return this;
        }

        public PostDoc build() {
            return built;
        }
    }


    // endregion


}
