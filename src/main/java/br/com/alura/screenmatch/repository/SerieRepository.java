package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAutor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer totalTemporadas, Double avaliacao);

    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriePorTemporadaEPorAvaliacao(Integer totalTemporadas, Double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.epsodios e WHERE e.titulo ILIKE %:trechoEpsodio%")
    List<Episodio> buscarEpsodiosPorTrecho(String trechoEpsodio);

    @Query("SELECT e FROM Serie s JOIN s.epsodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> top5EpsodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.epsodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :opcaoAno")
    List<Episodio> buscarEpsodiosPorAno(Serie serie, int opcaoAno);

    @Query("SELECT s FROM Serie s JOIN s.epsodios e GROUP BY s ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> encontrarEpsodiosMaisRecentes();


}
