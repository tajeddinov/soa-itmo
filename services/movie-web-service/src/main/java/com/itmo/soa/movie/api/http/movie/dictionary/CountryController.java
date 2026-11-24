package com.itmo.soa.movie.api.http.movie.dictionary;

import com.itmo.soa.contract.response.movie.DictionaryResponse;
import com.itmo.soa.contract.service.movie.MovieServiceRemote;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/movies/countries")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CountryController {

    @EJB
    MovieServiceRemote movieService;

    @GET
    public DictionaryResponse<String> search(@QueryParam("search") String search) {
        return new DictionaryResponse<>(movieService.searchCountries(search));
    }
}
