package com.startingblue.fourtooncookie.diary.dto.request;

public record DiaryPageRequest(Integer page, Integer size) {

    private static final int MIN_PAGE_NUMBER = 0;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    public DiaryPageRequest(Integer page, Integer size) {
        this.page = defaultIfNullOrInvalid(page, DEFAULT_PAGE, MIN_PAGE_NUMBER, Integer.MAX_VALUE);
        this.size = defaultIfNullOrInvalid(size, DEFAULT_SIZE, MIN_PAGE_SIZE, MAX_PAGE_SIZE);
        validate();
    }

    private void validate() {
        if (page < MIN_PAGE_NUMBER) {
            throw new IllegalArgumentException("Page number cannot be less than " + MIN_PAGE_NUMBER);
        }
        if (size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Size must be between " + MIN_PAGE_SIZE + " and " + MAX_PAGE_SIZE);
        }
    }

    private static Integer defaultIfNullOrInvalid(Integer value, int defaultValue, int minValue, int maxValue) {
        if (value == null || value < minValue || value > maxValue) {
            return defaultValue;
        }
        return value;
    }
}
