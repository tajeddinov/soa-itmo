package com.itmo.soa.contract.request.movie;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequest implements Serializable {

    @Null(message = "Поле id нельзя изменить")
    private Object id;

    @Null(message = "Поле creationDate нельзя изменить")
    private Object creationDate;

    @Pattern(regexp = ".*\\S.*", message = "Название не может быть пустым")
    private String name;

    @Valid
    private CoordinatesDto coordinates;

    @Positive(message = "Значение должно быть больше 0")
    private Integer oscarsCount;

    private Optional<@Pattern(
            regexp = "ACTION|DRAMA|COMEDY|MUSICAL|HORROR",
            message = "Допустимые значения: ACTION, DRAMA, COMEDY, MUSICAL, HORROR"
    ) String> genre;

    @Pattern(
            regexp = "G|PG|PG_13|R|NC_17",
            message = "Допустимые значения: G, PG, PG_13, R, NC_17"
    )
    private String mpaaRating;

    private Optional<@Valid PersonDto> screenwriter;

    @AssertTrue(message = "Нужно передать хотя бы одно поле")
    public boolean isNotEmpty() {
        return Stream.of(id, creationDate, name, coordinates, oscarsCount, genre, mpaaRating, screenwriter).anyMatch(v -> v != null);
    }

    // Optional не сериализуется, а PATCH различает пропущенное поле и явный null.
    private Object writeReplace() {
        return new SerializedPatch(id, creationDate, name, coordinates, oscarsCount,
                genre != null, genre == null ? null : genre.orElse(null), mpaaRating,
                screenwriter != null, screenwriter == null ? null : screenwriter.orElse(null));
    }

    @AllArgsConstructor
    private static final class SerializedPatch implements Serializable {

        private final Object id;
        private final Object creationDate;
        private final String name;
        private final CoordinatesDto coordinates;
        private final Integer oscarsCount;
        private final boolean genrePresent;
        private final String genre;
        private final String mpaaRating;
        private final boolean screenwriterPresent;
        private final PersonDto screenwriter;

        private Object readResolve() {
            return new UpdateMovieRequest(id, creationDate, name, coordinates, oscarsCount,
                    genrePresent ? Optional.ofNullable(genre) : null, mpaaRating,
                    screenwriterPresent ? Optional.ofNullable(screenwriter) : null);
        }
    }
}
