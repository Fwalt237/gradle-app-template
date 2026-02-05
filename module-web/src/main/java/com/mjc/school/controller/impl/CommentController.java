package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.CommentModelAssembler;
import com.mjc.school.controller.assembler.LinkBuilderUtil;
import com.mjc.school.controller.assembler.PageModelAssembler;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.*;
import com.mjc.school.versioning.ApiVersion;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.mjc.school.controller.RestApiConst.COMMENTS_API_ROOT_PATH;


@ApiVersion(1)
@RestController
@RequestMapping(value = COMMENTS_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController
    implements BaseController<CommentsDtoRequest, CommentsDtoResponse, Long, ResourceSearchFilterRequestDTO, CommentsDtoRequest> {

    private final BaseService<CommentsDtoRequest, CommentsDtoResponse, Long, ResourceSearchFilterRequestDTO, CommentsDtoRequest> commentsService;
    private final CommentModelAssembler commentAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;

    @Autowired
    public CommentController(
            final BaseService<CommentsDtoRequest, CommentsDtoResponse, Long, ResourceSearchFilterRequestDTO, CommentsDtoRequest> commentsService,
            final CommentModelAssembler commentAssembler,
            final PageModelAssembler pageAssembler,
            final LinkBuilderUtil linkBuilder) {
        this.commentsService = commentsService;
        this.commentAssembler = commentAssembler;
        this.pageAssembler = pageAssembler;
        this.linkBuilder = linkBuilder;
    }



    @ApiOperation(value = "View all comments", response = PageDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all comments"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @GetMapping
    public PageDtoResponse<CommentsDtoResponse> readAll(
            final ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<CommentsDtoResponse> page = commentsService.readAll(searchRequest);

        page.getModelDtoList().forEach(commentAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(CommentController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }


    @ApiOperation(value = "Retrieve specific comment with the supplied id", response = CommentsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the comment with the supplied id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @ApiVersion(2)
    @GetMapping("/{id}")
    public CommentsDtoResponse readById(@PathVariable Long id) {
        CommentsDtoResponse comment = commentsService.readById(id);
        return commentAssembler.toModel(comment);
    }


    @ApiOperation(value = "Create a comment", response = CommentsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a comment"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentsDtoResponse create(@RequestBody CommentsDtoRequest dtoRequest) {
        CommentsDtoResponse comment = commentsService.create(dtoRequest);
        return commentAssembler.toModel(comment);
    }


    @ApiOperation(value = "Update a comment", response = CommentsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a comment"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommentsDtoResponse update(@PathVariable Long id,
            @RequestBody CommentsDtoRequest dtoRequest) {
        CommentsDtoResponse comment = commentsService.update(id, dtoRequest);
        return commentAssembler.toModel(comment);
    }



    @ApiOperation(value = "Deletes specific comment with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific comment"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        commentsService.deleteById(id);
    }
}
