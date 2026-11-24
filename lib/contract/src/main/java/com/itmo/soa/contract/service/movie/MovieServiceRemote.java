package com.itmo.soa.contract.service.movie;

import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import com.itmo.soa.contract.response.movie.MoviePageResponse;
import com.itmo.soa.contract.response.movie.MovieResponse;

import java.util.List;

public interface MovieServiceRemote {

    MoviePageResponse list(List<String> filters, List<String> sorts, String page, String size);

    MovieResponse create(CreateMovieRequest request);

    MovieResponse get(int id);

    MovieResponse patch(int id, UpdateMovieRequest request);

    void delete(int id);

    int deleteByGenre(String genre);

    List<String> searchCountries(String search);

    List<String> searchGenres(String search);

    List<String> searchMpaaRatings(String search);

    List<String> searchColors(String search);
}
