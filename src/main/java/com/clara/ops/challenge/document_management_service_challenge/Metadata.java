package com.clara.ops.challenge.document_management_service_challenge;

public class Metadata {
  private Integer currentPage;
  private Integer itemsPerPage;
  private Integer currentItems;
  private Integer totalPages;
  private Integer totalItems;

  public Metadata(
      Integer currentPage,
      Integer itemsPerPage,
      Integer currentItems,
      Integer totalPages,
      Integer totalItems) {
    this.currentPage = currentPage;
    this.itemsPerPage = itemsPerPage;
    this.currentItems = currentItems;
    this.totalPages = totalPages;
    this.totalItems = totalItems;
  }

  public Metadata() {}

  public Integer getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  public Integer getItemsPerPage() {
    return itemsPerPage;
  }

  public void setItemsPerPage(Integer itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  public Integer getCurrentItems() {
    return currentItems;
  }

  public void setCurrentItems(Integer currentItems) {
    this.currentItems = currentItems;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public Integer getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(Integer totalItems) {
    this.totalItems = totalItems;
  }
}
