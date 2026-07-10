package com.school.school_management.dto.response;

import java.util.List;

public class PageResponse<T> {

   //the actual data (list of students/courses/etc.)
   private List<T> content;

   //current page number (starts at 0)
   private int pageNumber;

   //how man items per page
   private int pageSize;

   //total number of items in database
   private Long totalElements;

   //total number of pages
   private int totalPages;

   //is this the last page?
   private boolean lastPage;

   //is this the first page?
   private boolean firstPage;

   //constructor

   public PageResponse(
           List<T> content,
           int pageNumber,
           int pageSize,
           Long totalElements,
           int totalPages,
           boolean lastPage,
           boolean firstPage) {
      this.content = content;
      this.pageNumber = pageNumber;
      this.pageSize = pageSize;
      this.totalElements = totalElements;
      this.totalPages = totalPages;
      this.lastPage = lastPage;
      this.firstPage = firstPage;
   }

   //getters and setter

   public List<T> getContent() {
      return content;
   }

   public void setContent(List<T> content) {
      this.content = content;
   }

   public int getPageNumber() {
      return pageNumber;
   }

   public void setPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
   }

   public int getPageSize() {
      return pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public Long getTotalElements() {
      return totalElements;
   }

   public void setTotalElements(Long totalElements) {
      this.totalElements = totalElements;
   }

   public int getTotalPages() {
      return totalPages;
   }

   public void setTotalPages(int totalPages) {
      this.totalPages = totalPages;
   }

   public boolean isLastPage() {
      return lastPage;
   }

   public void setLastPage(boolean lastPage) {
      this.lastPage = lastPage;
   }

   public boolean isFirstPage() {
      return firstPage;
   }

   public void setFirstPage(boolean firstPage) {
      this.firstPage = firstPage;
   }
}
