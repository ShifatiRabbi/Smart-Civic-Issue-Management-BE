package com.sr.smart_civic_platform.user.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/*
 * Purpose:
 * Spring Data এর Page<T> object সরাসরি return না করে, একটা
 * clean/predictable shape এ client কে pagination info দেওয়া।
 *
 * Why not return Page<T> directly:
 * Spring Data এর Page এ অনেক internal/unnecessary field serialize হয়
 * (pageable, sort details ইত্যাদি) যেইটা client এর দরকার নেই এবং
 * internal implementation detail leak করে। এই DTO শুধু দরকারি অংশ রাখে।
 */
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public static <T> PagedResponse<T> fromPage(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
}