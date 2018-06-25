package com.nixmash.blog.jpa.service.implementations;

import com.google.common.collect.Lists;
import com.nixmash.blog.jpa.annotations.CachePostUpdate;
import com.nixmash.blog.jpa.common.ApplicationSettings;
import com.nixmash.blog.jpa.dto.AlphabetDTO;
import com.nixmash.blog.jpa.dto.CategoryDTO;
import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.enums.ContentType;
import com.nixmash.blog.jpa.enums.PostDisplayType;
import com.nixmash.blog.jpa.enums.PostType;
import com.nixmash.blog.jpa.enums.TwitterCardType;
import com.nixmash.blog.jpa.exceptions.CategoryNotFoundException;
import com.nixmash.blog.jpa.exceptions.DuplicatePostNameException;
import com.nixmash.blog.jpa.exceptions.PostNotFoundException;
import com.nixmash.blog.jpa.exceptions.TagNotFoundException;
import com.nixmash.blog.jpa.model.Category;
import com.nixmash.blog.jpa.model.CurrentUser;
import com.nixmash.blog.jpa.model.Like;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.PostImage;
import com.nixmash.blog.jpa.model.PostMeta;
import com.nixmash.blog.jpa.model.Tag;
import com.nixmash.blog.jpa.repository.CategoryRepository;
import com.nixmash.blog.jpa.repository.LikeRepository;
import com.nixmash.blog.jpa.repository.PostImageRepository;
import com.nixmash.blog.jpa.repository.PostMetaRepository;
import com.nixmash.blog.jpa.repository.PostRepository;
import com.nixmash.blog.jpa.repository.TagRepository;
import com.nixmash.blog.jpa.service.interfaces.PostService;
import com.nixmash.blog.jpa.service.interfaces.TagService;
import com.nixmash.blog.jpa.utils.PostUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


@Slf4j
@Service("postService")
@Transactional
@CacheConfig(cacheNames = "posts")
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    @PersistenceContext
    private EntityManager em;

    // endregion

    //region Add / UpdatePost

    @Transactional(rollbackFor = DuplicatePostNameException.class)
    @Override
    @CachePostUpdate
    public Post add(PostDTO postDTO) throws DuplicatePostNameException {
        Post post;
        try {
            post = postRepository.save(PostUtils.postDtoToPost(postDTO));
            em.refresh(post);

        } catch (Exception e) {
            throw new DuplicatePostNameException("Duplicate Post Name for Post Title: " +
                    postDTO.getPostTitle());
        }

        if (postDTO.getTags() != null) {

            saveNewTagsToDataBase(postDTO);

            post.setTags(new HashSet<>());
            for (TagDTO tagDTO: postDTO.getTags()) {
                Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
                post.getTags().add(tag);
            }
        }

        Category category = categoryRepository.findByCategoryId(postDTO.getCategoryId());
        post.setCategory(category);

        return post;
    }

    @Transactional(rollbackFor = PostNotFoundException.class)
    @Override
    @CachePostUpdate
    public Post update(PostDTO postDTO) throws PostNotFoundException {

        Post post = postRepository.findByPostId(postDTO.getPostId());
        post.update(postDTO.getPostTitle(), postDTO.getPostContent(), postDTO.getIsPublished(), postDTO.getDisplayType());

        saveNewTagsToDataBase(postDTO);

        post.getTags().clear();
        for (TagDTO tagDTO: postDTO.getTags()) {
            Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());

            if (!post.getTags().contains(tag))
                post.getTags().add(tag);
        }

        Category category = categoryRepository.findByCategoryId(postDTO.getCategoryId());
        post.setCategory(category);

        return post;
    }

    //endregion





    //region Get Posts

    @Transactional(readOnly = true)
    @Override
    public Page<Post> getPosts(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest =
                new PageRequest(pageNumber, pageSize, sortByPostDateDesc());
        return postRepository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "pagedPosts",
            key = "#pageNumber.toString().concat('-').concat(#pageSize.toString())")
    public Page<Post> getPublishedPosts(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest =
                new PageRequest(pageNumber, pageSize, sortByPostDateDesc());
        return postRepository.findByIsPublishedTrue(pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll(sortByPostDateDesc());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAdminRecentPosts() {
        return postRepository.findFirst25ByOrderByPostDateDesc(sortByPostDateDesc());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAllPublishedPostsByPostType(PostType postType) {
        return postRepository.findAllPublishedByPostType(postType);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Post> getPagedPostsByPostType(PostType postType, int pageNumber, int pageSize) {
        PageRequest pageRequest =
                new PageRequest(pageNumber, pageSize, sortByPostDateDesc());
        return postRepository.findPublishedByPostTypePaged(postType, pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAllPublishedPosts() {
        return postRepository.findByIsPublishedTrue(sortByPostDateDesc());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Post> getOneMostRecent() {
        log.debug("Getting most recent post");
        Page<Post> posts = postRepository.findAll(new PageRequest(0, 1, sortByPostDateDesc()));
        if (posts.getContent().isEmpty()) {
            log.debug("No documents");
            return Optional.empty();
        } else {
            Post post = posts.getContent().get(0);
            log.trace("Returning {}", post);
            return Optional.of(post);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPostsWithDetail() {
        return postRepository.findAllWithDetail();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Post> getPublishedPostsByTagId(long tagId, int pageNumber, int pageSize) {
        PageRequest pageRequest =
                new PageRequest(pageNumber, pageSize, sortByPostDateDesc());
        return postRepository.findPagedPublishedByTagId(tagId, pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAllPostsByCategoryId(long categoryId) {
        return postRepository.findAllByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPublishedPostsByTagId(long tagId) {
        return postRepository.findAllPublishedByTagId(tagId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAllPostsByTagId(long tagId) {
        return postRepository.findAllByTagId(tagId);
    }

    //endregion


    // region Posts A-Z

    @Transactional(readOnly = true)
    @Override
    public List<AlphabetDTO> getAlphaLInks() {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        // Example: 12AGHJLM
        String activeAlphas = postRepository.getAlphaLinkString();

        List<AlphabetDTO> alphaLinks = new ArrayList<>();

        // Iterate over alphabet char Array, set AlphabetDTO.active if char in activeAlphas
        for (char c: alphabet)
            alphaLinks.add(new AlphabetDTO(String.valueOf(c), activeAlphas.indexOf(c) >= 0));

        // add AlphabetDTO record for "0-9", set active if any digits in activeAlphas String
        alphaLinks.add(new AlphabetDTO("0-9", activeAlphas.matches(".*\\d+.*")));

        // sort AlphabetDTO List, "0-9" followed by alphabet
        Collections.sort(alphaLinks, (o1, o2) ->
                o1.getAlphaCharacter().compareTo(o2.getAlphaCharacter()));

        // All AlphabetDTO items returned with true/false if contain links
        return alphaLinks;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> getAlphaPosts() {
        List<Post> posts = Lists.newArrayList(postRepository.findByIsPublishedTrue(sortByPostDateDesc()));

        // converting all posts to postDTO objects
        //
        // 1) post titles starting with a digit assigned "09" alphaKey
        // 2) postDTO list adds all post titles starting with letter, assigned title firstLetter as alphaKey
        // 3) for NixMash Spring Demo site, Changelists do not appear in A-Z listing

        List<PostDTO> postDTOs = posts
                .stream()
                .filter(p -> Character.isDigit(p.getPostTitle().charAt(0)))
                .map(PostDTO::buildAlphaNumericTitles)
                .sorted(byfirstLetter)
                .collect(Collectors.toList());

        postDTOs.addAll(
                posts
                        .stream()
                        .filter(p -> Character.isAlphabetic(p.getPostTitle().charAt(0)) && !p.getPostTitle().startsWith("Changelist"))
                        .map(PostDTO::buildAlphaTitles)
                        .sorted(byfirstLetter)
                        .collect(Collectors.toList()));

        return postDTOs;
    }

    private Comparator<PostDTO> byfirstLetter = (e1, e2) -> e1
            .getPostTitle().compareTo(e2.getPostTitle());

    // endregion



    //region Utility methods

    public Sort sortByPostDateDesc() {
        return new Sort(Sort.Direction.DESC, "postDate");
    }


    @Transactional
    private void saveNewTagsToDataBase(PostDTO postDTO) {
        for (TagDTO tagDTO: postDTO.getTags()) {
            Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
            if (tag == null) {
                tag = new Tag(tagDTO.getTagValue());
                tagRepository.save(tag);
            }
        }
    }
}
