package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.LinkBuilderUtil;
import com.mjc.school.controller.assembler.PageModelAssembler;
import com.mjc.school.controller.assembler.TagModelAssembler;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.*;
import com.mjc.school.versioning.ApiVersion;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.mjc.school.controller.RestApiConst.TAG_API_ROOT_PATH;


@ApiVersion(1)
@RestController
@RequestMapping(value = TAG_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController
    implements BaseController<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> {

    private final BaseService<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> tagService;
    private final TagModelAssembler tagAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;

    @Autowired
    public TagController(final BaseService<TagDtoRequest, TagDtoResponse, Long, ResourceSearchFilterRequestDTO, TagDtoRequest> tagService,
                         final TagModelAssembler tagAssembler,
                         final PageModelAssembler pageAssembler,
                         final LinkBuilderUtil linkBuilder) {
        this.tagService = tagService;
        this.tagAssembler = tagAssembler;
        this.pageAssembler = pageAssembler;
        this.linkBuilder = linkBuilder;
    }


    @ApiOperation(value = "View all tags", response = PageDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all tags"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PageDtoResponse<TagDtoResponse> readAll(ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<TagDtoResponse> page = tagService.readAll(searchRequest);

        page.getModelDtoList().forEach(tagAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(TagController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }

    @ApiOperation(value = "Retrieve specific tag with the supplied id", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the tag with the supplied id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @ApiVersion(2)
    @GetMapping("/{id}")
    public TagDtoResponse readById(@PathVariable Long id) {
        TagDtoResponse tag = tagService.readById(id);
        return tagAssembler.toModel(tag);
    }


    @ApiOperation(value = "Create a tag", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a tag"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TagDtoResponse create(@RequestBody TagDtoRequest dtoRequest) {
        TagDtoResponse tag = tagService.create(dtoRequest);
        return tagAssembler.toModel(tag);
    }



    @ApiOperation(value = "Update a tag", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a tag"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TagDtoResponse update(@PathVariable Long id,
            @RequestBody TagDtoRequest dtoRequest) {
        TagDtoResponse tag = tagService.update(id, dtoRequest);
        return tagAssembler.toModel(tag);
    }

    @ApiOperation(value = "Deletes specific tag with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the tag"),
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
         tagService.deleteById(id);
    }
}
