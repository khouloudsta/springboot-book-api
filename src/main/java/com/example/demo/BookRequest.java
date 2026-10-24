package com.example.demo;

import jakarta.validation.constraints.NotBlank;

    public class BookRequest {

        @NotBlank(message = "Title cannot be empty")
        private String title;

        private Long authorId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Long authorId) {
            this.authorId = authorId;
        }
    }
