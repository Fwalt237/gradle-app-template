package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.*;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.impl.AuthorService;
import com.mjc.school.service.impl.CommentService;
import com.mjc.school.service.impl.TagService;
import com.mjc.school.versioning.ApiVersion;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mjc.school.controller.RestApiConst.NEWS_API_ROOT_PATH;



@ApiVersion(1)
@RestController
@RequestMapping(value = NEWS_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class NewsController
    implements BaseController<CreateNewsDtoRequest, NewsDtoResponse, Long, ResourceSearchFilterRequestDTO, UpdateNewsDtoRequest> {

    private final BaseService<CreateNewsDtoRequest, NewsDtoResponse, Long, ResourceSearchFilterRequestDTO, UpdateNewsDtoRequest> newsService;
    private final TagService tagService;
    private final AuthorService authorService;
    private final CommentService commentService;
    private final NewsModelAssembler newsAssembler;
    private final AuthorModelAssembler authorAssembler;
    private final TagModelAssembler tagAssembler;
    private final CommentModelAssembler commentAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;


    @Autowired
    public NewsController(
         final BaseService<CreateNewsDtoRequest, NewsDtoResponse, Long, ResourceSearchFilterRequestDTO, UpdateNewsDtoRequest> newsService,
         final TagService tagService,
         final AuthorService authorService,
         final CommentService commentService,
         final NewsModelAssembler newsAssembler,
         final AuthorModelAssembler authorAssembler,
         final TagModelAssembler tagAssembler,
         final CommentModelAssembler commentAssembler,
         final PageModelAssembler pageAssembler,
         final LinkBuilderUtil linkBuilder) {
        this.newsService = newsService;
        this.tagService = tagService;
        this.authorService = authorService;
        this.commentService = commentService;
        this.newsAssembler = newsAssembler;
        this.authorAssembler = authorAssembler;
        this.tagAssembler = tagAssembler;
        this.commentAssembler = commentAssembler;
        this.pageAssembler = pageAssembler;
        this.linkBuilder = linkBuilder;
    }

    @ApiOperation(value = "View all news", response = PageDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @GetMapping
    public PageDtoResponse<NewsDtoResponse> readAll(
            final ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<NewsDtoResponse> page = newsService.readAll(searchRequest);

        page.getModelDtoList().forEach(newsAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(NewsController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }


    @ApiOperation(value = "Retrieve specific news with the supplied id", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the news with the supplied id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @GetMapping("/{id}")
    public NewsDtoResponse readById(
            @PathVariable Long id) {
        NewsDtoResponse news = newsService.readById(id);
        return newsAssembler.toModel(news);
    }



    @ApiOperation(value = "Create a piece of news", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a piece of news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public NewsDtoResponse create(
            @RequestBody CreateNewsDtoRequest dtoRequest) {
        NewsDtoResponse news = newsService.create(dtoRequest);
        return newsAssembler.toModel(news);
    }


    @ApiOperation(value = "Update a news", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public NewsDtoResponse update(
            @PathVariable Long id,
            @RequestBody UpdateNewsDtoRequest dtoRequest) {
        NewsDtoResponse news = newsService.update(id, dtoRequest);
        return newsAssembler.toModel(news);
    }


    @ApiOperation(value = "Deletes specific news with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific news"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @PathVariable Long id) {
        newsService.deleteById(id);
    }


    @ApiOperation(value = "Get tags for a news article", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Retrieves all tags associated with a specific news article"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/{id}/tags")
    public List<TagDtoResponse> readTagsByNewsId(
             @PathVariable Long id) {
        List<TagDtoResponse> tags = tagService.readByNewsId(id);
        tags.forEach(tagAssembler::toModel);
        return tags;
    }


    @ApiOperation(value = "Get author of a news article", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Retrieves the author information for a specific news article"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/{id}/author")
    public AuthorDtoResponse readAuthorByNewsId(@PathVariable Long id) {
        AuthorDtoResponse author = authorService.readByNewsId(id);
        return authorAssembler.toModel(author);
    }


    @ApiOperation(value = "Get comments for a news article", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Retrieves all comments associated with a specific news article"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @GetMapping("/{id}/comments")
    public List<CommentsDtoResponse> readCommentsByNewsId(@PathVariable Long id) {
        List<CommentsDtoResponse> comments = commentService.readByNewsId(id);
        comments.forEach(commentAssembler::toModel);
        return comments;
    }
}
