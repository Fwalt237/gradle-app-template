package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.assembler.AuthorModelAssembler;
import com.mjc.school.controller.assembler.LinkBuilderUtil;
import com.mjc.school.controller.assembler.PageModelAssembler;
import com.mjc.school.service.BaseService;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.PageDtoResponse;
import com.mjc.school.service.dto.ResourceSearchFilterRequestDTO;
import com.mjc.school.versioning.ApiVersion;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.mjc.school.controller.RestApiConst.AUTHOR_API_ROOT_PATH;


@ApiVersion(1)
@RestController
@RequestMapping(value = AUTHOR_API_ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorController
    implements BaseController<AuthorDtoRequest, AuthorDtoResponse, Long, ResourceSearchFilterRequestDTO, AuthorDtoRequest> {

    private final BaseService<AuthorDtoRequest, AuthorDtoResponse, Long, ResourceSearchFilterRequestDTO, AuthorDtoRequest> authorService;
    private final AuthorModelAssembler authorAssembler;
    private final PageModelAssembler pageAssembler;
    private final LinkBuilderUtil linkBuilder;

    @Autowired
    public AuthorController(
        final BaseService<AuthorDtoRequest, AuthorDtoResponse, Long, ResourceSearchFilterRequestDTO, AuthorDtoRequest> authorService
    ,final AuthorModelAssembler authorAssembler,final PageModelAssembler pageAssembler,final LinkBuilderUtil linkBuilder) {
        this.authorService = authorService;
        this.authorAssembler=authorAssembler;
        this.pageAssembler=pageAssembler;
        this.linkBuilder = linkBuilder;
    }

    @ApiOperation(value = "View all author", response = PageDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all authors"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @GetMapping
    public PageDtoResponse<AuthorDtoResponse> readAll(
            final ResourceSearchFilterRequestDTO searchRequest) {
        PageDtoResponse<AuthorDtoResponse> page = authorService.readAll(searchRequest);

        page.getModelDtoList().forEach(authorAssembler::toModel);

        String baseUrl = linkBuilder.buildCollectionLink(AuthorController.class);
        return pageAssembler.addPaginationLinks(page,searchRequest,baseUrl);
    }

    @ApiOperation(value = "Retrieve specific author with the supplied id", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the author with the supplied id"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @ApiVersion(2)
    @GetMapping("/{id}")
    public AuthorDtoResponse readById(@PathVariable Long id) {
        AuthorDtoResponse author = authorService.readById(id);
        return authorAssembler.toModel(author);
    }


    @ApiOperation(value = "Create a piece of author", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created an author"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDtoResponse create(@RequestBody AuthorDtoRequest dtoRequest) {
        AuthorDtoResponse author =  authorService.create(dtoRequest);
        return authorAssembler.toModel(author);
    }


    @ApiOperation(value = "Update an author", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated author"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    }
    )
    @Override
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuthorDtoResponse update(@PathVariable Long id,
            @RequestBody AuthorDtoRequest dtoRequest) {
        AuthorDtoResponse author =  authorService.update(id, dtoRequest);
        return authorAssembler.toModel(author);
    }


    @ApiOperation(value = "Deletes specific author with the supplied id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deletes the specific author"),
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
        authorService.deleteById(id);
    }
}
