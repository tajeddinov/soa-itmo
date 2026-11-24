package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.contract.error.exception.InvalidDataException;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import com.itmo.soa.contract.response.movie.MoviePageResponse;
import com.itmo.soa.contract.response.movie.MovieResponse;
import com.itmo.soa.contract.service.movie.MovieServiceRemote;
import com.itmo.soa.movie.domain.movie.model.Color;
import com.itmo.soa.movie.domain.movie.model.Country;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.MovieRuleViolationException;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import com.itmo.soa.movie.domain.movie.service.actions.CreateMovieAction;
import com.itmo.soa.movie.domain.movie.service.actions.DeleteMovieAction;
import com.itmo.soa.movie.domain.movie.service.actions.DeleteMoviesByGenreAction;
import com.itmo.soa.movie.domain.movie.service.actions.PatchMovieAction;
import com.itmo.soa.movie.domain.movie.service.filters.GetMovieByIdFilter;
import com.itmo.soa.movie.domain.movie.service.filters.GetMoviesPageFilter;
import com.itmo.soa.movie.domain.movie.service.filters.SearchDictionaryFilter;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
@Remote(MovieServiceRemote.class)
public class MovieService implements MovieServiceRemote {

    @Inject
    CreateMovieAction createMovie;
    @Inject
    PatchMovieAction patchMovie;
    @Inject
    DeleteMovieAction deleteMovie;
    @Inject
    DeleteMoviesByGenreAction deleteMoviesByGenre;

    @Inject
    GetMovieByIdFilter getMovieById;
    @Inject
    GetMoviesPageFilter getMoviesPage;
    @Inject
    SearchDictionaryFilter searchDictionary;

    @Override
    public MoviePageResponse list(List<String> filters, List<String> sorts, String page, String size) {
        return MovieResponseMapper.toResponse(getMoviesPage.execute(filters, sorts, page, size));
    }

    @Override
    public MovieResponse create(CreateMovieRequest request) {
        try {
            return MovieResponseMapper.toResponse(createMovie.execute(request));
        } catch (MovieRuleViolationException e) {
            throw invalidBody(e);
        }
    }

    @Override
    public MovieResponse get(int id) {
        return MovieResponseMapper.toResponse(getMovieById.execute(id));
    }

    @Override
    public MovieResponse patch(int id, UpdateMovieRequest request) {
        try {
            return MovieResponseMapper.toResponse(patchMovie.execute(id, request));
        } catch (MovieRuleViolationException e) {
            throw invalidBody(e);
        }
    }

    @Override
    public void delete(int id) {
        deleteMovie.execute(id);
    }

    @Override
    public int deleteByGenre(String genre) {
        return deleteMoviesByGenre.execute(MovieRequestMapper.genreFromPath(genre));
    }

    @Override
    public List<String> searchCountries(String search) {
        return searchDictionary.execute(Country.class, search).stream().map(Enum::name).toList();
    }

    @Override
    public List<String> searchGenres(String search) {
        return searchDictionary.execute(MovieGenre.class, search).stream().map(Enum::name).toList();
    }

    @Override
    public List<String> searchMpaaRatings(String search) {
        return searchDictionary.execute(MpaaRating.class, search).stream().map(Enum::name).toList();
    }

    @Override
    public List<String> searchColors(String search) {
        return searchDictionary.execute(Color.class, search).stream().map(Enum::name).toList();
    }

    private static InvalidDataException invalidBody(MovieRuleViolationException e) {
        return new InvalidDataException("Некорректное тело запроса",
                List.of(FieldError.body(e.field(), e.rejectedValue(), e.getMessage())));
    }
}
