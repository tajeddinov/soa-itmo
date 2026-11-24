package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.infrastructure.storage.movie.entity.MovieEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class JpaMovieRepository {

    @PersistenceContext(unitName = "movies")
    private EntityManager em;

    public MovieEntity insert(MovieEntity entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    public Optional<MovieEntity> findById(Integer id) {
        return Optional.ofNullable(em.find(MovieEntity.class, id));
    }

    public MovieEntity update(MovieEntity entity) {
        return em.merge(entity);
    }

    public void delete(MovieEntity entity) {
        em.remove(entity);
    }

    public int deleteByGenre(MovieGenre genre) {
        return em.createQuery("delete from MovieEntity m where m.genre = :genre")
                .setParameter("genre", genre)
                .executeUpdate();
    }
}
