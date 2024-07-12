package com.startingblue.fourtooncookie.diary.dto.request;

public record DiaryPageRequest(Integer pageNumber, Integer pageSize) {

    private static final int MIN_PAGE_NUMBER = 0;
    private static final int MAX_PAGE_NUMBER = 200;

    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 10;

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public DiaryPageRequest(Integer pageNumber, Integer pageSize) {
        this.pageNumber = defaultIfNullOrInvalid(pageNumber, DEFAULT_PAGE_NUMBER, MIN_PAGE_NUMBER, MAX_PAGE_NUMBER);
        this.pageSize = defaultIfNullOrInvalid(pageSize, DEFAULT_PAGE_SIZE, MIN_PAGE_SIZE, MAX_PAGE_SIZE);
        validate();
    }

    private void validate() {
        if (pageNumber < MIN_PAGE_NUMBER || pageNumber > MAX_PAGE_NUMBER) {
            throw new IllegalArgumentException("pageNumber number cannot be less than " + MIN_PAGE_NUMBER + " and " + MAX_PAGE_NUMBER);
        }
        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("pageSize must be between " + MIN_PAGE_SIZE + " and " + MAX_PAGE_SIZE);
        }
    }

    private static Integer defaultIfNullOrInvalid(Integer value, Integer defaultValue, Integer minValue, Integer maxValue) {
        if (value == null || value < minValue || value > maxValue) {
            return defaultValue;
        }
        return value;
    }
}
