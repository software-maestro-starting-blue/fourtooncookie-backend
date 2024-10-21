package com.startingblue.fourtooncookie.artwork.domain;

import com.startingblue.fourtooncookie.translation.annotation.TranslatableClass;
import com.startingblue.fourtooncookie.translation.annotation.TranslatableField;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Objects;
import java.util.Set;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TranslatableClass(className = "Artwork")
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artwork_id")
    private Long id;

    @NotBlank(message = "작품명은 필수 입니다.")
    @Size(min = 1, max = 255, message = "작품명의 글자수는 1에서 255자 이내여야 합니다.")
    @TranslatableField
    private String title;

    @NotNull(message = "작품 썸네일 URL이 존재해야 합니다.")
    @Column(name = "thumbnail_url", nullable = false)
    private URL thumbnailUrl;

    public Artwork(final String title, final URL thumbnailUrl) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        validate();
    }

    public void update(final String title, final URL thumbnailUrl) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        validate();
    }

    private void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Artwork>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artwork artwork = (Artwork) o;
        return Objects.equals(id, artwork.id) && Objects.equals(title, artwork.title) && Objects.equals(thumbnailUrl, artwork.thumbnailUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, thumbnailUrl);
    }

}
