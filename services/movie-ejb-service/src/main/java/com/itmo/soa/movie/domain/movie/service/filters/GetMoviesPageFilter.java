package com.itmo.soa.movie.domain.movie.service.filters;

import com.itmo.soa.movie.domain.movie.model.Movie;
import com.itmo.soa.movie.infrastructure.storage.movie.JpaMovieQueryRepository;
import com.wdbt.query.model.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetMoviesPageFilter {

    @Inject
    JpaMovieQueryRepository movieQueryRepository;

    public Page<Movie> execute(List<String> filters, List<String> sorts, String page, String size) {
        return movieQueryRepository.findPage(filters, sorts, page, size);
    }
}
