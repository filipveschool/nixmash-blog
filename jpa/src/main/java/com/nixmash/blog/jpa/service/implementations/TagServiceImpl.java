package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.dto.PostDTO;
import com.nixmash.blog.jpa.dto.TagDTO;
import com.nixmash.blog.jpa.exceptions.TagNotFoundException;
import com.nixmash.blog.jpa.model.Post;
import com.nixmash.blog.jpa.model.Tag;
import com.nixmash.blog.jpa.repository.TagRepository;
import com.nixmash.blog.jpa.service.interfaces.TagService;
import com.nixmash.blog.jpa.utils.PostUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @PersistenceContext
    private EntityManager em;

    // region Tags

    @Transactional(readOnly = true)
    @Override
    public Tag getTag(String tagValue) {
        Tag found = tagRepository.findByTagValueIgnoreCase(tagValue);
        if (found == null) {
            log.info("No tag found with id: {}", tagValue);
            throw new TagNotFoundException("No tag found with id: " + tagValue);
        }

        return found;
    }


    @Transactional
    @Override
    public Tag createTag(TagDTO tagDTO) {
        Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
        if (tag == null) {
            tag = new Tag(tagDTO.getTagValue());
            tagRepository.save(tag);
        }
        return tag;
    }

    @Transactional
    @Override
    public Tag updateTag(TagDTO tagDTO) {
        Tag tag = tagRepository.findOne(tagDTO.getTagId());
        tag.setTagValue(tagDTO.getTagValue());
        return tag;
    }

    @Transactional
    @Override
    public void deleteTag(TagDTO tagDTO, List<Post> posts) {
        if (posts != null) {
            Tag tag = tagRepository.findOne(tagDTO.getTagId());
            for (Post post: posts) {
                post.getTags().remove(tag);
            }
        }
        tagRepository.delete(tagDTO.getTagId());
    }

    @Transactional(readOnly = true)
    @Override
    public Set<TagDTO> getTagDTOs() {
        Set<Tag> tags = tagRepository.findAll();
        return PostUtils.tagsToTagDTOs(tags);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getTagValues() {
        Set<Tag> tags = tagRepository.findAll();
        return PostUtils.tagsToTagValues(tags);
    }


    @Transactional(readOnly = true)
    @Override
    public Set<TagDTO> getTagDTOs(Long postId) {
        return null;
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "tagCloud", key = "#root.methodName.concat('-').concat(#tagCount.toString())")
    public List<TagDTO> getTagCloud(int tagCount) {
        List<Tag> tagcloud = em.createNamedQuery("getTagCloud", Tag.class)
                .getResultList();
        int _tagCount = tagCount > 0 ? tagCount : Integer.MAX_VALUE;
        List<TagDTO> tagDTOs = tagcloud
                .stream()
                .filter(t -> t.getPosts().size() > 0)
                .limit(_tagCount)
                .map(TagDTO::new)
                .sorted(comparing(TagDTO::getTagValue))
                .collect(Collectors.toList());
        return tagDTOs;
    }

    // endregion




}
